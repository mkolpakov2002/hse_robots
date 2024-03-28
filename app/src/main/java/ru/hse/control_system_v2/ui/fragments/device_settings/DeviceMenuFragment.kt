package ru.hse.control_system_v2.ui.fragments.device_settings

import android.bluetooth.BluetoothAdapter
import android.net.InetAddresses
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.model.entities.DeviceOld
import ru.hse.control_system_v2.databinding.FragmentDeviceMenuBinding
import java.util.*

class DeviceMenuFragment : Fragment() {
    private lateinit var binding: FragmentDeviceMenuBinding
    private val viewModel: DeviceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceMenuBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isNew = arguments?.getBoolean("isNew", true) ?: true
        val inputDeviceOld = arguments?.getSerializable("deviceOld") as? DeviceOld ?: DeviceOld()

        viewModel.setCurrentDevice(inputDeviceOld)
        setupViews(isNew)
        observeCurrentDevice()
    }

    private fun setupViews(isNew: Boolean) {
        binding.apply {
            deviceSave.setOnClickListener {
                viewModel.saveDevice()
                findNavController(binding.root)
                    .navigate(R.id.action_deviceMenuFragment_to_mainMenuFragment)
            }

            deviceConnect.setOnClickListener {
                val deviceIdList = arrayListOf(viewModel.currentDeviceOld.value?.id)
                val bundle = Bundle().apply { putIntegerArrayList("deviceIdList", deviceIdList) }
                findNavController(binding.root)
                    .navigate(R.id.action_deviceMenuFragment_to_connectionTypeFragment, bundle)
            }

            deviceDelete.setOnClickListener {
                viewModel.deleteDevice()
                findNavController(binding.root)
                    .navigate(R.id.action_deviceMenuFragment_to_mainMenuFragment)
            }

            if (isNew) {
                deviceConnect.visibility = View.GONE
                deviceDelete.visibility = View.GONE
            }
        }
    }

    private fun observeCurrentDevice() {
        viewModel.currentDeviceOld.observe(viewLifecycleOwner) { device ->
            binding.apply {
                deviceNameEdit.setText(device.name)
                deviceMacEdit.setText(device.bluetoothAddress)
                encryptionProtocolEdit.setText(device.protocolEncryption)
                deviceProtoEdit.setText(device.protocol)
                deviceIpEdit.setText(device.wifiAddress)
                devicePortEdit.setText(device.port.toString())
                iconImageViewMenu.setImageDrawable(ContextCompat.getDrawable(requireContext(), device.getDeviceImage()))

                val isBtSupported = BluetoothAdapter.checkBluetoothAddress(device.bluetoothAddress)
                val isWiFiSupported = InetAddresses.isNumericAddress(device.wifiAddress)

                deviceMenuBtIcon.visibility = if (isBtSupported) View.VISIBLE else View.INVISIBLE
                deviceMenuWifiIcon.visibility = if (isWiFiSupported) View.VISIBLE else View.INVISIBLE
            }
        }
    }
}