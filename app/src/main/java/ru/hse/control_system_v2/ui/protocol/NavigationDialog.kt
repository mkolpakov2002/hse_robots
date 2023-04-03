package ru.hse.control_system_v2.ui.protocol

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.data.classes.protocol.ProtocolPrototypeModel

/**
 * Диалог выбора типа только что считанного протокола
 */
class NavigationDialog : DialogFragment() {

    // Привязываем слушателя к активности при присоединении диалога
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    // Создаем вид диалога из макета navigation_dialog.xml
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Создаем билдер для диалога
            val builder = AlertDialog.Builder(it)
            // Получаем инфлейтер из контекста
            val inflater = requireActivity().layoutInflater
            // Инфлейтим макет диалога
            val view = inflater.inflate(R.layout.protocol_navigation_dialog, null)
            val protocol = arguments?.getSerializable("protocol") ?: ProtocolPrototypeModel(
                0,
                "",
                ArrayList()
            )
            val b = Bundle()
            b.putSerializable("protocol", protocol)
            // Находим радио группу по идентификатору
            val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
            // Устанавливаем заголовок диалога
            builder.setTitle("Выберите путь навигации")
                // Устанавливаем вид диалога
                .setView(view)
                // Устанавливаем положительную кнопку диалога
                .setPositiveButton("OK") { dialog, id ->
                    // Получаем выбранный радио баттон по идентификатору
                    val radioButton: RadioButton = view.findViewById(radioGroup.checkedRadioButtonId)
                    // Получаем текст выбранного радио баттона
                    // Создаем переменную для хранения фрагмента в зависимости от выбора
                    when (val text = radioButton.text.toString()) {
                        // Если выбран первый фрагмент, создаем его экземпляр
                        "Протокол Лежнёва" -> Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.action_navigationDialog_to_lezhnyovProtocolMenuFragment, b)
                        // Если выбран второй фрагмент, создаем его экземпляр
                        "ROS конфигурация" -> Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.action_navigationDialog_to_settingsFragment)
                        // В противном случае, бросаем исключение
                        else -> throw IllegalArgumentException("Unknown fragment: $text")
                    }
                }
                // Устанавливаем отрицательную кнопку диалога
                .setNegativeButton("Отмена") { dialog, _ ->
                    // Закрываем диалог без действий
                    dialog.cancel()
                }
            // Создаем и возвращаем диалог
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}