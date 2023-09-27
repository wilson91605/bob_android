package com.example.hiwin.teacher_version_bob.activity;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.hiwin.teacher_version_bob.R;
import com.example.hiwin.teacher_version_bob.communication.bluetooth.concrete.SymbolPackageHandler;
import com.example.hiwin.teacher_version_bob.communication.bluetooth.concrete.SerialSocket;
import com.example.hiwin.teacher_version_bob.communication.bluetooth.framework.SerialListener;
import com.example.hiwin.teacher_version_bob.communication.service.SerialService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 * 藍芽連接之主畫面，繼承此Activity。
 */
public abstract class BluetoothCommunicationActivity extends AppCompatActivity {
    private static final String TAG_NAME = "BluetoothCommunicationActivity";


    private enum Connected {False, Pending, True}

    private Connected connected = Connected.False;
    private MenuItem item_connection;
    private String deviceAddress;
    private SerialService serialService;


    /**
     * 當接收到藍芽封包時，此函式會被執行。
     * @param msg 接收到的藍芽字串
     */
    protected abstract void receive(String msg);

    protected final boolean isConnected() {
        return connected == Connected.True;
    }

    protected abstract Toolbar getToolbar();

    protected abstract int getContentView();

    protected abstract void initialize(Bundle savedInstanceState);

    protected abstract String getDeviceAddress(Bundle savedInstanceState);

    /**
     * 當藍芽連線時，此方法會被呼叫
     */
    protected abstract void onConnect();

    /**
     * 當藍芽斷線時，此方法會被呼叫
     */
    protected abstract void onDisconnect();

    /**
     * 當藍芽發生錯誤時，此方法會被呼叫
     * @param e 錯誤資訊
     */
    protected abstract void onSerialError(Exception e);


    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        setSupportActionBar(getToolbar());
        initialize(savedInstanceState);

        boolean sus = bindService(new Intent(this, SerialService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d("BindService", sus + "");
        deviceAddress = getDeviceAddress(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        item_connection = menu.add("Connection");
        item_connection.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        setConnectionMenuItem(false);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == item_connection) {
            try {
                if (connected == Connected.False)
                    connect();
                else if (connected == Connected.True)
                    disconnect();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG_NAME, e.getMessage());
            }
            return true;
        }

        return false;
    }


    @Override
    public void onDestroy() {
        if (connected == Connected.True)
            disconnect();
        stopService(new Intent(this, SerialService.class));
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (serialService != null)
            serialService.attach(serialDataListener);
        else
            // prevents service destroy on unbind from recreated activity caused by orientation change
            startService(new Intent(this, SerialService.class));
    }

    @Override
    public void onStop() {
        if (serialService != null && !this.isChangingConfigurations())
            serialService.detach();
        super.onStop();
    }


    private void connect() throws IOException {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            Log.d(TAG_NAME, "connecting...");
            SerialSocket socket = new SerialSocket(this, device, new SymbolPackageHandler(new byte[]{0x04}));
            serialService.connect(socket);
            connected = Connected.Pending;

        } catch (Exception e) {
            serialDataListener.onSerialConnectError(e);
            throw e;
        }
    }

    private void disconnect() {
        onDisconnect();
        Log.d(TAG_NAME, "disconnect");
        connected = Connected.False;
        serialService.disconnect();
        setConnectionMenuItem(false);
    }

    private void setConnectionMenuItem(boolean connected) {
        if (connected) {
            item_connection.setIcon(R.drawable.link_off);
            item_connection.setTitle("Disconnect");
        } else {
            item_connection.setIcon(R.drawable.link);
            item_connection.setTitle("Connect");
        }
    }


    /**
     * 送出位元組陣列
     * @param bytes 位元組陣列內容
     */
    protected void send(byte[] bytes) {
        try {
//            使用PackageCodecFacade.encode(bytes)，編碼封包
            serialService.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 送出字串
     * @param msg 封包內容
     */
    protected void sendMessage(String msg) {
        try {
//            使用PackageCodecFacade.encodeString(msg)，編碼封包
            serialService.write(msg.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            serialService = ((SerialService.SerialBinder) binder).getService();
            serialService.attach(serialDataListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serialService = null;
        }
    };

    private final SerialListener serialDataListener = new SerialListener() {
        @Override
        public void onSerialConnect() {
            onConnect();
            connected = Connected.True;
            Log.d(TAG_NAME, "Bluetooth device connected");
            Toast.makeText(BluetoothCommunicationActivity.this, "Bluetooth device connected", Toast.LENGTH_SHORT).show();
            setConnectionMenuItem(true);

        }

        @Override
        public void onSerialConnectError(Exception e) {
            Log.e(TAG_NAME, "Connection Error:"+e.getMessage());
            onSerialError(e);
            disconnect();
        }

        @Override
        public void onSerialRead(byte[] data) {
//            使用PackageCodecFacade.decodeString(data,true)，解碼封包
            receive(new String(data,StandardCharsets.UTF_8));
        }

        @Override
        public void onSerialIoError(Exception e) {
            Log.e(TAG_NAME, "IO Error:"+e.getMessage());
            onSerialError(e);
            disconnect();
        }
    };
}
