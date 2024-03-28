package ru.hse.control_system_v2.model.entities.universal.classes.device_desc.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "capabilities")
data class CapabilityEntity(
    @PrimaryKey val id: Int = 0,
    val deviceId: String,
    val reportable: Boolean,
    val type: String,
    val retrievable: Boolean,
    val parameters: String,
    val state: String?,
    val lastUpdated: Float
)