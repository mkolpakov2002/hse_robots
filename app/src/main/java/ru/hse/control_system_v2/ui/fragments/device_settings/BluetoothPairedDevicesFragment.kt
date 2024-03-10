package ru.hse.control_system_v2.ui.fragments.device_settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.domain.connection.ConnectionFactory.bluetoothBounded
import ru.hse.control_system_v2.model.entities.Device
import ru.hse.control_system_v2.databinding.FragmentBluetoothPairedDevicesBinding
import ru.hse.control_system_v2.ui.fragments.device_settings.NewBtDevicesAdapter.OnDeviceClicked

class BluetoothPairedDevicesFragment : Fragment(), OnDeviceClicked, OnRefreshListener {
    private lateinit var binding: FragmentBluetoothPairedDevicesBinding
    private lateinit var pairedList: RecyclerView
    private lateinit var pairedDevicesTitleTextView: TextView
    private lateinit var swipeToRefreshLayout: SwipeRefreshLayout
    var newBtDevicesAdapter: NewBtDevicesAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBluetoothPairedDevicesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeToRefreshLayout = binding.swipeRefreshLayoutAddDevice
        swipeToRefreshLayout.setOnRefreshListener(this)
        val fabToOpenSettings = binding.floatingActionButtonOpenSettings
        fabToOpenSettings.setOnClickListener { view: View -> openSettings(view) }
        pairedDevicesTitleTextView = binding.pairedDevicesTitleAddActivity
        pairedList = binding.pairedList
        pairedList.layoutManager = LinearLayoutManager(context)
        searchForDevice()
    }

    private fun openSettings(view: View) {
        val intent_add_device = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        startActivity(intent_add_device)
    }

    // Добавляем сопряжённые устройства в List View
    fun searchForDevice() {
        //SuppressLint MissingPermission
        val pairedDevices = bluetoothBounded
        // Если список спаренных устройств не пуст
        if (pairedDevices.isNotEmpty()) {
            newBtDevicesAdapter = NewBtDevicesAdapter(
                ArrayList(pairedDevices),
                this
            )
            if (pairedList.adapter != null) newBtDevicesAdapter!!.setDevicePrototypeList(
                ArrayList(
                    pairedDevices
                )
            ) else pairedList.adapter = newBtDevicesAdapter
            pairedDevicesTitleTextView.setText(R.string.paired_devices)
        } else {
            //no_devices_added
            pairedDevicesTitleTextView.setText(R.string.no_devices_added)
            pairedList.adapter = null
        }
        swipeToRefreshLayout.isRefreshing = false
    }

    fun checkDeviceAddress(devicePrototype: Device?) {
        val b = Bundle()
        b.putSerializable("device", devicePrototype)
        findNavController(requireView()).navigate(R.id.deviceMenuFragment, b)
    }

    override fun selectedDevice(devicePrototype: Device) {
        checkDeviceAddress(devicePrototype)
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    override fun onRefresh() {
        swipeToRefreshLayout.isRefreshing = true
        // Bluetooth включён. Предложим пользователю добавить устройства и начать передачу данных.
        searchForDevice()
    }
}