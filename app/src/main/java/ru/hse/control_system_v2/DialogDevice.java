package ru.hse.control_system_v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.ArrayList;

import ru.hse.control_system_v2.dbdevices.AddDeviceDBActivity;
import ru.hse.control_system_v2.dbdevices.DeviceDBHelper;
import ru.hse.control_system_v2.dbprotocol.ProtocolDBHelper;
import ru.hse.control_system_v2.list_devices.DeviceItem;


public class DialogDevice extends DialogFragment {
    DeviceDBHelper dbHelper;
    Spinner spinnerProtocol;
    String newName, name, MAC, protocol, devClass, devType;
    int id;
    AlertDialog.Builder builder;
    ProtocolDBHelper protocolDBHelper;
    ArrayList<String> data;
    Context c;
    DeviceDBHelper dbdevice;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            c = context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dbdevice = DeviceDBHelper.getInstance(c);
        id = MainActivity.currentDevice.getId();
        name = MainActivity.currentDevice.getName();
        MAC = MainActivity.currentDevice.getMAC();
        protocol = MainActivity.currentDevice.getProtocol();
        devClass = MainActivity.currentDevice.getDevClass();
        devType = MainActivity.currentDevice.getDevType();
        builder = new AlertDialog.Builder(getActivity());
        dbHelper = new DeviceDBHelper(requireActivity());
        protocolDBHelper = new ProtocolDBHelper(requireActivity());

        data = protocolDBHelper.getProtocolNames();

        return builder.setTitle(getResources().getString(R.string.alert_info))
                .setMessage(getResources().getString(R.string.alert_device_name) + name + "\n" + getResources().getString(R.string.alert_MAC) + MAC+ "\n" + getResources().getString(R.string.alert_class) + devClass + "\n"
                        + getResources().getString(R.string.alert_type) + devType + "\n" + getResources().getString(R.string.alert_protocol) + protocol)
                .setPositiveButton(getResources().getString(R.string.loading_label), (dialog, whichButton) -> {
                    //запуск подключения происходит ниже
                    MainActivity.currentDevice.startBluetoothConnectionService(c);
                })
                .setNegativeButton(getResources().getString(R.string.alert_delete), (dialog, whichButton) -> {
                            dbdevice.deleteDevice(id);
                            ((MainActivity) c).onRefresh();}
                    )
                .setOnDismissListener(dialogInterface -> {
                    //action when dialog is dismissed goes here
                    //nothing now
                })
                .setNeutralButton(getResources().getString(R.string.alert_change), (dialog, which) -> {
                    changeDeviceAlert();
                })
                .create();
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
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_spinner_item, data);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProtocol = new Spinner(c);
        EditText editTextNameAlert = new EditText(c);
        spinnerProtocol.setAdapter(spinnerAdapter);
        editTextNameAlert.setText(name);
        editTextNameAlert.setInputType(InputType.TYPE_CLASS_TEXT);
        editTextNameAlert.setHint(getResources().getString(R.string.alert_device_name_tint));
        for(int i = 0; i<data.size(); i++){
            if (data.get(i).equals(protocol)){
                spinnerProtocol.setSelection(i);
                break;
            }
        }
        AlertDialog.Builder setSettingsToDeviceAlertDialog =
                new AlertDialog.Builder((MainActivity) c);
        setSettingsToDeviceAlertDialog.setTitle(getResources().getString(R.string.alert_editing));
        //alert_editing

        LinearLayout layout = new LinearLayout(c);
        layout.setOrientation(LinearLayout.VERTICAL);
        if(spinnerProtocol.getParent() != null) {
            ((ViewGroup)spinnerProtocol.getParent()).removeView(spinnerProtocol); // <- fix crash
        }
        layout.addView(editTextNameAlert);
        layout.addView(spinnerProtocol);
        setSettingsToDeviceAlertDialog.setView(layout);
        setSettingsToDeviceAlertDialog.setPositiveButton(getResources().getString(R.string.alert_save), (dialogInterface, i) -> {
            newName = editTextNameAlert.getText().toString();
            protocol = data.get((int) spinnerProtocol.getSelectedItemId());

            ContentValues contentValues = new ContentValues();
            contentValues.put(DeviceDBHelper.KEY_MAC, MAC);
            contentValues.put(DeviceDBHelper.KEY_NAME, newName);
            contentValues.put(DeviceDBHelper.KEY_PROTO, protocol);
            dbHelper.update(contentValues, id);
            dbHelper.viewData();
            //Обновление MainActivity
            ((MainActivity) c).onRefresh();

        });
        setSettingsToDeviceAlertDialog.setNegativeButton(getResources().getString(R.string.cancel_add_bd_label), (dialogInterface, i) -> {
            dialogInterface.cancel();
        });
        setSettingsToDeviceAlertDialog.create();
        setSettingsToDeviceAlertDialog.show();
    }
}
