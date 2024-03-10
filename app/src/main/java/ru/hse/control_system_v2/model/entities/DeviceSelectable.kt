package ru.hse.control_system_v2.model.entities

import ru.hse.control_system_v2.R

/**
 * логика для выбора модели
 */
interface DeviceSelectable {
//    var uiClass: String
//    var uiType: String

    fun getDeviceImage() : Int {
//        val imageType = if (uiClass == "class_arduino") {
//            uiType
//        } else {
//            uiClass
//        }
//        if (uiClass == "class_arduino") {
//            when (imageType) {
//                "type_computer" -> return R.drawable.type_computer
//                "type_sphere" -> {}
//                "type_anthropomorphic" -> {}
//                "type_cubbi" -> return R.drawable.type_cubbi
//                "no_type" -> return R.drawable.type_no_type
//            }
//        } else {
//            when (imageType) {
//                "class_android" -> return R.drawable.class_android
//                "no_class" -> return R.drawable.type_no_type
//                "class_computer" -> return R.drawable.class_computer
//            }
//        }
        return R.drawable.type_computer
    }
}