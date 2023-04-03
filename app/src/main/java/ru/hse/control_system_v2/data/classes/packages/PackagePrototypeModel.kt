package ru.hse.control_system_v2.data.classes.packages

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.hse.control_system_v2.AppConstants
import ru.hse.control_system_v2.ui.packages.XmlTag
import java.io.Serializable

/**
 * Model класс протокола неизвестного типа с считанными из файла тегами
 */
@Entity(tableName = AppConstants.PROTO_DATABASE_NAME)
open class PackagePrototypeModel (
    @PrimaryKey(autoGenerate = true)
    open var id: Long = 0,
    var name: String = "",
    open var tagList: ArrayList<XmlTag>) : Serializable {

}