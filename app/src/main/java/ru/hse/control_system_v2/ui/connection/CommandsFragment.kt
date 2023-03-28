package ru.hse.control_system_v2.ui.connection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.button.MaterialButton
import ru.hse.control_system_v2.R

// Класс фрагмента для отображения произвольного количества кнопок
class CommandsFragment : Fragment() {

    // Ключ для передачи списка текстов кнопок в аргументах фрагмента
    companion object {
        const val BUTTON_TEXT_LIST_KEY = "buttonTextList"
    }

    // Поле для хранения списка текстов кнопок
    private var buttonTextList: List<String>? = null

    // Поле для хранения списка кнопок
    private var buttonList: MutableList<MaterialButton> = mutableListOf()

    // Фабричный метод для создания нового экземпляра фрагмента с заданным списком текстов кнопок
    fun newInstance(buttonTextList: List<String>): CommandsFragment {
        val fragment = CommandsFragment()
        val args = Bundle()
        args.putStringArrayList(BUTTON_TEXT_LIST_KEY, ArrayList(buttonTextList))
        fragment.arguments = args
        return fragment
    }

    // Метод для получения списка текстов кнопок из аргументов фрагмента
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buttonTextList = arguments?.getStringArrayList(BUTTON_TEXT_LIST_KEY)
    }

    // Метод для создания представления фрагмента и добавления кнопок в макет
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_commands, container, false)
        val buttonContainer = view.findViewById<LinearLayout>(R.id.button_container)
        buttonTextList?.forEach { buttonText ->
            val button = MaterialButton(requireContext())
            button.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            button.text = buttonText
            buttonContainer.addView(button)
            buttonList.add(button)
        }
        return view
    }
}