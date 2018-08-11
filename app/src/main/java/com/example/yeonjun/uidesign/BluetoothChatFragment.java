/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.yeonjun.uidesign;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothChatFragment extends Fragment {

    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private SensorListAdapter sensorListAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    public static BluetoothChatService mChatService = null;

    Button btnUnpairing;
    TextView connectedStatus, connectedMac, connectedDevice;
    private static String deviceName, deviceMAC;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        sp = getActivity().getSharedPreferences(getString(R.string.sh_pref), Context.MODE_PRIVATE);
        editor = sp.edit();

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }

        new SensorListTask(mHandler, sp).execute();
    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mChatService != null) {
//            mChatService.stop();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        connectedStatus = view.findViewById(R.id.connectedStatus);
        connectedMac = view.findViewById(R.id.connectedMac);
        connectedDevice = view.findViewById(R.id.connectedDevice);

        btnUnpairing = view.findViewById(R.id.btnUnpairing);
        btnUnpairing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChatService != null) {
                    mChatService.stop();
                    deviceMAC = "";
                    deviceName = "";
                    editor.remove(StatusCode.SSN);
                    editor.apply();
                }
            }
        });

        mConversationView = (ListView) view.findViewById(R.id.in);
//
//        sensorListAdapter = new SensorListAdapter(getActivity(), sp.getString(StatusCode.LIST_SENSOR, null));
//
//        mConversationView.setAdapter(sensorListAdapter);

        mConversationView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String SSN = ((TextView)view.findViewById(R.id.itemSSN)).getText().toString();
                final String MAC = ((TextView)view.findViewById(R.id.itemMAC)).getText().toString();
                final String Device = ((TextView)view.findViewById(R.id.itemDeviceName)).getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Sensor Info");
                builder.setIcon(getResources().getDrawable(R.drawable.ic_info_black_24dp));
                builder.setMessage("SSN : " + SSN + "\nMAC : " + MAC + "\nDevice : " + Device);
                builder.setPositiveButton("Connect",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(MySingletone.getInstance().isMACValid(MAC)) {
                                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(MAC);
                                    mChatService.connect(device, true);
                                    deviceMAC = MAC;
                                    deviceName = Device;
                                    connectedMac.setText(MAC);
                                    connectedDevice.setText(Device);
                                }
                                else
                                    MySingletone.getInstance().ShowToastMessage("Invalid MAC address", getContext());
                            }
                        });
                builder.setNegativeButton("Deregister", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(!MAC.equals(deviceMAC)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Caution");
                            builder.setIcon(getResources().getDrawable(R.drawable.ic_warning_black_24dp));
                            builder.setMessage("Are you sure you want to deregister the selected sensor?");
                            builder.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            new SensorDeregisterTask(mHandler, getActivity()
                                                    .getSharedPreferences(getString(R.string.sh_pref), Context.MODE_PRIVATE))
                                                    .execute(SSN);
                                        }
                                    });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {   }
                            });
                            builder.show();
                        }
                        else
                            MySingletone.getInstance().ShowToastMessage("Currently connected devices cannot be unregistered", getContext());
                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {   }
                });
                builder.show();
            }
        });

        new SensorListTask(mHandler, sp).execute();
    }

    public static void resetDeviceInfo(){
//        deviceMAC = "";
//        deviceName = "";
    }
    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        // Initialize the array adapter for the conversation thread

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
//    private void sendMessage(String message) {
//        // Check that we're actually connected before trying anything
//        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
//            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Check that there's actually something to send
//        if (message.length() > 0) {
//            // Get the message bytes and tell the BluetoothChatService to write
//            byte[] send = message.getBytes();
//            mChatService.write(send);
//
//            // Reset out string buffer to zero and clear the edit text field
//            mOutStringBuffer.setLength(0);
//        }
//    }

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        connectedStatus.setText(resId);
        connectedMac.setText(deviceMAC);
        connectedDevice.setText(deviceName);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        connectedStatus.setText(subTitle);
        connectedMac.setText(deviceMAC);
        connectedDevice.setText(deviceName);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                                setStatus(getResources().getString(R.string.title_connected_to, mConnectedDeviceName));
                                new SensorRegisterTask(mHandler,
                                        getActivity().getSharedPreferences(getString(R.string.sh_pref), Context.MODE_PRIVATE))
                                        .execute(deviceMAC, deviceName);
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            break;
                        case BluetoothChatService.STATE_NONE:
                            deviceMAC = "";
                            deviceName = "";
                            editor.remove(StatusCode.SSN);
                            editor.apply();
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
//                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.i("JADE-SENSOR-INPUT", readMessage);
                    editor.putString(StatusCode.RT_AIR, readMessage);
                    new AirDataTransferTask(mHandler, sp).execute(readMessage);
//                    MySingletone.getInstance().ShowToastMessage(readMessage, getContext());
//                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;

                case StatusCode.SUCCESS:
                    break;
                case StatusCode.FAILED:
                    break;
                case StatusCode.REGIST_SENSOR:
                case StatusCode.DEREGIST_SENSOR:
                    new SensorListTask(mHandler, getActivity()
                            .getSharedPreferences(getString(R.string.sh_pref), Context.MODE_PRIVATE)).execute();
                    break;
                case StatusCode.RECEIVE_SENSOR_LIST:
                    sensorListAdapter = new SensorListAdapter(getActivity(), getActivity()
                            .getSharedPreferences(getString(R.string.sh_pref), Context.MODE_PRIVATE)
                            .getString(StatusCode.LIST_SENSOR, null));
                    mConversationView.setAdapter(sensorListAdapter);
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        deviceName = device.getName();
        deviceMAC = device.getAddress();

        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }

    private class SensorListAdapter extends BaseAdapter{
        private JSONArray list;
        private Context context;
        private LayoutInflater inflater;

        public SensorListAdapter(Context context, String data) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            try {
                JSONObject jobj = new JSONObject(data);
                list = jobj.getJSONArray("sensor");
            }catch (Exception e){
                Log.i("JADE-ERROR", e.toString());
            }
        }

        @Override
        public int getCount() {
            return list.length();
        }

        @Override
        public JSONObject getItem(int position) {
            JSONObject item = new JSONObject();
            try{
                item = list.getJSONObject(position);
            }catch (Exception e){
                Log.i("JADE-ERROR", e.toString());
            }finally {
                return item;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflater.inflate(R.layout.list_sensor, viewGroup, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            TextView itemSSN = view.findViewById(R.id.itemSSN);
            TextView itemMAC = view.findViewById(R.id.itemMAC);
            TextView itemDeviceName = view.findViewById(R.id.itemDeviceName);

            try{
                JSONObject item = list.getJSONObject(position);
                itemSSN.setText(String.valueOf(item.getInt("SSN")));
                itemMAC.setText(item.getString("mac"));
                itemDeviceName.setText(item.getString("name"));
            } catch (Exception e){
                Log.i("JADE-ERROR", e.toString());
            }
            return view;
        }
    }

}
