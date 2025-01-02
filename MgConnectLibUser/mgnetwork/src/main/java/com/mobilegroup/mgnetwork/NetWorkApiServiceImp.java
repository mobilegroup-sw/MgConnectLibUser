package com.mobilegroup.mgnetwork;


import android.content.Context;

import com.mobilegroup.core.beans.MgDataResult;
import com.mobilegroup.core.callbacks.MgDataReceiver;
import com.mobilegroup.core.communication.NetWorkApiService;
import com.mobilegroup.core.enums.MgConnectCommand;
import com.mobilegroup.core.exceptions.MgException;

public class NetWorkApiServiceImp implements NetWorkApiService {

    private Context context;
    private NetworkManager networkManager;

    public NetWorkApiServiceImp(Context context) {
        this.context = context;
        networkManager = new NetworkManager();
    }


    @Override
    public void sendCommand(String requestId, MgConnectCommand command, MgDataReceiver callback) {
//        networkManager.sendMgConnectCommand(context,requestId, COMMAND_PREFIX +" "+command.getCommand(), callback);
        simulator(requestId, callback);
    }

    @Override
    public void sendBlockEngine(String requestId, MgDataReceiver callback) {
//        networkManager.sendMgConnectCommand(context,requestId, COMMAND_PREFIX +" "+MgConnectCommand.BlockEngine.getCommand(), callback);
        simulator(requestId, callback);
    }

    @Override
    public void sendUnlockEngine(String requestId, MgDataReceiver callback) {
//        networkManager.sendMgConnectCommand(context,requestId, COMMAND_PREFIX +" "+MgConnectCommand.UnlockEngine.getCommand(), callback);
        simulator(requestId, callback);
    }

    @Override
    public void sendLockDoor(String requestId, MgDataReceiver callback) {
//        networkManager.sendMgConnectCommand(context,requestId, COMMAND_PREFIX +" "+MgConnectCommand.LockDoor.getCommand(), callback);
        simulator(requestId, callback);
    }

    @Override
    public void sendUnlockDoor(String requestId, MgDataReceiver callback) {
//        networkManager.sendMgConnectCommand(context,requestId, COMMAND_PREFIX +" "+MgConnectCommand.UnlockDoor.getCommand(), callback);
        simulator(requestId, callback);
    }

    @Override
    public void sendVerifyKey(String requestId, MgDataReceiver callback) {
//        networkManager.sendMgConnectCommand(context,requestId, COMMAND_PREFIX +" "+MgConnectCommand.VerifyKey.getCommand(), callback);
        simulator(requestId, callback);
    }

    @Override
    public void sendReplaceKey(String requestId, MgDataReceiver callback) {
//        networkManager.sendMgConnectCommand(context,requestId, COMMAND_PREFIX +" "+MgConnectCommand.ReplaceKey.getCommand(), callback);
        simulator(requestId, callback);
    }


    @Override
    public void sendDone(String requestId, MgDataReceiver callback) {
//        networkManager.sendMgConnectCommand(context,requestId, COMMAND_PREFIX +" "+MgConnectCommand.Done.getCommand(), callback);
        simulator(requestId, callback);
    }

    private void simulator(String requestId, MgDataReceiver callback) {
        new Thread(() -> {
            try {
                Thread.sleep(200);

                if (callback != null) {
                    MgDataResult result = new MgDataResult();
                    result.setSuccess(true);
                    result.setRequestId(requestId);
                    callback.onReceiveData(result);
                }
            } catch (InterruptedException e) {

                if (callback != null)
                    callback.onError(requestId, new MgException(MgException.ErrorCode.UNKNOWN_ERROR, "Something went wrong!"));
            }
        }).start();
    }
}
