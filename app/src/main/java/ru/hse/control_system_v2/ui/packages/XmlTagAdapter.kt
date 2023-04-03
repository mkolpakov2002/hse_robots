package ru.hse.control_system_v2.ui.packages

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.data.classes.packages.LezhnyovPackagePreSupportedTags

class XmlTagAdapter(private val xmlTags: ArrayList<XmlTag>) : RecyclerView.Adapter<XmlTagAdapter.XmlTagViewHolder>() {

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
        // Установить текст для имени тега
        holder.tagNameTextView.text = xmlTag.name
        // Установить текст для значения тега
        holder.tagValueEditText.setText(xmlTag.value)
        holder.tagNameTextView.isEnabled = !LezhnyovPackagePreSupportedTags.preSupportedTagList.any { it.name == xmlTag.name }
        // Добавить слушатель изменения текста для редактирования значения тега
        holder.tagValueEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ничего не делать
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Обновить значение тега в модели данных
                xmlTag.value = s.toString()
                onTagValueChangeListener?.onTagValueChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // Ничего не делать
            }
        })
        // Добавить слушатель изменения текста для редактирования имени тега
        holder.tagNameTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ничего не делать
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Обновить значение тега в модели данных
                xmlTag.name = s.toString()
                onTagValueChangeListener?.onTagValueChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // Ничего не делать
            }
        })
    }

    override fun getItemCount(): Int {
        // Вернуть размер списка данных
        return xmlTags.size
    }

    public fun getCurrentValues() : ArrayList<XmlTag>{
        return xmlTags
    }

    var onTagValueChangeListener: OnTagValueChangeListener? = null

    // Интерфейс для обратного вызова
    interface OnTagValueChangeListener {
        fun onTagValueChanged(newValue: String)
    }

    // Класс ViewHolder для отображения элементов
    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class XmlTagViewHolder(itemView: View) : ViewHolder(itemView){

        val tagNameTextView: TextView = itemView.findViewById(R.id.tag_name_text_view)
        val tagValueEditText: EditText = itemView.findViewById(R.id.tag_value_edit_text)

    }

}