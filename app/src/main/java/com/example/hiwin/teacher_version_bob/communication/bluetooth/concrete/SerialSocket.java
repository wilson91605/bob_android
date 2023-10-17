package com.example.hiwin.teacher_version_bob.communication.bluetooth.concrete;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.example.hiwin.teacher_version_bob.Constants;
import com.example.hiwin.teacher_version_bob.communication.bluetooth.framework.SerialListener;
import com.example.hiwin.teacher_version_bob.communication.bluetooth.framework.PackageHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Executors;

public class SerialSocket implements Runnable {

    private static final UUID BLUETOOTH_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BroadcastReceiver disconnectBroadcastReceiver;
    private final PackageHandler handler;
    private final Context context;
    private SerialListener listener;
    private final BluetoothDevice device;
    private BluetoothSocket socket;
    private boolean connected;

    public SerialSocket(Context context, BluetoothDevice device, PackageHandler handler) {
//        if(context instanceof Activity)
//            throw new InvalidParameterException("expected non UI context");

        this.context = context;
        this.device = device;
        this.handler = handler;
        disconnectBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (listener != null)
                    listener.onSerialIoError(new IOException("background disconnect"));
                disconnect(); // disconnect now, else would be queued until UI re-attached
            }
        };
    }

    String getName() {
        return device.getName() != null ? device.getName() : device.getAddress();
    }

    /**
     * connect-success and most connect-errors are returned asynchronously to listener
     */
    public void connect(SerialListener listener) throws IOException {
        this.listener = listener;
        context.registerReceiver(disconnectBroadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_DISCONNECT));
        Executors.newSingleThreadExecutor().submit(this);
    }

    public void disconnect() {
        listener = null; // ignore remaining data and errors
        // connected = false; // run loop will reset connected
        if (socket != null) {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            socket = null;
        }
        try {
            context.unregisterReceiver(disconnectBroadcastReceiver);
        } catch (Exception ignored) {
        }
    }

    public void write(byte[] data) throws IOException {
        if (!connected)
            throw new IOException("not connected");
        socket.getOutputStream().write(handler.convertToPackage(data));
    }

    @Override
    public void run() { // connect & read
        try {
            socket = device.createRfcommSocketToServiceRecord(BLUETOOTH_SPP);
            socket.connect();
            if(listener != null)
                listener.onSerialConnect();
        } catch (Exception e) {
            if(listener != null)
                listener.onSerialConnectError(e);
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            socket = null;
            return;
        }
        connected = true;
        try {
            byte[] buffer = new byte[1024];
            int len;
            //noinspection InfiniteLoopStatement
            while (true) {
                len = socket.getInputStream().read(buffer);
                byte[] data = Arrays.copyOf(buffer, len);

                handler.handle(data);

                if(handler.hasNextPackage()){
                    if(listener != null)
                        listener.onSerialRead(handler.getPackageAndNext());
                }
            }
        } catch (Exception e) {
            connected = false;
            if (listener != null)
                listener.onSerialIoError(e);
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            socket = null;
        }
    }

}
