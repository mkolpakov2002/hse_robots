package ru.hse.control_system_v2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import ru.hse.control_system_v2.data.ProtocolDBHelper.DATABASE_NAME
import androidx.sqlite.db.SupportSQLiteOpenHelper

@Database(
    entities = [DeviceItemType::class /*, AnotherEntityType.class, AThirdEntityType.class */],
    version = 3
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceItemTypeDao(): DeviceItemTypeDao?

    companion object {
        private var INSTANCE: AppDatabase? = null
        //private var protocolDBHelper: ProtocolDBHelper? = null

//        private val DB_CALLBACK = object : RoomDatabase.Callback() {
//            override fun onCreate(db: SupportSQLiteDatabase) {
//                super.onCreate(db)
//                //
//            }
//        }

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

//        fun getProtoAppDataBase(context: Context): ProtocolDBHelper? {
//            if (protocolDBHelper == null){
//                synchronized(AppDatabase::class){
//                    protocolDBHelper = ProtocolDBHelper()
//                }
//            }
//            return protocolDBHelper
//        }
//
//        fun getProtocolNames(): ArrayList<String?>? {
//            return protocolDBHelper?.protocolNames
//        }
//
//        fun destroyDataBase(){
//            INSTANCE = null
//        }
//
//        fun destroyProtoDataBase(){
//            protocolDBHelper = null
//        }
    }

}