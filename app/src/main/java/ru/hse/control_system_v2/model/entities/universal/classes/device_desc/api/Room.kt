package ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api

data class Room(
    val id: String,
    val name: String,
    val householdId: String,
    val devices: List<String>
)