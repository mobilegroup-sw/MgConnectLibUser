package com.mobilegroup.core.networking;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.mobilegroup.core.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RequestHelper {
    private static final String TAG = RequestHelper.class.getSimpleName();

    public static String getUnitDataByPlateNumber(Context context, String plateNum) {
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
}
