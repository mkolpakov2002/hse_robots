package ru.hse.control_system_v2;

import java.util.ArrayList;
import java.util.List;

import ru.hse.control_system_v2.list_devices.DeviceItemType;

public class DeviceHandler {
    private static ArrayList<DeviceItemType> devicesList;

    public static synchronized ArrayList<DeviceItemType> getDevicesList(){
        return devicesList;
    }

    public static synchronized void setDevicesList(ArrayList<DeviceItemType> devicesList){
        DeviceHandler.devicesList = devicesList;
    }

}