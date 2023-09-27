package com.example.hiwin.teacher_version_bob.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hiwin.teacher_version_bob.DeviceAdapter;
import com.example.hiwin.teacher_version_bob.R;
import com.example.hiwin.teacher_version_bob.fragment.ModeDialogFragment;

import java.util.ArrayList;

public class BluetoothConnectionActivity extends AppCompatActivity {
    /*
        reference:
            http://tw.gitbook.net/android/android_bluetooth.html
     */
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);
        context = this;
        initializeToolbar();
        bluetoothInitialize();
        initializeDeviceList();
    }

    /**
     * 新增工具列
     */
    private void initializeToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.devs_toolbar);
        setSupportActionBar(myToolbar);
    }

    /**
     * 初始化並列出已配對之藍芽裝置
     */
    private void initializeDeviceList() {
        ListView deviceList = (ListView) findViewById(R.id.devicesList);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceList.setAdapter(
                new DeviceAdapter(
                        context,
                        new ArrayList<>(
                                bluetoothAdapter.getBondedDevices())));
        deviceList.setOnItemClickListener(onClickListView);
    }

    /**
     * 取得藍芽權限，當藍芽未開啟時要求開啟藍芽
     */
    private void bluetoothInitialize() {
        requestPermissions(new String[]{Manifest.permission.BLUETOOTH}, 1);
        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(turnOn, 0);
    }

    /**
     * 當裝置被點選時執行之程式
     */
    private final AdapterView.OnItemClickListener onClickListView = (parent, view, position, id) -> {
        BluetoothDevice selected_device = (BluetoothDevice) parent.getItemAtPosition(position);
        Toast.makeText(BluetoothConnectionActivity.this, selected_device.getName(), Toast.LENGTH_SHORT).show();

        ModeDialogFragment newFragment = new ModeDialogFragment();
        newFragment.setListener(mode -> {
            Intent it = new Intent(context, mode.getSelectedClass());
            it.putExtra("address", selected_device.getAddress());
            startActivity(it);

        });
        newFragment.show(getSupportFragmentManager(), "missiles");
    };
}
