package ru.hse.control_system_v2;

import static ru.hse.control_system_v2.Constants.APP_LOG_TAG;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.hse.control_system_v2.list_devices.DeviceItemType;

public class DeviceHandler {
    private static List<DeviceItemType> devicesList;

    public static synchronized List<DeviceItemType> getDevicesList(){
        return DeviceHandler.devicesList;
    }

    public static synchronized void setDevicesList(DeviceItemType... devicesList){
        DeviceHandler.devicesList = Arrays.asList(devicesList);
    }

}