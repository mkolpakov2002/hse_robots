package ru.hse.control_system_v2.ui.fragments.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.utility.AppConstants.APP_LOG_TAG

class OneButtonAlertDialogFragment : DialogFragment() {
    private var mTitle = "title"
    private var mMessage = "message"
    var onDismissListener: OnDismissListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDismissListener) {
            onDismissListener = context
        } else {
            Log.d(APP_LOG_TAG, "no OneButtonAlertDialogFragment.OnDismissListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments ?: return
        mTitle = args.getString("dialogTitle", "title")
        mMessage = args.getString("dialogText", "message")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext()).setTitle(mTitle).setMessage(mMessage)
            .setPositiveButton(R.string.ok, null).create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDialogDismissed()
    }

    interface OnDismissListener {
        fun onDialogDismissed()
    }
}