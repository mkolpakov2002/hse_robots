package ru.hse.control_system_v2.data

import android.bluetooth.BluetoothAdapter
import android.net.InetAddresses
import android.util.Patterns
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.hse.control_system_v2.AppConstants.DATABASE_NAME
import java.io.Serializable

@Entity(tableName = DATABASE_NAME)
class DeviceItemType constructor(var deviceMAC: String? = "",
                     var devClass: String = "no_class",
                     var devType: String = "no_type",
                     _devIp: String = "",
                     @PrimaryKey(autoGenerate = true)
                     override var devId: Int = 0,
                     override var name: String? = "",
                     var videoUrlList: ArrayList<String> = ArrayList()
)
    : ItemType(devId, name), Serializable {

    //4141
    var devPort: Int = 0
    var devProtocol: String = "arduino_default"

    //"192.168.1.138"
    var devIp: String = _devIp
        set(value) {
            field = value.replace(':', '.').replace('/', '.')
        }

    val isBtSupported: Boolean
        get() = deviceMAC != null && BluetoothAdapter.checkBluetoothAddress(deviceMAC)

    val isWiFiSupported: Boolean
        get() = devIp.let {  InetAddresses.isNumericAddress(it) }

    init {

    }
}