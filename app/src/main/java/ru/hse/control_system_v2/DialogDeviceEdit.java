package ru.hse.control_system_v2;

import static ru.hse.control_system_v2.Constants.APP_LOG_TAG;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.hse.control_system_v2.dbdevices.AddDeviceDBActivity;
import ru.hse.control_system_v2.dbprotocol.ProtocolDBHelper;
import ru.hse.control_system_v2.list_devices.DeviceItemType;

public class DialogDeviceEdit extends DialogFragment {

    private Context c;
    private Spinner spinnerProtocol, spinnerClass, spinnerType;
    private ArrayList<String> data;
    private List<String> listClasses, listTypes;
    private DeviceItemType currentDevice;
    private String name;
    private String MAC;
    private String protocol;
    private String devClass;
    private String devType;
    private String devIp;
    private String devPort;
    private boolean isNewDev = false;
    private EditText editTextNameAlert;
    private EditText editTextIpAlert;
    private EditText editTextPortAlert;
    private MainActivity ma;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity){
            ma = (MainActivity) context;
        }
        c = context;
    }

    void getDeviceInformation(){
        name = currentDevice.getDevName();
        MAC = currentDevice.getDeviceMAC();
        protocol = currentDevice.getDevProtocol();
        devClass = currentDevice.getDevClass();
        devType = currentDevice.getDevType();
        devIp = currentDevice.getDevIp();
        devPort = String.valueOf(currentDevice.getDevPort());
    }

    public DialogDeviceEdit(){
        //nothing
    }
    public DialogDeviceEdit(DeviceItemType currentDevice, String deviceInfo){
        if(currentDevice == null){
            isNewDev = true;
            //Get information from List View in String
            int i = deviceInfo.indexOf(':');
            i = i - 2;
            //В текущем пункте List View находим первый символ ":", всё после него, а также два символа до него - адрес выбранного устройства
            this.MAC = deviceInfo.substring(i);
            if(i==0){
                this.name = "";
            } else {
                this.name = deviceInfo.substring(0,i-1);
            }
        } else {
            this.currentDevice = currentDevice;
            getDeviceInformation();
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        ProtocolDBHelper protocolDBHelper = new ProtocolDBHelper(c);
        data = protocolDBHelper.getProtocolNames();
        listClasses = Arrays.asList("class_android", "class_computer", "class_arduino", "no_class");
        listTypes = Arrays.asList("type_sphere", "type_anthropomorphic", "type_cubbi", "type_computer", "no_type");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_device, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(c, R.style.AlertDialogStyle);
        builder.setView(dialogView);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(c, android.R.layout.simple_spinner_item, data);


        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProtocol = dialogView.findViewById(R.id.spinner_proto);
        editTextNameAlert = dialogView.findViewById(R.id.editDeviceName);
        editTextIpAlert = dialogView.findViewById(R.id.editDeviceIp);
        editTextPortAlert = dialogView.findViewById(R.id.editDevicePort);
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
        editTextNameAlert.setText(name);
        editTextIpAlert.setText(devIp);
        editTextPortAlert.setText(devPort);

        if(!isNewDev){
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
        editTextNameAlert.addTextChangedListener(new TextChangedListener<>(editTextNameAlert) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                if(s.length() == 0){
                    editTextNameAlert.requestFocus();
                    target.setError(getString(R.string.error_empty));
                }
            }
        });

        editTextIpAlert.setInputType(InputType.TYPE_CLASS_DATETIME);
        editTextIpAlert.setHint(getResources().getString(R.string.alert_device_ip_hint));
        editTextIpAlert.addTextChangedListener(new TextChangedListener<>(editTextIpAlert) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                if(s.length() == 0){
                    editTextIpAlert.requestFocus();
                    target.setError(getString(R.string.error_empty));
                }
            }
        });

        editTextPortAlert.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextPortAlert.setHint(getResources().getString(R.string.alert_device_port_tint));
        editTextPortAlert.addTextChangedListener(new TextChangedListener<>(editTextPortAlert) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                if(s.length() == 0){
                    editTextPortAlert.requestFocus();
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
            boolean isPortAccepted;
            try {
                devPort = editTextPortAlert.getText().toString();
                int devPortInt = Integer.parseInt(devPort);
                isPortAccepted = true;
            }
            catch (NumberFormatException e) {
                isPortAccepted = false;
            }

            if(editTextNameAlert.getText().length()==0){
                editTextNameAlert.requestFocus();
                editTextNameAlert.setError(getString(R.string.error_empty));
            } else if(editTextIpAlert.getText().length()==0 ||
                    Patterns.IP_ADDRESS
                            .matcher(editTextIpAlert.getText().toString()).matches()){
                editTextIpAlert.requestFocus();
                editTextIpAlert.setError(getString(R.string.error_empty));
            } else if(editTextPortAlert.getText().length()==0){
                editTextPortAlert.requestFocus();
                editTextPortAlert.setError(getString(R.string.error_empty));
            } else if(!isPortAccepted){
                editTextPortAlert.requestFocus();
                editTextPortAlert.setError("Неверный номер порта");
            } else {
                devIp = editTextIpAlert.getText().toString();
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
        String newName = editTextNameAlert.getText().toString();
        protocol = data.get((int) spinnerProtocol.getSelectedItemId());
        String classDevice = listClasses.get((int) spinnerClass.getSelectedItemId());
        String typeDevice;
        if (classDevice.equals("class_arduino"))
            typeDevice = listTypes.get((int) spinnerType.getSelectedItemId());
        else
            typeDevice = "no_type";

        AppDataBase dbDevices = App.getDatabase();
        DeviceItemTypeDao devicesDao = dbDevices.getDeviceItemTypeDao();
        currentDevice.setDevName(newName);
        currentDevice.setDeviceMAC(MAC);
        currentDevice.setDevClass(classDevice);
        currentDevice.setDevType(typeDevice);
        currentDevice.setDevProtocol(protocol);
        currentDevice.setDevIp(devIp);
        currentDevice.setDevPort(Integer.parseInt(devPort));
        devicesDao.update(currentDevice);
        //Обновление MainActivity
        MainMenuFragment mainMenuFragment = ma.getMainMenuFragment();
        if(mainMenuFragment != null){
            mainMenuFragment.onRefresh();
        }
    }

    void saveNewDevice(){
        name = editTextNameAlert.getText().toString();
        protocol = data.get((int) spinnerProtocol.getSelectedItemId());
        String classDevice = listClasses.get((int) spinnerClass.getSelectedItemId());
        String typeDevice;
        if (classDevice.equals("class_arduino"))
            typeDevice = listTypes.get((int) spinnerType.getSelectedItemId());
        else
            typeDevice = "no_type";

        if (BluetoothAdapter.checkBluetoothAddress(MAC)) {

            AppDataBase dbDevices = App.getDatabase();
            DeviceItemTypeDao devicesDao = dbDevices.getDeviceItemTypeDao();
            List<DeviceItemType> devicesWithMAC;
            devicesWithMAC = devicesDao.getAllDevicesWithSuchMAC(MAC);
            if(devicesWithMAC.size()!=0){
                Toast.makeText(c, "Already added MAC address", Toast.LENGTH_LONG).show();
                Log.d("Add device", "Device denied");
            } else {
                currentDevice = new DeviceItemType(name,MAC,protocol,classDevice,typeDevice,devIp,Integer.parseInt(devPort));
                devicesDao.insertAll(currentDevice);
            }
        }
        else {
            Toast.makeText(c, "Wrong MAC address", Toast.LENGTH_LONG).show();
            Log.d("Add device", "Device denied");
        }

        //Обновление MainActivity
        if(!isNewDev){
            MainMenuFragment mainMenuFragment = ma.getMainMenuFragment();
            if(mainMenuFragment != null){
                mainMenuFragment.onRefresh();
            }
        }

        ((AddDeviceDBActivity) c).exitActivity();
    }

}
