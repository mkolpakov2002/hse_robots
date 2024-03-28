package ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api

data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val hwVersion: String,
    val swVersion: String
)