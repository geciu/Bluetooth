package com.example.rssi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 101;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanFilter mScanFilter;
    private ScanSettings mScanSettings;
    //  private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    // private AdvertiseData mAdvertiseData;
    // private AdvertiseSettings mAdvertiseSettings;

    private TextView majorTxt, minorTxt, uuidTxt, foundTxt, distanceTxt, accuracyTxt, rssiTxt;
    private Button actionButton;

    private static final long SCAN_PERIOD = 10000;
    private Handler handler = new Handler();

    private boolean isScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBle();
        bindViews();
    }

    private void initBle() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported!!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        // setScanFilter();
        // setScanSettings();
    }

    private void action() {
        if (!isScanning) {
            actionButton.setText("STOP SCAN");
            isScanning = true;
            mBluetoothLeScanner.startScan(mScanCallback);
        } else {
            actionButton.setText("START SCAN");
            isScanning = false;
            mBluetoothLeScanner.stopScan(mScanCallback);
        }
    }

    private void bindViews() {
        majorTxt = findViewById(R.id.majorTxt);
        minorTxt = findViewById(R.id.minorTxt);
        uuidTxt = findViewById(R.id.uuidTxt);
        foundTxt = findViewById(R.id.foundTxt);
        distanceTxt = findViewById(R.id.distanceTxt);
        accuracyTxt = findViewById(R.id.accuracyTxt);
        rssiTxt = findViewById(R.id.rssiTxt);
        actionButton = findViewById(R.id.actionBtn);
        actionButton.setOnClickListener((v) -> action());
        if (!isScanning) {
            actionButton.setText("Start SCAN");
        } else {
            actionButton.setText("Stop SCAN");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setScanFilter() {
        ScanFilter.Builder mBuilder = new ScanFilter.Builder();
        ByteBuffer mManufacturerData = ByteBuffer.allocate(23);
        ByteBuffer mManufacturerDataMask = ByteBuffer.allocate(24);
        byte[] uuid = getIdAsByte(UUID.fromString("0CF052C297CA407C84F8B62AAC4E9020"));
        mManufacturerData.put(0, (byte) 0xBE);
        mManufacturerData.put(1, (byte) 0xAC);
        for (int i = 2; i <= 17; i++) {
            mManufacturerData.put(i, uuid[i - 2]);
        }
        for (int i = 0; i <= 17; i++) {
            mManufacturerDataMask.put((byte) 0x01);
        }
        mBuilder.setManufacturerData(224, mManufacturerData.array(), mManufacturerDataMask.array());
        mScanFilter = mBuilder.build();
    }

    public static byte[] getIdAsByte(java.util.UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }


    private void setScanSettings() {
        ScanSettings.Builder mBuilder = new ScanSettings.Builder();
        mBuilder.setReportDelay(0);
        mBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        mScanSettings = mBuilder.build();
    }

    /*
    protected void setAdvertiseData() {
        AdvertiseData.Builder mBuilder = new AdvertiseData.Builder();
        ByteBuffer mManufacturerData = ByteBuffer.allocate(24);
        byte[] uuid = getIdAsByte(UUID.fromString("0CF052C297CA407C84F8B62AAC4E9020"));
        mManufacturerData.put(0, (byte) 0xBE); // Beacon Identifier
        mManufacturerData.put(1, (byte) 0xAC); // Beacon Identifier
        for (int i = 2; i <= 17; i++) {
            mManufacturerData.put(i, uuid[i - 2]); // adding the UUID
        }
        mManufacturerData.put(18, (byte) 0x00); // first byte of Major
        mManufacturerData.put(19, (byte) 0x09); // second byte of Major
        mManufacturerData.put(20, (byte) 0x00); // first minor
        mManufacturerData.put(21, (byte) 0x06); // second minor
        mManufacturerData.put(22, (byte) 0xB5); // txPower
        mBuilder.addManufacturerData(224, mManufacturerData.array()); // using google's company ID
        mAdvertiseData = mBuilder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void setAdvertiseSettings() {
        AdvertiseSettings.Builder mBuilder = new AdvertiseSettings.Builder();
        mBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
        mBuilder.setConnectable(false);
        mBuilder.setTimeout(0);
        mBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
        mAdvertiseSettings = mBuilder.build();
    }
*/
    protected ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            Log.d("MainActivity", "mScanCallback:" + result);
            ScanRecord mScanRecord = result.getScanRecord();
            //mScanRecord.getTxPowerLevel()
            byte[] manufacturerData = mScanRecord.getManufacturerSpecificData(224);//todo
            int mRssi = result.getRssi();
            double accuracy = calculateAccuracy(mScanRecord.getTxPowerLevel(), mRssi);
            String distance = getDistance(accuracy);

            foundTxt.setText("Found:" + result);
            rssiTxt.setText("RSSI:" + result.getRssi() + " dBm");
            distanceTxt.setText("Distance:" + distance);
            accuracyTxt.setText("Accuracy:" + accuracy);
            //uuidTxt.setText("UUID:"+mScanRecord.);
        }
    };


    public double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }
        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }

    private String getDistance(double accuracy) {
        if (accuracy == -1.0) {
            return "Unknown";
        } else if (accuracy < 1) {
            return "Immediate";
        } else if (accuracy < 3) {
            return "Near";
        } else {
            return "Far";
        }
    }
}
