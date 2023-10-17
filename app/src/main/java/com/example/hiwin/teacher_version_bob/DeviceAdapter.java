package com.example.hiwin.teacher_version_bob;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/*
    reference:
        https://qiita.com/vc7/items/c863908b5273edd4fe53
        https://xnfood.com.tw/android-listview-baseadapter/
 */
public class DeviceAdapter extends BaseAdapter {
    private final List<BluetoothDevice> devices;
    private static LayoutInflater inflater = null;

    public DeviceAdapter(Context context, List<BluetoothDevice> devices) {
        this.devices = devices;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        viewHolder = new ViewHolder();

        convertView = inflater.inflate(R.layout.row_of_device, parent, false);
        // 把拿到的 textView 設定進 view holder
        viewHolder.dev_name = (TextView) convertView.findViewById(R.id.dev_row_name);
        viewHolder.dev_address = (TextView) convertView.findViewById(R.id.dev_row_address);
        viewHolder.dev_state = (TextView) convertView.findViewById(R.id.dev_row_state);

        viewHolder.dev_name.setText(devices.get(position).getName());
        viewHolder.dev_address.setText(devices.get(position).getAddress());
        viewHolder.dev_state.setText(parseBondState(devices.get(position).getBondState()));
        return convertView;
    }

    static class ViewHolder {
        TextView dev_name;
        TextView dev_address;
        TextView dev_state;
    }

    private String parseBondState(int value) {
        switch (value) {
            case 0xa:
                return "BOND_NONE";
            case 0xb:
                return "BOND_BONDING";
            case 0xc:
                return "BOND_BONDED";
            default:
                return null;
        }
    }
}
