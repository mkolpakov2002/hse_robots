package ru.hse.control_system_v2.data

import androidx.room.*
import ru.hse.control_system_v2.AppConstants.PROTO_DATABASE_NAME

@Dao
interface ProtocolDao {
    // Добавление Note в бд
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg item: DeviceItemType?)

    // Удаление Note из бд
    @Query("DELETE FROM {$PROTO_DATABASE_NAME} WHERE devId = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM {$PROTO_DATABASE_NAME}")
    suspend fun deleteAll()

    @Update
    suspend fun update(item: ProtocolItemType?)

    // Получение всех Person из бд
    @Query("SELECT * FROM addedProtocols")
    suspend fun getAll(): ArrayList<ProtocolItemType>
}