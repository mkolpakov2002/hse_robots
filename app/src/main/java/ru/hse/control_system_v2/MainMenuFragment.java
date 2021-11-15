package ru.hse.control_system_v2;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Iterables;

import java.util.ArrayList;

import ru.hse.control_system_v2.dbdevices.AddDeviceDBActivity;
import ru.hse.control_system_v2.list_devices.ButtonItemType;
import ru.hse.control_system_v2.list_devices.DeviceItemType;
import ru.hse.control_system_v2.list_devices.ItemType;
import ru.hse.control_system_v2.list_devices.MultipleTypesAdapter;

public class MainMenuFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //инициализация swipe refresh
    private SwipeRefreshLayout swipeToRefreshLayout;
    private BluetoothAdapter btAdapter;
    private FloatingActionButton fabToStartConnecting;
    private FloatingActionButton fabToDelete;
    private RecyclerView recycler;
    private MultipleTypesAdapter multipleTypesAdapter = null;
    private TextView headerText;
    private Context fragmentContext;
    private MainActivity ma;
    private DialogConnection progressOfConnectionDialog;
    private View view;
    private BottomSheetDialog bottomSheetBehavior;

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

        int orientation = this.getResources().getConfiguration().orientation;
        GridLayoutManager gridLayoutManager;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            gridLayoutManager = new GridLayoutManager(fragmentContext, 3, LinearLayoutManager.VERTICAL, false);
        } else {
            // code for landscape mode
            gridLayoutManager = new GridLayoutManager(fragmentContext, 6, LinearLayoutManager.VERTICAL, false);
        }
        headerText = view.findViewById(R.id.paired_devices_title_add_activity);

        swipeToRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeToRefreshLayout.setOnRefreshListener(this);
        swipeToRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        fabToStartConnecting = view.findViewById(R.id.floating_action_button_start_sending_data);
        fabToStartConnecting.setOnClickListener(v -> {
            showStartOfConnection();
            Intent startBluetoothConnectionService = new Intent(fragmentContext, BluetoothConnectionService.class);
            DeviceHandler.setDevicesList(Iterables.toArray(multipleTypesAdapter.getSelectedDevices(), DeviceItemType.class));
            fragmentContext.startService(startBluetoothConnectionService);
        });

        fabToDelete = view.findViewById(R.id.floating_action_button_delete_selected);
        fabToDelete.hide();
        fabToDelete.setOnClickListener(v -> {
            AppDataBase dbDevices = App.getDatabase();
            DeviceItemTypeDao devicesDao = dbDevices.getDeviceItemTypeDao();
            for(DeviceItemType device: multipleTypesAdapter.getSelectedDevices()){
                devicesDao.delete(device.getDevId());
            }
            onRefresh();
        });

        recycler = view.findViewById(R.id.recycler_main);
        recycler.setLayoutManager(gridLayoutManager);

        bottomSheetBehavior = new BottomSheetDialog(ma,R.style.SAIBottomSheetDialog_Backup_Light);
        bottomSheetBehavior.setContentView(R.layout.bottom_sheet_dialog_add_device);
        bottomSheetBehavior.setCancelable(true);
        bottomSheetBehavior.dismiss();
        hideBottomSheet();

        Button buttonToAddDeviceViaMAC = bottomSheetBehavior.findViewById(R.id.button_manual_mac);
        Button buttonToAddDevice = bottomSheetBehavior.findViewById(R.id.button_add_device);
        if (buttonToAddDevice != null) {
            buttonToAddDevice.setOnClickListener(view1 -> {
                bottomSheetBehavior.dismiss();
                Navigation.findNavController(requireParentFragment().requireView()).navigateUp();
                Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.action_mainMenuFragment_to_addDeviceDBActivity2);
            });
        }
        if (buttonToAddDeviceViaMAC != null) {
            buttonToAddDeviceViaMAC.setOnClickListener(view1 -> {
                bottomSheetBehavior.dismiss();
                Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.action_mainMenuFragment_to_device_entering_mac);
            });
        }

        if(savedInstanceState!=null){
            //recycler.getLayoutManager().onRestoreInstanceState(recylerViewState);
        }
    }

    public void showStartOfConnection(){
        fabToStartConnecting.hide();
        Navigation.findNavController(requireParentFragment().requireView()).navigateUp();
        Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.action_mainMenuFragment_to_connection_dialog);
        showToast("Соединение начато");
    }

    //Результат работы Service
    private final BroadcastReceiver mMessageReceiverNotSuccess = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            showToast("Подключение не успешно");
            Navigation.findNavController(requireParentFragment().requireView()).navigateUp();
            onRefresh();
        }
    };

    private final BroadcastReceiver mMessageReceiverSuccess = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Устройство подключено, Service выполнился успешно
            multipleTypesAdapter.clearSelected();
            Navigation.findNavController(requireParentFragment().requireView()).navigateUp();
            Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.action_mainMenuFragment_to_bluetoothDeviceActivity);
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
        hideBottomSheet();
        ma.showMainMenu();
        hideFabToStartConnecting();
        if (BluetoothAdapter.getDefaultAdapter() != null && btIsEnabledFlagVoid()) {
            headerText.setText(R.string.favorites_devices);
            // Bluetooth включён, надо показать кнопку добавления устройств и другую информацию
            AppDataBase dbDevices = App.getDatabase();
            DeviceItemTypeDao devicesDao = dbDevices.getDeviceItemTypeDao();
            ArrayList<DeviceItemType> newDevicesList = new ArrayList<>(devicesDao.getAll());
            if (multipleTypesAdapter == null){ // it works first time
                ArrayList<DeviceItemType> allDevicesList = new ArrayList<>(newDevicesList);
                ArrayList<ItemType> items = new ArrayList<>();
                items.add(new ButtonItemType(ma));
                items.addAll(allDevicesList);
                multipleTypesAdapter = new MultipleTypesAdapter(items, fragmentContext, allDevicesList);
                recycler.setAdapter(multipleTypesAdapter);
            } else {
                // it works second time and later
                multipleTypesAdapter.clearSelected();
                multipleTypesAdapter.onNewData(newDevicesList);
            }
        } else if(!btIsEnabledFlagVoid()) {
            headerText.setText(R.string.suggestionEnableBluetooth);
            recycler.setAdapter(null);
            multipleTypesAdapter = null;
        } else {
            // отсутствует Bluetooth адаптер, работа приложения невозможна
            AlertDialog dialog = new AlertDialog.Builder(ma).create();
            dialog.setTitle(getString(R.string.error));
            dialog.setMessage(getString(R.string.suggestionNoBtAdapter));
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
                    (dialog1, which) -> {
                        // скрывает диалог и завершает работу приложения
                        dialog1.dismiss();
                        ma.finish();
                    });
            // нельзя закрыть этот диалог
            dialog.setCancelable(false);
            dialog.show();
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
            onRefresh();
        }
    };

    public void hideFabToStartConnecting(){
        fabToDelete.hide();
        fabToStartConnecting.hide();
        ma.showMainMenu();
    }

    public void showFabToStartConnecting(){
        ma.hideMainMenu();
        fabToDelete.show();
        fabToStartConnecting.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO
        //Parcelable recylerViewState = Objects.requireNonNull(recycler.getLayoutManager()).onSaveInstanceState();
    }

    public synchronized void showBottomSheet(){
        bottomSheetBehavior.show();
    }

    public synchronized void hideBottomSheet(){
        bottomSheetBehavior.cancel();
    }
}
