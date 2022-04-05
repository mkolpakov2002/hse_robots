package ru.hse.control_system_v2.list_devices;

import static ru.hse.control_system_v2.Constants.APP_LOG_TAG;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Parcelable;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import ru.hse.control_system_v2.R;

@Entity(tableName = "devices")
public class DeviceItemType implements ItemType {
    @PrimaryKey(autoGenerate = true)
    private int devId;
    private String devName;
    private String deviceMAC;
    private String devClass;
    private String devType;
    private String devIp;
    private int devPort;
    private String devProtocol;
    private String devVideoCommand;
    //True -- wifi, False - Bt, Null - no connection
    @Ignore
    private Boolean isConnected;
    @Ignore
    private Socket wifiSocket;
    @Ignore
    private BluetoothSocket bluetoothSocket;
    @Ignore
    private boolean isSelectedOnScreen = false;
    @Ignore
    private String imageType;

    public DeviceItemType(String devName, String deviceMAC, String devProtocol, String devClass, String devType, String devIp, int devPort) {
        this.devName = devName;
        this.deviceMAC = deviceMAC;
        this.devProtocol = devProtocol;
        this.devClass = devClass;
        this.devType = devType;
        //Ip и порт
        this.devIp = devIp.replace(':', '.').replace('/', '.');
        this.devPort = devPort;
        if (devClass.equals("class_arduino")) {
            imageType = devType;
        } else {
            imageType = devClass;
        }
    }

    public DeviceItemType(){
        devProtocol = "arduino_default";
        devClass = "no_class";
        devType = "no_type";
        devPort = 0;
        deviceMAC = devName = devIp = "";
        imageType = devClass;
    }

    public void setIsSelectedOnScreen(boolean isSelectedOnScreen) {
        this.isSelectedOnScreen = isSelectedOnScreen;
    }

    public boolean getIsSelectedOnScreen() {
        return isSelectedOnScreen;
    }

    @Override
    public String getImageType() {
        return imageType;
    }

    //"192.168.1.138"
    public void setDevIp(String devIp) {
        this.devIp = devIp.replace(':', '.').replace('/', '.');
    }

    //4141
    public void setDevPort(int devPort) {
        this.devPort = devPort;
    }

    public void setDevId(int devId) {
        this.devId = devId;
    }

    public void setDevVideoCommand(String devVideoCommand) {
        this.devVideoCommand = devVideoCommand;
    }

    public String getDevVideoCommand() {
        return devVideoCommand;
    }

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
                this.getDevPort() == (item.getDevPort()) &&
                this.getDevProtocol().equals(item.getDevProtocol()) &&
                this.getDevId() == (item.getDevId());
    }

    public Boolean isWiFiBtConnected() {
        return isConnected;
    }

    public BluetoothSocket getBtSocket() {
        return bluetoothSocket;
    }

    public Socket getWiFiSocket() {
        return wifiSocket;
    }

    public void setBtSocket(BluetoothSocket socket) {
        if (socket != null) {
            bluetoothSocket = socket;
            openBtConnection();
        } else
            isConnected = null;
    }

    public void setWifiSocket(Socket socket) {
        if (socket != null) {
            wifiSocket = socket;
            isConnected = true;
        } else isConnected = null;
    }

    public void closeConnection() {
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            try {
                bluetoothSocket.close();
                bluetoothSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("bluetoothSocket", e.getMessage());
            }
        }
        if (wifiSocket != null && wifiSocket.isConnected()) {
            try {
                wifiSocket.close();
                wifiSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("wifiSocket", e.getMessage());
            }
        }
        isConnected = null;
    }

    @SuppressLint("MissingPermission")
    public void openBtConnection() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.connect();
                isConnected = false;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(APP_LOG_TAG, "Попытка подключения для " + getDevName() + " неуспешна");
                Log.d(APP_LOG_TAG, e.getMessage());
                closeConnection();
                isConnected = null;
            }
        } else isConnected = null;
    }

    public void setDeviceMAC(String deviceMAC) {
        this.deviceMAC = deviceMAC;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public void setDevClass(String devClass) {
        this.devClass = devClass;
        if (devClass.equals("class_arduino")) {
            imageType = devType;
        } else {
            imageType = devClass;
        }
    }

    public void setDevType(String devType) {
        this.devType = devType;
        if (devClass.equals("class_arduino")) {
            imageType = devType;
        } else {
            imageType = devClass;
        }
    }

    public void setDevProtocol(String devProtocol) {
        this.devProtocol = devProtocol;
    }

    public String getDeviceMAC() {
        return deviceMAC;
    }

    public int getDevId() {
        return devId;
    }

    public String getDevName() {
        return devName;
    }

    public String getDevClass() {
        return devClass;
    }

    public String getDevType() {
        return devType;
    }

    public String getDevProtocol() {
        return devProtocol;
    }

    public String getDevIp() {
        return devIp;
    }

    public int getDevPort() {
        return devPort;
    }

    public boolean isBtSupported() {
        return deviceMAC != null && BluetoothAdapter.checkBluetoothAddress(deviceMAC);
    }

    public boolean isWiFiSupported() {
        return ((devIp != null) && Patterns.IP_ADDRESS.matcher(devIp).matches());
    }

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
        if (devClass.equals("class_arduino")) {
            switch (imageType) {
                case "type_computer":
                    mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
                    break;
                case "type_sphere":
                    //mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
                    break;
                case "type_anthropomorphic":
                    //mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
                    break;
                case "type_cubbi":
                    mViewHolder.deviceImage.setImageResource(R.drawable.type_cubbi);
                    break;
                case "no_type":
                    mViewHolder.deviceImage.setImageResource(R.drawable.type_no_type);
                    break;
            }
        } else {
            switch (imageType) {
                case "class_android":
                    mViewHolder.deviceImage.setImageResource(R.drawable.class_android);
                    break;
                case "no_class":
                    mViewHolder.deviceImage.setImageResource(R.drawable.type_no_type);
                    break;
                case "class_computer":
                    mViewHolder.deviceImage.setImageResource(R.drawable.class_computer);
                    break;
            }
        }
        mViewHolder.deviceImage.setVisibility(View.VISIBLE);

        if (isWiFiSupported()) {
            mViewHolder.wifiSupportIcon.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.wifiSupportIcon.setVisibility(View.GONE);
        }

        if (isBtSupported()) {
            mViewHolder.btSupportIcon.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.btSupportIcon.setVisibility(View.GONE);
        }

    }

    @Override
    public String getTextInfo() {
        return devName;
    }
}
