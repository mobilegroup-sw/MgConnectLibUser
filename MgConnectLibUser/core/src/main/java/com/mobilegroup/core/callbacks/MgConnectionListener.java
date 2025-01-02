package com.mobilegroup.core.callbacks;


import com.mobilegroup.core.enums.BluetoothConnectionState;

public interface MgConnectionListener {
    void onConnectionStateChanged(BluetoothConnectionState state);
    void onConnectionError(Exception error);
}
