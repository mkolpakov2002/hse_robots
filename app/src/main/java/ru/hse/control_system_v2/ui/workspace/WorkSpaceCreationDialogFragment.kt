package ru.hse.control_system_v2.ui.workspace

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.data.workspace.WorkSpaceSharedPreferences
import ru.hse.control_system_v2.data.workspace.model.WorkSpace
import ru.hse.control_system_v2.databinding.DialogWorkspaceCreationBinding
import ru.hse.control_system_v2.ui.device_settings.FRAGMENT_RESULT_WORK_SPACE_KEY
import ru.hse.control_system_v2.ui.device_settings.WORK_SPACE_KEY

class WorkSpaceCreationDialogFragment: BottomSheetDialogFragment(R.layout.dialog_workspace_creation) {

    private val binding by viewBinding<DialogWorkspaceCreationBinding>()

    companion object {
        @JvmStatic
        fun newInstance(): WorkSpaceCreationDialogFragment {
            return WorkSpaceCreationDialogFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnApply.setOnClickListener {
            val bundle = bundleOf(
                WORK_SPACE_KEY to WorkSpace(
                    isJoystickEnabled = binding.switchJoystick.isChecked,
                    isVideoStreamEnabled = binding.switchVideo.isChecked,
                    isPackageDataEnabled = binding.switchPackageData.isChecked,
                ).toString()
            )
            requireActivity().supportFragmentManager.setFragmentResult(FRAGMENT_RESULT_WORK_SPACE_KEY, bundle)
            Toast.makeText(requireContext(), "Рабочее пространство сохранено", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

}