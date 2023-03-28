package ru.hse.control_system_v2.ui.protocol

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.control_system_v2.R

class XmlTagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val tagNameTextView = itemView.findViewById<TextView>(R.id.tag_name_text_view)
    private val tagValueEditText = itemView.findViewById<EditText>(R.id.tag_value_edit_text)

    fun bind(xmlTag: XmlTag) {
        // Установить текст для имени тега
        tagNameTextView.text = xmlTag.name
        // Установить текст для значения тега
        tagValueEditText.setText(xmlTag.value)
        // Добавить слушатель изменения текста для редактирования значения тега
        tagValueEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ничего не делать
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Обновить значение тега в модели данных
                xmlTag.value = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // Ничего не делать
            }
        })
    }
}