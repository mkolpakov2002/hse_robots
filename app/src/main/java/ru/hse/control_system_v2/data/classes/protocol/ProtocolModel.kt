package ru.hse.control_system_v2.data.classes.protocol

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.hse.control_system_v2.AppConstants.Companion.PROTO_DATABASE_NAME
import ru.hse.control_system_v2.ui.protocol.XmlTag
import java.io.Serializable

/**
 * Model класс протокола неизвестного типа с считанными из файла тегами
 */
@Entity(tableName = PROTO_DATABASE_NAME)
open class ProtocolModel (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var tagList: ArrayList<XmlTag>) : Serializable {

}