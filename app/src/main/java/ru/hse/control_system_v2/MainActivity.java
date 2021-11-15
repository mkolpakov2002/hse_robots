package ru.hse.control_system_v2;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.hse.control_system_v2.dbdevices.AddDeviceDBActivity;


public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabToEnBt;
    private int isFirstLaunch;
    private SharedPreferences sPref;
    private BluetoothAdapter btAdapter;
    private BottomNavigationView main_bottom_menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabToEnBt = findViewById(R.id.floating_action_button_En_Bt);
        fabToEnBt.setOnClickListener(v -> {
            Intent intentBtEnabled = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intentBtEnabled);
        });

        fabToEnBt.hide();
        sPref = getPreferences(MODE_PRIVATE);
        isFirstLaunch = sPref.getInt("isFirstLaunch", 1);
        ////////////////////////////////////
        // настройка поведения нижнего экрана


        this.registerReceiver(BluetoothStateChanged, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        setUpNavigation();
        checkForBtAdapter();
    }

    void setUpNavigation(){
        main_bottom_menu = findViewById(R.id.bottomnav);
        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if(navHostFragment != null){
            NavigationUI.setupWithNavController(main_bottom_menu,
                    (navHostFragment).getNavController());
        }
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
        } else if(btIsEnabledFlagVoid()){
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
        } else {
            showFabToEnBt();
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
        OneButtonAlertDialogFragment.newInstance(title, content).show(this.getSupportFragmentManager(), "dialog_alert");
    }

    private synchronized void showFabToEnBt(){
        fabToEnBt.show();
    }

    private synchronized void hideFabToEnBt(){
        fabToEnBt.hide();
    }

    public synchronized void showMainMenu(){
        main_bottom_menu.setVisibility(View.VISIBLE);
    }

    public synchronized void hideMainMenu(){
        main_bottom_menu.setVisibility(View.INVISIBLE);
    }

    //выполняемый код при изменении состояния bluetooth
    private final BroadcastReceiver BluetoothStateChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(btIsEnabledFlagVoid()){
                // Bluetooth включён, надо скрыть кнопку включения Bluetooth
                hideFabToEnBt();
            } else {
                // Bluetooth выключён, надо показать кнопку включения Bluetooth
                showFabToEnBt();
            }
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }
}