package ru.hse.control_system_v2.ui.device_settings

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.Navigation.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.data.AppDatabase.Companion.getAppDataBase
import ru.hse.control_system_v2.data.DeviceItemType
import ru.hse.control_system_v2.data.workspace.model.WorkSpace
import ru.hse.control_system_v2.ui.MainActivity
import ru.hse.control_system_v2.ui.SpinnerArrayAdapter
import ru.hse.control_system_v2.ui.TextChangedListener
import ru.hse.control_system_v2.ui.workspace.WorkSpaceCreationDialogFragment.Companion.newInstance
import java.util.*

internal const val FRAGMENT_RESULT_WORK_SPACE_KEY = "FRAGMENT_RESULT_WORK_SPACE_KEY"
internal const val WORK_SPACE_KEY = "WORK_SPACE_KEY"

class DeviceMenuFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var name: String? = null
    private var MAC: String? = null
    private var protocol: String? = null
    private var devClass: String? = null
    private var devType: String? = null
    private var devIp: String? = null
    private var devPort: String? = null
    private var imageType: String? = null
    private var devVideoCommand: String? = null
    private var deviceId = 0
    private var currentDevice: DeviceItemType? = null
    private var ma: MainActivity? = null
    private val alertDialog: AlertDialog? = null
    var deviceNameView: TextInputEditText? = null
    var deviceMACView: TextInputEditText? = null
    var deviceVideoCommandView: TextInputEditText? = null
    var deviceDevVideoCommandLayout: TextInputLayout? = null
    var deviceClassView: MaterialAutoCompleteTextView? = null
    var deviceTypeView: MaterialAutoCompleteTextView? = null
    var deviceProtoView: MaterialAutoCompleteTextView? = null
    var deviceIpView: TextInputEditText? = null
    var devicePortView: TextInputEditText? = null
    private var mPreviousMac: String? = null
    private val listClasses: List<String>? = null
    private val listTypes: List<String>? = null
    private val data: ArrayList<String>? = null
    var connectButton: MaterialButton? = null
    var deleteButton: MaterialButton? = null
    var saveButton: MaterialButton? = null
    var fragmentContext: Context? = null
    var deviceImage: ImageView? = null
    var deviceTypeViewLayout: TextInputLayout? = null
    var adapterType: SpinnerArrayAdapter<String>? = null
    var isNew = true
    var btIcon: ImageView? = null
    var wifiIcon: ImageView? = null
    override fun onAttach(context: Context) {
        fragmentContext = context
        ma = fragmentContext as MainActivity?
        currentDevice = DeviceItemType()
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
            isNew = b.getBoolean("isNew")
            currentDevice = b.getSerializable("device") as DeviceItemType?
        }
        deviceInformation
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device_menu, container, false)
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
    val deviceInformation: Unit
        get() {
            deviceId = currentDevice!!.devId
            name = currentDevice!!.name
            MAC = currentDevice!!.deviceMAC
            protocol = currentDevice!!.devProtocol
            devClass = currentDevice!!.devClass
            devType = currentDevice!!.devType
            devIp = currentDevice!!.devIp
            devPort = currentDevice!!.devPort.toString()
            //        imageType = currentDevice.getImageType();
        }

    fun showDeviceInformation(view: View) {
        deviceImage = view.findViewById(R.id.icon_image_view_menu)
        saveButton = view.findViewById(R.id.device_save)
        saveButton?.setOnClickListener(View.OnClickListener { saveDevice() })
        deviceTypeViewLayout = view.findViewById(R.id.device_type_layout)
        deviceNameView = view.findViewById(R.id.device_name_edit)
        deviceNameView?.setText(name)
        deviceNameView?.addTextChangedListener(object :
            TextChangedListener<TextInputEditText?>(deviceNameView) {
            override fun onTextChanged(target: TextInputEditText?, s: Editable?) {
                name = s.toString().trim { it <= ' ' }
                if (s.toString().trim { it <= ' ' }.isEmpty()) {
                    target!!.error = getString(R.string.error_incorrect)
                }
                onRefresh()
            }
        })
        deviceVideoCommandView = view.findViewById(R.id.device_dev_video_command_edit)
        deviceVideoCommandView?.setText(devVideoCommand)
        deviceVideoCommandView?.addTextChangedListener(object :
            TextChangedListener<TextInputEditText?>(deviceVideoCommandView) {
            override fun onTextChanged(target: TextInputEditText?, s: Editable?) {
                devVideoCommand = s.toString().trim { it <= ' ' }
                onRefresh()
            }
        })
        deviceDevVideoCommandLayout = view.findViewById(R.id.device_dev_video_command_layout)
        if (!isWiFiSupported) {
            deviceDevVideoCommandLayout?.setEnabled(false)
        }
        deviceDevVideoCommandLayout?.setEndIconOnClickListener(View.OnClickListener {
            //TODO
        })
        deviceMACView = view.findViewById(R.id.device_mac_edit)
        deviceMACView?.setText(MAC)
        deviceMACView?.addTextChangedListener(object : TextWatcher {
            //https://github.com/r-cohen/macaddress-edittext
            private fun setMacEdit(
                cleanMac: String,
                formattedMac: String,
                selectionStart: Int,
                lengthDiff: Int
            ) {
                deviceMACView!!.removeTextChangedListener(this)
                if (cleanMac.length <= 12) {
                    deviceMACView!!.setText(formattedMac)
                    deviceMACView!!.setSelection(selectionStart + lengthDiff)
                    mPreviousMac = formattedMac
                } else {
                    deviceMACView?.setText(mPreviousMac)
                    deviceMACView?.setSelection(mPreviousMac!!.length)
                }
                deviceMACView?.addTextChangedListener(this)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (deviceMACView?.getText().toString() != deviceMACView?.getText().toString()) {
                    val upperText =
                        deviceMACView?.getText().toString().uppercase(Locale.getDefault())
                    deviceMACView?.setText(upperText)
                    deviceMACView?.setSelection(deviceMACView!!.length()) //fix reverse texting
                }
                val enteredMac = deviceMACView?.getText().toString().uppercase(Locale.getDefault())
                val cleanMac = clearNonMacCharacters(enteredMac)
                var formattedMac = formatMacAddress(cleanMac)
                val selectionStart = deviceMACView?.getSelectionStart()
                formattedMac = handleColonDeletion(enteredMac, formattedMac, selectionStart!!)
                val lengthDiff = formattedMac.length - enteredMac.length
                setMacEdit(cleanMac, formattedMac, selectionStart!!, lengthDiff)
                MAC = s.toString().trim { it <= ' ' }
                onRefresh()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        deviceClassView = view.findViewById(R.id.device_class_edit)
        deviceClassView?.setText(devClass)
        val adapterClass = SpinnerArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_dropdown_item,
            Arrays.asList("class_android", "class_computer", "class_arduino", "no_class")
        )
        deviceClassView?.setAdapter(adapterClass)
        deviceClassView?.addTextChangedListener(object :
            TextChangedListener<MaterialAutoCompleteTextView?>(deviceClassView) {
            override fun onTextChanged(target: MaterialAutoCompleteTextView?, s: Editable?) {
                if (devClass != s.toString()) {
                    devClass = s.toString()
                    onRefresh()
                }
            }
        })
        deviceTypeView = view.findViewById(R.id.device_type_edit)
        deviceTypeView?.setText(devType)
        adapterType = SpinnerArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_dropdown_item,
            Arrays.asList(
                "type_sphere",
                "type_anthropomorphic",
                "type_cubbi",
                "type_computer",
                "no_type"
            )
        )
        deviceTypeView?.setAdapter(adapterType)
        deviceTypeView?.addTextChangedListener(object :
            TextChangedListener<MaterialAutoCompleteTextView?>(deviceTypeView) {
            override fun onTextChanged(target: MaterialAutoCompleteTextView?, s: Editable?) {
                if (deviceTypeViewLayout!!.isEnabled() && devType != s.toString()) {
                    devType = s.toString()
                    onRefresh()
                }
            }
        })
        deviceProtoView = view.findViewById(R.id.device_proto_edit)
        deviceProtoView!!.setText(protocol)
        //TODO
//        SpinnerArrayAdapter<String> adapterProto = new SpinnerArrayAdapter<String>(
//                fragmentContext, android.R.layout.simple_spinner_dropdown_item,
//                AppDatabase.Companion.getProtocolNames());
//        deviceProtoView.setAdapter(adapterProto);
        deviceProtoView?.addTextChangedListener(object :
            TextChangedListener<MaterialAutoCompleteTextView?>(deviceProtoView) {
            override fun onTextChanged(target: MaterialAutoCompleteTextView?, s: Editable?) {
                protocol = s.toString().trim { it <= ' ' }
                onRefresh()
            }
        })
        deviceIpView = view.findViewById(R.id.device_ip_edit)
        deviceIpView!!.setText(devIp)
        deviceIpView!!.addTextChangedListener(object :
            TextChangedListener<TextInputEditText?>(deviceIpView) {
            override fun onTextChanged(target: TextInputEditText?, s: Editable?) {
                devIp = s.toString().trim { it <= ' ' }
                onRefresh()
            }
        })
        devicePortView = view.findViewById(R.id.device_port_edit)
        devicePortView?.setText(devPort)
        devicePortView?.addTextChangedListener(object :
            TextChangedListener<TextInputEditText?>(devicePortView) {
            override fun onTextChanged(target: TextInputEditText?, s: Editable?) {
                devPort = s.toString().trim { it <= ' ' }
                if (devPort!!.isEmpty()) devPort = "0"
                onRefresh()
            }
        })
        connectButton = view.findViewById(R.id.device_connect)
        connectButton?.setOnClickListener(View.OnClickListener { ma!!.showBottomSheetToConnect() })
        if (isNew) connectButton?.setVisibility(View.GONE)
        deleteButton = view.findViewById(R.id.device_delete)
        deleteButton?.setOnClickListener(View.OnClickListener { view ->
            val dbDevices = getAppDataBase(requireContext())
            //TODO
            val devicesDao = dbDevices.deviceItemTypeDao()
            if (devicesDao != null) {
                //TODO
                //devicesDao.delete(deviceId);
            }
            findNavController(view).navigate(R.id.action_deviceMenuFragment_to_mainMenuFragment)
        })
        if (isNew) deleteButton?.setVisibility(View.GONE)
        requireView().findViewById<View>(R.id.workspace_edit).setOnClickListener { view1: View? ->
            newInstance().show(childFragmentManager, null)
        }
        btIcon = view.findViewById(R.id.device_menu_bt_icon)
        wifiIcon = view.findViewById(R.id.device_menu_wifi_icon)
        onRefresh()
    }

    fun saveDevice() {
        if (deviceNameView!!.text.toString().trim { it <= ' ' }.isEmpty()) {
            deviceNameView!!.error = getString(R.string.error_incorrect)
        } else if (deviceMACView!!.text.toString()
                .trim { it <= ' ' }.isNotEmpty() && !isBtSupported
        ) {
            deviceMACView!!.error = getString(R.string.error_incorrect)
        } else if (deviceIpView!!.text.toString()
                .trim { it <= ' ' }.isNotEmpty() && !isWiFiSupported
        ) {
            deviceIpView!!.error = getString(R.string.error_incorrect)
        } else {
            val newName = deviceNameView!!.text.toString()
            protocol = deviceProtoView!!.text.toString()
            val classDevice = deviceClassView!!.text.toString()
            val typeDevice: String
            typeDevice =
                if (classDevice == "class_arduino") deviceTypeView!!.text.toString() else "no_type"
            MAC = deviceMACView!!.text.toString().trim { it <= ' ' }
            devIp = deviceIpView!!.text.toString().trim { it <= ' ' }
            devPort = devicePortView!!.text.toString().trim { it <= ' ' }

            //TODO
//            AppDatabase dbDevices = App.getDatabase();
//            DeviceItemTypeDao devicesDao = dbDevices.getDeviceItemTypeDao();
            currentDevice!!.name = newName
            currentDevice!!.deviceMAC = MAC
            currentDevice!!.devClass = classDevice
            currentDevice!!.devType = typeDevice
            currentDevice!!.devProtocol = protocol!!
            currentDevice!!.devIp = devIp!!
            //TODO
            //currentDevice.setDevVideoCommand(devVideoCommand);
            try {
                val i = devPort!!.toInt()
                currentDevice!!.devPort = i
            } catch (e: NumberFormatException) {
                currentDevice!!.devPort = 0
            }
            currentDevice!!.devPort = devPort!!.toInt()
            //TODO
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
        if (mPreviousMac != null && mPreviousMac!!.length > 1) {
            val previousColonCount = colonCount(mPreviousMac!!)
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

    fun setDeviceImage() {
        imageType = if (devClass == "class_arduino") {
            devType
        } else {
            devClass
        }
        if (devClass == "class_arduino") {
            when (imageType) {
                "type_computer" -> deviceImage!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        fragmentContext!!, R.drawable.type_computer
                    )
                )
                "type_sphere" -> {}
                "type_anthropomorphic" -> {}
                "type_cubbi" -> deviceImage!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        fragmentContext!!, R.drawable.type_cubbi
                    )
                )
                "no_type" -> deviceImage!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        fragmentContext!!, R.drawable.type_no_type
                    )
                )
            }
        } else {
            when (imageType) {
                "class_android" -> deviceImage!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        fragmentContext!!, R.drawable.class_android
                    )
                )
                "no_class" -> deviceImage!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        fragmentContext!!, R.drawable.type_no_type
                    )
                )
                "class_computer" -> deviceImage!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        fragmentContext!!, R.drawable.class_computer
                    )
                )
            }
        }
    }

    fun onRefresh() {
        if (deviceClassView!!.text.toString() == "class_arduino") {
            deviceTypeViewLayout!!.isEnabled = true
            adapterType = SpinnerArrayAdapter(
                requireActivity(), android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList(
                    "type_sphere",
                    "type_anthropomorphic",
                    "type_cubbi",
                    "type_computer",
                    "no_type"
                )
            )
            deviceTypeView!!.setAdapter(adapterType)
        } else {
            deviceTypeViewLayout!!.isEnabled = false
            deviceTypeView!!.setText("no_type")
        }
        setDeviceImage()
        if (name == currentDevice!!.name && MAC == currentDevice!!.deviceMAC && protocol == currentDevice!!.devProtocol && devClass == currentDevice!!.devClass && devType == currentDevice!!.devType && devIp == currentDevice!!.devIp && devPort == currentDevice!!.devPort.toString() //TODO
        //                &&
        //                imageType.equals(currentDevice.getImageType())
        ) {
            if (currentDevice!!.isBtSupported ||
                currentDevice!!.isWiFiSupported
            ) {
                connectButton!!.isEnabled = true
            }
            if (!isNew) {
                saveButton!!.isEnabled = false
                deleteButton!!.isEnabled = true
            }
        } else {
            saveButton!!.isEnabled = true
            connectButton!!.isEnabled = false
            deleteButton!!.isEnabled = false
        }
        if (isBtSupported) btIcon!!.visibility = View.VISIBLE else btIcon!!.visibility =
            View.INVISIBLE
        if (isWiFiSupported) wifiIcon!!.visibility = View.VISIBLE else wifiIcon!!.visibility =
            View.INVISIBLE
        deviceDevVideoCommandLayout!!.isEnabled = isWiFiSupported
    }

    val isBtSupported: Boolean
        get() = MAC != null && BluetoothAdapter.checkBluetoothAddress(MAC)
    val isWiFiSupported: Boolean
        get() = devIp != null && Patterns.IP_ADDRESS.matcher(devIp).matches()

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