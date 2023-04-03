package ru.hse.control_system_v2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.hse.control_system_v2.data.classes.device.DeviceItemTypeDao
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel
import ru.hse.control_system_v2.data.classes.protocol.LezhnyovProtocolDao
import ru.hse.control_system_v2.data.classes.protocol.LezhnyovProtocolModel
import ru.hse.control_system_v2.data.classes.protocol.ProtocolDBHelper.DATABASE_NAME

/**
 * Класс локальной базы данных
 */
@Database(
    entities = [DeviceModel::class, LezhnyovProtocolModel::class /*, AnotherEntityType.class, AThirdEntityType.class */],
    version = 4
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceItemTypeDao(): DeviceItemTypeDao?

    abstract fun lezhnyovProtocolDao(): LezhnyovProtocolDao?

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