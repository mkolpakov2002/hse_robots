package ru.hse.control_system_v2.data

import androidx.room.TypeConverter
import ru.hse.control_system_v2.ui.protocol.XmlTag

/**
 * конвертеры для записи и получения данных из БД
 */
class Converters {

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