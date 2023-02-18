package ru.hse.control_system_v2.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.hse.control_system_v2.AppConstants.PROTO_DATABASE_NAME

@Entity(tableName = PROTO_DATABASE_NAME)
class ProtocolItemType(@PrimaryKey(autoGenerate = true) var id: Int, var name: String, var len: Int, var code: String) {

}