package ru.hse.control_system_v2.list_devices;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import ru.hse.control_system_v2.R;

@Entity(tableName = "devices")
public class DeviceItemType implements ItemType{
    @PrimaryKey(autoGenerate = true)
    private int devId;
    private String devName;
    private String deviceMAC;
    private String devClass;
    private String devType;
    //TODO
    //public static String host_address="192.168.1.138";
    private String devIp;
    private int devPort;
    private String devProtocol;
    @Ignore
    private Boolean isConnected;
    @Ignore
    private Socket wifiSocket;
    @Ignore
    private BluetoothSocket bluetoothSocket;
    @Ignore
    private boolean isSelectedOnScreen = false;
    @Ignore
    private String imageType = "default";

    public DeviceItemType(String devName, String deviceMAC, String devProtocol, String devClass, String devType) {
        this.devName = devName;
        this.deviceMAC = deviceMAC;
        this.devProtocol = devProtocol;
        this.devClass = devClass;
        this.devType = devType;
        //Ip и порт
        //this.devIp = devIp;
        //this.devIp = "192.168.1.138";
        //this.devPort = 9002;

        switch (devClass) {
            case "class_android":
                imageType = "class_android";
                break;
            case "class_computer":

            case "type_computer":
                imageType = "class_computer";
                break;
            case "no_class":
                imageType = "no_class";
                break;
        }

        switch (devType) {
            case "type_sphere":
                //mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
                //imageType = "type_sphere";
                break;
            case "type_anthropomorphic":
                //mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
                //imageType = "type_anthropomorphic";
                break;
            case "type_cubbi":
                imageType = "type_cubbi";
                break;
        }
        //mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
        //imageType = "class_android";
        //else {
        //mViewHolder.deviceImage.setImageResource(R.drawable.type_no_type);
        //imageType = "class_android";
        //}
    }

    public void setIsSelectedOnScreen(boolean isSelectedOnScreen){
        this.isSelectedOnScreen = isSelectedOnScreen;
    }

    public boolean getIsSelectedOnScreen(){
        return isSelectedOnScreen;
    }

    @Override
    public String getImageType() {
        return imageType;
    }

    public void setDevIp(String devIp){
        this.devIp = "192.168.1.138";
    }

    public void setDevPort(int devPort){
        this.devPort = 4141;
    }

    public void setDevId(int devId){ this.devId = devId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DeviceItemType item = (DeviceItemType) o;
        return this.getDevName().equals(item.getDevName()) &&
                this.getDeviceMAC().equals(item.getDeviceMAC()) &&
                this.getDevType().equals(item.getDevType()) &&
                this.getDevClass().equals(item.getDevClass()) &&
                this.getDevIp().equals(item.getDevIp()) &&
                this.getDevPort()==(item.getDevPort()) &&
                this.getDevProtocol().equals(item.getDevProtocol());
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
            bluetoothSocket = socket;
            openBtConnection();
        } else
            isConnected = false;
    }

    public void setWifiSocket(Socket socket) {
        SocketAddress addr;
        addr = new InetSocketAddress(devIp, devPort);
        if(socket != null){
            wifiSocket = socket;
            openWifiConnection(addr);

        } else isConnected = false;
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
        }
        if(wifiSocket!=null && wifiSocket.isConnected()){
            try {
                wifiSocket.close();
                wifiSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("wifiSocket", e.getMessage());
            }
        }
        isConnected = false;
    }

    public void openBtConnection(){
        if (bluetoothSocket!=null){
            try {
                bluetoothSocket.connect();
                isConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("BLUETOOTH", e.getMessage());
                closeConnection();
                isConnected = false;
            }
        } else isConnected = false;
    }

    private void openWifiConnection(SocketAddress addr){
        if (wifiSocket!=null){
            //TODO
            //проверить корректность ip и порта
            try {
                wifiSocket.connect(addr);
                isConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("WiFi", e.getMessage());
                closeConnection();
                isConnected = false;
            }
        } else isConnected = false;
    }


    public void setDeviceMAC(String deviceMAC) {
        this.deviceMAC=deviceMAC;
    }

    public void setDevName(String devName) {
        this.devName=devName;
    }

    public void setDevClass(String devClass) { this.devClass=devClass; }

    public void setDevType(String devType) {this.devType=devType; }

    public void setDevProtocol(String devProtocol) { this.devProtocol=devProtocol; }

    public String getDeviceMAC() {
        return deviceMAC;
    }

    public int getDevId() {
        return devId;
    }

    public String getDevName() {
        return devName;
    }

    public String getDevClass() { return devClass; }

    public String getDevType() { return devType; }

    public String getDevProtocol() { return devProtocol; }

    public String getDevIp() { return devIp; }

    public int getDevPort() { return devPort; }

    @Override
    public int getItemViewType() {
        return ItemType.DEVICE_ITEM_TYPE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        ViewHolderFactory.ListDevicesHolder mViewHolder = (ViewHolderFactory.ListDevicesHolder) viewHolder;
        mViewHolder.mName.setText(devName);
        mViewHolder.checkMark.setVisibility(View.GONE);
        mViewHolder.materialCardView.setStrokeColor(Color.TRANSPARENT);
        switch (devClass) {
            case "class_android":
                mViewHolder.deviceImage.setImageResource(R.drawable.class_android);
                break;
            case "class_computer":
            case "type_computer":
                mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
                break;
            case "no_class":
                mViewHolder.deviceImage.setImageResource(R.drawable.type_no_type);
                break;
        }

        switch (devType) {
            case "type_sphere":
                //mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
                //imageType = "type_sphere";
                break;
            case "type_anthropomorphic":
                //mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
                //imageType = "type_anthropomorphic";
                break;
            case "type_cubbi":
                mViewHolder.deviceImage.setImageResource(R.drawable.type_cubbi);
                break;
        }
        //mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
        //imageType = "class_android";
        //else {
        //mViewHolder.deviceImage.setImageResource(R.drawable.type_no_type);
        //imageType = "class_android";
        //}

        mViewHolder.deviceImage.setVisibility(View.VISIBLE);

    }

    @Override
    public String getTextInfo() {
        return devName;
    }
}
