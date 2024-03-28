package ru.hse.control_system_v2.ui.fragments.home

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.model.entities.DeviceOld

open class MultipleTypesAdapterKt(
    private val context: Context,
    deviceOldItemTypes: List<DeviceOld>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_BUTTON = 0
        const val TYPE_ITEM = 1
    }

    data class Item(val deviceOld: DeviceOld?, var isSelected: Boolean = false, var isButton: Boolean)

    private var items = ArrayList<Item>()
    var isMultiSelect = false
    var onItemLongClickListener: OnItemLongClickListener? = null
    var onItemClickListener: OnItemClickListener? = null

    init {
        items.add(Item(deviceOld = null, isSelected = false, isButton = true))
        items.addAll(deviceOldItemTypes.map { Item(deviceOld = it, isSelected = false, isButton = false) })
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].isButton) TYPE_BUTTON else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_BUTTON -> ButtonViewHolder(inflater.inflate(R.layout.item_main_list_button_add, parent, false))
            TYPE_ITEM -> ItemViewHolder(inflater.inflate(R.layout.item_main_list_device, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is ButtonViewHolder -> bindButtonViewHolder(holder, item)
            is ItemViewHolder -> bindItemViewHolder(holder, item)
        }
    }

    private fun bindButtonViewHolder(holder: ButtonViewHolder, item: Item) {
        holder.buttonTextInfo.text = context.getString(R.string.button_add_device)
        holder.buttonLayout.setOnClickListener { onItemClickListener?.onItemClick(item) }
    }

    private fun bindItemViewHolder(holder: ItemViewHolder, item: Item) {
        holder.mName.text = item.deviceOld?.name ?: "NPE"
        updateItemSelection(holder, item)
        item.deviceOld?.let { holder.deviceImage.setImageResource(it.getDeviceImage()) }
        holder.deviceImage.visibility = View.VISIBLE
        holder.wifiSupportIcon.visibility = if (item.deviceOld?.isWiFiSupported == true) View.VISIBLE else View.GONE
        holder.btSupportIcon.visibility = if (item.deviceOld?.isBluetoothSupported == true) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener { handleItemClick(holder, item) }
        holder.itemView.setOnLongClickListener { handleItemLongClick(holder, item) }
    }

    private fun updateItemSelection(holder: ItemViewHolder, item: Item) {
        if (item.isSelected) {
            holder.materialCardView.strokeColor = ContextCompat.getColor(context, R.color.color_accent)
            holder.checkMark.visibility = View.VISIBLE
        } else {
            holder.checkMark.visibility = View.GONE
            holder.materialCardView.strokeColor = Color.TRANSPARENT
        }
    }

    private fun handleItemClick(holder: ItemViewHolder, item: Item) {
        if (isMultiSelect) {
            item.isSelected = !item.isSelected
            updateItemSelection(holder, item)
            if (item.isSelected) {
                (holder.checkMark.drawable as Animatable).start()
            }
            if (items.none { it.isSelected }) isMultiSelect = false
        }
        onItemClickListener?.onItemClick(item)
    }

    private fun handleItemLongClick(holder: ItemViewHolder, item: Item): Boolean {
        if (!item.isButton) {
            isMultiSelect = true
            item.isSelected = true
            holder.checkMark.visibility = View.VISIBLE
            holder.materialCardView.strokeColor = ContextCompat.getColor(context, R.color.color_accent)
            (holder.checkMark.drawable as Animatable).start()
            onItemLongClickListener?.onItemLongClick()
        }
        return true
    }

    override fun getItemCount(): Int = items.size

    fun areDevicesConnectable(): Boolean = true

    fun getSelectedItems(): ArrayList<DeviceOld> {
        return ArrayList(items.mapNotNull { if (it.isSelected) it.deviceOld else null })
    }

    fun updateItems(newItems: List<DeviceOld>) {
        items.clear()
        items.add(Item(deviceOld = null, isSelected = false, isButton = true))
        items.addAll(newItems.map { Item(deviceOld = it, isSelected = false, isButton = false) })
        notifyDataSetChanged()
    }

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ButtonViewHolder(itemView: View) : ViewHolder(itemView) {
        val buttonLayout: ConstraintLayout = itemView.findViewById(R.id.button_add_layout)
        val buttonTextInfo: TextView = itemView.findViewById(R.id.button_add_device_text)
    }

    class ItemViewHolder(itemView: View) : ViewHolder(itemView) {
        val mName: TextView = itemView.findViewById(R.id.item_name)
        val deviceImage: ImageView = itemView.findViewById(R.id.icon_image_view)
        val checkMark: ImageView = itemView.findViewById(R.id.check_mark)
        val wifiSupportIcon: ImageView = itemView.findViewById(R.id.wifi_icon)
        val btSupportIcon: ImageView = itemView.findViewById(R.id.bt_icon)
        val materialCardView: MaterialCardView = itemView.findViewById(R.id.device_item_card_view)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick()
    }

    interface OnItemClickListener {
        fun onItemClick(item: Item)
    }
}