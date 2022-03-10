package com.example.bluetoothtool.common;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bluetoothtool.R;

import java.util.List;

public class ScanListAdapter extends BaseAdapter {
    private List<BluetoothDevice> deviceList;
    private List<Short> rssiList;
    private Context mContext;
    private int curPosition=0;

    public ScanListAdapter(List<BluetoothDevice> list, List<Short> rssiList, Context mContext){
        this.deviceList=list;
        this.rssiList=rssiList;
        this.mContext=mContext;
    }

    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        convertView= LayoutInflater.from(mContext).inflate(R.layout.item_scan,viewGroup,false);
        TextView txtName=(TextView)convertView.findViewById(R.id.txtName);
        TextView txtMac=(TextView)convertView.findViewById(R.id.txtMac);
        TextView txtState=(TextView)convertView.findViewById(R.id.txtState);
        TextView txtRssi=(TextView)convertView.findViewById(R.id.txtRssi);

        BluetoothDevice device=deviceList.get(position);
        txtName.setText(device.getName());
        txtMac.setText(device.getAddress());
        txtRssi.setText(""+rssiList.get(position));
        if (device.getBondState()!=BluetoothDevice.BOND_BONDED) {
            txtState.setText("未匹配");
        }
        else if(device.getBondState()==BluetoothDevice.BOND_BONDED){
            txtState.setText("已匹配");
        }

        return convertView;
    }
}
