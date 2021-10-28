package ru.hse.control_system_v2;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import ru.hse.control_system_v2.dbdevices.AddDeviceDBActivity;


public class MainActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton fabToEnBt;
    private int isFirstLaunch;
    private SharedPreferences sPref;
    private BluetoothAdapter btAdapter;
    private BottomNavigationView main_bottom_menu;
    private BottomSheetDialog bottomSheetBehavior;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());
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
        bottomSheetBehavior = new BottomSheetDialog(this,R.style.BottomSheetDialog);

        bottomSheetBehavior.setContentView(R.layout.bottom_sheet_dialog_add_device);
        bottomSheetBehavior.setCancelable(true);
        bottomSheetBehavior.dismiss();
        hideBottomSheet();
        Button buttonToAddDeviceViaMAC = bottomSheetBehavior.findViewById(R.id.button_manual_mac);
        Button buttonToAddDevice = bottomSheetBehavior.findViewById(R.id.button_add_device);
        if (buttonToAddDevice != null) {
            buttonToAddDevice.setOnClickListener(view -> {
                Intent intent = new Intent(this, AddDeviceDBActivity.class);
                bottomSheetBehavior.dismiss();
                startActivity(intent);
            });
        }
        if (buttonToAddDeviceViaMAC != null) {
            buttonToAddDeviceViaMAC.setOnClickListener(view -> {
                bottomSheetBehavior.dismiss();
                DialogSaveDeviceWithMAC dialog = new DialogSaveDeviceWithMAC();
                dialog.show(this.getSupportFragmentManager(), "dialog");
            });
        }

        ImageButton closeBottomSheet = bottomSheetBehavior.findViewById(R.id.close_bottom_sheet);
        if (closeBottomSheet != null) {
            closeBottomSheet.setOnClickListener(view -> {
                hideBottomSheet();
            });
        }
        setUpNavigation();
    }

    void setUpNavigation(){
        main_bottom_menu =findViewById(R.id.bottomnav);
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
            androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this).create();
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
                if (btIsEnabledFlagVoid()){
                    createOneButtonAlertDialog(getResources().getString(R.string.instruction_alert),
                            getResources().getString(R.string.other_discoverable_devices));
                }
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
        // объект Builder для создания диалогового окна
        //TODO
        //ошибка как тут - https://habr.com/ru/company/mobileup/blog/440284/

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this,R.style.dialogTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_simple_tip, null);

        builder.setView(dialogView);
        TextView dialogTitle = dialogView.findViewById(R.id.dialog_tip_title);
        dialogTitle.setText(title);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_tip);
        dialogMessage.setText(content);
        final AlertDialog alertDialog = builder.create();
        MaterialButton buttonToDismiss = dialogView.findViewById(R.id.dialog_tip_ok);
        buttonToDismiss.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    public synchronized void showFabToEnBt(){
        fabToEnBt.show();
    }

    public synchronized void hideFabToEnBt(){
        fabToEnBt.hide();
    }

    public synchronized void showBottomSheet(){
        bottomSheetBehavior.show();
    }

    public synchronized void hideBottomSheet(){
        bottomSheetBehavior.cancel();
    }

    public synchronized void showMainMenu(){
        main_bottom_menu.setVisibility(View.VISIBLE);
    }

    public synchronized void hideMainMenu(){
        main_bottom_menu.setVisibility(View.INVISIBLE);
    }
}