package ru.hse.control_system_v2;

import android.bluetooth.BluetoothSocket;

import java.util.ArrayList;

import ru.hse.control_system_v2.list_devices.DeviceItemType;

public class SocketHandler {
    private static ArrayList<BluetoothSocket> socketList;
    private static ArrayList<Boolean> resultOfConnection;
    private static ArrayList<DeviceItemType> devicesList;
    private static int numberOfConnections;

    public static synchronized ArrayList<BluetoothSocket> getSocketList(){
        return socketList;
    }

    public static synchronized void setSocketList(ArrayList<BluetoothSocket> socketList){
        SocketHandler.socketList = socketList;
    }

    public static synchronized int getNumberOfConnections(){
        return numberOfConnections;
    }

    public static synchronized void setNumberOfConnections(int numberOfConnections){
        SocketHandler.numberOfConnections = numberOfConnections;
    }

    public static synchronized ArrayList<DeviceItemType> getDevicesList(){
        return devicesList;
    }

    public static synchronized void setDevicesList(ArrayList<DeviceItemType> devicesList){
        SocketHandler.devicesList = devicesList;
    }
}