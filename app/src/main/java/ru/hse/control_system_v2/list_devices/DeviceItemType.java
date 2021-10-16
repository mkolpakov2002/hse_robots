package ru.hse.control_system_v2.list_devices;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.net.Socket;

import ru.hse.control_system_v2.R;

public class DeviceItemType implements ItemType{
    private final String name;
    private final String deviceMAC;
    private final String devClass;
    private final String devType;
    private final String protocol;
    int id;
    Context c;
    BluetoothSocket bluetoothSocket;
    Boolean isConnected;
    Socket wifiSocket;

    public DeviceItemType(int id, String name, String deviceMAC, String protocol, String devClass, String devType, Context c) {
        this.name = name;
        this.deviceMAC = deviceMAC;
        this.id = id;
        this.protocol = protocol;
        this.devClass = devClass;
        this.devType = devType;
        this.c = c;

        //this.devImage = devImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceItemType item = (DeviceItemType) o;
        return name.equals(item.name) &&
                deviceMAC.equals(item.deviceMAC);
    }

    public Boolean isConnected(){
        return isConnected;
    }

    public BluetoothSocket getBtSocket() {
        return bluetoothSocket;
    }

    public void setBtSocket(BluetoothSocket socket) {
        if(socket != null){
            isConnected = true;
            bluetoothSocket = socket;
        } else
            isConnected = false;
    }

    public void closeConnection(){
        if (bluetoothSocket!=null){
            try {
                bluetoothSocket.close();
                bluetoothSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("BLUETOOTH", e.getMessage());
            }
            isConnected = false;
        }
    }

    public void openBtConnection(){
        if (bluetoothSocket!=null){
            try {
                bluetoothSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("BLUETOOTH", e.getMessage());
                closeConnection();
                isConnected = false;
            }
        }
    }

    public String getMAC() {
        return deviceMAC;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDevClass() { return devClass; }

    public String getDevType() { return devType; }

    public String getProtocol() { return protocol; }

    @Override
    public int getItemViewType() {
        return ItemType.DEVICE_ITEM_TYPE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        ViewHolderFactory.ListDevicesHolder mViewHolder = (ViewHolderFactory.ListDevicesHolder) viewHolder;
        mViewHolder.mName.setText(name);
        mViewHolder.checkMark.setVisibility(View.GONE);
        //TODO

        if(devType.equals("type_sphere")){
            //mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
        } else if (devType.equals("type_anthropomorphic")){
            //mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
        } else if (devType.equals("type_cubbi")){
            mViewHolder.deviceImage.setImageResource(R.drawable.type_cubbi);
        } else if (devType.equals("type_computer")){
            mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
        } else {
            mViewHolder.deviceImage.setImageResource(R.drawable.type_no_type);
        }
        mViewHolder.deviceImage.setVisibility(View.VISIBLE);

    }
}
