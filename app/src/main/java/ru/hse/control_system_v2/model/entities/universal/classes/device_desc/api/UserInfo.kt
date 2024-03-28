package ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api

data class UserInfo(
    val status: String,
    val requestId: String,
    val rooms: List<Room>,
    val groups: List<Group>,
    val devices: List<Device>,
    val scenarios: List<Scenario>,
    val households: List<Household>
)