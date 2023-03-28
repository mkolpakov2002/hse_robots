package ru.hse.control_system_v2.ui.protocol

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hse.control_system_v2.R

class LezhnyovProtocolMenuFragment : Fragment() {

    // Создаем список тегов для демонстрации
    private val xmlTags = listOf(
        XmlTag("title", "Hello world"),
        XmlTag("author", "John Doe"),
        XmlTag("date", "2023-03-27")
    )
    // Создаем адаптер для RecycleView с тегами
    private val xmlTagAdapter = XmlTagAdapter(xmlTags)

    // Создаем вид RecycleView для фрагмента из макета xml_tag_fragment.xml
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lezhnyov_protocol_menu, container, false)
    }

    // Инициализируем RecycleView после создания фрагмента
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Находим RecycleView по идентификатору
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        // Устанавливаем адаптер для RecycleView
        recyclerView.adapter = xmlTagAdapter
        // Устанавливаем линейный менеджер для RecycleView
        recyclerView.layoutManager = LinearLayoutManager(context)
    }
}