package ru.hse.control_system_v2;

import static ru.hse.control_system_v2.Constants.APP_LOG_TAG;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import ru.hse.control_system_v2.dbdevices.DeviceModel;
import ru.hse.control_system_v2.dbdevices.DevicesAdapter;
import ru.hse.control_system_v2.list_devices.DeviceItemType;


public class MainActivity extends AppCompatActivity implements OneButtonAlertDialogFragment.OnDismissListener {

    private int isFirstLaunch;
    private SharedPreferences sPref;
    private BottomNavigationView main_bottom_menu;
    private NavDestination currentVisibleFragment;
    private NavHostFragment navHostFragment;
    private NavController navController;
    private Boolean isBtConnection;
    private BottomSheetDialog bottomSheetDialogToConnect;
    //Переменная для хранения времени между нажатиями кнопки назад
    private static long back_pressed = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String currentFragment = Objects.requireNonNull(getCurrentVisibleFragment().getLabel()).toString();
                switch (currentFragment){
                    case "main":
                        createOneButtonAlertDialog("Инструкция", getString(R.string.instruction_for_app));
                        break;
                    case "settings":
                        createOneButtonAlertDialog("Инструкция", getString(R.string.instruction_for_app_settings));
                        break;
                    case "DeviceMenuFragment":
                        createOneButtonAlertDialog("Инструкция", getString(R.string.instruction_for_app_device_menu));
                        break;
                    case "AddDeviceFragment":
                        createOneButtonAlertDialog("Инструкция", getString(R.string.instruction_for_app_add_device));
                        break;
                    default:
                        //nothing
                        break;
                }
                return true;
            }
        });

        registerReceiver(mMessageReceiverNeedToStartBtService, new IntentFilter("startingBtService"));
        registerReceiver(mMessageReceiverNeedToStartWiFiService, new IntentFilter("startingWiFiService"));
        registerReceiver(mMessageReceiverNotSuccess, new IntentFilter("not_success"));
        registerReceiver(mMessageReceiverSuccess, new IntentFilter("success"));

        sPref = getPreferences(MODE_PRIVATE);
        isFirstLaunch = sPref.getInt("isFirstLaunch", 1);
        isBtConnection = null;
        // настройка поведения нижнего экрана

        bottomSheetDialogToConnect = new BottomSheetDialog(this);
        bottomSheetDialogToConnect.setContentView(R.layout.bottom_sheet_dialog_connection_type);
        bottomSheetDialogToConnect.setCancelable(true);
        bottomSheetDialogToConnect.dismiss();

        Button buttonToConnectViaWiFi = bottomSheetDialogToConnect.findViewById(R.id.button_wifi);
        Button buttonToConnectViaBt = bottomSheetDialogToConnect.findViewById(R.id.button_bt);
        if (buttonToConnectViaWiFi != null) {
            buttonToConnectViaWiFi.setOnClickListener(view1 -> {
                boolean isConnectionPossible = true;
                for(DeviceItemType current: DeviceHandler.getDevicesList()){
                    if(!current.isWiFiSupported()){
                        isConnectionPossible = false;
                        bottomSheetDialogToConnect.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(main_bottom_menu, "Устройство "+ current.getDevName() + " не поддерживает WiFi",
                                        Snackbar.LENGTH_LONG)
                                .setAction("Подробнее", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        createOneButtonAlertDialog(getString(R.string.alert_info), getString(R.string.instruction_for_app_wifi));
                                    }
                                });
                        snackbar.show();
                        break;
                    }
                }
                if(isConnectionPossible){
                    Intent serviceStarted;
                    serviceStarted = new Intent("startingWiFiService");
                    sendBroadcast(serviceStarted);
                    bottomSheetDialogToConnect.dismiss();
                }
            });
        }
        if (buttonToConnectViaBt != null) {
            buttonToConnectViaBt.setOnClickListener(view1 -> {
                boolean isConnectionPossible = true;
                for(DeviceItemType current: DeviceHandler.getDevicesList()){
                    if(!current.isBtSupported()){
                        isConnectionPossible = false;
                        bottomSheetDialogToConnect.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(main_bottom_menu, "Устройство "+ current.getDevName() + " не поддерживает Bluetooth",
                                        Snackbar.LENGTH_LONG)
                                .setAction("Подробнее", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        createOneButtonAlertDialog(getString(R.string.alert_info), getString(R.string.instruction_for_app_bt));
                                    }
                                });
                        snackbar.show();
                        break;
                    }
                }
                if(isConnectionPossible){
                    Intent serviceStarted;
                    serviceStarted = new Intent("startingBtService");
                    sendBroadcast(serviceStarted);
                    bottomSheetDialogToConnect.dismiss();
                }
            });
        }

        setUpNavigation();
        checkForBtAdapter();
    }

    void setUpNavigation() {
        main_bottom_menu = findViewById(R.id.bottomnav);
        navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavigationUI.setupWithNavController(main_bottom_menu,
                    (navHostFragment).getNavController());
        }

        navController = Objects.requireNonNull(navHostFragment).getNavController();
        currentVisibleFragment = navController.getCurrentDestination();
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                Log.e(APP_LOG_TAG, "onDestinationChanged: " + destination.getLabel());
                //отслеживания фпагмента на главном экране
                currentVisibleFragment = destination;

            }
        });
    }

    public final NavDestination getCurrentVisibleFragment() {
        return currentVisibleFragment;
    }


    // проверка на наличие Bluetooth адаптера; дальнейшее продолжение работы в случае наличия
    public void checkForBtAdapter() {

        if (!App.isBtWiFiSupported()) {
            // объект Builder для создания диалогового окна
            androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialog_AppCompat).create();
            dialog.setTitle(getString(R.string.error));
            dialog.setMessage(getString(R.string.suggestionNoBtAdapter));
            dialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                    (dialog1, which) -> {
                        // Closes the dialog and terminates the activity.
                        dialog1.dismiss();
                        this.finish();
                    });
        } else {
            if (isFirstLaunch == 1) {
                sPref = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putInt("isFirstLaunch", 0);
                ed.apply();
                isFirstLaunch = 0;
                requestPerms();
                createOneButtonAlertDialog(getResources().getString(R.string.instruction_alert), "Добро пожаловать! Используйте это приложение для управления вашими роботами. Подробная инструкция доступна по нажатию на знак вопроса.");

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForBtAdapter();
    }

    private void requestPerms() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
    }

    final int PERMISSION_REQUEST_CODE = 123;

    // создает диалоговое окно с 1й кнопкой
    private void createOneButtonAlertDialog(String title, String content) {
        Bundle message = new Bundle();
        message.putString("dialogText", content);
        message.putString("dialogTitle", title);
        navController.navigate(R.id.oneButtonAlertDialogFragment, message);
    }

    public synchronized void showMainMenu() {
        main_bottom_menu.setVisibility(View.VISIBLE);
    }

    public synchronized void hideMainMenu() {
        main_bottom_menu.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public int getBottomAppBarSize() {
        int resourceId = getResources().getIdentifier("design_bottom_navigation_height", "dimen", this.getPackageName());
        int height = 0;
        if (resourceId > 0) {
            height = getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

    private final BroadcastReceiver mMessageReceiverNeedToStartBtService = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            isBtConnection = true;
            if (App.isBtEnabled()) {
                startConnectionService();
            } else {
                Bundle args = new Bundle();
                args.putString("dialogTitle", getString(R.string.error));
                args.putString("dialogText", "Для соединения необходимо включить Bluetooth");
                navController.navigateUp();
                navController.navigate(R.id.oneButtonAlertDialogFragment, args);
            }

        }
    };

    private final BroadcastReceiver mMessageReceiverNeedToStartWiFiService = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            isBtConnection = false;
            if (App.isWiFiEnabled()) {
                startConnectionService();
            } else {
                Bundle args = new Bundle();
                args.putString("dialogTitle", getString(R.string.error));
                args.putString("dialogText", "Для соединения необходимо включить WiFi");
                navController.navigateUp();
                navController.navigate(R.id.oneButtonAlertDialogFragment, args);
            }
        }
    };

    //Результат работы Service
    private final BroadcastReceiver mMessageReceiverNotSuccess = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            navController.navigate(R.id.mainMenuFragment);
            if(getMainMenuFragment()!=null)
                getMainMenuFragment().onRefresh();
            createOneButtonAlertDialog("Ошибка", "Подключение не успешно.");
            isBtConnection = null;
        }
    };

    private final BroadcastReceiver mMessageReceiverSuccess = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Устройство подключено, Service выполнился успешно
            navController.navigate(R.id.mainMenuFragment);
            if (isBtConnection)
                navController.navigate(R.id.bluetoothDeviceActivity);
            else
                navController.navigate(R.id.wiFiDeviceActivity);
            isBtConnection = null;
        }
    };

    @Override
    public void onBackPressed() {
        if (getCurrentVisibleFragment().getId() == R.id.mainMenuFragment && !main_bottom_menu.isShown()) {
            if(getMainMenuFragment()!=null)
                getMainMenuFragment().onRefresh();
        } else if (getCurrentVisibleFragment().getId() == R.id.mainMenuFragment){
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                finish();
            } else {
                //показ сообщения, о необходимости второго нажатия кнопки назад при выходе
                Snackbar snackbar = Snackbar
                        .make(main_bottom_menu, "Нажмите второй раз для выхода из приложения",
                                Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            back_pressed = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    private void startConnectionService() {
        if (isBtConnection) {
            Intent startBluetoothConnectionService = new Intent(App.getContext(), BluetoothConnectionService.class);
            startService(startBluetoothConnectionService);
        } else {
            Intent startWiFiConnectionService = new Intent(App.getContext(), WiFiConnectionService.class);
            startService(startWiFiConnectionService);
        }
        navController.navigate(R.id.connection_dialog);

    }

    void enableNetwork() {
        if (isBtConnection) {
            Intent intentBtEnabled = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.startActivity(intentBtEnabled);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                Intent panelIntent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                this.startActivity(panelIntent);
            } else {
                WifiManager wifiManager = (WifiManager)
                        App.getContext().getSystemService(WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);
            }
        }

    }

    @Override
    public void onDialogDismissed() {
        if (isBtConnection != null) {
            enableNetwork();
        }
    }

    public synchronized void showBottomSheetToConnect() {
        bottomSheetDialogToConnect.show();
    }

    public synchronized void hideBottomSheetToConnect() {
        bottomSheetDialogToConnect.cancel();
    }

    public MainMenuFragment getMainMenuFragment(){
        if(getCurrentVisibleFragment().getId() == R.id.mainMenuFragment){
            FragmentManager fragmentManager = null;
            if (navHostFragment != null) {
                fragmentManager = navHostFragment.getChildFragmentManager();
            }
            Fragment current = null;
            if (fragmentManager != null) {
                current = fragmentManager.getPrimaryNavigationFragment();
            }
            if(current instanceof MainMenuFragment){
                return (MainMenuFragment) current;
            }
        }
        return null;
    }


}