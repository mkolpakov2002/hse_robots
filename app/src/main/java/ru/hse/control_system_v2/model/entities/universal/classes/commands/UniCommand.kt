package ru.hse.control_system_v2.model.entities.universal.classes.commands

data class UniCommand(
    val deviceId: String,
    val entityId: String,
    val commandType: String,
    val commandData: Any
)