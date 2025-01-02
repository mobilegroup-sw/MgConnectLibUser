package com.mobilegroup.core.networking;


import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobilegroup.core.Utils;


import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class NetworkUtil {
    private static String TAG = NetworkUtil.class.getSimpleName();
    private static NetworkUtil instance;
    private RequestQueue requestQueue;

    private NetworkUtil(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized NetworkUtil getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkUtil(context);
        }
        return instance;
    }

    public interface OnResponseListener {
        void onResponse(String response);

        void onErrorResponse(String error);

        void onProgressUpdate(int progress);  // New method to update progress
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }


    public void sendAsyncGet(String url, final OnResponseListener listener) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (listener != null) {
                            listener.onResponse(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (listener != null) {
                            String errorMessage = error.getMessage();
                            if (errorMessage == null) {
                                errorMessage = "Unknown error";  // or any default message you'd prefer
                            }
                            listener.onErrorResponse(errorMessage);
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }




    public String sendSyncGet(String url) {
        String TAG = "NetworkManager";
        Log.d(TAG, "sendSyncGet() has been called");
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] responseHolder = new String[1];

        Log.d(TAG, "Sending a GET request to: " + url);

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    response -> {
                        // Successful response
                        Log.d(TAG, "Request successful. Response: " + response);
                        responseHolder[0] = response;
                        latch.countDown();
                    }, error -> {
                // Handle error response
                if (error.networkResponse != null) {
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    Log.e(TAG, "Error response from server: " + responseBody);
                    responseHolder[0] = responseBody; // Capture the error response
                } else {
                    Log.e(TAG, "Request Error: " + error.toString());
                }
                latch.countDown();
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("key", "API_KEY"); // Add your API key for all the get requests
                    return headers;
                }
            };

            requestQueue.add(stringRequest);

            if (!latch.await(30, TimeUnit.SECONDS)) { // Wait for the response
                Log.e(TAG, "Request timed out.");
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "Request interrupted: error = " + Utils.getStackTraceString(e));
        } catch (Exception e) {
            Log.e(TAG, "Failed to send request: error = " + Utils.getStackTraceString(e));
        }
        return responseHolder[0];
    }

    public void sendAsyncPost(String url, String postData, Map<String, String> headers, OnResponseListener listener) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (listener != null) {
                            listener.onResponse(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (listener != null) {
                            listener.onErrorResponse(error.getMessage());
                        }
                    }
                }) {
            @Override
            public byte[] getBody() {
                if (postData == null)
                    return null;
                return postData.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headers != null) {
                    return headers;
                }
                return super.getHeaders();
            }
        };

        requestQueue.add(stringRequest);
    }

    public String sendSyncPost(String url, String postData, Map<String, String> headers) {
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, future, future) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                if (postData == null) {
                    return null;
                }
                return postData.getBytes(StandardCharsets.UTF_8); // Ensure encoding is specified
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headers != null) {
                    return headers;
                }
                return super.getHeaders();
            }
        };

        requestQueue.add(request);

        // Wait for the response, with a timeout to prevent indefinite waiting
        try {
            return future.get(30, TimeUnit.SECONDS); // Throws TimeoutException if there is no response in 30 seconds
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return null;
        }
    }




    // Static function to check if there is an active network connection
    @RequiresPermission(allOf = {Manifest.permission.ACCESS_NETWORK_STATE})
    public static boolean isNetworkConnected(Context context) {
        String funcName = "isNetworkConnected() ";
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            Log.d(TAG, funcName + "Network connection status: " + (isConnected ? "Connected" : "Not Connected"));
            return isConnected;
        } catch (Exception e) {
            Log.e(TAG, funcName + "Exception occurred while checking network connection: " + e.getMessage(), e);
            return false;
        }
    }

    // General GET request - use this when you need to make a GET request
    public static void getRequest(final Context context, String url, final OnResponseListener listener) {
        String funcName = "getRequest() ";
        Log.d(TAG, funcName + "has been called. URL: " + url);

        // Check if the network is connected
        if (!isNetworkConnected(context)) {
            Log.e(TAG, funcName + "Network is not connected.");
            listener.onErrorResponse("Network is not connected.");
            return;
        }

        // Create a RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Create a StringRequest for the GET request
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, funcName + "Response received: " + response);
                        listener.onResponse(response); // Pass the response to the listener
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log the error details
                        if (error.networkResponse != null) {
                            String statusCode = String.valueOf(error.networkResponse.statusCode);
                            Log.e(TAG, funcName + "Network error: Status code: " + statusCode);
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                Log.e(TAG, funcName + "Response error body: " + responseBody);
                                listener.onErrorResponse(responseBody);
                            } catch (UnsupportedEncodingException e) {
                                Log.e(TAG, funcName + "Error parsing response body: " + e.getMessage());
                                listener.onErrorResponse("Error parsing response body: " + e.getMessage());
                            }
                        } else {
                            Log.e(TAG, funcName + "Network error, no response received");
                            Log.e(TAG, funcName + "Error: " + error.getMessage());
                            listener.onErrorResponse("Network error, no response received");
                        }
                    }
                }
        );

        // Add the request to the Volley queue
        requestQueue.add(stringRequest);
    }

    // General POST request
    public static void postRequest(final Context context, String url, final JSONObject jsonBody, final Map<String, String> headers, final OnResponseListener listener) {
        String funcName = "postRequest() ";
        Log.d(TAG, funcName + "has been called. URL: " + url);

        // Check if the network is connected
        if (!isNetworkConnected(context)) {
            Log.e(TAG, funcName + "Network is not connected.");
            listener.onErrorResponse("Network is not connected.");
            return;
        }

        // Create a RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Create a StringRequest for the POST request
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, funcName + "Response received: " + response);
                        listener.onResponse(response); // Pass the response to the listener
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log the error details
                        if (error.networkResponse != null) {
                            String statusCode = String.valueOf(error.networkResponse.statusCode);
                            Log.e(TAG, funcName + "Network error: Status code: " + statusCode);
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                Log.e(TAG, funcName + "Response error body: " + responseBody);
                                listener.onErrorResponse(responseBody);
                            } catch (UnsupportedEncodingException e) {
                                Log.e(TAG, funcName + "Error parsing response body: " + e.getMessage());
                                listener.onErrorResponse("Error parsing response body: " + e.getMessage());
                            }
                        } else {
                            Log.e(TAG, funcName + "Network error, no response received");
                            Log.e(TAG, funcName + "Error: " + error.getMessage());
                            listener.onErrorResponse("Network error, no response received");
                        }
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                try {
                    return jsonBody == null ? null : jsonBody.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    Log.e(TAG, funcName + "Unsupported encoding while converting JSON body to bytes.");
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers != null ? headers : new HashMap<String, String>(); // Use provided headers or an empty map
            }
        };

        // Set retry policy in case of network timeout or error
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, // 10 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Number of retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        // Add the request to the Volley queue
        requestQueue.add(stringRequest);
    }


}
