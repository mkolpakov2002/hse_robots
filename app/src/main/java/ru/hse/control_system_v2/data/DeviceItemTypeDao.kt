package ru.hse.control_system_v2.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.hse.control_system_v2.AppConstants.DATABASE_NAME

@Dao
interface DeviceItemTypeDao {
    // Добавление Note в бд
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg item: DeviceItemType?)

    // Удаление Note из бд
    @Query("DELETE FROM devices WHERE devId = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM devices")
    suspend fun deleteAll()

    @Update
    suspend fun update(item: DeviceItemType?)

    @Query("UPDATE devices SET devProtocol=:newProto WHERE devProtocol = :deletingProto")
    suspend fun deleteProto(deletingProto: String?, newProto: String?)

    // Получение всех Person из бд
    @Query("SELECT * FROM devices")
    fun getAll(): Flow<List<DeviceItemType>>
}