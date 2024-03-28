package ru.hse.control_system_v2.model.entities.universal.classes.device_desc.room

import ru.hse.control_system_v2.model.entities.universal.classes.connection_desc.yandex.DeviceActionResult
import ru.hse.control_system_v2.model.entities.universal.classes.connection_desc.yandex.RemoteDeviceDataSource
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api.Capability
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api.Device

class DeviceRepository(
    private val remoteDataSource: RemoteDeviceDataSource
) {
    suspend fun getDevices(): List<Device> {
        val localDevices = emulateDevices()
        return if (localDevices.isNotEmpty()) {
            localDevices
        } else {
            val remoteDevices = remoteDataSource.getDevices()

            remoteDevices
        }
    }

    fun emulateDevices(): List<Device> {
        val devices = mutableListOf<Device>()

        // Лампочка с умением on_off
        val socketCapability = Capability(
            type = "devices.capabilities.on_off",
            state = mapOf("instance" to "on", "value" to true)
        )
        val socket = Device(
            id = "d7e57431-7953-49aa-b46e-589495b71986",
            name = "Лампа",
            aliases = listOf("Лампа в гостиной"),
            type = "devices.types.socket",
            state = "",
            groups = emptyList(),
            room = null,
            externalId = "external-socket-001",
            skillId = "skill-001",
            capabilities = listOf(socketCapability),
            properties = emptyList(),
            householdId = "household-001"
        )
        devices.add(socket)

        return devices
    }

    suspend fun handleDeviceAction(deviceId: String, capability: Capability): DeviceActionResult? {
        return remoteDataSource.handleDeviceAction(deviceId, capability)
    }

}