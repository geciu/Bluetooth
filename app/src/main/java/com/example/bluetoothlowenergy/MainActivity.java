package com.example.bluetoothlowenergy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    TextView mStatusBlueTv, mPairedTv;
    ImageView mBlueIv;
    Button mOnBtn, mOffBtn, mDiscoverBtn, mPairedBtn;

    BluetoothAdapter mBlueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatusBlueTv = findViewById(R.id.statusBluetoothTv);
        mPairedTv = findViewById(R.id.pairedTv);
        mBlueIv = findViewById(R.id.bluetoothIv);
        mOnBtn = findViewById(R.id.onBtn);
        mOffBtn = findViewById(R.id.offBtn);
        mDiscoverBtn = findViewById(R.id.discoverableBtn);
        mPairedBtn = findViewById(R.id.pairedBtn);

        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBlueAdapter == null) {
            mStatusBlueTv.setText("Bluetooth jest niedostepny");
        }
        else {
            mStatusBlueTv.setText("Bluetooth jest dostepny");
        }

        if (mBlueAdapter.isEnabled()){
            mBlueIv.setImageResource(R.drawable.ic_action_on);
        }
        else{
            mBlueIv.setImageResource(R.drawable.ic_action_off);
        }

        mOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mBlueAdapter.isEnabled()){
                    showToast("Włączanie Bluetooth'a");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                }
                else {
                showToast("Bluetooth jest włączony");
                }
            }
        });

        mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mBlueAdapter.isDiscovering()){
                    showToast("Urządzenie jest widoczne");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_DISCOVER_BT);
                }
            }
        });

        mOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBlueAdapter.isEnabled()){
                    mBlueAdapter.disable();
                    mBlueIv.setImageResource(R.drawable.ic_action_off);
                }
                else{
                    showToast("Bluetooth juz jest wyłączony");
                }
            }
        });

        mPairedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBlueAdapter.isEnabled()){
                    mPairedTv.setText("Połączono urządzenia");
                    Set<BluetoothDevice> devices = mBlueAdapter.getBondedDevices();
                    for(BluetoothDevice device: devices){
                        mPairedTv.append("\nUrządzenie: " + device.getName() + "," + device);
                    }
                }
                else{
                    showToast("Włącz Bluetooth");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case REQUEST_ENABLE_BT:
            if(resultCode == RESULT_OK){
                mBlueIv.setImageResource(R.drawable.ic_action_on);
                showToast("Bluetooth jest włączony");
            }
            else{
                showToast("Nie udało się połączyć");
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    
        public void addDevice(BluetoothDevice device, int rssi) {

        String address = device.getAddress();
        if (!mBTDevicesHashMap.containsKey(address)) {
            BTLE_Device btleDevice = new BTLE_Device(device);
            btleDevice.setRSSI(rssi);

            mBTDevicesHashMap.put(address, btleDevice);
            mBTDevicesArrayList.add(btleDevice);
        }
        else {
            mBTDevicesHashMap.get(address).setRSSI(rssi);
        }

        adapter.notifyDataSetChanged();
    }

    public void startScan(){
        btn_Scan.setText("Scanning...");

        mBTDevicesArrayList.clear();
        mBTDevicesHashMap.clear();

        mBTLeScanner.start();
}
