package ru.hse.control_system_v2.data

import androidx.room.TypeConverter

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

}