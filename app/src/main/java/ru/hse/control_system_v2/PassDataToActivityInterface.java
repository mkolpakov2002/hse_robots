package ru.hse.control_system_v2;

import java.util.List;

import ru.hse.control_system_v2.data.DeviceItemType;

public interface PassDataToActivityInterface {
    void startConnectionService(List<DeviceItemType> selectedDevices);
}
