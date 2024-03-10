package ru.hse.control_system_v2.ui.device_settings

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json.Default.decodeFromString
import ru.hse.control_system_v2.AppConstants
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.connection.encryption.*
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel
import ru.hse.control_system_v2.data.AppDatabase.Companion.getAppDataBase
import ru.hse.control_system_v2.data.classes.workspace.model.WorkSpace
import ru.hse.control_system_v2.databinding.FragmentDeviceMenuBinding
import ru.hse.control_system_v2.ui.MainActivity
import ru.hse.control_system_v2.ui.SpinnerArrayAdapter
import ru.hse.control_system_v2.ui.TextChangedListener
import ru.hse.control_system_v2.ui.connection_type.DeviceAdapter
import ru.hse.control_system_v2.ui.workspace.WorkSpaceCreationDialogFragment.Companion.newInstance
import java.util.*
import kotlin.collections.ArrayList

internal const val FRAGMENT_RESULT_WORK_SPACE_KEY = "FRAGMENT_RESULT_WORK_SPACE_KEY"
internal const val WORK_SPACE_KEY = "WORK_SPACE_KEY"

class DeviceMenuFragment : Fragment() {
    private lateinit var binding: FragmentDeviceMenuBinding

    private lateinit var inputDevice: DeviceModel
    private lateinit var currentDevice: DeviceModel
    private var ma: MainActivity? = null

    lateinit var deviceNameView: TextInputEditText
    lateinit var deviceMACView: TextInputEditText
    //lateinit var deviceVideoCommandView: TextInputEditText
    //TODO: дать возможность создания множественных объектов VideoModel для видеострима
    //private lateinit var deviceDevVideoCommandLayout: TextInputLayout
    lateinit var deviceClassView: MaterialAutoCompleteTextView
    lateinit var deviceTypeView: MaterialAutoCompleteTextView
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
    private lateinit var deviceTypeViewLayout: TextInputLayout
    private lateinit var encryptionProtocolLayout: TextInputLayout
    private var adapterType: SpinnerArrayAdapter<String>? = null
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
            inputDevice = it.getSerializable("device") as DeviceModel
            currentDevice = DeviceModel(inputDevice)
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

        requireActivity().supportFragmentManager.setFragmentResultListener(FRAGMENT_RESULT_WORK_SPACE_KEY, viewLifecycleOwner) { key, bundle ->
            val workspace = decodeFromString(WorkSpace.serializer(), bundle.getString(WORK_SPACE_KEY)!!)
            currentDevice.workSpace = workspace
            // TODO: currentDevice.workspace = workspace
        }

        showDeviceInformation(view)
    }

    private fun showDeviceInformation(view: View) {
        deviceImage = binding.iconImageViewMenu
        saveButton = binding.deviceSave
        saveButton.setOnClickListener{ saveDevice() }
        deviceTypeViewLayout = binding.deviceTypeLayout
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
//        deviceVideoCommandView = binding.deviceDevVideoCommandEdit
//        deviceVideoCommandView.setText(devVideoCommand)
//        deviceVideoCommandView.addTextChangedListener(object :
//            TextChangedListener<TextInputEditText?>(deviceVideoCommandView) {
//            override fun onTextChanged(target: TextInputEditText?, s: Editable?) {
//                devVideoCommand = s.toString().trim { it <= ' ' }
//                onRefresh()
//            }
//        })
//        deviceDevVideoCommandLayout = binding.deviceDevVideoCommandLayout
//        if (!isWiFiSupported) {
//            deviceDevVideoCommandLayout.isEnabled = false
//        }
//        deviceDevVideoCommandLayout.setEndIconOnClickListener({
        //TODO: замена ввода одного адреса ввода на сразу несколько, создание VideoModel
//        })
        deviceMACView = binding.deviceMacEdit
        deviceMACView.setText(currentDevice.bluetoothAddress)
        deviceMACView.addTextChangedListener(object : TextWatcher {
            //https://github.com/r-cohen/macaddress-edittext
            private fun setMacEdit(
                cleanMac: String,
                formattedMac: String,
                selectionStart: Int,
                lengthDiff: Int
            ) {
                deviceMACView.removeTextChangedListener(this)
                if (cleanMac.length <= 12) {
                    deviceMACView.setText(formattedMac)
                    deviceMACView.setSelection(selectionStart + lengthDiff)
                    mPreviousMac = formattedMac
                } else {
                    deviceMACView.setText(mPreviousMac)
                    deviceMACView.setSelection(mPreviousMac.length)
                }
                deviceMACView.addTextChangedListener(this)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (deviceMACView.text.toString() != deviceMACView.text.toString()) {
                    val upperText =
                        deviceMACView.text.toString().uppercase(Locale.getDefault())
                    deviceMACView.setText(upperText)
                    deviceMACView.setSelection(deviceMACView.length()) //fix reverse texting
                }
                val enteredMac = deviceMACView.text.toString().uppercase(Locale.getDefault())
                val cleanMac = clearNonMacCharacters(enteredMac)
                var formattedMac = formatMacAddress(cleanMac)
                val selectionStart = deviceMACView.selectionStart
                formattedMac = handleColonDeletion(enteredMac, formattedMac, selectionStart)
                val lengthDiff = formattedMac.length - enteredMac.length
                setMacEdit(cleanMac, formattedMac, selectionStart, lengthDiff)
                currentDevice.bluetoothAddress = s.toString().trim { it <= ' ' }
                onRefresh()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        deviceClassView = view.findViewById(R.id.device_class_edit)
        deviceClassView.setText(currentDevice.uiClass)
        val adapterClass = SpinnerArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_dropdown_item,
            AppConstants.DEVICE_UI_CLASS_LIST
        )
        deviceClassView.setAdapter(adapterClass)
        deviceClassView.addTextChangedListener(object :
            TextChangedListener<MaterialAutoCompleteTextView?>(deviceClassView) {
            override fun onTextChanged(target: MaterialAutoCompleteTextView?, s: Editable?) {
                if (currentDevice.uiClass != s.toString()) {
                    currentDevice.uiClass = s.toString()
                    onRefresh()
                }
            }
        })
        deviceTypeView = view.findViewById(R.id.device_type_edit)
        deviceTypeView.setText(currentDevice.uiType)
        adapterType = SpinnerArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_dropdown_item,
            AppConstants.DEVICE_UI_TYPE_LIST
        )
        deviceTypeView.setAdapter(adapterType)
        deviceTypeView.addTextChangedListener(object :
            TextChangedListener<MaterialAutoCompleteTextView?>(deviceTypeView) {
            override fun onTextChanged(target: MaterialAutoCompleteTextView?, s: Editable?) {
                if (deviceTypeViewLayout.isEnabled && currentDevice.uiType != s.toString()) {
                    currentDevice.uiType = s.toString()
                    onRefresh()
                }
            }
        })

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
                if (encryptionProtocolLayout.isEnabled && currentDevice.protocol_encryption != s.toString()) {
                    currentDevice.protocol_encryption = s.toString()
                    onRefresh()
                }
            }
        })

        deviceProtoView = binding.deviceProtoEdit
        deviceProtoView.setText(currentDevice.protocol)
        //TODO: после создания БД для протоколов, выводить их тут
//        SpinnerArrayAdapter<String> adapterProto = new SpinnerArrayAdapter<String>(
//                fragmentContext, android.R.layout.simple_spinner_dropdown_item,
//                AppDatabase.Companion.getProtocolNames());
//        deviceProtoView.setAdapter(adapterProto);
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
        binding.workspaceEdit.setOnClickListener {
            newInstance().show(childFragmentManager, null)
        }
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
                // Suspend the coroutine until the lifecycle is DESTROYED.
                // repeatOnLifecycle launches the block in a new coroutine every time the
                // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
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

    private fun selectEncryptionMethod(): Class<out Any> {
        return when (currentDevice.protocol_encryption) {
            "AES" -> AES::class.java
            "Blowfish" -> Blowfish::class.java
            "ChaCha20" -> ChaCha20::class.java
            "Salsa20" -> Salsa20::class.java
            else -> {
                ChipperGost34_12_2015::class.java
            }
        }
    }

    fun onRefresh() {
        if (deviceClassView.text.toString() == AppConstants.DEVICE_UI_CLASS_LIST[2]) {
            deviceTypeViewLayout.isEnabled = true
        } else {
            deviceTypeViewLayout.isEnabled = false
            deviceTypeView.setText(AppConstants.DEVICE_UI_TYPE_LIST[4])
        }
        setDeviceImage()
        // изменений в информации нет
        if ((inputDevice.name == currentDevice.name
            && inputDevice.bluetoothAddress == currentDevice.bluetoothAddress
            && inputDevice.protocol == currentDevice.protocol
            && inputDevice.uiClass == currentDevice.uiClass
            && inputDevice.uiType == currentDevice.uiType
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
        //TODO: см. VideoModel
        //deviceDevVideoCommandLayout.isEnabled = isWiFiSupported
    }

    private val isBtSupported: Boolean
        get() = BluetoothAdapter.checkBluetoothAddress(currentDevice.bluetoothAddress)
    private val isWiFiSupported: Boolean
        get() = currentDevice.wifiAddress.let { InetAddresses.isNumericAddress(it) }

    companion object {
        private fun formatMacAddress(cleanMac: String): String {
            var grouppedCharacters = 0
            var formattedMac = StringBuilder()
            for (element in cleanMac) {
                formattedMac.append(element)
                ++grouppedCharacters
                if (grouppedCharacters == 2) {
                    formattedMac.append(":")
                    grouppedCharacters = 0
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