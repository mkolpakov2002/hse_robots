package ru.hse.control_system_v2.model.entities.universal.classes.device_desc.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices WHERE id = :deviceId")
    suspend fun getDeviceById(deviceId: String): DeviceEntity?

    @Query("SELECT * FROM capabilities WHERE deviceId = :deviceId")
    suspend fun getCapabilitiesByDeviceId(deviceId: String): List<CapabilityEntity>

    @Query("SELECT * FROM properties WHERE deviceId = :deviceId")
    suspend fun getPropertiesByDeviceId(deviceId: String): List<PropertyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapabilities(capabilities: List<CapabilityEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperties(properties: List<PropertyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<DeviceEntity>)

    @Delete
    suspend fun deleteDevice(device: DeviceEntity)

    @Delete
    suspend fun deleteCapabilities(capabilities: List<CapabilityEntity>)

    @Delete
    suspend fun deleteProperties(properties: List<PropertyEntity>)

    @Query("SELECT * FROM devices")
    suspend fun getAllDevices(): List<DeviceEntity>
}