package ru.hse.control_system_v2.ui.fragments.connection_settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.control_system_v2.utility.AppConstants.CONNECTION_LIST
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.domain.connection.ConnectionClass
import ru.hse.control_system_v2.domain.connection.ConnectionManager
import ru.hse.control_system_v2.model.entities.ConnectionDeviceModel
import ru.hse.control_system_v2.model.entities.ConnectionType
import ru.hse.control_system_v2.model.entities.Device
import ru.hse.control_system_v2.databinding.FragmentConnectionTypeBinding

class ConnectionTypeFragment: Fragment() {

    private lateinit var binding: FragmentConnectionTypeBinding

    lateinit var floatingActionButton: ExtendedFloatingActionButton

    lateinit var devicesIdList: ArrayList<Int>

    lateinit var devices: ArrayList<Device>

    val connectionTypes = CONNECTION_LIST.toMutableList()

    val connectionDeviceModels = ArrayList<ConnectionDeviceModel>()

    lateinit var adapter: ConnectionTypeAdapter

    private val viewModel : ConnectionTypeViewModel by viewModels()

    // Словарь для отслеживания состояний устройств
    private val connectionStates = mutableMapOf<Int, ConnectionClass.ConnectionState>()

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
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentConnectionTypeBinding.inflate(inflater,container,false)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.devices?.collect { savedDevices ->
                val foundDevices = ArrayList<Device>()
                for (id in devicesIdList) {
                    val device = savedDevices.find { it.id == id }
                    if (device != null) {
                        foundDevices.add(device)
                    }
                }
                devices = foundDevices

                // Initialize the connection device models with the default protocol for each device
                devices.forEach { device ->
                    if(device.isBluetoothSupported)
                        connectionDeviceModels
                            .add(ConnectionDeviceModel(device, ConnectionType(CONNECTION_LIST[0])))
                    else if(device.isWiFiSupported)
                        connectionDeviceModels
                            .add(ConnectionDeviceModel(device, ConnectionType(CONNECTION_LIST[1])))
                }

                // Initialize the recycler view and its adapter
                val recyclerView = binding.recyclerView
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                adapter = ConnectionTypeAdapter(connectionDeviceModels) { position ->
                    // This is a lambda function that is invoked when a device item is clicked
                    showConnectionTypeDialog(position)
                }
                recyclerView.adapter = adapter
            }
        }

        floatingActionButton = binding.floatingActionButton

        floatingActionButton.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.connection_dialog)
            // Открываем соединения и начинаем наблюдение за их состояниями
            viewLifecycleOwner.lifecycleScope.launch {
                ConnectionManager.prepareConnections(connectionDeviceModels)
                val connections = ConnectionManager.openPendingConnections()
                connections.forEach { connection ->
                    observeConnectionState(connection)
                }
            }
        }

        return binding.root
    }

    private fun showConnectionTypeDialog(position: Int) {
        val connectionDeviceModel = connectionDeviceModels[position]
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("Выберите протокол для ${connectionDeviceModel.deviceItemType.name}")
        builder.setSingleChoiceItems(
            connectionTypes.toTypedArray(),
            connectionTypes.indexOf(connectionDeviceModel.connectionType.connectionProtocol)
        ) { dialog, which ->
            connectionDeviceModel.connectionType =
                ConnectionType(connectionTypes[which])
            adapter.notifyItemChanged(position)
            dialog.dismiss()
        }
        builder.show()
    }

    private fun observeConnectionState(connection: ConnectionClass<*>) {
        lifecycleScope.launch {
            connection.stateFlow.collect { state ->
                // Обновляем состояние соединения в словаре
                connectionStates[connection.connectionDeviceModel.deviceItemType.id] = state

                // Проверяем, остались ли устройства в состоянии CONNECTING
                val isAnyConnecting = connectionStates.values.any { it == ConnectionClass.ConnectionState.CONNECTING }

                if (!isAnyConnecting) {
                    withContext(Dispatchers.Main) {
                        binding.root.findNavController().popBackStack()
                    }
                }
                when (state) {
                    ConnectionClass.ConnectionState.DISCONNECTED -> {
                        // Обработка состояния DISCONNECTED
                        Log.e("Updated state", "DISCONNECTED")
                        ConnectionManager.removeConnection(connection)
                    }
                    ConnectionClass.ConnectionState.CONNECTING -> {
                        // Обработка состояния CONNECTING
                        Log.e("Updated state", "CONNECTING")
                    }
                    ConnectionClass.ConnectionState.ALIVE -> {
                        // Обработка состояния ALIVE
                        Log.e("Updated state", "ALIVE")
                    }
                    ConnectionClass.ConnectionState.DISABLED -> {
                        // Обработка состояния DISABLED
                        Log.e("Updated state", "DISABLED")
                        ConnectionManager.removeConnection(connection)
                    }
                }
            }
        }
    }
}