package ru.hse.control_system_v2;

import static ru.hse.control_system_v2.Constants.APP_LOG_TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.hse.control_system_v2.dbprotocol.ProtocolDBHelper;
import ru.hse.control_system_v2.dbprotocol.ProtocolRepo;
import ru.hse.control_system_v2.list_devices.DeviceItemType;

public class WiFiDeviceActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private boolean isHoldCommand;
    private byte[] message;      // комманда посылаемая на arduino
    private byte prevCommand = 0;
    private ArrayList<WiFiDataThread> wifiDataThreadForArduinoList;
    private List<DeviceItemType> devicesList;
    private ArrayList<DeviceItemType> disconnectedDevicesList;
    private TextView outputText;
    ProtocolRepo getDevicesID;
    private int countCommands;
    private int lengthMes;
    private boolean active;
    private Resources res;

    private ImageButton buttonUp;
    private ImageButton buttonDown;
    private ImageButton buttonLeft;
    private ImageButton buttonRight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_wifi_device);
        findViewById(R.id.button_stop).setEnabled(false);

        disconnectedDevicesList = new ArrayList<>();
        devicesList = new ArrayList<>();
        devicesList = DeviceHandler.getDevicesList();
        checkForActiveDevices();

        outputText = findViewById(R.id.incoming_data);
        outputText.setMovementMethod(new ScrollingMovementMethod());
        String devProtocol = devicesList.get(0).getDevProtocol();


        wifiDataThreadForArduinoList = new ArrayList<>();
        outputText.append("\n" + getResources().getString(R.string.bluetooth_device_activity_connected_first) + " " + devicesList.size() + " " + getResources().getString(R.string.bluetooth_device_activity_from) + " " + (devicesList.size() + disconnectedDevicesList.size()) + " " + getResources().getString(R.string.devices_title));
        outputText.append("\n" + getResources().getString(R.string.bluetooth_device_activity_list_of_connections));
        for (int i = 0; i < devicesList.size(); i++) {
            outputText.append("\n" + getResources().getString(R.string.device_title) + " " + devicesList.get(i).getDevName() + " " + getResources().getString(R.string.bluetooth_device_activity_connected_second));
            WiFiDataThread bluetoothDataThreadForArduino = new WiFiDataThread(this, devicesList.get(i));
            wifiDataThreadForArduinoList.add(bluetoothDataThreadForArduino);
            wifiDataThreadForArduinoList.get(i).start();
        }

        res = getResources();
        isHoldCommand = false;

        getDevicesID = new ProtocolRepo(getApplicationContext(), devProtocol);
        ProtocolDBHelper protocolDBHelper = ProtocolDBHelper.getInstance(getApplicationContext());
        lengthMes = protocolDBHelper.getLength(devProtocol);
        message = new byte[lengthMes];
        countCommands = 0;

        buttonUp = findViewById(R.id.button_up);
        buttonUp.setOnTouchListener(touchListener);
        buttonDown = findViewById(R.id.button_down);
        buttonDown.setOnTouchListener(touchListener);
        buttonLeft = findViewById(R.id.button_left);
        buttonLeft.setOnTouchListener(touchListener);
        buttonRight = findViewById(R.id.button_right);
        buttonRight.setOnTouchListener(touchListener);

        findViewById(R.id.button_down).setOnTouchListener(touchListener);
        findViewById(R.id.button_left).setOnTouchListener(touchListener);
        findViewById(R.id.button_right).setOnTouchListener(touchListener);
        findViewById(R.id.button_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeDevicesInfo();
                outputText.append("\n" + getResources().getString(R.string.send_command_stop));
                completeMessage("STOP");
                countCommands = 0;
            }
        });

        SwitchMaterial hold_command = findViewById(R.id.switch_hold_command_mm);
        hold_command.setOnCheckedChangeListener(this);
        hold_command.setChecked(false);

        Arrays.fill(message, (byte) 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        String message = data.getStringExtra("message");
        Intent intent = new Intent();
        intent.putExtra("message", message);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public synchronized void printDataToTextView(String printData) {
        Log.d(APP_LOG_TAG, "Печатаемое сообщение в BtDeviceActivity: " + printData);
        outputText.append("\n" + "---" + "\n" + printData);
    }

    public synchronized boolean isActive() {
        return active;
    }

    void checkForActiveDevices() {
        for (DeviceItemType currentDevice : devicesList) {
            if (!currentDevice.isConnected()) {
                disconnectedDevicesList.add(currentDevice);
            }
        }
        for (DeviceItemType currentDevice : disconnectedDevicesList) {
            if (currentDevice.isConnected()) {
                devicesList.add(currentDevice);
            }
        }
        devicesList.removeIf(currentDevice -> !currentDevice.isConnected());
        disconnectedDevicesList.removeIf(DeviceItemType::isConnected);
    }

    public synchronized void addDisconnectedDevice(DeviceItemType currentDevice) {
        disconnectedDevicesList.add(currentDevice);
        //TODO
        //Диалог с предложением переподключить эти устройства
        Log.d(APP_LOG_TAG, "устройство отсоединилось");

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
        for (int i = 0; i < wifiDataThreadForArduinoList.size(); i++) {
            Log.d(APP_LOG_TAG, "BtDeviceActivity в onPause");
            wifiDataThreadForArduinoList.get(i).sendData(message, lengthMes);
        }

        for (int i = 0; i < wifiDataThreadForArduinoList.size(); i++) {
            wifiDataThreadForArduinoList.get(i).Disconnect();
        }
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            completeDevicesInfo();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // если нажали на кнопку и не важно есть удержание команд или нет
                if (buttonUp.equals(v)) {
                    Log.d(APP_LOG_TAG, "Отправляю команду движения вперёд;");
                    outputText.append("\n" + getResources().getString(R.string.send_command_forward));
                    completeMessage("FORWARD");
                    countCommands = 0;
                } else if (buttonDown.equals(v)) {
                    outputText.append("\n" + getResources().getString(R.string.send_command_back));
                    Log.d(APP_LOG_TAG, "Отправляю команду движения назад;");
                    //Toast.makeText(getApplicationContext(), "Назад поехали", Toast.LENGTH_SHORT).show();
                    completeMessage("BACK");
                    countCommands = 0;
                } else if (buttonLeft.equals(v)) {
                    outputText.append("\n" + getResources().getString(R.string.send_command_left));
                    //Toast.makeText(getApplicationContext(), "Влево поехали", Toast.LENGTH_SHORT).show();
                    Log.d(APP_LOG_TAG, "Отправляю команду движения влево;");
                    completeMessage("LEFT");
                    countCommands = 0;
                } else if (buttonRight.equals(v)) {//Toast.makeText(getApplicationContext(), "Вправо поехали", Toast.LENGTH_SHORT).show();
                    outputText.append("\n" + getResources().getString(R.string.send_command_right));
                    Log.d(APP_LOG_TAG, "Отправляю команду движения вправо;");
                    completeMessage("RIGHT");
                    countCommands = 0;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                // если отпустили кнопку
                if (!isHoldCommand) {
                    // и нет удержания команд то все кнопки отправляют команду стоп
                    outputText.append("\n" + getResources().getString(R.string.send_command_button_released));
                    if (buttonUp.equals(v)) {
                        completeMessage("FORWARD_STOP");
                        countCommands = 0;
                    } else if (buttonDown.equals(v)) {
                        completeMessage("BACK_STOP");
                        countCommands = 0;
                    } else if (buttonLeft.equals(v)) {
                        completeMessage("LEFT_STOP");
                        countCommands = 0;
                    } else if (buttonRight.equals(v)) {
                        completeMessage("RIGHT_STOP");
                        countCommands = 0;
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

            for (int i = 0; i < wifiDataThreadForArduinoList.size(); i++) {
                wifiDataThreadForArduinoList.get(i).sendData(message, lengthMes);
            }
        } else {
            outputText.append("\n" + getResources().getString(R.string.send_command_insufficient_data));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.switch_hold_command_mm) {
            isHoldCommand = isChecked;
            if (isHoldCommand) {
                outputText.append("\n" + getResources().getString(R.string.send_command_hold_enabled));
                findViewById(R.id.button_stop_bt).setEnabled(true);
            } else {
                outputText.append("\n" + getResources().getString(R.string.send_command_hold_disabled));
                findViewById(R.id.button_stop_bt).setEnabled(false);
            }
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
            Log.d(APP_LOG_TAG, "BtDeviceActivity в onDestroy, отключение устройств");
            devicesList.get(i).closeConnection();

        }
    }
}