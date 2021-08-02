package ru.hse.control_system_v2;

import java.util.List;

import ru.hse.control_system_v2.list_devices.DeviceItemType;

public class DeviceHandler {
    private static List<DeviceItemType> devicesList;

    public static synchronized List<DeviceItemType> getDevicesList(){
        return devicesList;
    }

    public static synchronized void setDevicesList(List<DeviceItemType> devicesList){
        DeviceHandler.devicesList = devicesList;
    }

}