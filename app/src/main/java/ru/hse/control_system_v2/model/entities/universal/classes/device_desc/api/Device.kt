package ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api

data class Device(
    val id: String,
    val name: String,
    val aliases: List<String>,
    val type: String,
    val state: String,
    val groups: List<String>,
    val room: String?,
    val externalId: String,
    val skillId: String,
    val capabilities: List<Capability>,
    val properties: List<Property>,
    val deviceInfo: DeviceInfo? = null,
    val householdId: String
)