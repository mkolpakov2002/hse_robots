package ru.hse.control_system_v2.ui.packages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.data.AppDatabase
import ru.hse.control_system_v2.data.classes.packages.LezhnyovPackageModel
import ru.hse.control_system_v2.databinding.FragmentLezhnyovProtocolMenuBinding
import kotlin.properties.Delegates

class LezhnyovPackageMenuFragment : Fragment(), XmlTagAdapter.OnTagValueChangeListener {

    private val dataBinding by lazy {
        FragmentLezhnyovProtocolMenuBinding.inflate(layoutInflater)
    }

    // Создаем список тегов для демонстрации
    private val xmlTags = ArrayList<XmlTag>()
    // Создаем адаптер для RecycleView с тегами
    private lateinit var xmlTagAdapter: XmlTagAdapter
    private var isNew by Delegates.notNull<Boolean>()
    private lateinit var saveButton: MaterialButton
    private lateinit var deleteButton: MaterialButton
    private lateinit var currentProtocol: LezhnyovPackageModel

    // Создаем вид RecycleView для фрагмента из макета xml_tag_fragment.xml
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return dataBinding.root
    }

    // Инициализируем RecycleView после создания фрагмента
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentProtocol = LezhnyovPackageModel(0, "", false, ArrayList())
        val b = arguments
        if (b != null) {
            isNew = b.getBoolean("isNew", true)
            currentProtocol = (arguments?.getSerializable("packages"))
                    as LezhnyovPackageModel
        }
        saveButton = dataBinding.protocolSave2
        saveButton.setOnClickListener {
            isNew = false
            CoroutineScope(Dispatchers.Main).launch {
                currentProtocol.name = dataBinding.textInputLayout2.toString()
                currentProtocol.tagList = xmlTagAdapter.getCurrentValues()
                val id = AppDatabase.getAppDataBase(requireContext()).lezhnyovProtocolDao()?.insertAll(currentProtocol)
                isNew = false
                if(id != null)
                    currentProtocol.id = id
            }
        }
        deleteButton = dataBinding.protocolDelete2
        deleteButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                AppDatabase.getAppDataBase(requireContext()).lezhnyovProtocolDao()?.delete(currentProtocol.id)
            }
            Navigation.findNavController(requireView()).navigate(R.id.settingsFragment)
        }

        // Находим RecycleView по идентификатору
        val recyclerView: RecyclerView = dataBinding.recyclerView
        xmlTagAdapter = XmlTagAdapter(ArrayList(currentProtocol.tagList))
        // Устанавливаем адаптер для RecycleView
        recyclerView.adapter = xmlTagAdapter
        // Устанавливаем линейный менеджер для RecycleView
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun onRefresh(){
        if(isNew){
            saveButton.isEnabled = true
            deleteButton.isEnabled = false
        } else {
            saveButton.isEnabled =
                (xmlTagAdapter.getCurrentValues().size == currentProtocol.tagList.size
                && xmlTagAdapter.getCurrentValues().mapIndexed
                { index, element -> element == currentProtocol.tagList[index] }.all { it })
            deleteButton.isEnabled = !saveButton.isEnabled
        }
    }

    override fun onTagValueChanged(newValue: String) {
        onRefresh()
    }
}