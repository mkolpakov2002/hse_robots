package ru.hse.control_system_v2.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.hse.control_system_v2.utility.AppConstants
import ru.hse.control_system_v2.utility.AppConstants.DEFAULT_DEVICE_BLUETOOTH_ADDRESS
import ru.hse.control_system_v2.utility.AppConstants.DEFAULT_DEVICE_MANUFACTURE
import ru.hse.control_system_v2.utility.AppConstants.DEFAULT_DEVICE_MODEL
import ru.hse.control_system_v2.utility.AppConstants.DEFAULT_DEVICE_NAME
import ru.hse.control_system_v2.utility.AppConstants.DEFAULT_DEVICE_PORT
import ru.hse.control_system_v2.utility.AppConstants.DEFAULT_DEVICE_PROTOCOL
import ru.hse.control_system_v2.utility.AppConstants.DEFAULT_DEVICE_WIFI_ADDRESS
import ru.hse.control_system_v2.utility.AppConstants.DEVICE_PROTOCOL_ENCRYPTION_LIST
import java.io.Serializable

/**
 * Model класс сложного устройства для общения по протоколу
 */
@Entity(tableName = AppConstants.DATABASE_NAME)
open class Device(@PrimaryKey(autoGenerate = true)
                       var id: Int = 0,
                  var name: String = DEFAULT_DEVICE_NAME,
                  var nameByUser: String = DEFAULT_DEVICE_NAME,
                  var protocol: String = DEFAULT_DEVICE_PROTOCOL,
                  var protocol_encryption: String = DEVICE_PROTOCOL_ENCRYPTION_LIST[5],
                  override var bluetoothAddress: String = DEFAULT_DEVICE_BLUETOOTH_ADDRESS,
                  var manufacture: String = DEFAULT_DEVICE_MANUFACTURE,
                  var model: String = DEFAULT_DEVICE_MODEL,
                  override var wifiAddress: String = DEFAULT_DEVICE_WIFI_ADDRESS,
                  override var port: Int = DEFAULT_DEVICE_PORT,
                  var vendorId: Int = 0,
)
    :
    Serializable,
    DeviceBluetoothConnectable,
    DeviceWiFiConnectable,
    DeviceSelectable {

    // Копирующий конструктор
    constructor(other: Device) : this(
        id = other.id,
        name = other.name,
        protocol = other.protocol,
        protocol_encryption = other.protocol_encryption,
        bluetoothAddress = other.bluetoothAddress,
        manufacture = other.manufacture,
        model = other.model,
        wifiAddress = other.wifiAddress,
        port = other.port,
        vendorId = other.vendorId
    )

}