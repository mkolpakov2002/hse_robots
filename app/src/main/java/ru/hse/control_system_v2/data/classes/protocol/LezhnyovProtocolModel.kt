package ru.hse.control_system_v2.data.classes.protocol

import ru.hse.control_system_v2.ui.protocol.XmlTag

class LezhnyovProtocolModel(
    id: Int,
    name: String,
    isPackageData: Boolean,
    tagList: ArrayList<XmlTag>) : ProtocolModel(
    id,
    name,
    tagList
) {
}