package ru.hse.control_system_v2;

import static ru.hse.control_system_v2.Constants.APP_LOG_TAG;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.hse.control_system_v2.dbprotocol.ProtocolDBHelper;
import ru.hse.control_system_v2.dbprotocol.ProtocolRepo;
import ru.hse.control_system_v2.list_devices.DeviceItemType;

public class ConnectionActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    private boolean isHoldCommand;
    private byte[] message;      // комманда посылаемая на arduino
    private byte prevCommand = 0;
    private ArrayList<ConnectionThread> dataThreadForArduinoList;
    private List<DeviceItemType> devicesList;
    private ArrayList<DeviceItemType> disconnectedDevicesList;
    private TextView outputText;
    ProtocolRepo getDevicesID;
    private int countCommands;
    private int lengthMes;
    private boolean active;
    private Resources res;
    MaterialAlertDialogBuilder materialAlertDialogBuilder;
    Dialog disconnectedDialog;
    Dialog networkDialog;
    boolean isBtService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_connection);
        Bundle b = getIntent().getExtras();
        isBtService = b.getBoolean("isBtService");

        disconnectedDevicesList = new ArrayList<>();
        devicesList = new ArrayList<>();
        devicesList = DeviceHandler.getDevicesList();
        checkForActiveDevices();
        if(devicesList.size()>0){
            initializeData();
        } else {
            addDisconnectedDevice();
        }
    }

    void initializeData(){
        registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(mReceiver,new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        String devProtocol = devicesList.get(0).getDevProtocol();
        findViewById(R.id.button_stop_bt).setEnabled(false);
        outputText = findViewById(R.id.incoming_data_bt);
        outputText.setMovementMethod(new ScrollingMovementMethod());

        dataThreadForArduinoList = new ArrayList<>();
        outputText.append("\n" + getResources().getString(R.string.bluetooth_device_activity_connected_first) + " " + devicesList.size() + " " + getResources().getString(R.string.bluetooth_device_activity_from) + " " + (devicesList.size() + disconnectedDevicesList.size()) + " " + getResources().getString(R.string.devices_title));
        outputText.append("\n" + getResources().getString(R.string.bluetooth_device_activity_list_of_connections));
        for (int i = 0; i < devicesList.size(); i++) {
            outputText.append("\n" + getResources().getString(R.string.device_title) + " " + devicesList.get(i).getDevName() + " " + getResources().getString(R.string.bluetooth_device_activity_connected_second));
            ConnectionThread bluetoothDataThreadForArduino = new ConnectionThread(this, devicesList.get(i), isBtService);
            dataThreadForArduinoList.add(bluetoothDataThreadForArduino);
            dataThreadForArduinoList.get(i).start();
        }

        res = getResources();
        isHoldCommand = false;

        getDevicesID = new ProtocolRepo(getApplicationContext(), devProtocol);
        ProtocolDBHelper protocolDBHelper = ProtocolDBHelper.getInstance(getApplicationContext());
        lengthMes = protocolDBHelper.getLength(devProtocol);
        message = new byte[lengthMes];
        countCommands = 0;

        findViewById(R.id.button_up_bt).setOnTouchListener(touchListener);
        findViewById(R.id.button_down_bt).setOnTouchListener(touchListener);
        findViewById(R.id.button_left_bt).setOnTouchListener(touchListener);
        findViewById(R.id.button_right_bt).setOnTouchListener(touchListener);
        findViewById(R.id.button_stop_bt).setOnClickListener(this);

        SwitchMaterial hold_command = findViewById(R.id.switch_hold_command_mm_Bt);
        hold_command.setOnCheckedChangeListener(this);
        hold_command.setChecked(false);

        Arrays.fill(message, (byte) 0);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive (Context context, Intent intent) {
            if((isBtService && !App.isBtEnabled()) ||
                    (!isBtService && !App.isWiFiEnabled())){
                addDisconnectedDevice();
            }
        }};

    public synchronized void printDataToTextView(String printData) {
        Log.d(APP_LOG_TAG, "Печатаемое сообщение в DeviceActivity: " + printData);
        outputText.append("\n" + "---" + "\n" + printData);
    }

    public synchronized boolean isActive() {
        return active;
    }

    synchronized void checkForActiveDevices() {
        for (DeviceItemType currentDevice : devicesList) {
            if (!currentDevice.isWiFiBtConnected()) {
                disconnectedDevicesList.add(currentDevice);
            }
        }
        for (DeviceItemType currentDevice : disconnectedDevicesList) {
            if (currentDevice.isWiFiBtConnected()) {
                devicesList.add(currentDevice);
            }
        }
        devicesList.removeIf(currentDevice -> !currentDevice.isWiFiBtConnected());
        disconnectedDevicesList.removeIf(DeviceItemType::isWiFiBtConnected);
    }

    public synchronized void addDisconnectedDevice() {
        ArrayList<DeviceItemType> current = disconnectedDevicesList;
        checkForActiveDevices();
        if((disconnectedDialog == null || !disconnectedDialog.isShowing())
                && ((isBtService && App.isBtEnabled()) ||
                (!isBtService && App.isWiFiEnabled()))
                && disconnectedDevicesList!=current){
            materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
            materialAlertDialogBuilder.setTitle(getString(R.string.error));
            if(disconnectedDevicesList.size()==1){
                materialAlertDialogBuilder.setMessage("Устройство " + devicesList.get(0).getDevName() + "отключилось. Продолжить работу?");
            } else {
                materialAlertDialogBuilder.setMessage("Некоторые устройства отключились. Продолжить работу?");
            }
            materialAlertDialogBuilder.setPositiveButton("Продолжить работу", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    addDisconnectedDevice();
                    dialogInterface.dismiss();
                }
            });
            materialAlertDialogBuilder.setNegativeButton("Выйти", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            disconnectedDialog = materialAlertDialogBuilder.show();
        } else if (((isBtService && !App.isBtEnabled()) || (!isBtService && !App.isWiFiEnabled()))
                && (networkDialog== null || !networkDialog.isShowing())){
            if(networkDialog != null && disconnectedDialog.isShowing())
                disconnectedDialog.dismiss();
            materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
            materialAlertDialogBuilder.setTitle(getString(R.string.error));
            materialAlertDialogBuilder.setMessage("Сеть отключена. Дальнейшее управление невозможно.");
            materialAlertDialogBuilder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            materialAlertDialogBuilder.setCancelable(false);
            networkDialog = materialAlertDialogBuilder.show();
        } else {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        active = true;
        checkForActiveDevices();

        //arduino.BluetoothConnectionServiceVoid();     // соединяемся с bluetooth
        //TODO - вызывает вылет приложения
    }

    @Override
    protected void onPause() {

        super.onPause();
        active = false;
        completeDevicesInfo();

        if (getDevicesID.getTag(res.getString(R.string.TAG_TURN_COM)))
            message[countCommands++] = getDevicesID.get("new_command");

        if (getDevicesID.getTag(res.getString(R.string.TAG_TYPE_COM)))
            message[countCommands++] = getDevicesID.get("type_move");

        message[countCommands++] = getDevicesID.get("STOP");
        for (int i = 0; i < dataThreadForArduinoList.size(); i++) {
            Log.d(APP_LOG_TAG, "DeviceActivity в onPause");
            dataThreadForArduinoList.get(i).sendData(message, lengthMes);
        }

        for (int i = 0; i < dataThreadForArduinoList.size(); i++) {
            dataThreadForArduinoList.get(i).Disconnect();
        }
    }

    @Override
    public void onClick(View v) {
        completeDevicesInfo();
        switch (v.getId()) {
            case R.id.button_stop_bt:
                outputText.append("\n" + getResources().getString(R.string.send_command_stop));
                completeMessage("STOP");
                countCommands = 0;
                break;
        }
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            completeDevicesInfo();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // если нажали на кнопку и не важно есть удержание команд или нет
                switch (v.getId()) {
                    case R.id.button_up_bt:
                        Log.d(APP_LOG_TAG, "Отправляю команду движения вперёд;");
                        outputText.append("\n" + getResources().getString(R.string.send_command_forward));
                        completeMessage("FORWARD");
                        countCommands = 0;
                        break;
                    case R.id.button_down_bt:
                        outputText.append("\n" + getResources().getString(R.string.send_command_back));
                        Log.d(APP_LOG_TAG, "Отправляю команду движения назад;");
                        //Toast.makeText(getApplicationContext(), "Назад поехали", Toast.LENGTH_SHORT).show();
                        completeMessage("BACK");
                        countCommands = 0;
                        break;
                    case R.id.button_left_bt:
                        outputText.append("\n" + getResources().getString(R.string.send_command_left));
                        //Toast.makeText(getApplicationContext(), "Влево поехали", Toast.LENGTH_SHORT).show();
                        Log.d(APP_LOG_TAG, "Отправляю команду движения влево;");
                        completeMessage("LEFT");
                        countCommands = 0;
                        break;
                    case R.id.button_right_bt:
                        //Toast.makeText(getApplicationContext(), "Вправо поехали", Toast.LENGTH_SHORT).show();
                        outputText.append("\n" + getResources().getString(R.string.send_command_right));
                        Log.d(APP_LOG_TAG, "Отправляю команду движения вправо;");
                        completeMessage("RIGHT");
                        countCommands = 0;
                        break;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                // если отпустили кнопку
                if (!isHoldCommand) {
                    // и нет удержания команд то все кнопки отправляют команду стоп
                    outputText.append("\n" + getResources().getString(R.string.send_command_button_released));
                    switch (v.getId()) {
                        case R.id.button_up_bt:
                            completeMessage("FORWARD_STOP");
                            countCommands = 0;
                            break;
                        case R.id.button_down_bt:
                            completeMessage("BACK_STOP");
                            countCommands = 0;
                            break;
                        case R.id.button_left_bt:
                            completeMessage("LEFT_STOP");
                            countCommands = 0;
                            break;
                        case R.id.button_right_bt:
                            completeMessage("RIGHT_STOP");
                            countCommands = 0;
                            break;
                    }
                    Log.d(APP_LOG_TAG, "Количество посылаемых команд " + countCommands);
                }
            }
            return false;
        }
    };

    public void completeDevicesInfo() {
        countCommands = 0;
        if (getDevicesID.getTag(res.getString(R.string.TAG_CLASS_FROM)))
            message[countCommands++] = getDevicesID.get("class_android");

        if (getDevicesID.getTag(res.getString(R.string.TAG_TYPE_FROM)))
            message[countCommands++] = getDevicesID.get("type_computer"); // класс и тип устройства отправки

        if (getDevicesID.getTag(res.getString(R.string.TAG_CLASS_TO)))
            message[countCommands++] = getDevicesID.get(devicesList.get(0).getDevClass());

        if (getDevicesID.getTag(res.getString(R.string.TAG_TYPE_TO)))
            message[countCommands++] = getDevicesID.get(devicesList.get(0).getDevType());// класс и тип устройства приема
    }

    public void completeMessage(String command) {

        Byte code = getDevicesID.get(command);
        if (code != null) {
            if (getDevicesID.getTag(res.getString(R.string.TAG_TURN_COM))) {
                message[countCommands++] = (prevCommand == code) ? getDevicesID.get("redo_command") : getDevicesID.get("new_command");
                prevCommand = code;
            }

            if (getDevicesID.getTag(res.getString(R.string.TAG_TYPE_COM)))
                message[countCommands++] = getDevicesID.get("type_move");
            message[countCommands++] = code;

            for (int i = 0; i < dataThreadForArduinoList.size(); i++) {
                dataThreadForArduinoList.get(i).sendData(message, lengthMes);
            }
        } else {
            outputText.append("\n" + getResources().getString(R.string.send_command_insufficient_data));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_hold_command_mm_Bt:
                isHoldCommand = isChecked;
                if (isHoldCommand) {
                    outputText.append("\n" + getResources().getString(R.string.send_command_hold_enabled));
                    findViewById(R.id.button_stop_bt).setEnabled(true);
                } else {
                    outputText.append("\n" + getResources().getString(R.string.send_command_hold_disabled));
                    findViewById(R.id.button_stop_bt).setEnabled(false);
                }
                break;
        }
    }


    // Метод для вывода всплывающих данных на экран
    public void showToast(String outputInfoString) {
        Toast outputInfoToast = Toast.makeText(this, outputInfoString, Toast.LENGTH_SHORT);
        outputInfoToast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
        for (int i = 0; i < devicesList.size(); i++) {
            Log.d(APP_LOG_TAG, "DeviceActivity в onDestroy, отключение устройств");
            devicesList.get(i).closeConnection();

        }
    }
}
