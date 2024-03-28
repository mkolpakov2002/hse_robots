package ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api

data class Group(
    val id: String,
    val name: String,
    val aliases: List<String>,
    val householdId: String,
    val type: String,
    val devices: List<String>,
    val capabilities: List<GroupCapability>
)