package com.mobilegroup.btcommandsender;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mobilegroup.core.beans.MgDataResult;
import com.mobilegroup.core.callbacks.MgConnectionListener;
import com.mobilegroup.core.callbacks.MgDataReceiver;
import com.mobilegroup.core.enums.BluetoothConnectionState;
import com.mobilegroup.core.enums.MgConnectCommand;
import com.mobilegroup.mgconnectlibuser.MGConnectSDKFacade;


public class MainActivity extends AppCompatActivity {
    // Request code for Bluetooth connect permission
    private static final int REQUEST_BLUETOOTH_CONNECT = 1;


    // Flag to track connection state
    private boolean isConnecting = false;
   private Button connectButton;

    private EditText macAddressInput;
    private TextView outputTextView;
    private ProgressBar progressConnect;


    private Button buttonBtSend;
    private ProgressBar progressBtSend;
    private EditText InputBtSend;

    private Button buttonNetSend;
    private ProgressBar progressNetSend;
    private EditText InputNetSend;



    private MGConnectSDKFacade mgConnectSDK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MGConnectSDKFacade.init(getApplicationContext());
        mgConnectSDK = MGConnectSDKFacade.getInstance();

        macAddressInput = findViewById(R.id.mac_address_input);
        macAddressInput.setText("C864714069192012");
//        macAddressInput.setText("C864714069243245");//mazda
         connectButton = findViewById(R.id.connect_button);
        progressConnect = findViewById(R.id.progress_connect);
        outputTextView = findViewById(R.id.output_textview);
        ScrollView scrollView = findViewById(R.id.scrollView);

        // BT
        buttonBtSend = findViewById(R.id.send_bt_command_button);
        progressBtSend = findViewById(R.id.progress_bt);
        InputBtSend = findViewById(R.id.bt_command_input);

        //Network
        buttonNetSend= findViewById(R.id.send_network_command_button);
        progressNetSend = findViewById(R.id.progress_network);
        InputNetSend = findViewById(R.id.network_command_input);


        connectButton.setOnClickListener(v -> {
            if(connectButton.getText().equals("Connect")){
                connectToBtDevice();
            } else if (connectButton.getText().equals("Disconnect")) {
                disconnectFromBtDevice();
            }
        });

        buttonBtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String command = InputBtSend.getText().toString().trim();
                switch (command){
                    case "start":
                        mgConnectSDK.sendCommandViaBluetooth(MgConnectCommand.Start.getCommand(), MgConnectCommand.Start);
                        break;
                    case "DimaMode":
                        mgConnectSDK.sendCommandViaBluetooth(MgConnectCommand.DimaMode.getCommand(), MgConnectCommand.DimaMode);
                        break;
                    case "BlockEngine":
                        mgConnectSDK.sendCommandViaBluetooth(MgConnectCommand.BlockEngine.getCommand(), MgConnectCommand.BlockEngine);
                        break;
                    case "UnlockEngine":
                        mgConnectSDK.sendCommandViaBluetooth(MgConnectCommand.UnlockEngine.getCommand(), MgConnectCommand.UnlockEngine);
                        break;
                    case "LockDoor":
                        mgConnectSDK.sendCommandViaBluetooth(MgConnectCommand.LockDoor.getCommand(), MgConnectCommand.LockDoor);
                        break;
                    case "UnlockDoor":
                        mgConnectSDK.sendCommandViaBluetooth(MgConnectCommand.UnlockDoor.getCommand(), MgConnectCommand.UnlockDoor);
                        break;
                    case "VerifyKey":
                        mgConnectSDK.sendCommandViaBluetooth(MgConnectCommand.VerifyKey.getCommand(), MgConnectCommand.VerifyKey);
                        break;
                    case "ReplaceKey":
                        mgConnectSDK.sendCommandViaBluetooth(MgConnectCommand.ReplaceKey.getCommand(), MgConnectCommand.ReplaceKey);
                        break;
                    case "GetCarData":
                        mgConnectSDK.sendCommandViaBluetooth(MgConnectCommand.GetCarData.getCommand(), MgConnectCommand.GetCarData);
                        break;
                    case "Done":
                        mgConnectSDK.sendCommandViaBluetooth(MgConnectCommand.Done.getCommand(), MgConnectCommand.Done);
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "Invalid Command", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void connectToBtDevice(){
        String macAddress = macAddressInput.getText().toString().trim();
        if (macAddress.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter a unit name", Toast.LENGTH_SHORT).show();
        } else {

            progressConnect.setVisibility(View.VISIBLE);


            mgConnectSDK.connectToBluetoothDevice(macAddress, new MgConnectionListener() {
                @Override
                public void onConnectionStateChanged(BluetoothConnectionState state) {
                    if (state.equals(BluetoothConnectionState.Connected)) {
                        runOnUiThread(() -> {
                            connectButton.setText("Disconnect");
                            progressConnect.setVisibility(View.GONE);
                            appendOutput("Connected to: " + macAddressInput.getText().toString().trim());
                            Toast.makeText(MainActivity.this, "Connection successful", Toast.LENGTH_SHORT).show();
                        });
                    } else if(state.equals(BluetoothConnectionState.Disconnected)){
                        runOnUiThread(() -> {
                            connectButton.setText("Connect");
                            progressConnect.setVisibility(View.GONE);
                            appendOutput("Disconnected from : " + macAddressInput.getText().toString().trim() );
                            Toast.makeText(MainActivity.this, "Disconnected from : " + macAddressInput.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                        });
                    }

                }

                @Override
                public void onConnectionError(Exception error) {
                    runOnUiThread(() -> {
                        progressConnect.setVisibility(View.GONE);
                        appendOutput("ConnectionError device: " + macAddressInput.getText().toString().trim()+" error "+error.getMessage());

                    });
                }
            });

            mgConnectSDK.registerBluetoothDataReceiver(mgDataReceiver);

        }
    }

    private void disconnectFromBtDevice(){
            progressConnect.setVisibility(View.VISIBLE);
            mgConnectSDK.disconnectFromDevice();
    }



    private void appendOutput(String message) {
        runOnUiThread(() -> {
        outputTextView.append(message + "\n");
        });
    }

    // Method to check if the app has Bluetooth permission and request it if needed
    private void checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // Request Bluetooth connect permission
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
            }
        }
    }

    // Handle the result of the Bluetooth permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_CONNECT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Bluetooth permission granted
            } else {
                // Show message if Bluetooth permission is denied
                Toast.makeText(this, "Bluetooth permission is required to connect devices", Toast.LENGTH_SHORT).show();
            }
        }
    }


    MgConnectionListener connectionListener = new MgConnectionListener() {

        @Override
        public void onConnectionStateChanged(BluetoothConnectionState state) {
            if (state.equals(BluetoothConnectionState.Connected)) {
                runOnUiThread(() -> {
                    connectButton.setText("Disconnect");
                    progressConnect.setVisibility(View.GONE);
                    appendOutput("Connected to: " + macAddressInput.getText().toString().trim());
                    Toast.makeText(MainActivity.this, "Connection successful", Toast.LENGTH_SHORT).show();
                });
            } else {
                runOnUiThread(() -> {
                    connectButton.setText("Connect");
                    progressConnect.setVisibility(View.GONE);
                    appendOutput("Connection to: " + macAddressInput.getText().toString().trim() + " fault");
                    Toast.makeText(MainActivity.this, "Connection to: " + macAddressInput.getText().toString().trim() + " fault", Toast.LENGTH_SHORT).show();
                });
            }
        }

        @Override
        public void onConnectionError(Exception error) {
            new Handler(Looper.getMainLooper()).post(() -> {
                progressConnect.setVisibility(View.GONE);
                appendOutput("Connection to: " + macAddressInput.getText().toString().trim() + " fault");
                appendOutput("Error: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Connection to: " + macAddressInput.getText().toString().trim() + " fault", Toast.LENGTH_SHORT).show();
            });
        }
    };

    MgDataReceiver mgDataReceiver = new MgDataReceiver() {
        @Override
        public void onReceiveData( MgDataResult result) {
            if(result!=null){
                appendOutput("'"+result.getRequestId()+"'" + " command sent");
            }
//            switch (requestId) {
//                case "IgnEn":
//
//                    runOnUiThread(() -> {
//
//                        appendOutput("IgnEn " + macAddressInput.getText().toString().trim());
//                        Toast.makeText(MainActivity.this, "IgnEn to:" + macAddressInput.getText().toString().trim(), Toast.LENGTH_SHORT).show();
//                    });
//
//                    break;
//                case "IgnDis":
//                    break;
//                case "Lock":
//                    break;
//                case "Unlock":
//                    break;
//
//            }

        }

        @Override
        public void onError(String requestId, Exception error) {

        }
    };


}
