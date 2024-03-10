package ru.hse.control_system_v2.ui.fragments.device_settings

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.net.InetAddresses
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.hse.control_system_v2.utility.AppConstants
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.model.entities.Device
import ru.hse.control_system_v2.model.db.AppDatabase.Companion.getAppDataBase
import ru.hse.control_system_v2.databinding.FragmentDeviceMenuBinding
import ru.hse.control_system_v2.ui.MainActivity
import ru.hse.control_system_v2.utility.SpinnerArrayAdapter
import ru.hse.control_system_v2.utility.TextChangedListener
import java.util.*
import kotlin.collections.ArrayList

class DeviceMenuFragment : Fragment() {
    private lateinit var binding: FragmentDeviceMenuBinding

    private lateinit var inputDevice: Device
    private lateinit var currentDevice: Device
    private var ma: MainActivity? = null

    lateinit var deviceNameView: TextInputEditText
    lateinit var deviceMACView: TextInputEditText
    lateinit var deviceProtoView: MaterialAutoCompleteTextView
    lateinit var protocolEncryptionView: MaterialAutoCompleteTextView
    lateinit var deviceIpView: TextInputEditText
    lateinit var devicePortView: TextInputEditText
    private lateinit var mPreviousMac: String
    private lateinit var connectButton: MaterialButton
    private lateinit var deleteButton: MaterialButton
    private lateinit var saveButton: MaterialButton
    private var fragmentContext: Context? = null
    private lateinit var deviceImage: ImageView
    private lateinit var encryptionProtocolLayout: TextInputLayout
    private var isNew = true
    private lateinit var btIcon: ImageView
    private lateinit var wifiIcon: ImageView

    override fun onAttach(context: Context) {
        fragmentContext = context
        ma = fragmentContext as MainActivity?
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isNew = it.getBoolean("isNew", true)
            inputDevice = it.getSerializable("device") as Device
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDeviceMenuBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showDeviceInformation(view)
    }

    private fun showDeviceInformation(view: View) {
        if (!this::inputDevice.isInitialized)
            inputDevice = Device()
        currentDevice = Device(inputDevice)
        deviceImage = binding.iconImageViewMenu
        saveButton = binding.deviceSave
        saveButton.setOnClickListener{ saveDevice() }
        deviceNameView = binding.deviceNameEdit
        deviceNameView.setText(currentDevice.name)
        deviceNameView.addTextChangedListener(object :
            TextChangedListener<TextInputEditText?>(deviceNameView) {
            override fun onTextChanged(target: TextInputEditText?, s: Editable?) {
                currentDevice.name = s.toString().trim { it <= ' ' }
                if (s.toString().trim { it <= ' ' }.isEmpty()) {
                    target!!.error = getString(R.string.error_incorrect)
                }
                onRefresh()
            }
        })

        deviceMACView = binding.deviceMacEdit
        deviceMACView.setText(currentDevice.bluetoothAddress)
        //TODO
        //deviceMACView addTextChangedListener

        protocolEncryptionView = binding.encryptionProtocolEdit
        protocolEncryptionView.setText(currentDevice.protocol_encryption)
        val adapterEncryption = SpinnerArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_dropdown_item,
            AppConstants.DEVICE_PROTOCOL_ENCRYPTION_LIST
        )
        protocolEncryptionView.setAdapter(adapterEncryption)
        protocolEncryptionView.addTextChangedListener(object :
            TextChangedListener<MaterialAutoCompleteTextView?>(protocolEncryptionView) {
            override fun onTextChanged(target: MaterialAutoCompleteTextView?, s: Editable?) {
                if (binding.encryptionProtocolLayout.isEnabled && currentDevice.protocol_encryption != s.toString()) {
                    currentDevice.protocol_encryption = s.toString()
                    onRefresh()
                }
            }
        })

        deviceProtoView = binding.deviceProtoEdit
        deviceProtoView.setText(currentDevice.protocol)
        deviceProtoView.addTextChangedListener(object :
            TextChangedListener<MaterialAutoCompleteTextView?>(deviceProtoView) {
            override fun onTextChanged(target: MaterialAutoCompleteTextView?, s: Editable?) {
                currentDevice.protocol = s.toString().trim { it <= ' ' }
                onRefresh()
            }
        })
        deviceIpView = binding.deviceIpEdit
        deviceIpView.setText(currentDevice.wifiAddress)
        deviceIpView.addTextChangedListener(object :
            TextChangedListener<TextInputEditText?>(deviceIpView) {
            override fun onTextChanged(target: TextInputEditText?, s: Editable?) {
                currentDevice.wifiAddress = s.toString().trim { it <= ' ' }
                onRefresh()
            }
        })
        devicePortView = binding.devicePortEdit
        devicePortView.setText(currentDevice.port.toString())
        devicePortView.addTextChangedListener(object :
            TextChangedListener<TextInputEditText?>(devicePortView) {
            override fun onTextChanged(target: TextInputEditText?, s: Editable?) {
                try {
                    val i = s.toString().toInt()
                    currentDevice.port = i
                } catch (e: NumberFormatException) {
                    currentDevice.port = 0
                }
                onRefresh()
            }
        })
        connectButton = binding.deviceConnect
        connectButton.setOnClickListener{
            val list = ArrayList<Int>()
            list.add(currentDevice.id)
            val b = Bundle()
            b.putIntegerArrayList("deviceIdList", list)
            findNavController(binding.root).navigate(R.id.action_deviceMenuFragment_to_connectionTypeFragment, b)
        }
        if (isNew) connectButton.visibility = View.GONE
        deleteButton = binding.deviceDelete
        deleteButton.setOnClickListener(View.OnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                getAppDataBase(requireContext()).deviceItemTypeDao()?.delete(currentDevice.id)
                findNavController(view).navigate(R.id.action_deviceMenuFragment_to_mainMenuFragment)
            }
        })
        if (isNew) deleteButton.visibility = View.GONE
        btIcon = binding.deviceMenuBtIcon
        wifiIcon = binding.deviceMenuWifiIcon
        onRefresh()
    }

    private fun saveDevice() {
        var isDataAcceptable = true
        if (deviceNameView.text.toString().trim { it <= ' ' }.isEmpty()) {
            deviceNameView.error = getString(R.string.error_incorrect)
            isDataAcceptable = false
        }
        if (deviceMACView.text.toString()
                .trim { it <= ' ' }.isNotEmpty() && !isBtSupported) {
            deviceMACView.error = getString(R.string.error_incorrect)
            isDataAcceptable = false
        }
        if (deviceIpView.text.toString()
                .trim { it <= ' ' }.isNotEmpty() && !isWiFiSupported) {
            deviceIpView.error = getString(R.string.error_incorrect)
            isDataAcceptable = false
        }
        if(isDataAcceptable){
            CoroutineScope(Dispatchers.IO).launch {
                getAppDataBase(requireContext()).deviceItemTypeDao()?.insertAll(currentDevice)
            }
            findNavController(requireView()).navigate(R.id.mainMenuFragment)
        }
    }

    private fun handleColonDeletion(
        enteredMac: String,
        formattedMacParam: String,
        selectionStart: Int
    ): String {
        var formattedMac = formattedMacParam
        if (this::mPreviousMac.isInitialized && mPreviousMac.length > 1) {
            val previousColonCount = colonCount(mPreviousMac)
            val currentColonCount = colonCount(enteredMac)
            if (currentColonCount < previousColonCount) {
                formattedMac =
                    formattedMac.substring(0, selectionStart - 1) + formattedMac.substring(
                        selectionStart
                    )
                val cleanMac = clearNonMacCharacters(formattedMac)
                formattedMac = formatMacAddress(cleanMac)
            }
        }
        return formattedMac
    }

    private fun setDeviceImage() {
        deviceImage.setImageDrawable(
            ContextCompat.getDrawable(
                fragmentContext!!, currentDevice.getDeviceImage()
            )
        )
    }

    fun onRefresh() {
        setDeviceImage()
        if ((inputDevice.name == currentDevice.name
            && inputDevice.bluetoothAddress == currentDevice.bluetoothAddress
            && inputDevice.protocol == currentDevice.protocol
            && inputDevice.wifiAddress == currentDevice.wifiAddress
            && inputDevice.port == currentDevice.port) && !isNew) {
            if (currentDevice.isBluetoothSupported ||
                currentDevice.isWiFiSupported) {
                connectButton.isEnabled = true
            }
            saveButton.isEnabled = false
            deleteButton.isEnabled = true
        } else {
            // есть что сохранить
            saveButton.isEnabled = true
            connectButton.isEnabled = false
            deleteButton.isEnabled = false
        }
        if (isBtSupported) btIcon.visibility = View.VISIBLE else btIcon.visibility =
            View.INVISIBLE
        if (isWiFiSupported) wifiIcon.visibility = View.VISIBLE else wifiIcon.visibility =
            View.INVISIBLE
    }

    private val isBtSupported: Boolean
        get() = BluetoothAdapter.checkBluetoothAddress(currentDevice.bluetoothAddress)
    private val isWiFiSupported: Boolean
        get() = currentDevice.wifiAddress.let { InetAddresses.isNumericAddress(it) }

    companion object {
        private fun formatMacAddress(cleanMac: String): String {
            var groupedCharacters = 0
            var formattedMac = StringBuilder()
            for (element in cleanMac) {
                formattedMac.append(element)
                ++groupedCharacters
                if (groupedCharacters == 2) {
                    formattedMac.append(":")
                    groupedCharacters = 0
                }
            }
            if (cleanMac.length == 12) {
                formattedMac = StringBuilder(formattedMac.substring(0, formattedMac.length - 1))
            }
            return formattedMac.toString()
        }

        private fun clearNonMacCharacters(mac: String): String {
            return mac.replace("[^A-Fa-f/d]".toRegex(), "")
        }

        private fun colonCount(formattedMac: String): Int {
            return formattedMac.replace("[^:]".toRegex(), "").length
        }
    }
}