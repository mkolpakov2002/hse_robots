package ru.hse.control_system_v2.ui.fragments.device_settings

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.model.entities.DeviceOld
import ru.hse.control_system_v2.ui.fragments.device_settings.NewBtDevicesAdapter.DevicesAdapterVh

class NewBtDevicesAdapter(
    private var devicePrototypeList: ArrayList<BluetoothDevice>,
    private val onDeviceClicked: OnDeviceClicked
) : RecyclerView.Adapter<DevicesAdapterVh?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesAdapterVh {
        val context = parent.context
        return DevicesAdapterVh(
            LayoutInflater.from(context).inflate(
                R.layout.item_bt_paired_device,
                parent,
                false
            )
        )
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: DevicesAdapterVh, position: Int) {
        val userModel = devicePrototypeList[position]

        holder.deviceNameTextView.text = userModel.name
        holder.deviceAddressTextView.text = userModel.address
    }

    override fun getItemCount(): Int {
        return devicePrototypeList.size
    }

    fun setDevicePrototypeList(devicePrototypeList: ArrayList<BluetoothDevice>) {
        this.devicePrototypeList = devicePrototypeList
        notifyDataSetChanged()
    }


    interface OnDeviceClicked {
        fun selectedDevice(deviceOldPrototype: DeviceOld?)
    }

    inner class DevicesAdapterVh @SuppressLint("MissingPermission") constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var deviceNameTextView: TextView = itemView.findViewById(R.id.deviceName)
        var deviceAddressTextView: TextView = itemView.findViewById(R.id.deviceAddress)
        var imIcon: ImageView = itemView.findViewById(R.id.imageView)

        init {
            itemView.setOnClickListener { view: View? ->
                val selected = DeviceOld()
                selected.name = devicePrototypeList[bindingAdapterPosition].name
                selected.bluetoothAddress = devicePrototypeList[bindingAdapterPosition].address
                onDeviceClicked.selectedDevice(selected)
            }
        }
    }
}
