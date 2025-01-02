package com.mobilegroup.mgbluetooth;

import static com.mobilegroup.core.Constants.COMMAND_PREFIX;

import android.content.Context;

import com.mobilegroup.core.callbacks.MgConnectionListener;
import com.mobilegroup.core.callbacks.MgDataReceiver;
import com.mobilegroup.core.communication.BluetoothService;
import com.mobilegroup.core.enums.MgConnectCommand;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BluetoothServiceImpl implements BluetoothService {


    private BluetoothManager BluetoothManager;

    public BluetoothServiceImpl(Context context) {
        BluetoothManager = new BluetoothManager(context);
    }


    @Override
    public void connectToDevice(String plateNumber, MgConnectionListener callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            BluetoothManager.start(plateNumber, callback);

        });
        executor.shutdown();
    }

    @Override
    public void registerMgDataReceiver(MgDataReceiver callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            BluetoothManager.setReadListener(callback);

        });
        executor.shutdown();


    }

    @Override
    public void sendCommand(String requestId, MgConnectCommand command) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            BluetoothManager.sendCommand( requestId,COMMAND_PREFIX +" "+command.getCommand());
        });
        executor.shutdown();

    }

    @Override
    public void disconnectFromDevice(String requestId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            BluetoothManager.stop();

        });
        executor.shutdown();
    }

    @Override
    public void sendBlockEngine(String requestId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            BluetoothManager.sendCommand( requestId,COMMAND_PREFIX +" "+MgConnectCommand.BlockEngine.getCommand());
        });
        executor.shutdown();
    }

    @Override
    public void sendUnlockEngine(String requestId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            BluetoothManager.sendCommand( requestId,COMMAND_PREFIX +" "+MgConnectCommand.UnlockEngine.getCommand());
        });
        executor.shutdown();
    }

    @Override
    public void sendLockDoor(String requestId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            BluetoothManager.sendCommand( requestId,COMMAND_PREFIX +" "+MgConnectCommand.LockDoor.getCommand());
        });
        executor.shutdown();
    }

    @Override
    public void sendUnlockDoor(String requestId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            BluetoothManager.sendCommand( requestId,COMMAND_PREFIX +" "+MgConnectCommand.UnlockDoor.getCommand());
        });
        executor.shutdown();
    }

    @Override
    public void sendVerifyKey(String requestId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            BluetoothManager.sendCommand( requestId,COMMAND_PREFIX +" "+MgConnectCommand.VerifyKey.getCommand());
        });
        executor.shutdown();
    }

    @Override
    public void sendReplaceKey(String requestId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            BluetoothManager.sendCommand( requestId,COMMAND_PREFIX +" "+MgConnectCommand.ReplaceKey.getCommand());
        });
        executor.shutdown();
    }

    @Override
    public void sendGetCarData(String requestId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            BluetoothManager.sendCommand( requestId,COMMAND_PREFIX +" "+MgConnectCommand.GetCarData.getCommand());
        });
        executor.shutdown();
    }

    @Override
    public void sendDone(String requestId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            BluetoothManager.sendCommand( requestId,COMMAND_PREFIX +" "+MgConnectCommand.Done.getCommand());
        });
        executor.shutdown();
    }
}
