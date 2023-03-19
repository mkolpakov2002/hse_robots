package ru.hse.control_system_v2.ui;

import java.util.List;

import ru.hse.control_system_v2.data.classes.device.model.DeviceModel;

public interface PassDataToActivityInterface {
    void startConnectionService(List<DeviceModel> selectedDevices);

}
