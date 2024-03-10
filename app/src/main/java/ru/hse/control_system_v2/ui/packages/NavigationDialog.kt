package ru.hse.control_system_v2.ui.packages

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation
import ru.hse.control_system_v2.R

/**
 * Диалог выбора типа только что считанного протокола
 */
class NavigationDialog : DialogFragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.protocol_navigation_dialog, null)
            val b = Bundle()
            val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
            builder.setTitle("Выберите путь навигации")
                .setView(view)
                .setPositiveButton("OK") { dialog, id ->
                    val radioButton: RadioButton = view.findViewById(radioGroup.checkedRadioButtonId)
                    when (val text = radioButton.text.toString()) {
                        "Протокол Лежнёва" -> Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.settingsFragment, b)
                        "ROS конфигурация" -> Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.settingsFragment)
                        else -> Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.settingsFragment)
                    }
                }
                .setNegativeButton("Отмена") { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}