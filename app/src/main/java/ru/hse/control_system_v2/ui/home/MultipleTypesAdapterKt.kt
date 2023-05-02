package ru.hse.control_system_v2.ui.home

import android.app.Activity
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
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel
import ru.hse.control_system_v2.ui.MainActivity

open class MultipleTypesAdapterKt(val context: Context, deviceItemTypes: ArrayList<DeviceModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    data class Item(val device: DeviceModel?, var isSelected: Boolean = false, var isButton: Boolean)

    // Флаг для режима множественного выбора
    var isMultiSelect = false

    // Интерфейс для обратного вызова при долгом нажатии
    interface OnItemLongClickListener {
        fun onItemLongClick()
    }

    // Ссылка на обработчик долгого нажатия
    var onItemLongClickListener: OnItemLongClickListener? = null

    // Интерфейс для обратного вызова при одиночном нажатии
    interface OnItemClickListener {
        fun onItemClick(item: Item)
    }

    // Ссылка на обработчик одиночного нажатия
    var onItemClickListener: OnItemClickListener? = null

    // Константы для типов элементов
    companion object {
        const val TYPE_BUTTON = 0
        const val TYPE_ITEM = 1
    }

    // Класс ViewHolder для отображения элементов
    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // Класс ViewHolder для отображения кнопки
    class ButtonViewHolder(itemView: View) : ViewHolder(itemView) {
        var buttonLayout: ConstraintLayout
        var buttonTextInfo: TextView

        init {
            buttonLayout = itemView.findViewById(R.id.button_add_layout)
            buttonTextInfo = itemView.findViewById(R.id.button_add_device_text)
        }
    }

    // Класс ViewHolder для отображения обычных элементов
    class ItemViewHolder(itemView: View) :
        ViewHolder(itemView) {
        var mName: TextView
        var deviceImage: ImageView
        var checkMark: ImageView
        var wifiSupportIcon: ImageView
        var btSupportIcon: ImageView
        var materialCardView: MaterialCardView

        init {
            mName = itemView.findViewById(R.id.item_name)
            deviceImage = itemView.findViewById(R.id.icon_image_view)
            checkMark = itemView.findViewById(R.id.check_mark)
            wifiSupportIcon = itemView.findViewById(R.id.wifi_icon)
            btSupportIcon = itemView.findViewById(R.id.bt_icon)
            materialCardView = itemView.findViewById(R.id.device_item_card_view)
        }
    }

    // Метод для определения типа элемента по позиции
    override fun getItemViewType(position: Int): Int {
        return if (items[position].isButton) {
            TYPE_BUTTON
        } else {
            TYPE_ITEM
        }
    }

    // Список для хранения элементов разных типов
    private var items = ArrayList<Item>()

    init {
        items.add(Item(
            device = null,
            isSelected = false,
            isButton = true))

        for(item in deviceItemTypes){
            items.add(Item(
                device = item,
                isSelected = false,
                isButton = false))
        }
    }

    // Метод для создания ViewHolder в зависимости от типа элемента
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when (viewType) {
            TYPE_BUTTON -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_main_list_button_add, parent, false)
            TYPE_ITEM -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_main_list_device, parent, false)
            else -> throw IllegalArgumentException("Invalid view type")
        }
        return when (viewType) {
            TYPE_BUTTON -> ButtonViewHolder(view)
            TYPE_ITEM -> ItemViewHolder(view)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    // Метод для привязки данных к ViewHolder в зависимости от типа элемента
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is ButtonViewHolder -> {
                holder.buttonTextInfo.text = context.getString(R.string.button_add_device)

                // Обработка нажатия на кнопку
                holder.buttonLayout.setOnClickListener {
                    // Добавляем новый элемент в список данных
                    onItemClickListener?.onItemClick(item)
                }
            }

            is ItemViewHolder -> {
                holder.mName.text = item.device?.name ?: "NPE"
                if(item.isSelected){
                    holder.materialCardView.strokeColor =
                        context.let { ContextCompat.getColor(it, R.color.color_accent) }
                    holder.checkMark.visibility = View.VISIBLE
                } else {
                    holder.checkMark.visibility = View.GONE
                    holder.materialCardView.strokeColor = Color.TRANSPARENT
                }

                if (item.device?.uiClass == "class_arduino") {
                    when (item.device.uiType) {
                        "type_computer" -> holder.deviceImage.setImageResource(R.drawable.type_computer)
                        "type_sphere" -> {}
                        "type_anthropomorphic" -> {}
                        "type_cubbi" -> holder.deviceImage.setImageResource(R.drawable.type_cubbi)
                        "no_type" -> holder.deviceImage.setImageResource(R.drawable.type_no_type)
                    }
                } else {
                    when (item.device?.uiType) {
                        "class_android" -> holder.deviceImage.setImageResource(R.drawable.class_android)
                        "no_class" -> holder.deviceImage.setImageResource(R.drawable.type_no_type)
                        "class_computer" -> holder.deviceImage.setImageResource(R.drawable.class_computer)
                    }
                }

                holder.deviceImage.visibility = View.VISIBLE
                if (item.device?.isWiFiSupported == true) {
                    holder.wifiSupportIcon.visibility = View.VISIBLE
                } else {
                    holder.wifiSupportIcon.visibility = View.GONE
                }

                if (item.device?.isBluetoothSupported == true) {
                    holder.btSupportIcon.visibility = View.VISIBLE
                } else {
                    holder.btSupportIcon.visibility = View.GONE
                }

                // Обработка нажатия на элемент
                holder.itemView.setOnClickListener {
                    if (isMultiSelect) {
                        // Если включен режим множественного выбора, то меняем состояние элемента и чекбокса
                        item.isSelected = !item.isSelected
                        if(item.isSelected){
                            holder.materialCardView.strokeColor =
                                context.let { ContextCompat.getColor(it, R.color.color_accent) }
                            (holder.checkMark.drawable as Animatable).start()
                        } else {
                            holder.materialCardView.strokeColor = Color.TRANSPARENT
                            holder.checkMark.visibility = View.GONE
                        }
                        // Добавляем или удаляем элемент из списка выбранных в зависимости от его состояния
                        if (items.none { s -> s.isSelected })
                            isMultiSelect = false
                    }
                    // вызываем обратный вызов для обработки одиночного нажатия
                    onItemClickListener?.onItemClick(item)
                }

                // Обработка долгого нажатия на элемент
                holder.itemView.setOnLongClickListener {
                    if (!item.isButton) {
                        // Если режим множественного выбора выключен, то включаем его и добавляем текущий элемент в список выбранных
                        isMultiSelect = true
                        item.isSelected = true
                        holder.checkMark.visibility = View.VISIBLE
                        holder.materialCardView.strokeColor =
                            context.let { ContextCompat.getColor(it, R.color.color_accent) }
                        (holder.checkMark.drawable as Animatable).start()
                        // Вызываем обратный вызов для активации ActionMode в активности или фрагменте
                        onItemLongClickListener?.onItemLongClick()
                    }
                    true
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun areDevicesConnectable(): Boolean {
        return true
    }
    // Метод для получения ArrayList выделенных элементов
    fun getSelectedItems(): ArrayList<DeviceModel> {
        val selectedArrayList: ArrayList<DeviceModel> = ArrayList()
        for(item in items){
            if (item.isSelected)
                item.device?.let { selectedArrayList.add(it) }
        }
        return ArrayList(selectedArrayList)
    }

    // Метод для обновления списка элементов
    fun updateItems(newItems: ArrayList<DeviceModel>) {
        items.clear()
        items.add(Item(
            device = null,
            isSelected = false,
            isButton = true))
        for (item in newItems){
            items.add(Item(
                device = item,
                isSelected = false,
                isButton = false))
        }
        notifyDataSetChanged()
    }
}