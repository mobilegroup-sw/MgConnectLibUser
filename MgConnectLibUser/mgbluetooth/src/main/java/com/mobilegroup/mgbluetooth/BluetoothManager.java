package com.mobilegroup.mgbluetooth;


import static com.mobilegroup.core.Constants.COMMAND_PREFIX;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.mobilegroup.core.beans.MgDataResult;
import com.mobilegroup.core.callbacks.MgConnectionListener;
import com.mobilegroup.core.callbacks.MgDataReceiver;
import com.mobilegroup.core.enums.BluetoothConnectionState;
import com.mobilegroup.core.enums.MgConnectCommand;
import com.mobilegroup.core.exceptions.MgException;
import com.mobilegroup.core.networking.RequestHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothManager {
    private static final String TAG = "BluetoothManager";
    private final BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Thread communicationThread;
    private volatile boolean isRunning;

    private final Context context;
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private MgConnectionListener connectionCallback;
    private MgDataReceiver readListener;
    private BluetoothConnectionState connectionState;
    String targetDeviceName;

    public BluetoothManager(Context context) {
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connectionState = BluetoothConnectionState.Disconnected;
    }

    public MgDataReceiver getReadListener() {
        return readListener;
    }

    public void setReadListener(MgDataReceiver readListener) {
        this.readListener = readListener;
    }

    public void start(String deviceName, MgConnectionListener connectionCallback) {
        this.connectionCallback = connectionCallback;
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            connectionCallback.onConnectionError(new MgException(MgException.ErrorCode.BT_CONNECTION_FAILED, "Bluetooth is not enabled or not supported."));
            return;
        }

        //TODO rest api request to get device imei
       // RequestHelper.getUnitDataByPlateNumber(context,deviceName);

        searchAndPairDevice(deviceName);

    }


    public void searchAndPairDevice(String targetDeviceName) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if (connectionCallback != null)
                connectionCallback.onConnectionError(new MgException(MgException.ErrorCode.MISSING_PERMISSIONS, "Required permissions are not granted."));
            return;
        }
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            if (connectionCallback != null)
                connectionCallback.onConnectionError(new MgException(MgException.ErrorCode.BT_CONNECTION_FAILED, "Bluetooth is not enabled or supported."));
            return;
        }
        this.targetDeviceName = targetDeviceName;
        // Register receivers for discovery and pairing
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(deviceDiscoveryReceiver, filter);


        // Start discovery
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver deviceDiscoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                if (connectionCallback != null)
                    connectionCallback.onConnectionError(new MgException(MgException.ErrorCode.MISSING_PERMISSIONS, "Required permissions are not granted."));
                return;
            }
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (connectionState.equals(BluetoothConnectionState.Connected)) {
                    bluetoothAdapter.cancelDiscovery();
                    return;
                }

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && device.getName() != null) {
                    Log.d(TAG, "Found device: " + device.getName());

                    // Check if the device matches the target name
                    if (device.getName().equalsIgnoreCase(targetDeviceName)) {
                        bluetoothAdapter.cancelDiscovery();

                        connectToDevice(device);

                    }
                }
            }
        }
    };


    private void connectToDevice(BluetoothDevice device) {
        new Thread(() -> {
            try {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    if (connectionCallback != null)
                        connectionCallback.onConnectionError(new MgException(MgException.ErrorCode.MISSING_PERMISSIONS, "Required permissions are not granted."));
                    return;
                }
                bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                bluetoothSocket.connect();
                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();
                Log.d(TAG, "targetDevice " + device.getName() + " Connected");
                connectionState = BluetoothConnectionState.Connected;
                if (connectionCallback != null) {
                    connectionCallback.onConnectionStateChanged(connectionState);
                }

                startCommunication();
                sendCommandsWithDelay();
            } catch (IOException e) {
                if (connectionCallback != null) {
                    connectionCallback.onConnectionError(new MgException(MgException.ErrorCode.BT_CONNECTION_FAILED, "Error connecting to device: " + e.getMessage()));
                }

            }
        }).start();
    }

    private void startCommunication() {
        isRunning = true;
        communicationThread = new Thread(() -> {
            byte[] buffer = new byte[1024];
            int bytes;

            try {
                while (isRunning) {
                    bytes = inputStream.read(buffer);
                    String data = new String(buffer, 0, bytes);

                    // Check for the specific data pattern
                    if (data.startsWith("Hc05") && data.endsWith("50cH")) {
                        Log.d(TAG, "Command result: " + data);
                        if (readListener != null) {
                            MgDataResult result = new MgDataResult();
                            result.setSuccess(true);
                            result.setRequestId(data);
                            readListener.onReceiveData(result);
                        }
                    }
                }
            } catch (IOException e) {
                if (readListener != null) {
                    readListener.onError("Error reading data:", e);
                }
            }
        });
        communicationThread.start();
    }

    public void sendCommand(String requestId, String command) {
        if (outputStream == null) {
            connectionCallback.onConnectionError(new MgException(MgException.ErrorCode.BT_CONNECTION_FAILED, "Output stream is not available. "));
            stop();
            return;
        }

        new Thread(() -> {
            try {
                outputStream.write(command.getBytes());
                Log.d(TAG, "Command sent: " + command);
                if (readListener != null) {
                    MgDataResult result = new MgDataResult();
                    result.setSuccess(true);
                    result.setRequestId(requestId);
                    readListener.onReceiveData(result);
                }
            } catch (IOException e) {
                Log.d(TAG, "Error sending command: " + e.getMessage());
                connectionCallback.onConnectionError(new MgException(MgException.ErrorCode.BT_CONNECTION_FAILED, "Error sending command: " + e.getMessage()));
            }
        }).start();
    }

    public void stop() {

        isRunning = false;

        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (bluetoothSocket != null) bluetoothSocket.close();

            context.unregisterReceiver(deviceDiscoveryReceiver);
            connectionState = BluetoothConnectionState.Disconnected;
            if (connectionCallback != null) {
                connectionCallback.onConnectionStateChanged(connectionState);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing resources: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Receiver not registered: " + e.getMessage());
        }

        if (communicationThread != null) {
            communicationThread.interrupt();
        }
    }

    private void sendCommandsWithDelay() {
        new Thread(() -> {
            try {
                // Wait for 20 milliseconds
                Thread.sleep(20);
                // Send the start command
                sendCommand(MgConnectCommand.Start.getCommand(), COMMAND_PREFIX + " " + MgConnectCommand.Start.getCommand());

                // Wait for 20 milliseconds
                Thread.sleep(20);

                // Send the password command
                sendCommand(MgConnectCommand.DimaMode.getCommand(), COMMAND_PREFIX + " " + MgConnectCommand.DimaMode.getCommand());
            } catch (InterruptedException e) {
                Log.e(TAG, "Error during command delay: " + e.getMessage());
            }
        }).start();
    }
}

