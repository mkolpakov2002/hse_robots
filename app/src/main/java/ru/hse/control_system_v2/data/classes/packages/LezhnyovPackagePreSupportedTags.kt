package ru.hse.control_system_v2.data.classes.packages

import ru.hse.control_system_v2.ui.packages.XmlTag

object LezhnyovPackagePreSupportedTags {
    val classFromDevice : ArrayList<XmlTag> = arrayListOf(
        XmlTag(name = "class_android", value = "30"),
        XmlTag(name = "class_computer", value = "65"),
        XmlTag(name = "class_arduino", value = "7E"),
        XmlTag(name = "no_class", value = "11")
    )
    val typeFromDevice : ArrayList<XmlTag> = arrayListOf(
        XmlTag(name = "type_sphere", value = "10"),
        XmlTag(name = "type_anthropomorphic", value = "09"),
        XmlTag(name = "type_cubbi", value = "41"),
        XmlTag(name = "type_computer", value = "9D"),
        XmlTag(name = "no_type", value = "22")
        )
    val classToDevice : ArrayList<XmlTag> = arrayListOf(
        XmlTag(name = "class_android", value = "30"),
        XmlTag(name = "class_computer", value = "65"),
        XmlTag(name = "class_arduino", value = "7E"),
        XmlTag(name = "no_class", value = "11")
        )
    val typeToDevice : ArrayList<XmlTag> = arrayListOf(
        XmlTag(name = "type_sphere", value = "10"),
        XmlTag(name = "type_anthropomorphic", value = "09"),
        XmlTag(name = "type_cubbi", value = "41"),
        XmlTag(name = "type_computer", value = "9D"),
        XmlTag(name = "no_type", value = "22")
        )
    val turnOfCommand : ArrayList<XmlTag> = arrayListOf(
        XmlTag(name = "redo_command", value = "15"),
        XmlTag(name = "new_command", value = "0A")
        )
    val typeOfCommand : ArrayList<XmlTag> = arrayListOf(
        XmlTag(name = "type_move", value = "A1"),
        XmlTag(name = "type_tele", value = "B4")
        )
    val typeOfMove : ArrayList<XmlTag> = arrayListOf(
        XmlTag(name = "STOP", value = "7F"),
        XmlTag(name = "FORWARD", value = "01"),
        XmlTag(name = "FORWARD_STOP", value = "41"),
        XmlTag(name = "BACK", value = "02"),
        XmlTag(name = "BACK_STOP", value = "42"),
        XmlTag(name = "LEFT", value = "03"),
        XmlTag(name = "LEFT_STOP", value = "43"),
        XmlTag(name = "RIGHT", value = "0C"),
        XmlTag(name = "RIGHT_STOP", value = "4C")
        )

    var preSupportedTagList: ArrayList<XmlTag> = ArrayList(
            classFromDevice
                    + typeFromDevice
                    + classToDevice
                    + typeToDevice
                    + turnOfCommand
                    + typeOfCommand
                    + typeOfMove)
}