package ru.hse.control_system_v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.hse.control_system_v2.dbdevices.DeviceDBHelper;
import ru.hse.control_system_v2.dbprotocol.ProtocolDBHelper;
import ru.hse.control_system_v2.list_devices.DeviceItemType;


public class DialogDevice extends DialogFragment {
    DeviceDBHelper dbHelper;
    Spinner spinnerProtocol, spinnerClass, spinnerType;
    String newName, name, MAC, protocol, devClass, devType;
    int id;
    AlertDialog.Builder builder;
    ProtocolDBHelper protocolDBHelper;
    ArrayList<String> data;
    List<String> listClasses, listTypes;
    Context c;
    DeviceDBHelper dbdevice;

    DeviceItemType currentDevice;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            c = context;
        }
    }

    public DialogDevice(DeviceItemType currentDevice){
        this.currentDevice = currentDevice;
    }

    void getDeviceInformation(){
        id = currentDevice.getId();
        name = currentDevice.getName();
        MAC = currentDevice.getMAC();
        protocol = currentDevice.getProtocol();
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
        dbdevice = DeviceDBHelper.getInstance(c);
        getDeviceInformation();

        builder = new AlertDialog.Builder(c,R.style.dialogTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_device_menu, null);

        builder.setView(dialogView);
        dbHelper = new DeviceDBHelper(c);
        protocolDBHelper = new ProtocolDBHelper(c);

        data = protocolDBHelper.getProtocolNames();
        listClasses = List.of("class_android", "class_computer", "class_arduino", "no_class");
        listTypes = List.of("type_sphere", "type_anthropomorphic", "type_cubbi", "type_computer", "no_type");
        showDeviceInformation(dialogView);

        final AlertDialog alertDialog = builder.create();
        MaterialButton buttonToConnect = dialogView.findViewById(R.id.dialog_device_connect);
        buttonToConnect.setOnClickListener(view -> {
            if(alertDialog != null && alertDialog.isShowing()){
                alertDialog.dismiss();
            }
            NavHostFragment navHostFragment = (NavHostFragment)((MainActivity) c).getSupportFragmentManager().getPrimaryNavigationFragment();
            assert navHostFragment != null;
            FragmentManager fragmentManager = navHostFragment.getChildFragmentManager();
            Fragment current = fragmentManager.getPrimaryNavigationFragment();
            if(current instanceof MainMenuFragment){
                MainMenuFragment mainMenuFragment = (MainMenuFragment) current;
                mainMenuFragment.showStartOfConnection();
            }
            //запуск подключения происходит ниже
            DeviceHandler.setDevicesList(Collections.singletonList(currentDevice));
            Intent startBluetoothConnectionService = new Intent(c, BluetoothConnectionService.class);
            startBluetoothConnectionService.putExtra("protocol", currentDevice.getProtocol());
            c.startService(startBluetoothConnectionService);
        });

        MaterialButton buttonToDeleteDevice = dialogView.findViewById(R.id.dialog_device_delete);
        buttonToDeleteDevice.setOnClickListener(view -> {
            if(alertDialog != null && alertDialog.isShowing()){
                alertDialog.dismiss();
            }
            dbdevice.deleteDevice(id);
            NavHostFragment navHostFragment = (NavHostFragment)((MainActivity) c).getSupportFragmentManager().getPrimaryNavigationFragment();
            assert navHostFragment != null;
            FragmentManager fragmentManager = navHostFragment.getChildFragmentManager();
            Fragment current = fragmentManager.getPrimaryNavigationFragment();
            if(current instanceof MainMenuFragment){
                MainMenuFragment mainMenuFragment = (MainMenuFragment) current;
                mainMenuFragment.onRefresh();
            }
        });

        MaterialButton buttonToChangeDevice = dialogView.findViewById(R.id.dialog_device_change);
        buttonToChangeDevice.setOnClickListener(view -> {
            if(alertDialog != null && alertDialog.isShowing()){
                alertDialog.dismiss();
            }
            changeDeviceAlert();
        });


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
        alertDialog.show(((MainActivity) c).getSupportFragmentManager(), "dialog");
    }
}
