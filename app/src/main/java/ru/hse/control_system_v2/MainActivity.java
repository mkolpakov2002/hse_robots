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
import android.view.View;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements OneButtonAlertDialogFragment.OnDismissListener {

    private int isFirstLaunch;
    private SharedPreferences sPref;
    private BluetoothAdapter btAdapter;
    private BottomNavigationView main_bottom_menu;
    private NavDestination currentVisibleFragment;
    private NavHostFragment navHostFragment;
    private NavController navController;
    private Boolean isBtConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        registerReceiver(mMessageReceiverNeedToStartBtService, new IntentFilter("startingBtService"));
        registerReceiver(mMessageReceiverNeedToStartWiFiService, new IntentFilter("startingWiFiService"));
        registerReceiver(mMessageReceiverNotSuccess, new IntentFilter("not_success"));
        registerReceiver(mMessageReceiverSuccess, new IntentFilter("success"));

        sPref = getPreferences(MODE_PRIVATE);
        isFirstLaunch = sPref.getInt("isFirstLaunch", 1);
        isBtConnection = null;
        ////////////////////////////////////
        // настройка поведения нижнего экрана


        this.registerReceiver(BluetoothStateChanged, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        setUpNavigation();
        checkForBtAdapter();
    }

    void setUpNavigation(){
        main_bottom_menu = findViewById(R.id.bottomnav);
        navHostFragment = (NavHostFragment)getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if(navHostFragment != null){
            NavigationUI.setupWithNavController(main_bottom_menu,
                    (navHostFragment).getNavController());
        }

        navController= Objects.requireNonNull(navHostFragment).getNavController();
        currentVisibleFragment = navController.getCurrentDestination();
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                Log.e(APP_LOG_TAG, "onDestinationChanged: "+destination.getLabel());
                //отслеживания фпагмента на главном экране
                if(destination.getId() == R.id.mainMenuFragment ||
                        destination.getId() == R.id.settingsFragment){
                    currentVisibleFragment = destination;
                }
            }
        });
    }

    public final NavDestination getCurrentVisibleFragment(){
        return currentVisibleFragment;
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


    // проверка на наличие Bluetooth адаптера; дальнейшее продолжение работы в случае наличия
    public void checkForBtAdapter() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            System.out.println("There is no bluetooth adapter on device!");
            // объект Builder для создания диалогового окна
            //suggestionNoBtAdapter
            androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this,R.style.AlertDialog_AppCompat).create();
            dialog.setTitle(getString(R.string.error));
            dialog.setMessage(getString(R.string.suggestionNoBtAdapter));
            dialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                    (dialog1, which) -> {
                        // Closes the dialog and terminates the activity.
                        dialog1.dismiss();
                        this.finish();
                    });
        } else {
            if (isFirstLaunch == 1){
                sPref = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putInt("isFirstLaunch", 0);
                ed.apply();
                isFirstLaunch = 0;
                requestPerms();
                createOneButtonAlertDialog(getResources().getString(R.string.instruction_alert),
                            getResources().getString(R.string.other_discoverable_devices));

            }
        }
    }

    // Метод для вывода всплывающих данных на экран
    public void showToast(String outputInfoString) {
        System.out.println("show toast: " + outputInfoString);
        Toast outputInfoToast = Toast.makeText(this, outputInfoString, Toast.LENGTH_SHORT);
        outputInfoToast.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForBtAdapter();
        Context c = App.getContext();
    }

    private void requestPerms(){
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions,PERMISSION_REQUEST_CODE);
    }

    final int REQUEST_CODE_OPEN = 20, PERMISSION_REQUEST_CODE = 123;

    //True, если Bluetooth включён
    public boolean btIsEnabledFlagVoid(){
        return btAdapter.isEnabled();
    }

    // создает диалоговое окно с 1й кнопкой
    private void createOneButtonAlertDialog(String title, String content) {
        navController.navigateUp();
        Bundle message = new Bundle();
        message.putString("dialogText",content);
        message.putString("dialogTitle",title);
        navController.navigate(R.id.action_mainMenuFragment_to_oneButtonAlertDialogFragment, message);
    }

    public synchronized void showMainMenu(){
        main_bottom_menu.setVisibility(View.VISIBLE);
    }

    public synchronized void hideMainMenu(){
        main_bottom_menu.setVisibility(View.GONE);
    }

    //выполняемый код при изменении состояния bluetooth
    private final BroadcastReceiver BluetoothStateChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(btIsEnabledFlagVoid()){
                // Bluetooth включён, надо скрыть кнопку включения Bluetooth
                //TODO
            } else {
                // Bluetooth выключён, надо показать кнопку включения Bluetooth
                //TODO
            }
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    public int getBottomAppBarSize(){
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
            if(btIsEnabledFlagVoid()){
                startConnectionService();
            } else {
                Bundle args = new Bundle();
                args.putString("dialogTitle",getString(R.string.error));
                args.putString("dialogText","Для соединения необходимо включить Bluetooth");
                navController.navigateUp();
                navController.navigate(R.id.action_mainMenuFragment_to_oneButtonAlertDialogFragment, args);
            }

        }
    };

    private final BroadcastReceiver mMessageReceiverNeedToStartWiFiService = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            isBtConnection = false;
            if(((WifiManager)getSystemService(Context.WIFI_SERVICE)).isWifiEnabled()){
                startConnectionService();
            } else {
                Bundle args = new Bundle();
                args.putString("dialogTitle",getString(R.string.error));
                args.putString("dialogText","Для соединения необходимо включить WiFi");
                navController.navigateUp();
                navController.navigate(R.id.action_mainMenuFragment_to_oneButtonAlertDialogFragment, args);
            }
        }
    };

    //Результат работы Service
    private final BroadcastReceiver mMessageReceiverNotSuccess = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            navController.navigateUp();
            createOneButtonAlertDialog("Ошибка", "Подключение не успешно.");
            getMainMenuFragment().onRefresh();
            isBtConnection = null;
        }
    };

    private final BroadcastReceiver mMessageReceiverSuccess = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Устройство подключено, Service выполнился успешно
            navController.navigateUp();
            getMainMenuFragment().onRefresh();
            if(isBtConnection)
                navController.navigate(R.id.action_mainMenuFragment_to_bluetoothDeviceActivity);
            else
                navController.navigate(R.id.action_mainMenuFragment_to_wiFiDeviceActivity);
            isBtConnection = null;
        }
    };

    @Override
    public void onBackPressed() {
        if(getCurrentVisibleFragment().getId()==R.id.mainMenuFragment && !main_bottom_menu.isShown()){
            getMainMenuFragment().onRefresh();
        } else {
            super.onBackPressed();
        }
    }

    private void startConnectionService(){
        if(isBtConnection){
            Intent startBluetoothConnectionService = new Intent(App.getContext(), BluetoothConnectionService.class);
            startService(startBluetoothConnectionService);
            navController.navigateUp();
            getMainMenuFragment().onRefresh();
            navController.navigate(R.id.action_mainMenuFragment_to_connection_dialog);
        } else {
            Intent startWiFiConnectionService = new Intent(App.getContext(), WiFiConnectionService.class);
            startService(startWiFiConnectionService);
            navController.navigateUp();
            getMainMenuFragment().onRefresh();
            navController.navigate(R.id.action_mainMenuFragment_to_connection_dialog);
        }
    }

    void enableNetwork(){
        if(isBtConnection){
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
        if(isBtConnection != null){
            enableNetwork();
        }
    }
}