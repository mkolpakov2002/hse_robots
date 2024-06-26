package ru.hse.control_system_v2.ui.fragments.home

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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.domain.connection.ConnectionFactory
import ru.hse.control_system_v2.model.db.AppDatabase
import ru.hse.control_system_v2.model.entities.DeviceOld
import ru.hse.control_system_v2.databinding.FragmentMainBinding
import ru.hse.control_system_v2.ui.MainActivity


class MainMenuFragment : Fragment(), OnRefreshListener, MultipleTypesAdapterKt.OnItemClickListener,
    MultipleTypesAdapterKt.OnItemLongClickListener {
    private lateinit  var multipleTypesAdapter: MultipleTypesAdapterKt
    private lateinit var fragmentContext: Context
    private var ma: MainActivity? = null
    private lateinit var bottomSheetDialogToAdd: BottomSheetDialog
    private var isMultiSelectVisible = false

    private val dataBinding by lazy {
        FragmentMainBinding.inflate(layoutInflater)
    }

    private lateinit var deviceOldItemTypeList: List<DeviceOld>

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
        fragmentContext.registerReceiver(
            bluetoothStateChanged,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )

        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Наблюдать за списком всех устройств
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.devices?.collect { devices ->
                deviceOldItemTypeList = devices
                onRefresh()
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
            if (multipleTypesAdapter.areDevicesConnectable()){
                val list = ArrayList<Int>()
                for (item in multipleTypesAdapter.getSelectedItems()) {
                    list.add(item.id)
                }
                val b = Bundle()
                b.putIntegerArrayList("deviceIdList", list)
                findNavController(dataBinding.root).navigate(R.id.action_mainMenuFragment_to_connectionTypeFragment, b)
            } else {
                (Snackbar.make(
                        dataBinding.root,
                        getString(R.string.selection_class_device_error),
                        Snackbar.LENGTH_LONG
                    ).setAction(getString(R.string.ok)) {}).show()
            }
        }
        dataBinding.floatingActionButtonDeleteSelected.hide()
        dataBinding.floatingActionButtonDeleteSelected.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                for(item in multipleTypesAdapter.getSelectedItems())
                    AppDatabase.getInstance(requireContext()).deviceOldItemTypeDao()?.delete(item.id)
                onRefresh()
            }
        }
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
        ma?.bottomAppBarSize?.let { dataBinding.recyclerMain.setPadding(0, 0, 0, it) }
        bottomSheetDialogToAdd = BottomSheetDialog(fragmentContext)
        bottomSheetDialogToAdd.setContentView(R.layout.bottom_sheet_dialog_add_device)
        bottomSheetDialogToAdd.setCancelable(true)
        bottomSheetDialogToAdd.dismiss()
        hideBottomSheetToAdd()
        val buttonToAddDeviceViaMAC =
            bottomSheetDialogToAdd.findViewById<Button>(R.id.button_manual_mac)
        val buttonToAddDevice =
            bottomSheetDialogToAdd.findViewById<Button>(R.id.button_add_device)

        buttonToAddDevice?.setOnClickListener {
            bottomSheetDialogToAdd.dismiss()
            if (!ConnectionFactory.isBtSupported) {
                val snackbar = Snackbar
                    .make(
                        dataBinding.swipeRefreshLayout, getString(R.string.suggestionNoBtAdapter),
                        Snackbar.LENGTH_LONG
                    )
                    .setAction(getString(R.string.ok)) { }
                snackbar.show()
            } else if (
                ConnectionFactory.isBtEnabled &&
                ConnectionFactory.isNotEmptyBluetoothBounded) {
                findNavController(requireParentFragment().requireView())
                    .navigate(R.id.addDeviceFragment)
            } else if (!ConnectionFactory.isBtEnabled) {
                val snackbar = Snackbar
                    .make(
                        dataBinding.swipeRefreshLayout, getString(R.string.en_bt_for_list),
                        Snackbar.LENGTH_LONG
                    )
                    .setAction(getString(R.string.ok)) {
                        val intentBtEnabled =
                            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        fragmentContext.startActivity(intentBtEnabled)
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
                        fragmentContext.startActivity(intentBtEnabled)
                    }
                snackbar.show()
            }
        }

        buttonToAddDeviceViaMAC?.setOnClickListener {
            bottomSheetDialogToAdd.dismiss()
            findNavController(requireParentFragment().requireView()).navigate(R.id.action_mainMenuFragment_to_deviceMenuFragment)
        }
    }

    override fun onRefresh() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                hideAllButtons()
                dataBinding.pairedDevicesTitleAddActivity.setText(R.string.favorites_devices)
                if (!this@MainMenuFragment::multipleTypesAdapter.isInitialized) {
                    initAdapter()
                } else {
                    multipleTypesAdapter.updateItems(deviceOldItemTypeList)
                }
                dataBinding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun initAdapter(){
        multipleTypesAdapter = MultipleTypesAdapterKt(requireContext(), deviceOldItemTypeList)
        dataBinding.recyclerMain.adapter = multipleTypesAdapter
        multipleTypesAdapter.onItemLongClickListener = this
        multipleTypesAdapter.onItemClickListener = this
    }

    //выполняемый код при изменении состояния bluetooth
    private val bluetoothStateChanged: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onRefresh()
        }
    }

    private fun hideAllButtons() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                dataBinding.floatingActionButtonDeleteSelected.hide()
                dataBinding.floatingActionButtonStartSendingData.hide()
                hideBottomSheetToAdd()
                isMultiSelectVisible = false
            }
        }
    }

    private fun showItemSelectionMenu() {
        hideBottomSheetToAdd()
        dataBinding.floatingActionButtonDeleteSelected.show()
        dataBinding.floatingActionButtonStartSendingData.show()
        isMultiSelectVisible = true
    }

    private fun showBottomSheetToAdd() {
        bottomSheetDialogToAdd.show()
    }

    private fun hideBottomSheetToAdd() {
        bottomSheetDialogToAdd.cancel()
    }

    override fun onItemClick(item: MultipleTypesAdapterKt.Item) {
        if(item.isButton){
            showBottomSheetToAdd()
        } else if(!multipleTypesAdapter.isMultiSelect && isMultiSelectVisible){
            hideAllButtons()
        } else if(!multipleTypesAdapter.isMultiSelect){
            val args = Bundle()
            args.putBoolean("isNew", false)
            args.putSerializable("deviceOld", item.deviceOld)
            findNavController(dataBinding.root).navigate(R.id.deviceMenuFragment, args)
        }

    }

    override fun onItemLongClick() {
        if(multipleTypesAdapter.isMultiSelect && !isMultiSelectVisible){
            showItemSelectionMenu()
        }
    }

}