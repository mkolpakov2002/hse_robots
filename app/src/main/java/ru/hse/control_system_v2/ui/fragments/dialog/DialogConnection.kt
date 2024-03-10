package ru.hse.control_system_v2.ui.fragments.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.hse.control_system_v2.R

class DialogConnection : DialogFragment() {
    lateinit var c: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            c = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(c)
        builder.setView(R.layout.dialog_connection)
        return builder.create()
    }
}
