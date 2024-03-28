package ru.hse.control_system_v2.model.entities.universal.classes.connection_desc.yandex

import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api.Capability
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api.Device

interface DeviceDataSource {
    suspend fun getDevices(): List<Device>
    suspend fun getDeviceById(deviceId: String): Device?
    suspend fun handleDeviceAction(deviceId: String, capability: Capability): DeviceActionResult?
}
