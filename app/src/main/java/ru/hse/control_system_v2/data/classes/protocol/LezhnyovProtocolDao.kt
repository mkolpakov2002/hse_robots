package ru.hse.control_system_v2.data.classes.protocol

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * логика таблицы БД с данными о вариациях протокола Лежнёва
 */
@Dao
interface LezhnyovProtocolDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(item: LezhnyovProtocolModel): Long

    @Query("DELETE FROM lezhnyovProtocol WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM lezhnyovProtocol")
    suspend fun deleteAll()

    @Update
    suspend fun update(item: LezhnyovProtocolModel)

    @Query("SELECT * FROM lezhnyovProtocol")
    fun getAll(): Flow<List<LezhnyovProtocolModel>>

    @Query("SELECT * FROM lezhnyovProtocol WHERE id = :id")
    fun getById(id: Long): LezhnyovProtocolModel
}