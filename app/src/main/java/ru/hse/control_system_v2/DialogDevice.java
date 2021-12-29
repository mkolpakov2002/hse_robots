package ru.hse.control_system_v2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Iterables;

import java.util.ArrayList;
import ru.hse.control_system_v2.list_devices.DeviceItemType;


public class DialogDevice extends DialogFragment {
    private String name, MAC, protocol, devClass, devType;
    private int id;
    private Context c;
    private DeviceItemType currentDevice;
    private MainActivity ma;
    private AlertDialog alertDialog;

    public DialogDevice(){
        //nothing
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            ma = (MainActivity) context;
        }
        c = context;
    }

    void getDeviceInformation(){
        currentDevice = DeviceHandler.getDevicesList().get(0);
        id = currentDevice.getDevId();
        name = currentDevice.getDevName();
        MAC = currentDevice.getDeviceMAC();
        protocol = currentDevice.getDevProtocol();
        devClass = currentDevice.getDevClass();
        devType = currentDevice.getDevType();
    }

    void showDeviceInformation(View dialogView){
        TextView deviceNameView = dialogView.findViewById(R.id.device_name);
        deviceNameView.setText(name);
        TextView deviceMACView = dialogView.findViewById(R.id.device_mac);
        deviceMACView.setText(MAC);
        TextView deviceClassView = dialogView.findViewById(R.id.device_class);
        deviceClassView.setText(devClass);
        TextView deviceTypeView = dialogView.findViewById(R.id.device_type);
        deviceTypeView.setText(devType);
        TextView deviceProtoView = dialogView.findViewById(R.id.device_proto);
        deviceProtoView.setText(protocol);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        getDeviceInformation();
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(c,R.style.AlertDialogStyle);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_device_menu, null);

        builder.setView(dialogView);
        showDeviceInformation(dialogView);

        builder.setTitle(getResources().getString(R.string.alert_info));
        builder.setPositiveButton(getResources().getString(R.string.alert_connect), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainMenuFragment mainMenuFragment = ma.getMainMenuFragment();
                if(mainMenuFragment != null){
                    mainMenuFragment.onRefresh();
                    //запуск подключения происходит ниже
                    if(alertDialog.isShowing()){
                        alertDialog.dismiss();
                    }
                    mainMenuFragment.showBottomSheetToConnect();
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.alert_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(alertDialog.isShowing()){
                    alertDialog.dismiss();
                }
                AppDataBase dbDevices = App.getDatabase();
                DeviceItemTypeDao devicesDao = dbDevices.getDeviceItemTypeDao();
                devicesDao.delete(id);
                MainMenuFragment mainMenuFragment = ma.getMainMenuFragment();
                if(mainMenuFragment != null){
                    mainMenuFragment.onRefresh();
                }
            }
        });
        builder.setNeutralButton(getResources().getString(R.string.alert_change), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(alertDialog.isShowing()){
                    alertDialog.dismiss();
                }
                changeDeviceAlert();
            }
        });
        alertDialog = builder.create();

        return alertDialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) {return;}
        String message = data.getStringExtra("message");
        Bundle args = new Bundle();
        args.putString("message", message);
        setArguments(args);
    }

    void changeDeviceAlert(){
        DialogDeviceEdit alertDialog = new DialogDeviceEdit(currentDevice,null);
        Bundle args = new Bundle();
        alertDialog.setArguments(args);
        //fragment.currentDevice = item;
        alertDialog.show((ma).getSupportFragmentManager(), "dialog");
    }
}
