package ru.hse.control_system_v2.model.entities.universal.classes.device_desc.room

data class DeviceInfoEntity(
    val deviceId: String,
    val manufacturer: String,
    val model: String,
    val hwVersion: String,
    val swVersion: String
)