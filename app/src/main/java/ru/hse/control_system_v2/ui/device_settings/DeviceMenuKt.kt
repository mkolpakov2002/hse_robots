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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.serialization.json.Json.Default.decodeFromString
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel
import ru.hse.control_system_v2.data.AppDatabase.Companion.getAppDataBase
import ru.hse.control_system_v2.data.classes.workspace.model.WorkSpace
import ru.hse.control_system_v2.databinding.FragmentDeviceMenuBinding
import ru.hse.control_system_v2.ui.MainActivity
import ru.hse.control_system_v2.ui.SpinnerArrayAdapter
import ru.hse.control_system_v2.ui.TextChangedListener
import ru.hse.control_system_v2.ui.workspace.WorkSpaceCreationDialogFragment.Companion.newInstance
import java.util.*

internal const val FRAGMENT_RESULT_WORK_SPACE_KEY = "FRAGMENT_RESULT_WORK_SPACE_KEY"
internal const val WORK_SPACE_KEY = "WORK_SPACE_KEY"

class DeviceMenuFragment : Fragment() {
    private lateinit var binding: FragmentDeviceMenuBinding

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private lateinit var name: String
    private lateinit var bluetoothAddress: String
    private lateinit var protocol: String
    private lateinit var devClass: String
    private lateinit var devType: String
    private lateinit var devIp: String
    private lateinit var devPort: String
    private lateinit var imageType: String
    private lateinit var devVideoCommand: String
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
    lateinit var deviceIpView: TextInputEditText
    lateinit var devicePortView: TextInputEditText
    private lateinit var mPreviousMac: String
    private lateinit var connectButton: MaterialButton
    private lateinit var deleteButton: MaterialButton
    private lateinit var saveButton: MaterialButton
    private var fragmentContext: Context? = null
    private lateinit var deviceImage: ImageView
    private lateinit var deviceTypeViewLayout: TextInputLayout
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
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
        val b = arguments
        if (b != null) {
            isNew = b.getBoolean("isNew", true)
            currentDevice = b.getSerializable("device") as DeviceModel
        }
        deviceInformation
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
            // TODO: currentDevice.workspace = workspace
        }

        showDeviceInformation(view)
    }

    //        imageType = currentDevice.getImageType();
    private val deviceInformation: Unit
        get() {
            if(!this::currentDevice.isInitialized)
                currentDevice = DeviceModel()
            name = currentDevice.name
            bluetoothAddress = currentDevice.bluetoothAddress
            protocol = currentDevice.protocol
            devClass = currentDevice.uiClass
            devType = currentDevice.uiType
            devIp = currentDevice.wifiAddress
            devPort = currentDevice.port.toString()
            //        imageType = currentDevice.getImageType();
        }

    private fun showDeviceInformation(view: View) {
        deviceImage = binding.iconImageViewMenu
        saveButton = binding.deviceSave
        saveButton.setOnClickListener(View.OnClickListener { saveDevice() })
        deviceTypeViewLayout = binding.deviceTypeLayout
        deviceNameView = binding.deviceNameEdit
        deviceNameView.setText(name)
        deviceNameView.addTextChangedListener(object :
            TextChangedListener<TextInputEditText?>(deviceNameView) {
            override fun onTextChanged(target: TextInputEditText?, s: Editable?) {
                name = s.toString().trim { it <= ' ' }
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
        deviceMACView.setText(bluetoothAddress)
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
                bluetoothAddress = s.toString().trim { it <= ' ' }
                onRefresh()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        deviceClassView = view.findViewById(R.id.device_class_edit)
        deviceClassView.setText(devClass)
        val adapterClass = SpinnerArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_dropdown_item,
            listOf("class_android", "class_computer", "class_arduino", "no_class")
        )
        deviceClassView.setAdapter(adapterClass)
        deviceClassView.addTextChangedListener(object :
            TextChangedListener<MaterialAutoCompleteTextView?>(deviceClassView) {
            override fun onTextChanged(target: MaterialAutoCompleteTextView?, s: Editable?) {
                if (devClass != s.toString()) {
                    devClass = s.toString()
                    onRefresh()
                }
            }
        })
        deviceTypeView = view.findViewById(R.id.device_type_edit)
        deviceTypeView.setText(devType)
        adapterType = SpinnerArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_dropdown_item,
            listOf(
                "type_sphere",
                "type_anthropomorphic",
                "type_cubbi",
                "type_computer",
                "no_type"
            )
        )
        deviceTypeView.setAdapter(adapterType)
        deviceTypeView.addTextChangedListener(object :
            TextChangedListener<MaterialAutoCompleteTextView?>(deviceTypeView) {
            override fun onTextChanged(target: MaterialAutoCompleteTextView?, s: Editable?) {
                if (deviceTypeViewLayout.isEnabled && devType != s.toString()) {
                    devType = s.toString()
                    onRefresh()
                }
            }
        })
        deviceProtoView = binding.deviceProtoEdit
        deviceProtoView.setText(protocol)
        //TODO: после создания БД для протоколов, выводить их тут
//        SpinnerArrayAdapter<String> adapterProto = new SpinnerArrayAdapter<String>(
//                fragmentContext, android.R.layout.simple_spinner_dropdown_item,
//                AppDatabase.Companion.getProtocolNames());
//        deviceProtoView.setAdapter(adapterProto);
        deviceProtoView.addTextChangedListener(object :
            TextChangedListener<MaterialAutoCompleteTextView?>(deviceProtoView) {
            override fun onTextChanged(target: MaterialAutoCompleteTextView?, s: Editable?) {
                protocol = s.toString().trim { it <= ' ' }
                onRefresh()
            }
        })
        deviceIpView = binding.deviceIpEdit
        deviceIpView.setText(devIp)
        deviceIpView.addTextChangedListener(object :
            TextChangedListener<TextInputEditText?>(deviceIpView) {
            override fun onTextChanged(target: TextInputEditText?, s: Editable?) {
                devIp = s.toString().trim { it <= ' ' }
                onRefresh()
            }
        })
        devicePortView = binding.devicePortEdit
        devicePortView.setText(devPort)
        devicePortView.addTextChangedListener(object :
            TextChangedListener<TextInputEditText?>(devicePortView) {
            override fun onTextChanged(target: TextInputEditText?, s: Editable?) {
                devPort = s.toString().trim { it <= ' ' }
                if (devPort.isEmpty()) devPort = "0"
                onRefresh()
            }
        })
        connectButton = binding.deviceConnect
        connectButton.setOnClickListener(View.OnClickListener { ma!!.showBottomSheetToConnect() })
        if (isNew) connectButton.visibility = View.GONE
        deleteButton = binding.deviceDelete
        deleteButton.setOnClickListener(View.OnClickListener {
            val dbDevices = getAppDataBase(requireContext())
            //TODO: после обновления БД на корутины
            val devicesDao = dbDevices.deviceItemTypeDao()
            if (devicesDao != null) {
                //TODO: после обновления БД на корутины
                //devicesDao.delete(deviceId);
            }
            findNavController(view).navigate(R.id.action_deviceMenuFragment_to_mainMenuFragment)
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
        if (deviceNameView.text.toString().trim { it <= ' ' }.isEmpty()) {
            deviceNameView.error = getString(R.string.error_incorrect)
        } else if (deviceMACView.text.toString()
                .trim { it <= ' ' }.isNotEmpty() && !isBtSupported
        ) {
            deviceMACView.error = getString(R.string.error_incorrect)
        } else if (deviceIpView.text.toString()
                .trim { it <= ' ' }.isNotEmpty() && !isWiFiSupported
        ) {
            deviceIpView.error = getString(R.string.error_incorrect)
        } else {
            val newName = deviceNameView.text.toString()
            protocol = deviceProtoView.text.toString()
            val classDevice = deviceClassView.text.toString()
            val typeDevice: String = if (classDevice == "class_arduino") deviceTypeView.text.toString() else "no_type"
            bluetoothAddress = deviceMACView.text.toString().trim { it <= ' ' }
            devIp = deviceIpView.text.toString().trim { it <= ' ' }
            devPort = devicePortView.text.toString().trim { it <= ' ' }

            //TODO: после обновления БД на корутины
//            AppDatabase dbDevices = App.getDatabase();
//            DeviceItemTypeDao devicesDao = dbDevices.getDeviceItemTypeDao();
            currentDevice.name = newName
            currentDevice.bluetoothAddress = bluetoothAddress as String
            currentDevice.uiClass = classDevice
            currentDevice.uiType = typeDevice
            currentDevice.protocol = protocol
            currentDevice.wifiAddress = devIp
            //TODO: работа с VideoModel
            //currentDevice.setDevVideoCommand(devVideoCommand);
            try {
                val i = devPort.toInt()
                currentDevice.port = i
            } catch (e: NumberFormatException) {
                currentDevice.port = 0
            }
            currentDevice.port = devPort.toInt()
            //TODO: после обновления БД на корутины
//            devicesDao.insertAll(currentDevice);
            findNavController(requireView()).navigate(R.id.mainMenuFragment)
        }
    }

    private fun handleColonDeletion(
        enteredMac: String,
        formattedMac: String,
        selectionStart: Int
    ): String {
        var formattedMac = formattedMac
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
        imageType = if (devClass == "class_arduino") {
            devType
        } else {
            devClass
        }
        if (devClass == "class_arduino") {
            when (imageType) {
                "type_computer" -> deviceImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        fragmentContext!!, R.drawable.type_computer
                    )
                )
                "type_sphere" -> {}
                "type_anthropomorphic" -> {}
                "type_cubbi" -> deviceImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        fragmentContext!!, R.drawable.type_cubbi
                    )
                )
                "no_type" -> deviceImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        fragmentContext!!, R.drawable.type_no_type
                    )
                )
            }
        } else {
            when (imageType) {
                "class_android" -> deviceImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        fragmentContext!!, R.drawable.class_android
                    )
                )
                "no_class" -> deviceImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        fragmentContext!!, R.drawable.type_no_type
                    )
                )
                "class_computer" -> deviceImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        fragmentContext!!, R.drawable.class_computer
                    )
                )
            }
        }
    }

    fun onRefresh() {
        if (deviceClassView.text.toString() == "class_arduino") {
            deviceTypeViewLayout.isEnabled = true
            adapterType = SpinnerArrayAdapter(
                requireActivity(), android.R.layout.simple_spinner_dropdown_item,
                listOf(
                    "type_sphere",
                    "type_anthropomorphic",
                    "type_cubbi",
                    "type_computer",
                    "no_type"
                )
            )
            deviceTypeView.setAdapter(adapterType)
        } else {
            deviceTypeViewLayout.isEnabled = false
            deviceTypeView.setText("no_type")
        }
        setDeviceImage()
        // изменений в информации нет
        if ((name == currentDevice.name
            && bluetoothAddress == currentDevice.bluetoothAddress
            && protocol == currentDevice.protocol
            && devClass == currentDevice.uiClass
            && devType == currentDevice.uiType
            && devIp == currentDevice.wifiAddress
            && devPort == currentDevice.port.toString() //TODO
        //                &&
        //                imageType.equals(currentDevice.getImageType())
        ) && !isNew) {
            if (currentDevice.isBluetoothSupported ||
                currentDevice.isWiFiSupported
            ) {
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
        get() = BluetoothAdapter.checkBluetoothAddress(bluetoothAddress)
    private val isWiFiSupported: Boolean
        get() = devIp.let { InetAddresses.isNumericAddress(it) }

    companion object {

        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
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