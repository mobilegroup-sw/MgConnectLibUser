package com.mobilegroup.mgconnectlibuser;

import android.content.Context;
import android.text.TextUtils;

import com.mobilegroup.core.callbacks.MgConnectionListener;
import com.mobilegroup.core.callbacks.MgDataReceiver;
import com.mobilegroup.core.communication.BluetoothService;
import com.mobilegroup.core.communication.NetWorkApiService;
import com.mobilegroup.core.enums.MgConnectCommand;
import com.mobilegroup.mgbluetooth.BluetoothServiceImpl;
import com.mobilegroup.mgnetwork.NetWorkApiServiceImp;

public class MGConnectSDKFacade {
    private static final String TAG = MGConnectSDKFacade.class.getSimpleName();
    private static MGConnectSDKFacade instance;

    private final BluetoothService bluetoothService;
    private final NetWorkApiService netWorkApiService;

    private MGConnectSDKFacade( BluetoothService bluetoothService, NetWorkApiService netWorkApiService) {
        this.bluetoothService = bluetoothService;
        this.netWorkApiService = netWorkApiService;
    }

    public static synchronized void init(Context context) {

        if (instance == null) {
            BluetoothServiceImpl bluetoothService = new BluetoothServiceImpl(context);
            NetWorkApiServiceImp netWorkApiService = new NetWorkApiServiceImp(context);
            instance = new MGConnectSDKFacade( bluetoothService, netWorkApiService);
        }
    }

    public static MGConnectSDKFacade getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SdkFacade is not initialized. Call init() first.");
        }
        return instance;
    }

    public void connectToBluetoothDevice(String plateNumber, MgConnectionListener callback) {
        if (!TextUtils.isEmpty(plateNumber) && callback != null) {
            bluetoothService.connectToDevice(plateNumber, callback);
        } else {
            throw new IllegalStateException("Set valid plateNumber or callback and try again");
        }
    }

    public void registerBluetoothDataReceiver(MgDataReceiver callback) {
        if (callback == null)
            throw new IllegalStateException("Set valid callback and try again");
        else{
            bluetoothService.registerMgDataReceiver(callback);
        }

    }

    public void sendCommandViaBluetooth(String requestId, MgConnectCommand command) {
        if (TextUtils.isEmpty(requestId))
            throw new IllegalStateException("Set valid requestId and try again");
        else{
            bluetoothService.sendCommand(requestId, command);
        }

    }

    public void disconnectFromDevice() {
        bluetoothService.disconnectFromDevice("disconnect");
    }


}
