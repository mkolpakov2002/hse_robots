package ru.hse.control_system_v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import ru.hse.control_system_v2.dbdevices.AddDeviceDBActivity;
import ru.hse.control_system_v2.dbdevices.DeviceDBHelper;
import ru.hse.control_system_v2.dbprotocol.ProtocolDBHelper;
import ru.hse.control_system_v2.list_devices.DeviceItemType;

public class DialogDeviceEdit extends DialogFragment {

    Context c;
    Spinner spinnerProtocol, spinnerClass, spinnerType;
    ArrayList<String> data;
    List<String> listClasses, listTypes;
    ProtocolDBHelper protocolDBHelper;
    DeviceItemType currentDevice;
    String newName, name, MAC, protocol, devClass, devType;
    int id;
    DeviceDBHelper dbHelper;
    boolean isNewDev = false;
    EditText editTextNameAlert;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            c = context;
        }
    }

    void getDeviceInformation(){
        id = currentDevice.getId();
        name = currentDevice.getName();
        MAC = currentDevice.getMAC();
        protocol = currentDevice.getProtocol();
        devClass = currentDevice.getDevClass();
        devType = currentDevice.getDevType();
    }

    public DialogDeviceEdit(DeviceItemType currentDevice, String MAC){
        if(currentDevice == null){
            isNewDev = true;
            this.MAC = MAC;
        } else {
            this.currentDevice = currentDevice;
            getDeviceInformation();
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        dbHelper = new DeviceDBHelper(c);
        protocolDBHelper = new ProtocolDBHelper(c);
        data = protocolDBHelper.getProtocolNames();
        listClasses = List.of("class_android", "class_computer", "class_arduino", "no_class");
        listTypes = List.of("type_sphere", "type_anthropomorphic", "type_cubbi", "type_computer", "no_type");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_device, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(c,R.style.dialogTheme);
        builder.setView(dialogView);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(c, android.R.layout.simple_spinner_item, data);


        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProtocol = dialogView.findViewById(R.id.spinner_proto);
        editTextNameAlert = dialogView.findViewById(R.id.editDeviceName);
        spinnerProtocol.setAdapter(spinnerAdapter);

        ArrayAdapter<String> adapterClass = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_item, listClasses);
        adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass = dialogView.findViewById(R.id.spinner_class);
        spinnerClass.setAdapter(adapterClass);

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerType.setEnabled("class_arduino".equals(listClasses.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerType.setEnabled(false);
            }
        });

        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_item, listTypes);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType = dialogView.findViewById(R.id.spinner_type);
        spinnerType.setAdapter(adapterType);

        if(!isNewDev){
            editTextNameAlert.setText(name);
            for(int i = 0; i<data.size(); i++){
                if (data.get(i).equals(protocol)){
                    spinnerProtocol.setSelection(i);
                    break;
                }
            }
            for(int i = 0; i<listClasses.size(); i++){
                if (listClasses.get(i).equals(devClass)){
                    spinnerClass.setSelection(i);
                    break;
                }
            }
            for(int i = 0; i<listTypes.size(); i++){
                if (listTypes.get(i).equals(devType)){
                    spinnerType.setSelection(i);
                    break;
                }
            }
        }

        editTextNameAlert.setInputType(InputType.TYPE_CLASS_TEXT);
        editTextNameAlert.setHint(getResources().getString(R.string.alert_device_name_tint));
        editTextNameAlert.addTextChangedListener(new TextChangedListener<EditText>(editTextNameAlert) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                if(s.length() == 0){
                    editTextNameAlert.requestFocus();
                    target.setError(getString(R.string.error_empty));
                }
            }
        });

        MaterialButton buttonToCancelChanges = dialogView.findViewById(R.id.dialog_edit_cancel);
        MaterialButton buttonToSaveChanges = dialogView.findViewById(R.id.dialog_edit_save);

        AlertDialog alertDialog = builder.create();

        buttonToCancelChanges.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        buttonToSaveChanges.setOnClickListener(view -> {
            if(editTextNameAlert.getText().length()==0){
                editTextNameAlert.requestFocus();
                editTextNameAlert.setError(getString(R.string.error_empty));
            } else {
                alertDialog.dismiss();
                if(isNewDev){
                    saveNewDevice();
                } else {
                    saveOldDevice();
                }
            }


        });
        return alertDialog;
    }

    void saveOldDevice(){
        newName = editTextNameAlert.getText().toString();
        protocol = data.get((int) spinnerProtocol.getSelectedItemId());
        String classDevice = listClasses.get((int) spinnerClass.getSelectedItemId());
        String typeDevice;
        if (classDevice.equals("class_arduino"))
            typeDevice = listTypes.get((int) spinnerType.getSelectedItemId());
        else
            typeDevice = "no_type";

        ContentValues contentValues = new ContentValues();
        contentValues.put(DeviceDBHelper.KEY_MAC, MAC);
        contentValues.put(DeviceDBHelper.KEY_NAME, newName);
        contentValues.put(DeviceDBHelper.KEY_PROTO, protocol);
        contentValues.put(DeviceDBHelper.KEY_CLASS, classDevice);
        contentValues.put(DeviceDBHelper.KEY_TYPE, typeDevice);

        dbHelper.update(contentValues, id);
        dbHelper.viewData();
        //Обновление MainActivity
        NavHostFragment navHostFragment = (NavHostFragment)((MainActivity) c).getSupportFragmentManager().getPrimaryNavigationFragment();
        assert navHostFragment != null;
        FragmentManager fragmentManager = navHostFragment.getChildFragmentManager();

        Fragment current = fragmentManager.getPrimaryNavigationFragment();
        if(current instanceof MainMenuFragment){
            MainMenuFragment mainMenuFragment = (MainMenuFragment) current;
            mainMenuFragment.onRefresh();
        }
    }

    void saveNewDevice(){
        DeviceDBHelper deviceDBHelper = new DeviceDBHelper(c);
        name = editTextNameAlert.getText().toString();
        protocol = data.get((int) spinnerProtocol.getSelectedItemId());
        String classDevice = listClasses.get((int) spinnerClass.getSelectedItemId());
        String typeDevice;
        if (classDevice.equals("class_arduino"))
            typeDevice = listTypes.get((int) spinnerType.getSelectedItemId());
        else
            typeDevice = "no_type";

        if (BluetoothAdapter.checkBluetoothAddress(MAC)) {
            ContentValues contentValues = new ContentValues();

            contentValues.put(DeviceDBHelper.KEY_MAC, MAC);
            contentValues.put(DeviceDBHelper.KEY_NAME, name);
            contentValues.put(DeviceDBHelper.KEY_PROTO, protocol);
            contentValues.put(DeviceDBHelper.KEY_CLASS, classDevice);
            contentValues.put(DeviceDBHelper.KEY_TYPE, typeDevice);

            int res = deviceDBHelper.insert(contentValues);
            if (res == 1) {
                Toast.makeText(c, "Accepted", Toast.LENGTH_LONG).show();
                Log.d("Add device", "Device accepted");
            }
            else {
                Toast.makeText(c, "MAC has already been registered", Toast.LENGTH_LONG).show();
                Log.d("Add device", "MAC is in database");
            }
            deviceDBHelper.viewData();
        }
        else {
            Toast.makeText(c, "Wrong MAC address", Toast.LENGTH_LONG).show();
            Log.d("Add device", "Device denied");
        }
        //Обновление MainActivity
        NavHostFragment navHostFragment = (NavHostFragment)((MainActivity) c).getSupportFragmentManager().getPrimaryNavigationFragment();
        assert navHostFragment != null;
        FragmentManager fragmentManager = navHostFragment.getChildFragmentManager();

        Fragment current = fragmentManager.getPrimaryNavigationFragment();
        if(current instanceof MainMenuFragment){
            MainMenuFragment mainMenuFragment = (MainMenuFragment) current;
            mainMenuFragment.onRefresh();
        }
        //TODO
        ((AddDeviceDBActivity) c).exitActivity();
    }

}
