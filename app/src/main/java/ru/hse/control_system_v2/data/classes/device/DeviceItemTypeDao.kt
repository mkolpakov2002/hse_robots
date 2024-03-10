package ru.hse.control_system_v2.data.classes.device

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel

/**
 * логика таблицы БД с данными об устройствах
 */
@Dao
interface DeviceItemTypeDao {
    // Добавление Device в бд
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg item: DeviceModel)

    // Удаление Note из бд
    @Query("DELETE FROM device WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM device")
    suspend fun deleteAll()

    @Update
    suspend fun update(item: DeviceModel)

    //TODO
//    @Query("UPDATE device SET packages=:newProto WHERE packages = :deletingProto")
//    suspend fun deleteProto(deletingProto: String?, newProto: String?)

    // Получение всех Person из бд
    @Query("SELECT * FROM device")
    fun getAll(): Flow<List<DeviceModel>>
}