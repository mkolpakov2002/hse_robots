package ru.hse.control_system_v2.data.classes.device.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import ru.hse.control_system_v2.AppConstants
import ru.hse.control_system_v2.AppConstants.DEFAULT_DEVICE_BLUETOOTH_ADDRESS
import ru.hse.control_system_v2.AppConstants.DEFAULT_DEVICE_CLASS
import ru.hse.control_system_v2.AppConstants.DEFAULT_DEVICE_MANUFACTURE
import ru.hse.control_system_v2.AppConstants.DEFAULT_DEVICE_MODEL
import ru.hse.control_system_v2.AppConstants.DEFAULT_DEVICE_NAME
import ru.hse.control_system_v2.AppConstants.DEFAULT_DEVICE_PORT
import ru.hse.control_system_v2.AppConstants.DEFAULT_DEVICE_PROTOCOL
import ru.hse.control_system_v2.AppConstants.DEFAULT_DEVICE_TYPE
import ru.hse.control_system_v2.AppConstants.DEFAULT_DEVICE_WIFI_ADDRESS
import ru.hse.control_system_v2.AppConstants.DEVICE_PROTOCOL_ENCRYPTION_LIST
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.data.classes.workspace.model.WorkSpace
import java.io.Serializable

/**
 * Model класс сложного устройства для общения по протоколу
 */
@Entity(tableName = AppConstants.DATABASE_NAME)
open class DeviceModel(@PrimaryKey(autoGenerate = true)
                       override var id: Int = 0,
                       override var name: String = DEFAULT_DEVICE_NAME,
                       var protocol: String = DEFAULT_DEVICE_PROTOCOL,
                       var protocol_encryption: String = DEVICE_PROTOCOL_ENCRYPTION_LIST[5],
                       override var bluetoothAddress: String = DEFAULT_DEVICE_BLUETOOTH_ADDRESS,
                       override var manufacture: String = DEFAULT_DEVICE_MANUFACTURE,
                       override var model: String = DEFAULT_DEVICE_MODEL,
                       override var wifiAddress: String = DEFAULT_DEVICE_WIFI_ADDRESS,
                       override var port: Int = DEFAULT_DEVICE_PORT,
                       @Ignore
                       var workSpace: WorkSpace = WorkSpace(
                           isJoystickEnabled = false,
                           isPackageDataEnabled = false,
                           isVideoStreamEnabled = false),
                       @Ignore
                       var videoList: ArrayList<VideoModel> = ArrayList(),
                       override var vendorId: Int = 0,
                       override var uiClass: String = "class_arduino",
                       override var uiType: String = "type_computer"
)
    :
    ItemType,
    Serializable,
    BluetoothConnectable,
    WiFiConnectable,
    DeviceModelSelectable {

    val deviceDrawable: Int
        get() {
        if (uiClass == "class_arduino") {
            when (uiType) {
                "type_computer" -> return (R.drawable.type_computer)
                "type_sphere" -> {}
                "type_anthropomorphic" -> {}
                "type_cubbi" -> return (R.drawable.type_cubbi)
                "no_type" -> return (R.drawable.type_no_type)
            }
        } else {
            when (uiType) {
                "class_android" -> return (R.drawable.class_android)
                "no_class" -> return (R.drawable.type_no_type)
                "class_computer" -> return (R.drawable.class_computer)
            }
        }
            return R.drawable.class_computer
    }

    // Копирующий конструктор
    constructor(other: DeviceModel) : this(
        id = other.id,
        name = other.name,
        protocol = other.protocol,
        protocol_encryption = other.protocol_encryption,
        bluetoothAddress = other.bluetoothAddress,
        manufacture = other.manufacture,
        model = other.model,
        wifiAddress = other.wifiAddress,
        port = other.port,
        workSpace = other.workSpace.copy(),
        videoList = ArrayList(other.videoList),
        vendorId = other.vendorId,
        uiClass = other.uiClass,
        uiType = other.uiType
    )

}