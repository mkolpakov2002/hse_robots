package ru.hse.control_system_v2.data.classes.file

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {
    @Query("SELECT * FROM file")
    fun getAll(): Flow<List<File>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg files: File)

    @Delete
    suspend fun delete(file: File)
}