package ru.hse.control_system_v2.data.classes.packages

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
interface LezhnyovPackageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(item: LezhnyovPackageModel): Long

    @Query("DELETE FROM lezhnyovProtocol WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM lezhnyovProtocol")
    suspend fun deleteAll()

    @Update
    suspend fun update(item: LezhnyovPackageModel)

    @Query("SELECT * FROM lezhnyovProtocol")
    fun getAll(): Flow<List<LezhnyovPackageModel>>

    @Query("SELECT * FROM lezhnyovProtocol WHERE id = :id")
    fun getById(id: Long): LezhnyovPackageModel
}