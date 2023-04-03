package ru.hse.control_system_v2.data.classes.protocol

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.hse.control_system_v2.ui.protocol.XmlTag

/**
 * Model класс протокола типа Лежнёва с считанными из файла тегами
 */
@Entity(tableName = "lezhnyovProtocol")
class LezhnyovProtocolModel(
    @PrimaryKey(autoGenerate = true)
    override var id: Long,
    name: String,
    var isPackageData: Boolean = false,
    tagList: ArrayList<XmlTag>): ProtocolPrototypeModel(id, name, tagList) {

    override var tagList: ArrayList<XmlTag>
        get() = super.tagList
        set(value) {
            for (item in LezhnyovProtocolPreSupportedTags.preSupportedTagList) {
                val index = value.indexOfFirst{
                    it.name == item.name
                }
                if (index > -1){
                    item.value = value[index].value
                    value.removeIf { it.name == item.name }
                }
            }
            super.tagList = value
        }
}