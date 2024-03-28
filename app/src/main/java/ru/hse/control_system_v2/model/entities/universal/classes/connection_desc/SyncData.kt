package ru.hse.control_system_v2.model.entities.universal.classes.connection_desc

data class SyncData(
    val deviceId: String,
    val entityId: String,
    val timestamp: Long,
    val dataVersion: Int,
    val previousSyncTimestamp: Long?
)