package ru.hse.control_system_v2.list_devices;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import ru.hse.control_system_v2.R;

public class DeviceItemType implements ItemType{
    private final String name;
    private final String deviceMAC;
    private final String devClass;
    private final String devType;
    //TODO
    //public static String host_address="192.168.1.138";
    private final String devIp;
    private final int devPort;
    private final String protocol;
    int id;
    Context c;
    BluetoothSocket bluetoothSocket;
    Boolean isConnected;
    Socket wifiSocket;
    SocketAddress addr;


    public DeviceItemType(int id, String name, String deviceMAC, String protocol, String devClass, String devType, Context c) {
        this.name = name;
        this.deviceMAC = deviceMAC;
        this.id = id;
        this.protocol = protocol;
        this.devClass = devClass;
        this.devType = devType;
        this.c = c;
        //подгружаем картинку в зависимости от типа робота (а не храним отдельно)
        //this.devImage = devImage;
        //TODO
        //Ip и порт
        //this.devIp = devIp;
        this.devIp = "192.168.1.138";
        this.devPort = 9002;
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

    public Socket getWiFiSocket() {
        return wifiSocket;
    }

    public void setBtSocket(BluetoothSocket socket) {
        if(socket != null){
            isConnected = true;
            bluetoothSocket = socket;
        } else
            isConnected = false;
    }

    public void setWifiSocket(Socket socket) {
        addr = new InetSocketAddress(devIp, devPort);
        if(socket != null){
            isConnected = true;
            wifiSocket = socket;
        } else
            isConnected = false;
    }

    public void closeConnection(){
        if (bluetoothSocket!=null && bluetoothSocket.isConnected()){
            try {
                bluetoothSocket.close();
                bluetoothSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("bluetoothSocket", e.getMessage());
            }
            isConnected = false;
        }
        if(wifiSocket!=null && wifiSocket.isConnected()){
            try {
                wifiSocket.close();
                wifiSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("wifiSocket", e.getMessage());
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

    public void openWifiConnection(){
        if (wifiSocket!=null){
            //TODO
            //проверить корректность ip и порта
            try {
                wifiSocket.connect(addr);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("WiFi", e.getMessage());
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

    public String getIp() { return devIp; }

    public int getPort() { return devPort; }

    @Override
    public int getItemViewType() {
        return ItemType.DEVICE_ITEM_TYPE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        ViewHolderFactory.ListDevicesHolder mViewHolder = (ViewHolderFactory.ListDevicesHolder) viewHolder;
        mViewHolder.mName.setText(name);
        mViewHolder.checkMark.setVisibility(View.GONE);
        mViewHolder.materialCardView.setStrokeColor(Color.TRANSPARENT);

        if(devClass.equals("class_android")){
            mViewHolder.deviceImage.setImageResource(R.drawable.class_android);
        } else if (devClass.equals("class_computer")||devClass.equals("type_computer")){
            mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
        } else if (devClass.equals("no_class")){
            mViewHolder.deviceImage.setImageResource(R.drawable.type_no_type);
        }

        if(devType.equals("type_sphere")){
            //mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
        } else if (devType.equals("type_anthropomorphic")){
            //mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
        } else if (devType.equals("type_cubbi")){
            mViewHolder.deviceImage.setImageResource(R.drawable.type_cubbi);
        } //else if (devType.equals("type_computer")){
            //mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
        //else {
            //mViewHolder.deviceImage.setImageResource(R.drawable.type_no_type);
        //}
        mViewHolder.deviceImage.setVisibility(View.VISIBLE);

    }
}
