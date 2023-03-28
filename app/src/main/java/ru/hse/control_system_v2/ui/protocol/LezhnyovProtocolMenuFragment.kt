package ru.hse.control_system_v2.ui.protocol

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.data.classes.protocol.ProtocolModel

class LezhnyovProtocolMenuFragment : Fragment() {

    // Создаем список тегов для демонстрации
    private val xmlTags = ArrayList<XmlTag>()
    // Создаем адаптер для RecycleView с тегами
    private lateinit var xmlTagAdapter: XmlTagAdapter

    // Создаем вид RecycleView для фрагмента из макета xml_tag_fragment.xml
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lezhnyov_protocol_menu, container, false)
    }

    // Инициализируем RecycleView после создания фрагмента
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val protocol =
            (arguments?.getSerializable("protocol") ?: ProtocolModel(0, "", ArrayList())) as ProtocolModel
        // Находим RecycleView по идентификатору
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        xmlTagAdapter = XmlTagAdapter(protocol.tagList)
        // Устанавливаем адаптер для RecycleView
        recyclerView.adapter = xmlTagAdapter
        // Устанавливаем линейный менеджер для RecycleView
        recyclerView.layoutManager = LinearLayoutManager(context)
    }
}