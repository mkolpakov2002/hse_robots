package ru.hse.control_system_v2;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.hse.control_system_v2.list_devices.DeviceItemType;
import ru.hse.control_system_v2.list_devices.MultipleTypesAdapter;

public class MainMenuFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //инициализация swipe refresh
    private SwipeRefreshLayout swipeToRefreshLayout;
    private BluetoothAdapter btAdapter;
    private ExtendedFloatingActionButton fabToStartConnecting;
    private FloatingActionButton fabToDelete;
    private RecyclerView recycler;
    private MultipleTypesAdapter multipleTypesAdapter = null;
    private TextView headerText;
    private Context fragmentContext;
    private MainActivity ma;
    private View view;
    private BottomSheetDialog bottomSheetDialogToAdd;
    private BottomSheetDialog bottomSheetDialogToConnect;
    private AlertDialog dialog;

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

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        fabToStartConnecting = view.findViewById(R.id.floating_action_button_start_sending_data);
        fabToStartConnecting.setOnClickListener(v -> {
            bottomSheetDialogToConnect.show();
        });

        fabToDelete = view.findViewById(R.id.floating_action_button_delete_selected);
        fabToDelete.hide();
        fabToDelete.setOnClickListener(v -> {
            AppDataBase dbDevices = App.getDatabase();
            DeviceItemTypeDao devicesDao = dbDevices.getDeviceItemTypeDao();
            for(DeviceItemType device: DeviceHandler.getDevicesList()){
                devicesDao.delete(device.getDevId());
            }
            onRefresh();
        });

        recycler = view.findViewById(R.id.recycler_main);
        recycler.setLayoutManager(gridLayoutManager);
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && fabToStartConnecting.isExtended()) {
                    fabToStartConnecting.shrink();
                } else if (dy < 0 && !fabToStartConnecting.isExtended()) {
                    fabToStartConnecting.extend();
                }
            }
        });
        recycler.setClipToPadding(false);
        recycler.setPadding(0,0,0,ma.getBottomAppBarSize());

        bottomSheetDialogToAdd = new BottomSheetDialog(ma);
        bottomSheetDialogToAdd.setContentView(R.layout.bottom_sheet_dialog_add_device);
        bottomSheetDialogToAdd.setCancelable(true);
        bottomSheetDialogToAdd.dismiss();
        hideBottomSheetToAdd();

        Button buttonToAddDeviceViaMAC = bottomSheetDialogToAdd.findViewById(R.id.button_manual_mac);
        Button buttonToAddDevice = bottomSheetDialogToAdd.findViewById(R.id.button_add_device);
        if (buttonToAddDevice != null) {
            buttonToAddDevice.setOnClickListener(view1 -> {
                bottomSheetDialogToAdd.dismiss();
                Navigation.findNavController(requireParentFragment().requireView()).navigateUp();
                Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.action_mainMenuFragment_to_addDeviceDBActivity2);
            });
        }
        if (buttonToAddDeviceViaMAC != null) {
            buttonToAddDeviceViaMAC.setOnClickListener(view1 -> {
                bottomSheetDialogToAdd.dismiss();
                Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.action_mainMenuFragment_to_device_entering_mac);
            });
        }


        bottomSheetDialogToConnect = new BottomSheetDialog(ma);
        bottomSheetDialogToConnect.setContentView(R.layout.bottom_sheet_dialog_connection_type);
        bottomSheetDialogToConnect.setCancelable(true);
        bottomSheetDialogToConnect.dismiss();
        hideBottomSheetToAdd();

        Button buttonToConnectViaWiFi = bottomSheetDialogToConnect.findViewById(R.id.button_wifi);
        Button buttonToConnectViaBt = bottomSheetDialogToConnect.findViewById(R.id.button_bt);
        if (buttonToConnectViaWiFi != null) {
            buttonToConnectViaWiFi.setOnClickListener(view1 -> {
                Intent serviceStarted;
                serviceStarted = new Intent("startingWiFiService");
                ma.sendBroadcast(serviceStarted);
                bottomSheetDialogToConnect.dismiss();
            });
        }
        if (buttonToConnectViaBt != null) {
            buttonToConnectViaBt.setOnClickListener(view1 -> {
                Intent serviceStarted;
                serviceStarted = new Intent("startingBtService");
                ma.sendBroadcast(serviceStarted);
                bottomSheetDialogToConnect.dismiss();
            });
        }

        if(savedInstanceState!=null){
            //recycler.getLayoutManager().onRestoreInstanceState(recylerViewState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    //Обновляем внешний вид приложения, скрываем и добавляем нужные элементы интерфейса
    @Override
    public void onRefresh() {
        ma.showMainMenu();
        hideAllButtons();

        if (BluetoothAdapter.getDefaultAdapter() != null && btIsEnabledFlagVoid()) {
            headerText.setText(R.string.favorites_devices);
            // Bluetooth включён, надо показать кнопку добавления устройств и другую информацию
            if (multipleTypesAdapter == null){ // it works first time
                multipleTypesAdapter = new MultipleTypesAdapter( fragmentContext);
                recycler.setAdapter(multipleTypesAdapter);
            } else {
                // it works second time and later
                multipleTypesAdapter.refreshAdapterData();
            }
        } else if(BluetoothAdapter.getDefaultAdapter() != null && !btIsEnabledFlagVoid()) {
            headerText.setText(R.string.suggestionEnableBluetooth);
            recycler.setAdapter(null);
            multipleTypesAdapter = null;
        } else if (BluetoothAdapter.getDefaultAdapter() == null || !ma.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI)){
            if(dialog !=null && dialog.isShowing()){
                dialog.hide();
            }
            // отсутствует Bluetooth адаптер, работа приложения невозможна
            MaterialAlertDialogBuilder adapterDialogBuilder = new MaterialAlertDialogBuilder(ma);
            adapterDialogBuilder.setTitle(getString(R.string.error));
            adapterDialogBuilder.setMessage(getString(R.string.suggestionNoBtAdapter));
            adapterDialogBuilder.setPositiveButton(getResources().getString(R.string.ok),
                    (dialog1, which) -> {
                        // скрывает диалог и завершает работу приложения
                        dialog1.dismiss();
                        ma.finish();
                    });
            // нельзя закрыть этот диалог
            adapterDialogBuilder.setCancelable(false);
            dialog = adapterDialogBuilder.create();
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

    public void hideAllButtons(){
        fabToDelete.hide();
        fabToStartConnecting.hide();
        hideBottomSheetToAdd();
        hideBottomSheetToConnect();
    }

    public void showItemSelectionMenu(){
        hideBottomSheetToAdd();
        hideBottomSheetToConnect();
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

    public synchronized void showBottomSheetToAdd(){
        bottomSheetDialogToAdd.show();
    }

    public synchronized void hideBottomSheetToAdd(){
        bottomSheetDialogToAdd.cancel();
    }

    public synchronized void showBottomSheetToConnect(){
        bottomSheetDialogToConnect.show();
    }

    public synchronized void hideBottomSheetToConnect(){
        bottomSheetDialogToConnect.cancel();
    }

}
