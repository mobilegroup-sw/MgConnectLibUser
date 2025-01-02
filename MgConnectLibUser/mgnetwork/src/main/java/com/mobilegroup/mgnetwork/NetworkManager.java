package com.mobilegroup.mgnetwork;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.mobilegroup.core.Utils;
import com.mobilegroup.core.callbacks.MgDataReceiver;
import com.mobilegroup.core.exceptions.MgException;
import com.mobilegroup.core.networking.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkManager {
    private static final String TAG = NetworkManager.class.getSimpleName();
    /**
     * Simulates a Network connection.
     *
     * @param deviceId The ID of the device to connect to.
     * @return True if the connection is successful.
     * @throws Exception If the connection fails.
     */
    public boolean connect(String deviceId) throws MgException {
        // Simulate delay and success
        try {
            // Simulate a connection delay
            Thread.sleep(1500);

            if (deviceId == null || deviceId.isEmpty()) {
                throw new MgException(MgException.ErrorCode.INVALID_TOKEN, "Device ID is invalid for Network connection.");
            }

            // Simulate a random failure
            if (Math.random() > 0.8) {
                throw new MgException(MgException.ErrorCode.NETWORK_CONNECTION_FAILED, "Failed to connect to device via Network.");
            }

            System.out.println("Connected to device via Network: " + deviceId);
            return true;
        } catch (InterruptedException e) {
            throw new MgException(MgException.ErrorCode.UNKNOWN_ERROR, "Unexpected interruption during Network connection.", e);
        }
    }

    public String getUnitDataByPlateNumber(Context context, String plateNum) {
        Log.d(TAG, "getUnitDataByPlateNumber() .. = " );
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("plateNumber", plateNum);
        } catch (JSONException e) {
            Log.d(TAG, Utils.getStackTraceString(e));
        }


        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        String  response = NetworkUtil.getInstance(context).sendSyncPost("http://host:port/", jsonObject.toString(), headers);
        Log.d(TAG,"getUnitDataByPlateNumber response = " + response);
        if (!TextUtils.isEmpty(response)){
            return response;
        }else {
            return null;
        }

    }

    public String sendMgConnectCommand(Context context,String requestId, String command, MgDataReceiver callback) {
        Log.d(TAG, "getUnitDataByPlateNumber() .. = " );
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("commandType", command);
            jsonObject.put("requestId", requestId);
        } catch (JSONException e) {
            Log.d(TAG, Utils.getStackTraceString(e));
        }


        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        String  response = NetworkUtil.getInstance(context).sendSyncPost("http://host:port/", jsonObject.toString(), headers);
        Log.d(TAG,"getUnitDataByPlateNumber response = " + response);
        if (!TextUtils.isEmpty(response)){
            return response;
        }else {
            if(callback!=null)
                callback.onError(requestId, new MgException(MgException.ErrorCode.UNKNOWN_ERROR, "Something went wrong!"));
            return null;
        }

    }
}
