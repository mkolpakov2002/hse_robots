package ru.hse.control_system_v2.ui;

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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import ru.hse.control_system_v2.App;
import ru.hse.control_system_v2.R;
import ru.hse.control_system_v2.data.AppDataBase;
import ru.hse.control_system_v2.data.DeviceItemTypeDao;
import ru.hse.control_system_v2.data.DeviceItemType;

public class MainMenuFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //инициализация swipe refresh
    private SwipeRefreshLayout swipeToRefreshLayout;
    private ExtendedFloatingActionButton fabToStartConnecting;
    private FloatingActionButton fabToDelete;
    private RecyclerView recycler;
    private MultipleTypesAdapter multipleTypesAdapter = null;
    private TextView headerText;
    private Context fragmentContext;
    private MainActivity ma;
    private View view;
    private BottomSheetDialog bottomSheetDialogToAdd;
    private AlertDialog dialog;

    @Override
    public void onAttach(@NonNull Context context) {
        fragmentContext = context;
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
    public void onStart() {
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

        fabToStartConnecting = view.findViewById(R.id.floating_action_button_start_sending_data);
        fabToStartConnecting.setOnClickListener(v -> {
            if (multipleTypesAdapter.areDevicesConnectable())
                ma.showBottomSheetToConnect();
            else {
                Snackbar snackbar = Snackbar
                        .make(swipeToRefreshLayout, getString(R.string.selection_class_device_error),
                                Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //nothing
                            }
                        });
                snackbar.show();
            }
        });

        fabToDelete = view.findViewById(R.id.floating_action_button_delete_selected);
        fabToDelete.hide();
        fabToDelete.setOnClickListener(v -> {
            AppDataBase dbDevices = App.getDatabase();
            DeviceItemTypeDao devicesDao = dbDevices.getDeviceItemTypeDao();
            for (DeviceItemType device : App.getDevicesList()) {
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
        recycler.setPadding(0, 0, 0, ma.getBottomAppBarSize());

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
                if(!App.isBtSupported()){
                    Snackbar snackbar = Snackbar
                            .make(swipeToRefreshLayout, getString(R.string.suggestionNoBtAdapter),
                                    Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            });
                    snackbar.show();
                } else if(App.isBtEnabled() && !BluetoothAdapter.getDefaultAdapter().getBondedDevices().isEmpty()){
                    Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.addDeviceFragment);
                } else if(!App.isBtEnabled()){
                    Snackbar snackbar = Snackbar
                            .make(swipeToRefreshLayout, getString(R.string.en_bt_for_list),
                                    Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intentBtEnabled = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    fragmentContext.startActivity(intentBtEnabled);
                                }
                            });
                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(swipeToRefreshLayout, getString(R.string.no_devices_added),
                                    Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intentBtEnabled = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    fragmentContext.startActivity(intentBtEnabled);
                                }
                            });
                    snackbar.show();
                }

            });
        }
        if (buttonToAddDeviceViaMAC != null) {
            buttonToAddDeviceViaMAC.setOnClickListener(view1 -> {
                DeviceItemType newDevice = new DeviceItemType();
                ArrayList<DeviceItemType> newList = new ArrayList<>();
                newList.add(newDevice);
                App.setDevicesList(newList);
                bottomSheetDialogToAdd.dismiss();
                Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.action_mainMenuFragment_to_deviceMenuFragment);
            });
        }

        if (savedInstanceState != null) {
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
        headerText.setText(R.string.favorites_devices);
        // Bluetooth включён, надо показать кнопку добавления устройств и другую информацию
        if (multipleTypesAdapter == null) { // it works first time
            multipleTypesAdapter = new MultipleTypesAdapter(fragmentContext);
            recycler.setAdapter(multipleTypesAdapter);
        } else {
            // it works second time and later
            multipleTypesAdapter.refreshAdapterData();
        }
        // Приложение обновлено, завершаем анимацию обновления
        swipeToRefreshLayout.setRefreshing(false);
    }

    //выполняемый код при изменении состояния bluetooth
    private final BroadcastReceiver BluetoothStateChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onRefresh();
        }
    };

    public void hideAllButtons() {
        fabToDelete.hide();
        fabToStartConnecting.hide();
        hideBottomSheetToAdd();
        ma.hideBottomSheetToConnect();
    }

    public void showItemSelectionMenu() {
        hideBottomSheetToAdd();
        ma.hideBottomSheetToConnect();
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

    public synchronized void showBottomSheetToAdd() {
        bottomSheetDialogToAdd.show();
    }

    public synchronized void hideBottomSheetToAdd() {
        bottomSheetDialogToAdd.cancel();
    }

}
