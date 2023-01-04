package ru.hse.control_system_v2.data;

import java.io.Serializable;

public class NewBtDevice implements Serializable {

    private String deviceName;


    public NewBtDevice(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}