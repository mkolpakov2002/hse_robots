package ru.hse.control_system_v2.ui;

import static ru.hse.control_system_v2.Constants.APP_LOG_TAG;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import ru.hse.control_system_v2.AppMain;
import ru.hse.control_system_v2.R;
import ru.hse.control_system_v2.ThemeUtils;
import ru.hse.control_system_v2.data.ConnectionService;
import ru.hse.control_system_v2.data.DeviceItemType;


public class MainActivity extends AppCompatActivity implements OneButtonAlertDialogFragment.OnDismissListener {

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
        if (AppMain.isActivityConnection()) {
            super.onCreate(null);
            AppMain.setActivityConnectionState(false);
        } else super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
        } else ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);
        setUpNavigation();
        checkForBtAdapter();
        if (AppMain.isServiceConnecting()) {
            navController.navigate(R.id.connection_dialog);
        }
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String currentFragment = Objects.requireNonNull(getCurrentVisibleFragment().getLabel()).toString();
                switch (currentFragment) {
                    case "main":
                        createOneButtonAlertDialog(getString(R.string.instruction_alert), getString(R.string.instruction_for_app));
                        break;
                    case "settings":
                        createOneButtonAlertDialog(getString(R.string.instruction_alert), getString(R.string.instruction_for_app_settings));
                        break;
                    case "DeviceMenuFragment":
                        createOneButtonAlertDialog(getString(R.string.instruction_alert), getString(R.string.instruction_for_app_device_menu));
                        break;
                    case "AddDeviceFragment":
                        createOneButtonAlertDialog(getString(R.string.instruction_alert), getString(R.string.instruction_for_app_add_device));
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
                if (AppMain.isWiFiSupported()) {
                    boolean isConnectionPossible = true;
                    for (DeviceItemType current : AppMain.getDevicesList()) {
                        if (!current.isWiFiSupported()) {
                            isConnectionPossible = false;
                            bottomSheetDialogToConnect.dismiss();
                            Snackbar snackbar = Snackbar
                                    .make(main_bottom_menu, getString(R.string.device_title) +
                                                    " " + current.getDevName() + " " +
                                                    getString(R.string.wifi_not_supported_text),
                                            Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.button_more), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            createOneButtonAlertDialog(getString(R.string.alert_info), getString(R.string.instruction_for_app_wifi));
                                        }
                                    });
                            snackbar.show();
                            break;
                        }
                    }
                    if (isConnectionPossible) {
                        Intent serviceStarted;
                        serviceStarted = new Intent("startingWiFiService");
                        sendBroadcast(serviceStarted);
                        bottomSheetDialogToConnect.dismiss();
                    }
                } else {
                    createOneButtonAlertDialog(getString(R.string.error), getString(R.string.suggestionNoWiFiAdapter));
                }

            });
        }
        if (buttonToConnectViaBt != null) {
            buttonToConnectViaBt.setOnClickListener(view1 -> {
                if (AppMain.isBtSupported()) {
                    boolean isConnectionPossible = true;
                    for (DeviceItemType current : AppMain.getDevicesList()) {
                        if (!current.isBtSupported()) {
                            isConnectionPossible = false;
                            bottomSheetDialogToConnect.dismiss();
                            Snackbar snackbar = Snackbar
                                    .make(main_bottom_menu, getString(R.string.device_title) +
                                                    " " + current.getDevName() + " " +
                                                    getString(R.string.bluetooth_not_supported_text),
                                            Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.button_more), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            createOneButtonAlertDialog(getString(R.string.alert_info), getString(R.string.instruction_for_app_bt));
                                        }
                                    });
                            snackbar.show();
                            break;
                        }
                    }
                    if (isConnectionPossible) {
                        Intent serviceStarted;
                        serviceStarted = new Intent("startingBtService");
                        sendBroadcast(serviceStarted);
                        bottomSheetDialogToConnect.dismiss();
                    }
                } else {
                    createOneButtonAlertDialog(getString(R.string.error), getString(R.string.suggestionNoBtAdapter));
                }

            });
        }
        requestMultiplePermissions();
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

        main_bottom_menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mainMenuFragment:
                        navController.navigate(R.id.mainMenuFragment);
                        return true;
                    case R.id.settingsFragment:
                        navController.navigate(R.id.settingsFragment);
                        return true;
                }
                return false;
            }
        });
    }

    public final NavDestination getCurrentVisibleFragment() {
        return currentVisibleFragment;
    }


    // проверка на наличие адаптеров
    public void checkForBtAdapter() {

        if (!AppMain.isBtWiFiSupported()) {
            // объект Builder для создания диалогового окна
            AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialog_AppCompat).create();
            dialog.setTitle(getString(R.string.error));
            dialog.setMessage(getString(R.string.suggestionNoBtWiFiAdapter));
            dialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                    (dialog1, which) -> {
                        dialog1.dismiss();
                    });
            dialog.show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (AppMain.isActivityConnection()) {
            AppMain.setActivityConnectionState(false);
        }
        checkForBtAdapter();
    }

    private void requestMultiplePermissions() {
        ArrayList<String> permissionList = new ArrayList<>();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
                permissionList.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
        }

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if(permissionList.size()>0){
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[0]),PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Arrays.stream(grantResults).anyMatch(n -> n!= PackageManager.PERMISSION_GRANTED)){
            // объект Builder для создания диалогового окна
            AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialog_AppCompat).create();
            dialog.setTitle(getString(R.string.error));
            dialog.setMessage("Чтобы использовать приложение Роботы ВШЭ, предоставьте разрешения на доступ к Bluetooth и хранилищу.");
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                    (dialog1, which) -> {
                        // Closes the dialog and terminates the activity.
                        dialog1.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + getPackageName()));
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        this.finish();
                    });
            dialog.setCancelable(false);
            dialog.show();
        }
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
            if (AppMain.isBtEnabled()) {
                startConnectionService();
            } else {
                Bundle args = new Bundle();
                args.putString("dialogTitle", getString(R.string.error));
                args.putString("dialogText", getString(R.string.en_bt_for_connection));
                navController.navigateUp();
                navController.navigate(R.id.oneButtonAlertDialogFragment, args);
            }

        }
    };

    private final BroadcastReceiver mMessageReceiverNeedToStartWiFiService = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            isBtConnection = false;
            if (AppMain.isWiFiEnabled()) {
                startConnectionService();
            } else {
                Bundle args = new Bundle();
                args.putString("dialogTitle", getString(R.string.error));
                args.putString("dialogText", getString(R.string.en_wifi_for_connection));
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
            {
                try {
                    if (getMainMenuFragment() != null)
                        getMainMenuFragment().onRefresh();
                } catch (java.lang.IllegalStateException e) {
                    //nothing
                }
            }
            AppMain.setServiceConnecting(false);
            createOneButtonAlertDialog(getString(R.string.error), getString(R.string.connection_error));
            isBtConnection = null;
        }
    };

    private final BroadcastReceiver mMessageReceiverSuccess = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(!AppMain.isActivityConnection()&& AppMain.isServiceConnecting()){
                //Устройство подключено, Service выполнился успешно
                navController.navigate(R.id.mainMenuFragment);
                Bundle b = new Bundle();
                b.putBoolean("isBtService", !AppMain.getDevicesList().get(0).isWiFiBtConnected());
                navController.navigate(R.id.connectionActivity, b);
                isBtConnection = null;
                AppMain.setServiceConnecting(false);
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (getCurrentVisibleFragment().getId() == R.id.mainMenuFragment && !main_bottom_menu.isShown()) {
            if (getMainMenuFragment() != null)
                getMainMenuFragment().onRefresh();
        } else if (getCurrentVisibleFragment().getId() == R.id.mainMenuFragment) {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                finish();
            } else {
                //показ сообщения, о необходимости второго нажатия кнопки назад при выходе
                Snackbar snackbar = Snackbar
                        .make(main_bottom_menu, getString(R.string.double_back_click),
                                Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            back_pressed = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    private void startConnectionService() {
        if(!AppMain.isServiceConnecting()){
            AppMain.setServiceConnecting(true);
            Intent startConnectionService = new Intent(AppMain.getContext(), ConnectionService.class);
            startConnectionService.putExtra("isBtService", isBtConnection);
            startService(startConnectionService);
            navController.navigate(R.id.connection_dialog);
        }
    }

    void enableNetwork() {
        if (isBtConnection) {
            BluetoothAdapter mBluetoothAdapter = AppMain.getBtAdapter();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.BLUETOOTH_CONNECT
                }, 2);
                return;
            }
            mBluetoothAdapter.enable();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent panelIntent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                this.startActivity(panelIntent);
            } else {
                WifiManager wifiManager = (WifiManager)
                        getApplicationContext().getSystemService(WIFI_SERVICE);
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

    public MainMenuFragment getMainMenuFragment() {
        if (getCurrentVisibleFragment().getId() == R.id.mainMenuFragment) {
            FragmentManager fragmentManager = null;
            if (navHostFragment != null) {
                fragmentManager = navHostFragment.getChildFragmentManager();
            }
            Fragment current = null;
            if (fragmentManager != null) {
                current = fragmentManager.getPrimaryNavigationFragment();
            }
            if (current instanceof MainMenuFragment) {
                return (MainMenuFragment) current;
            }
        }
        return null;
    }

}