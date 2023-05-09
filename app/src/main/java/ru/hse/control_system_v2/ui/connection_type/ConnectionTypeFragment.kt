package ru.hse.control_system_v2.ui.connection_type

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.connection.data.classes.ConnectionDeviceModel
import ru.hse.control_system_v2.connection.data.classes.ConnectionType
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel

class ConnectionTypeFragment (val devices: ArrayList<DeviceModel>) : Fragment() {

    // A list of connection types to choose from
    val connectionTypes = listOf("WiFi", "Bluetooth", "USB")

    // A list of connection device models to store the selected protocol for each device
    val connectionDeviceModels = ArrayList<ConnectionDeviceModel>()

    // A recycler view adapter to display the devices and their connection types
    lateinit var adapter: DeviceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_connection_type, container, false)

        // Initialize the connection device models with the default protocol for each device
        devices.forEach { device ->
            connectionDeviceModels.add(ConnectionDeviceModel(device, ConnectionType(device.protocol)))
        }

        // Initialize the recycler view and its adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DeviceAdapter(connectionDeviceModels) { position ->
            // This is a lambda function that is invoked when a device item is clicked
            showConnectionTypeDialog(position)
        }
        recyclerView.adapter = adapter

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