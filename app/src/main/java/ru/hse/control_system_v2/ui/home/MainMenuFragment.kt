package ru.hse.control_system_v2.ui.home

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.connection.ConnectionFactory
import ru.hse.control_system_v2.data.AppDatabase
import ru.hse.control_system_v2.data.DeviceItemType
import ru.hse.control_system_v2.databinding.FragmentMainBinding
import ru.hse.control_system_v2.ui.MainActivity


class MainMenuFragment : Fragment(), OnRefreshListener {
    private lateinit  var multipleTypesAdapter: MultipleTypesAdapter
    private var fragmentContext: Context? = null
    private var ma: MainActivity? = null
    private var bottomSheetDialogToAdd: BottomSheetDialog? = null

    private val dataBinding by lazy {
        FragmentMainBinding.inflate(layoutInflater)
    }

    private lateinit var deviceItemTypeList: ArrayList<DeviceItemType>

    override fun onAttach(context: Context) {
        fragmentContext = context
        ma = fragmentContext as MainActivity?
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return dataBinding.root
    }

    override fun onStart() {
        super.onStart()
        //onRefresh()
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentContext!!.registerReceiver(
            bluetoothStateChanged,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )

        lifecycleScope.launch {
            // Suspend the coroutine until the lifecycle is DESTROYED.
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                // Safely collect from db when the lifecycle is RESUMED
                // and stop collecting when the lifecycle is PAUSED
                AppDatabase.getAppDataBase(requireContext()).deviceItemTypeDao()?.getAll()
                    ?.collect { data ->
                        if(!::deviceItemTypeList.isInitialized)
                            deviceItemTypeList = ArrayList()
                        deviceItemTypeList.addAll(data)
                        onRefresh()
                    }
            }
        }

        val orientation = this.resources.configuration.orientation
        val gridLayoutManager: GridLayoutManager = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            GridLayoutManager(fragmentContext, 3, LinearLayoutManager.VERTICAL, false)
        } else {
            // code for landscape mode
            GridLayoutManager(fragmentContext, 6, LinearLayoutManager.VERTICAL, false)
        }

        dataBinding.swipeRefreshLayout.setOnRefreshListener(this)
        dataBinding.floatingActionButtonStartSendingData.setOnClickListener {
            if (multipleTypesAdapter.areDevicesConnectable()) ma!!.showBottomSheetToConnect() else {
                val snackbar = Snackbar
                    .make(
                        dataBinding.swipeRefreshLayout,
                        getString(R.string.selection_class_device_error),
                        Snackbar.LENGTH_LONG
                    )
                    .setAction(getString(R.string.ok)) {
                        //nothing
                    }
                snackbar.show()
            }
        }
        dataBinding.floatingActionButtonDeleteSelected.hide()
        dataBinding.floatingActionButtonDeleteSelected.setOnClickListener(View.OnClickListener { v: View? ->
//            AppDatabase dbDevices = App.getDatabase();
//            DeviceItemTypeDao devicesDao = dbDevices.getDeviceItemTypeDao();
            //TODO
            //
//            for (DeviceItemType device : App.getDevicesList()) {
//                devicesDao.delete(device.getDevId());
//            }
            onRefresh()
        })
        dataBinding.recyclerMain.layoutManager = gridLayoutManager
        dataBinding.recyclerMain.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && dataBinding.floatingActionButtonStartSendingData.isExtended) {
                    dataBinding.floatingActionButtonStartSendingData.shrink()
                } else if (dy < 0 && !dataBinding.floatingActionButtonStartSendingData.isExtended) {
                    dataBinding.floatingActionButtonStartSendingData.extend()
                }
            }
        })
        dataBinding.recyclerMain.clipToPadding = false
        dataBinding.recyclerMain.setPadding(0, 0, 0, ma!!.bottomAppBarSize)
        bottomSheetDialogToAdd = BottomSheetDialog(ma!!)
        bottomSheetDialogToAdd!!.setContentView(R.layout.bottom_sheet_dialog_add_device)
        bottomSheetDialogToAdd!!.setCancelable(true)
        bottomSheetDialogToAdd!!.dismiss()
        hideBottomSheetToAdd()
        val buttonToAddDeviceViaMAC =
            bottomSheetDialogToAdd!!.findViewById<Button>(R.id.button_manual_mac)
        val buttonToAddDevice =
            bottomSheetDialogToAdd!!.findViewById<Button>(R.id.button_add_device)

        buttonToAddDevice?.setOnClickListener {
            bottomSheetDialogToAdd!!.dismiss()
            if (!ConnectionFactory.connectionFactory.isBtSupported) {
                val snackbar = Snackbar
                    .make(
                        dataBinding.swipeRefreshLayout, getString(R.string.suggestionNoBtAdapter),
                        Snackbar.LENGTH_LONG
                    )
                    .setAction(getString(R.string.ok)) { }
                snackbar.show()
            } else if (ConnectionFactory.connectionFactory.isBtEnabled && BluetoothAdapter.getDefaultAdapter().bondedDevices.isNotEmpty()) {
                Navigation.findNavController(requireParentFragment().requireView())
                    .navigate(R.id.addDeviceFragment)
            } else if (!ConnectionFactory.connectionFactory.isBtEnabled) {
                val snackbar = Snackbar
                    .make(
                        dataBinding.swipeRefreshLayout, getString(R.string.en_bt_for_list),
                        Snackbar.LENGTH_LONG
                    )
                    .setAction(getString(R.string.ok)) {
                        val intentBtEnabled =
                            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        fragmentContext!!.startActivity(intentBtEnabled)
                    }
                snackbar.show()
            } else {
                val snackbar = Snackbar
                    .make(
                        dataBinding.swipeRefreshLayout, getString(R.string.no_devices_added),
                        Snackbar.LENGTH_LONG
                    )
                    .setAction(getString(R.string.ok)) {
                        val intentBtEnabled =
                            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        fragmentContext!!.startActivity(intentBtEnabled)
                    }
                snackbar.show()
            }
        }

        buttonToAddDeviceViaMAC?.setOnClickListener {
            val newDevice = DeviceItemType()
            val newList = ArrayList<DeviceItemType>()
            newList.add(newDevice)
            //TODO
            //App.setDevicesList(newList);
            bottomSheetDialogToAdd!!.dismiss()
            findNavController(requireParentFragment().requireView()).navigate(R.id.action_mainMenuFragment_to_deviceMenuFragment)
        }
    }

    //Обновляем внешний вид приложения, скрываем и добавляем нужные элементы интерфейса
    override fun onRefresh() {
        ma!!.showMainMenu()
        hideAllButtons()
        dataBinding.pairedDevicesTitleAddActivity.setText(R.string.favorites_devices)
        // Bluetooth включён, надо показать кнопку добавления устройств и другую информацию
        if (!this::multipleTypesAdapter.isInitialized) { // it works first time
            initAdapter()
        } else {
            // it works second time and later
            multipleTypesAdapter.refreshAdapterData(deviceItemTypeList)
        }
        // Приложение обновлено, завершаем анимацию обновления
        dataBinding.swipeRefreshLayout.isRefreshing = false
    }

    private fun initAdapter(){
        multipleTypesAdapter = MultipleTypesAdapter(requireContext(), deviceItemTypeList)
        dataBinding.recyclerMain.adapter = multipleTypesAdapter
    }

    //выполняемый код при изменении состояния bluetooth
    private val bluetoothStateChanged: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onRefresh()
        }
    }

    private fun hideAllButtons() {
        dataBinding.floatingActionButtonDeleteSelected.hide()
        dataBinding.floatingActionButtonStartSendingData.hide()
        hideBottomSheetToAdd()
        ma!!.hideBottomSheetToConnect()
    }

    fun showItemSelectionMenu() {
        hideBottomSheetToAdd()
        ma!!.hideBottomSheetToConnect()
        ma!!.hideMainMenu()
        dataBinding.floatingActionButtonDeleteSelected.show()
        dataBinding.floatingActionButtonStartSendingData.show()
    }


    @Synchronized
    fun showBottomSheetToAdd() {
        bottomSheetDialogToAdd!!.show()
    }

    @Synchronized
    fun hideBottomSheetToAdd() {
        bottomSheetDialogToAdd!!.cancel()
    }
}