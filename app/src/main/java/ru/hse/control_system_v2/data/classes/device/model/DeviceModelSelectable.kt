package ru.hse.control_system_v2.data.classes.device.model

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import ru.hse.control_system_v2.AppConstants
import ru.hse.control_system_v2.R

/**
 * логика для выбора модели
 */
interface DeviceModelSelectable {
    var manufacture: String
    var model: String
    var vendorId: Int
    var uiClass: String
    var uiType: String

    private fun getDeviceImage() : Int {
        val imageType = if (uiClass == "class_arduino") {
            uiType
        } else {
            uiClass
        }
        if (uiClass == "class_arduino") {
            when (imageType) {
                "type_computer" -> return R.drawable.type_computer
                "type_sphere" -> {}
                "type_anthropomorphic" -> {}
                "type_cubbi" -> return R.drawable.type_cubbi
                "no_type" -> return R.drawable.type_no_type
            }
        } else {
            when (imageType) {
                "class_android" -> return R.drawable.class_android
                "no_class" -> return R.drawable.type_no_type
                "class_computer" -> return R.drawable.class_computer
            }
        }
        return R.drawable.type_no_type
    }
}