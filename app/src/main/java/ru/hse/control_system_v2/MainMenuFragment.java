package ru.hse.control_system_v2;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ru.hse.control_system_v2.list_devices.ButtonItemType;
import ru.hse.control_system_v2.list_devices.DeviceItemType;
import ru.hse.control_system_v2.list_devices.ItemType;
import ru.hse.control_system_v2.list_devices.MultipleTypesAdapter;

public class MainMenuFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //инициализация swipe refresh
    private SwipeRefreshLayout swipeToRefreshLayout;
    private BluetoothAdapter btAdapter;
    private ExtendedFloatingActionButton fabToStartConnecting;
    private RecyclerView recycler;
    private MultipleTypesAdapter adapter = null;
    private TextView headerText;
    private ArrayList<DeviceItemType> allDevicesList = new ArrayList<>();
    private Context fragmentContext;
    private MainActivity ma;
    private AlertDialog progressOfConnectionDialog;
    private View view;

    @Override
    public void onAttach(@NonNull Context context) {
        fragmentContext=context;
        ma = ((MainActivity) fragmentContext);
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main, container, false);
        }
        return view;

    }

    @Override
    public void onStart(){
        super.onStart();
        onRefresh();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentContext.registerReceiver(mMessageReceiverNotSuccess, new IntentFilter("not_success"));
        fragmentContext.registerReceiver(mMessageReceiverSuccess, new IntentFilter("success"));
        fragmentContext.registerReceiver(BluetoothStateChanged, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        BottomNavigationView mainMenu = ma.findViewById(R.id.bottomnav);

        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentContext, R.style.dialogTheme);
        LayoutInflater inflater = ma.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_connection, null);
        builder.setView(dialogView);
        progressOfConnectionDialog = builder.create();


        GridLayoutManager gridLayoutManager = new GridLayoutManager(fragmentContext, 3, LinearLayoutManager.VERTICAL, false);
        headerText = view.findViewById(R.id.paired_devices_title_add_activity);

        swipeToRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeToRefreshLayout.setOnRefreshListener(this);
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        fabToStartConnecting = view.findViewById(R.id.floating_action_button_start_sending_data);
        fabToStartConnecting.setOnClickListener(v -> {
            showStartOfConnection();
            Intent startBluetoothConnectionService = new Intent(fragmentContext, BluetoothConnectionService.class);
            startBluetoothConnectionService.putExtra("protocol", adapter.getSelectedProto());
            fragmentContext.startService(startBluetoothConnectionService);
        });

        //dbdevice = new DeviceDBHelper(fragmentContext);
        recycler = view.findViewById(R.id.recycler_main);
        recycler.setLayoutManager(gridLayoutManager);
    }

    public void showStartOfConnection(){
        fabToStartConnecting.hide();
        progressOfConnectionDialog.setCancelable(false);
        progressOfConnectionDialog.show();
        DeviceHandler.setDevicesList((ArrayList<DeviceItemType>) adapter.getSelectedDevices());
        showToast("Соединение начато");
    }

    //Результат работы Service
    private final BroadcastReceiver mMessageReceiverNotSuccess = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            showToast("Подключение не успешно");
            progressOfConnectionDialog.hide();
            onRefresh();
        }
    };

    private final BroadcastReceiver mMessageReceiverSuccess = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Устройство подключено, Service выполнился успешно
            adapter.clearSelected();
            Bundle arguments = intent.getExtras();
            String classDevice = arguments.get("protocol").toString();
            Intent startSendingData = new Intent(fragmentContext, BluetoothDeviceActivity.class);
            startSendingData.putExtra("protocol", classDevice);
            startActivity(startSendingData);
            progressOfConnectionDialog.hide();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    //Обновляем внешний вид приложения, скрываем и добавляем нужные элементы интерфейса
    @Override
    public void onRefresh() {
        ma.hideBottomSheet();
        fabToStartConnecting.hide();
        if (btIsEnabledFlagVoid()) {
            headerText.setText(R.string.favorites_devices);
            // Bluetooth включён, надо показать кнопку добавления устройств и другую информацию
            AppDataBase dbDevices = App.getDatabase();
            DeviceItemTypeDao devicesDao = dbDevices.getDeviceItemTypeDao();
            ArrayList<DeviceItemType> newDevicesList = new ArrayList<>(devicesDao.getAll());


            if (adapter == null){ // it works first time
                allDevicesList = new ArrayList<>();
                allDevicesList.addAll(newDevicesList);
                ArrayList<ItemType> items = new ArrayList<>();
                items.add(new ButtonItemType(ma));
                items.addAll(allDevicesList);
                adapter = new MultipleTypesAdapter(items, fragmentContext, allDevicesList);
                recycler.setAdapter(adapter);
            } else {
                // it works second time and later
                adapter.onNewData(newDevicesList);
            }
        } else {
            headerText.setText(R.string.suggestionEnableBluetooth);
            recycler.setAdapter(null);
            adapter = null;
            ma.showFabToEnBt();
        }
        // Приложение обновлено, завершаем анимацию обновления
        swipeToRefreshLayout.setRefreshing(false);
    }

    //True, если Bluetooth включён
    public boolean btIsEnabledFlagVoid(){
        return btAdapter.isEnabled();
    }

    // Метод для вывода всплывающих данных на экран
    public void showToast(String outputInfoString) {
        System.out.println("show toast: " + outputInfoString);
        Toast outputInfoToast = Toast.makeText(ma, outputInfoString, Toast.LENGTH_SHORT);
        outputInfoToast.show();
    }

    //выполняемый код при изменении состояния bluetooth
    private final BroadcastReceiver BluetoothStateChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(btIsEnabledFlagVoid()){
                // Bluetooth включён, надо скрыть кнопку включения Bluetooth
                ma.hideFabToEnBt();
            } else {
                // Bluetooth выключён, надо показать кнопку включения Bluetooth
                ma.showFabToEnBt();
            }
            onRefresh();
        }
    };

    public void hideDeviceSelectedItems(){
        fabToStartConnecting.hide();
        ma.showMainMenu();
    }

    public void showDeviceSelectedItems(){
        ma.hideMainMenu();
        fabToStartConnecting.show();
    }
}
