package ru.hse.control_system_v2.model.entities.universal.classes.device_desc.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api.Capability
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api.DeviceInfo
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api.Property

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey  val id: String,
    val name: String,
    val aliases: List<String>,
    val type: String,
    val state: String,
    val groups: List<String>,
    val room: String?,
    val externalId: String,
    val skillId: String,
    val householdId: String
)