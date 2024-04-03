package ru.hse.control_system_v2.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import ru.hse.control_system_v2.utility.AppConstants.DATABASE_NAME
import ru.hse.control_system_v2.model.entities.DeviceOld
import ru.hse.control_system_v2.ui.packages.XmlTag

/**
 * Класс локальной базы данных
 */
@Database(
    entities = [DeviceOld::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceOldItemTypeDao(): DeviceItemTypeDao?

    companion object {
        private const val DATABASE_NAME = "device_database"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}

object Converters {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        return value?.split(",") ?: emptyList()
    }

    @TypeConverter
    fun toString(value: List<String>?): String {
        return value?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun fromMap(value: String?): Map<String, Any> {
        return value?.parseJson() ?: emptyMap()
    }

    private fun String.parseJson(): Map<String, Any> {
        return try {
            val jsonObject = Json.decodeFromString<JsonObject>(this)
            jsonObject.toMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    @TypeConverter
    fun toMap(value: Map<String, Any>?): String {
        return value?.toJson() ?: ""
    }

    @TypeConverter
    fun fromArray(strings: ArrayList<String>): String {
        var string = ""
        for (s in strings) string += "$s,"
        return string
    }

    @TypeConverter
    fun toArray(concatenatedStrings: String) : ArrayList<String> {
        val myStrings = ArrayList<String>()

        for(s in concatenatedStrings.split(","))
            myStrings.add(s)

        return myStrings
    }

    @TypeConverter
    fun fromXmlArray(strings: ArrayList<XmlTag>): String {
        var string = ""
        for (s in strings) string += "${s.name},${s.value};"
        return string
    }

    @TypeConverter
    fun toXmlArray(concatenatedStrings: String) : ArrayList<XmlTag> {
        val myStrings = ArrayList<XmlTag>()

        for(s in concatenatedStrings.split(";")){
            val arr = s.split(",").toTypedArray()
            myStrings.add(XmlTag(arr[0],arr[1]))
        }

        return myStrings
    }
}

private fun Map<String, Any>.toJson(): String {
    return Json.encodeToString(this)
}