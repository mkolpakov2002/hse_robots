package ru.hse.control_system_v2.ui.device_settings;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Set;

import ru.hse.control_system_v2.R;
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel;

public class BluetoothPairedDevicesFragment extends Fragment implements NewBtDevicesAdapter.OnDeviceClicked, SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView pairedList;
    private BluetoothAdapter btAdapter;
    private TextView pairedDevicesTitleTextView;
    //инициализация swipe refresh
    private SwipeRefreshLayout swipeToRefreshLayout;
    View view;

    public BluetoothPairedDevicesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddDeviceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BluetoothPairedDevicesFragment newInstance(String param1, String param2) {
        BluetoothPairedDevicesFragment fragment = new BluetoothPairedDevicesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_device, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        swipeToRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_add_device);
        swipeToRefreshLayout.setOnRefreshListener(this);

        ExtendedFloatingActionButton fabToOpenSettings = view.findViewById(R.id.floating_action_button_open_settings);
        fabToOpenSettings.setOnClickListener(this::openSettings);


        pairedDevicesTitleTextView = view.findViewById(R.id.paired_devices_title_add_activity);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        pairedList = view.findViewById(R.id.paired_list);
        pairedList.setLayoutManager(new LinearLayoutManager(getContext()));
        pairedList.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        searchForDevice();
    }

    private void openSettings(View view) {
        Intent intent_add_device = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intent_add_device);
    }

    // Добавляем сопряжённые устройства в List View
    @SuppressLint("MissingPermission")
    public void searchForDevice(){
        // Обновление List View - удаление старых данных
        pairedList.setAdapter(null);
        //TODO: запросить на старте все разрешения
        @SuppressLint("MissingPermission")
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        // Если список спаренных устройств не пуст
        if(pairedDevices.size()>0) {
            NewBtDevicesAdapter newBtDevicesAdapter =
                    new NewBtDevicesAdapter(
                    new ArrayList<>(pairedDevices),
                    this);

            pairedList.setAdapter(newBtDevicesAdapter);

            pairedDevicesTitleTextView.setText(R.string.paired_devices);
        } else {
            //no_devices_added
            pairedDevicesTitleTextView.setText(R.string.no_devices_added);
            pairedList.setAdapter(null);
        }
        swipeToRefreshLayout.setRefreshing(false);
    }

    public void checkDeviceAddress(DeviceModel devicePrototype) {
        Bundle b = new Bundle();
        b.putSerializable("device", devicePrototype);
        Navigation.findNavController(view).navigate(R.id.deviceMenuFragment, b);
    }

    @Override
    public void selectedDevice(DeviceModel devicePrototype) {
        checkDeviceAddress(devicePrototype);
    }

    @Override
    public void onResume(){
        super.onResume();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        swipeToRefreshLayout.setRefreshing(true);
        // Bluetooth включён. Предложим пользователю добавить устройства и начать передачу данных.
        searchForDevice();
    }

}