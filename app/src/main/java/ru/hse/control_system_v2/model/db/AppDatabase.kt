package ru.hse.control_system_v2.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.hse.control_system_v2.utility.AppConstants.DATABASE_NAME
import ru.hse.control_system_v2.model.entities.Device

/**
 * Класс локальной базы данных
 */
@Database(
    entities = [Device::class, ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceItemTypeDao(): DeviceItemTypeDao?

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase {
            if (INSTANCE == null){
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        //.addCallback(DB_CALLBACK)
                        .build()
                }
            }
            return INSTANCE!!
        }
    }

}