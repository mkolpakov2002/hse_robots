package ru.hse.control_system_v2.ui.protocol

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.hse.control_system_v2.R

class XmlTagAdapter(private val xmlTags: List<XmlTag>) : RecyclerView.Adapter<XmlTagViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): XmlTagViewHolder {
        // Загрузить разметку для элемента списка из ресурсов
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_xml_tag, parent, false)
        // Создать и вернуть объект ViewHolder с загруженным видом
        return XmlTagViewHolder(view)
    }

    override fun onBindViewHolder(holder: XmlTagViewHolder, position: Int) {
        // Получить элемент данных по позиции в списке
        val xmlTag = xmlTags[position]
        // Связать данные с видом с помощью метода bind в ViewHolder
        holder.bind(xmlTag)
    }

    override fun getItemCount(): Int {
        // Вернуть размер списка данных
        return xmlTags.size
    }
}