package com.mobilegroup.core.communication;


import com.mobilegroup.core.beans.MgDataResult;
import com.mobilegroup.core.callbacks.MgCallback;
import com.mobilegroup.core.callbacks.MgDataReceiver;
import com.mobilegroup.core.enums.MgConnectCommand;

public interface NetWorkApiService {
    void sendCommand(String requestId,MgConnectCommand command, MgDataReceiver callback);
    void sendBlockEngine(String requestId,MgDataReceiver callback);
    void sendUnlockEngine(String requestId,MgDataReceiver callback);
    void sendLockDoor(String requestId,MgDataReceiver callback);
    void sendUnlockDoor(String requestId,MgDataReceiver callback);
    void sendVerifyKey(String requestId,MgDataReceiver callback);
    void sendReplaceKey(String requestId,MgDataReceiver callback);
    void sendDone(String requestId,MgDataReceiver callback);
}
