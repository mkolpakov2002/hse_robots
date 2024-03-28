package ru.hse.control_system_v2.model.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.hse.control_system_v2.model.entities.DeviceOld

/**
 * логика таблицы БД с данными об устройствах
 */
@Dao
interface DeviceItemTypeDao {
    // Добавление DeviceOld в бд
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg item: DeviceOld)

    // Удаление Note из бд
    @Query("DELETE FROM deviceOld WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM deviceOld")
    suspend fun deleteAll()

    @Update
    suspend fun update(item: DeviceOld)

    //TODO
//    @Query("UPDATE deviceOld SET packages=:newProto WHERE packages = :deletingProto")
//    suspend fun deleteProto(deletingProto: String?, newProto: String?)

    // Получение всех Person из бд
    @Query("SELECT * FROM deviceOld")
    fun getAll(): Flow<List<DeviceOld>>
}