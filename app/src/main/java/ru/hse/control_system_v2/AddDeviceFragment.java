package ru.hse.control_system_v2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ru.hse.control_system_v2.dbdevices.DeviceModel;
import ru.hse.control_system_v2.dbdevices.DevicesAdapter;
import ru.hse.control_system_v2.list_devices.DeviceItemType;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddDeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddDeviceFragment extends Fragment implements DevicesAdapter.SelectedDevice, SwipeRefreshLayout.OnRefreshListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView pairedList;
    private BluetoothAdapter btAdapter;
    private TextView pairedDevicesTitleTextView;
    //инициализация swipe refresh
    private SwipeRefreshLayout swipeToRefreshLayout;
    private static boolean stateOfAlert;
    View view;

    public AddDeviceFragment() {
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
    public static AddDeviceFragment newInstance(String param1, String param2) {
        AddDeviceFragment fragment = new AddDeviceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        stateOfAlert = false;

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
    public void searchForDevice(){
        // Обновление List View - удаление старых данных
        pairedList.setAdapter(null);
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        // Если список спаренных устройств не пуст
        if(pairedDevices.size()>0) {
            List<DeviceModel> devicesList = new ArrayList<>();
            List<String> a = new ArrayList<>();
            // устанавливаем связь между данными
            // проходимся в цикле по этому списку
            for (BluetoothDevice device : pairedDevices) {
                // Обновление List View - добавляем в него сопряжённые устройства
                String deviceHardwareAddress = device.getName() + "\n" + device.getAddress(); // Name + MAC address в виде String переменной
                a.add(deviceHardwareAddress);
            }
            String[] array = a.toArray(new String[0]);

            for (String s : array) {
                DeviceModel deviceModel = new DeviceModel(s);

                devicesList.add(deviceModel);
            }
            DevicesAdapter devicesAdapter = new DevicesAdapter(devicesList, this);

            pairedList.setAdapter(devicesAdapter);

            pairedDevicesTitleTextView.setText(R.string.paired_devices);
        } else {
            //no_devices_added
            pairedDevicesTitleTextView.setText(R.string.no_devices_added);
            pairedList.setAdapter(null);
        }
        swipeToRefreshLayout.setRefreshing(false);
    }

    //Получаем адрес устройства из List View
    public void checkDeviceAddress(DeviceModel deviceModel) {
        DeviceItemType newDev = new DeviceItemType();
        String selectedDeviceInfo = deviceModel.getDeviceName();
        String MAC, name;
        //Get information from List View in String
        int i = selectedDeviceInfo.indexOf(':');
        i = i - 2;
        //В текущем пункте List View находим первый символ ":",
        // всё после него, а также два символа до него - адрес выбранного устройства
        MAC = selectedDeviceInfo.substring(i);
        if (i == 0) {
            name = "";
        } else {
            name = selectedDeviceInfo.substring(0, i - 1);
        }
        newDev.setDeviceMAC(MAC);
        newDev.setDevName(name);
        ArrayList<DeviceItemType> deviceItemTypeArrayList = new ArrayList<DeviceItemType>();
        deviceItemTypeArrayList.add(newDev);
        DeviceHandler.setDevicesList(deviceItemTypeArrayList);
        Navigation.findNavController(view).navigate(R.id.deviceMenuFragment);
    }

    @Override
    public void selectedDevice(DeviceModel deviceModel) {
        checkDeviceAddress(deviceModel);
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