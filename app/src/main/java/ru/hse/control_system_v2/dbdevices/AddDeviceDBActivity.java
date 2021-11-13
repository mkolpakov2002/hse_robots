package ru.hse.control_system_v2.dbdevices;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ru.hse.control_system_v2.App;
import ru.hse.control_system_v2.DialogDeviceEdit;
import ru.hse.control_system_v2.R;
import ru.hse.control_system_v2.ThemeUtils;
import ru.hse.control_system_v2.dbprotocol.ProtocolDBHelper;

public class AddDeviceDBActivity extends AppCompatActivity implements DevicesAdapter.SelectedDevice, SwipeRefreshLayout.OnRefreshListener{
    ProtocolDBHelper protocolDBHelper;
    ExtendedFloatingActionButton fabToOpenSettings;
    RecyclerView pairedList;
    BluetoothAdapter btAdapter;
    String selectedDeviceInfo;
    String deviceHardwareAddress;
    DevicesAdapter devicesAdapter;
    TextView pairedDevicesTitleTextView;

    String name;
    //инициализация swipe refresh
    SwipeRefreshLayout swipeToRefreshLayout;
    Bundle b;
    public static boolean stateOfAlert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bd_device);

        swipeToRefreshLayout = findViewById(R.id.swipeRefreshLayout_add_device);
        swipeToRefreshLayout.setOnRefreshListener(this);

        fabToOpenSettings = findViewById(R.id.floating_action_button_open_settings);
        fabToOpenSettings.setOnClickListener(this::openSettings);

        protocolDBHelper = new ProtocolDBHelper(this);

        stateOfAlert = false;

        pairedDevicesTitleTextView = findViewById(R.id.paired_devices_title_add_activity);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        pairedList = findViewById(R.id.paired_list);
        pairedList.setLayoutManager(new LinearLayoutManager(this));
        pairedList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        Toolbar toolbar = findViewById(R.id.toolbar_add_device);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> exitActivity());
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
                deviceHardwareAddress = device.getName() + "\n" + device.getAddress(); // Name + MAC address в виде String переменной
                a.add(deviceHardwareAddress);
            }
            String[] array = a.toArray(new String[0]);

            for (String s : array) {
                DeviceModel deviceModel = new DeviceModel(s);

                devicesList.add(deviceModel);
            }
            devicesAdapter = new DevicesAdapter(devicesList, this);

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
        selectedDeviceInfo = deviceModel.getDeviceName();
        DialogDeviceEdit alertDialog = new DialogDeviceEdit(null, selectedDeviceInfo);
        Bundle args = new Bundle();
        alertDialog.setArguments(args);
        //fragment.currentDevice = item;
        alertDialog.show(this.getSupportFragmentManager(), "dialog");
    }

    // Метод для вывода всплывающих данных на экран
    public void showToast(String outputInfoString) {
        Toast outputInfoToast = Toast.makeText(this, outputInfoString, Toast.LENGTH_SHORT);
        outputInfoToast.show();
    }

    @Override
    public void selectedDevice(DeviceModel deviceModel) {
        checkDeviceAddress(deviceModel);
    }

    //True, если Bluetooth включён
    public boolean btIsEnabledFlagVoid(){
        return btAdapter.isEnabled();
    }

    public void exitActivity(){
        finish();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        exitActivity();
    }

    @Override
    protected void onStart(){
        super.onStart();
        searchForDevice();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(stateOfAlert){
            exitActivity();
        } else{
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        swipeToRefreshLayout.setRefreshing(true);
        if (btIsEnabledFlagVoid()) {
            // Bluetooth включён. Предложим пользователю добавить устройства и начать передачу данных.
            searchForDevice();

        } else {
            swipeToRefreshLayout.setRefreshing(false);
            finish();
            showToast("Please, enable bluetooth");
        }
    }


}