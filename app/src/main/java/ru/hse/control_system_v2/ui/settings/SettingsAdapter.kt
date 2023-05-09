package ru.hse.control_system_v2.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.control_system_v2.R

class SettingsAdapter(private val settingsList: List<SettingsItemModel>, private val clickListener: (SettingsItemModel) -> Unit) : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_settings, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(settingsList[position], clickListener)
    }

    override fun getItemCount(): Int = settingsList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemImage = itemView.findViewById<ImageView>(R.id.settings_item_image)
        private val itemName = itemView.findViewById<TextView>(R.id.settings_item_name)

        fun bind(settingsItemModel: SettingsItemModel, clickListener: (SettingsItemModel) -> Unit) {
            itemImage.setImageResource(settingsItemModel.imageId)
            itemName.text = settingsItemModel.itemName
            itemView.setOnClickListener {
                clickListener(settingsItemModel)
            }
        }
    }
}