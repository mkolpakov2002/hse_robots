package ru.hse.control_system_v2.ui.fragments.connection_settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.model.entities.ConnectionDeviceModel

class ConnectionTypeAdapter(
    val connectionDeviceModels: ArrayList<ConnectionDeviceModel>,
    val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<ConnectionTypeAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceName = itemView.findViewById<TextView>(R.id.device_name)
        val deviceUiType = itemView.findViewById<ImageView>(R.id.device_icon)
        val connectionType = itemView.findViewById<TextView>(R.id.connection_type)
        val connectionStatus = itemView.findViewById<ImageView>(R.id.connectionStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_connection_type, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val connectionDeviceModel = connectionDeviceModels[position]
        holder.deviceName.text = connectionDeviceModel.deviceItemType.name
        holder.deviceUiType.setImageResource(connectionDeviceModel.deviceItemType.getDeviceImage())
        holder.connectionType.text = connectionDeviceModel.connectionType.connectionProtocol

        holder.connectionStatus.visibility = View.GONE

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return connectionDeviceModels.size
    }
}