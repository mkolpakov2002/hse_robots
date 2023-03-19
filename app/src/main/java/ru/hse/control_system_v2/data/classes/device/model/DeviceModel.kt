package ru.hse.control_system_v2.data.classes.device.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import ru.hse.control_system_v2.AppConstants
import ru.hse.control_system_v2.AppConstants.Companion.DEFAULT_DEVICE_BLUETOOTH_ADDRESS
import ru.hse.control_system_v2.AppConstants.Companion.DEFAULT_DEVICE_CLASS
import ru.hse.control_system_v2.AppConstants.Companion.DEFAULT_DEVICE_MANUFACTURE
import ru.hse.control_system_v2.AppConstants.Companion.DEFAULT_DEVICE_MODEL
import ru.hse.control_system_v2.AppConstants.Companion.DEFAULT_DEVICE_NAME
import ru.hse.control_system_v2.AppConstants.Companion.DEFAULT_DEVICE_PORT
import ru.hse.control_system_v2.AppConstants.Companion.DEFAULT_DEVICE_PROTOCOL
import ru.hse.control_system_v2.AppConstants.Companion.DEFAULT_DEVICE_TYPE
import ru.hse.control_system_v2.AppConstants.Companion.DEFAULT_DEVICE_WIFI_ADDRESS
import ru.hse.control_system_v2.data.classes.workspace.model.WorkSpace
import java.io.Serializable

/**
 * Model класс сложного устройства для общения по протоколу
 */
@Entity(tableName = AppConstants.DATABASE_NAME)
open class DeviceModel
constructor(@PrimaryKey(autoGenerate = true)
            override var id: Int = 0,
            override var name: String = DEFAULT_DEVICE_NAME,
            var uiClass: String = DEFAULT_DEVICE_CLASS,
            var uiType: String = DEFAULT_DEVICE_TYPE,
            var protocol: String = DEFAULT_DEVICE_PROTOCOL,
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
            var videoList: ArrayList<VideoModel> = ArrayList()
)
    :
    ItemType,
    Serializable,
    BluetoothConnectable,
    WiFiConnectable,
    USBConnectable {

}