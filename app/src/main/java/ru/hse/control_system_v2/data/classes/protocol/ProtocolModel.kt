package ru.hse.control_system_v2.data.classes.protocol

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.hse.control_system_v2.AppConstants.Companion.PROTO_DATABASE_NAME

/**
 * Model класс протокола Лежнёва с изменяемыми параметрами передаваемого пакета
 */
@Entity(tableName = PROTO_DATABASE_NAME)
class ProtocolModel(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var name: String,
    var len: Int,
    var code: String) {

}