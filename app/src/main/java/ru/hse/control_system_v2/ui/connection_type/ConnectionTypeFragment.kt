package ru.hse.control_system_v2.ui.connection_type

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import ru.hse.control_system_v2.AppConstants.CONNECTION_LIST
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.connection.data.classes.ConnectionDeviceModel
import ru.hse.control_system_v2.connection.data.classes.ConnectionType
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel

class ConnectionTypeFragment: Fragment() {

    private lateinit var bottomSheetDialogToConnect: BottomSheetDialog

    lateinit var devicesIdList: ArrayList<Int>

    lateinit var devices: ArrayList<DeviceModel>

    // A list of connection types to choose from
    val connectionTypes = CONNECTION_LIST.toMutableList()

    // A list of connection device models to store the selected protocol for each device
    val connectionDeviceModels = ArrayList<ConnectionDeviceModel>()

    // A recycler view adapter to display the devices and their connection types
    lateinit var adapter: DeviceAdapter

    //val viewModel: ConnectionTypeViewModel = ViewModelProvider(this)[ConnectionTypeViewModel::class.java]
    private val viewModel : ConnectionTypeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        devicesIdList = ArrayList()
        arguments?.let {bundle ->
            bundle.getIntegerArrayList("deviceIdList")?.let {
                devicesIdList.addAll(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_connection_type, container, false)

        // Наблюдать за списком всех устройств
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.devices?.collect { savedDevices ->
                // Создаем пустой список для хранения найденных объектов
                val foundDevices = ArrayList<DeviceModel>()
                // Перебираем все id из списка devicesIdList
                for (id in devicesIdList) {
                    // Ищем объект с таким id в списке savedDevices
                    val device = savedDevices.find { it.id == id }
                    // Если нашли, добавляем его в список foundDevices
                    if (device != null) {
                        foundDevices.add(device)
                    }
                }
                devices = foundDevices

                // Initialize the connection device models with the default protocol for each device
                devices.forEach { device ->
                    if(device.isBluetoothSupported)
                        connectionDeviceModels.add(ConnectionDeviceModel(device, ConnectionType(CONNECTION_LIST[0])))
                    else if(device.isWiFiSupported)
                        connectionDeviceModels.add(ConnectionDeviceModel(device, ConnectionType(CONNECTION_LIST[1])))
                }
            }
        }

        // Initialize the recycler view and its adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DeviceAdapter(connectionDeviceModels) { position ->
            // This is a lambda function that is invoked when a device item is clicked
            showConnectionTypeDialog(position)
        }
        recyclerView.adapter = adapter

        // настройка поведения нижнего экрана
        bottomSheetDialogToConnect = BottomSheetDialog(requireContext())
        bottomSheetDialogToConnect.setContentView(R.layout.bottom_sheet_dialog_connection_type)
        bottomSheetDialogToConnect.setCancelable(true)
        bottomSheetDialogToConnect.dismiss()

        val buttonToConnectViaWiFi: Button? =
            bottomSheetDialogToConnect.findViewById(R.id.button_wifi)
        buttonToConnectViaWiFi?.setOnClickListener {

        }
        val buttonToConnectViaBt: Button? = bottomSheetDialogToConnect.findViewById(R.id.button_bt)
        buttonToConnectViaBt?.setOnClickListener {

        }

        return view
    }

    // A function to show a dialog with a list of connection types to choose from
    private fun showConnectionTypeDialog(position: Int) {
        // Get the current connection device model at the given position
        val connectionDeviceModel = connectionDeviceModels[position]

        // Create a material alert dialog builder with a title and a single choice items list
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("Выберите протокол для ${connectionDeviceModel.deviceItemType.name}")
        builder.setSingleChoiceItems(
            connectionTypes.toTypedArray(),
            connectionTypes.indexOf(connectionDeviceModel.connectionType.connectionProtocol)
        ) { dialog, which ->
            // This is a lambda function that is invoked when an item is selected
            // Update the connection type for the connection device model
            connectionDeviceModel.connectionType =
                ConnectionType(connectionTypes[which])
            // Notify the adapter that the data has changed
            adapter.notifyItemChanged(position)
            // Dismiss the dialog
            dialog.dismiss()
        }

        // Show the dialog
        builder.show()
    }
}