package ru.hse.control_system_v2.ui.connection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.hse.control_system_v2.R

private const val PROTOCOL_NAME = "param1"
private const val UI_MODE = "param2"

class CommandsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var protocolName: String? = null
    private var uiMode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            protocolName = it.getString(PROTOCOL_NAME)
            uiMode = it.getString(UI_MODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_commands, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param protocolName Parameter 1.
         * @param uiMode Parameter 2.
         * @return A new instance of fragment CommandsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(protocolName: String, uiMode: String) =
            CommandsFragment().apply {
                arguments = Bundle().apply {
                    putString(PROTOCOL_NAME, protocolName)
                    putString(UI_MODE, uiMode)
                }
            }
    }
}