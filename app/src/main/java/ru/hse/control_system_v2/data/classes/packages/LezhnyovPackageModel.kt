package ru.hse.control_system_v2.data.classes.packages

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.hse.control_system_v2.ui.packages.XmlTag

/**
 * Model класс протокола типа Лежнёва с считанными из файла тегами
 */
@Entity(tableName = "lezhnyovProtocol")
class LezhnyovPackageModel(
    @PrimaryKey(autoGenerate = true)
    override var id: Long,
    name: String,
    var isPackageData: Boolean = false,
    tagList: ArrayList<XmlTag>): PackagePrototypeModel(id, name, tagList) {

    override var tagList: ArrayList<XmlTag>
        get() = super.tagList
        set(value) {
            for (item in LezhnyovPackagePreSupportedTags.preSupportedTagList) {
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