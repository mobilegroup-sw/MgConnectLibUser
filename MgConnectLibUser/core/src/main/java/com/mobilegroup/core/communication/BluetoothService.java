package com.mobilegroup.core.communication;


import com.mobilegroup.core.callbacks.MgConnectionListener;
import com.mobilegroup.core.callbacks.MgDataReceiver;
import com.mobilegroup.core.enums.MgConnectCommand;

public interface BluetoothService {
    void connectToDevice(String plateNumber, MgConnectionListener callback);
    void registerMgDataReceiver(MgDataReceiver callback);
    void sendCommand(String requestId, MgConnectCommand command);
    void disconnectFromDevice(String requestId);
    void sendBlockEngine(String requestId);
    void sendUnlockEngine(String requestId);
    void sendLockDoor(String requestId);
    void sendUnlockDoor(String requestId);
    void sendVerifyKey(String requestId);
    void sendReplaceKey(String requestId);
    void sendGetCarData(String requestId);
    void sendDone(String requestId);
}
