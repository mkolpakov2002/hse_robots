package ru.hse.control_system_v2.ui.connection_type

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.connection.data.classes.ConnectionDeviceModel

class DeviceAdapter(
    val connectionDeviceModels: ArrayList<ConnectionDeviceModel>,
    val onItemClick: (Int) -> Unit // A lambda function that is passed from the fragment
) : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    // A class that holds the views for each item
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceName = itemView.findViewById<TextView>(R.id.device_name)
        val deviceUiType = itemView.findViewById<ImageView>(R.id.device_icon)
        val connectionType = itemView.findViewById<TextView>(R.id.connection_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the item layout from XML
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_type_select, parent, false)
        // Return a new view holder
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the connection device model at this position
        val connectionDeviceModel = connectionDeviceModels[position]

        // Set the device name and connection type text views
        holder.deviceName.text = connectionDeviceModel.deviceItemType.name
        holder.deviceUiType.setImageResource(connectionDeviceModel.deviceItemType.getDeviceImage())
        holder.connectionType.text = connectionDeviceModel.connectionType.connectionProtocol

        // Set the item click listener
        holder.itemView.setOnClickListener {
            // Invoke the lambda function that is passed from the fragment
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        // Return the size of the connection device models list
        return connectionDeviceModels.size
    }
}