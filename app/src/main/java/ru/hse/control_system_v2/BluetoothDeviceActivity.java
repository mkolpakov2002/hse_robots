package ru.hse.control_system_v2;


import static ru.hse.control_system_v2.Constants.APP_LOG_TAG;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Arrays;

import ru.hse.control_system_v2.dbprotocol.ProtocolDBHelper;
import ru.hse.control_system_v2.dbprotocol.ProtocolRepo;
import ru.hse.control_system_v2.list_devices.DeviceItemType;

public class BluetoothDeviceActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private boolean isHoldCommand;
    private byte[] message;      // комманда посылаемая на arduino
    private byte prevCommand = 0;
    private ArrayList<BluetoothDataThread> bluetoothDataThreadForArduinoList;
    private ArrayList<DeviceItemType> devicesList;
    private ArrayList<DeviceItemType> disconnectedDevicesList;
    private TextView outputText;
    ProtocolRepo getDevicesID;
    private int countCommands;
    private int lengthMes;
    private boolean active;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_device);
        findViewById(R.id.button_stop).setEnabled(false);

        disconnectedDevicesList = new ArrayList<>();

        devicesList = DeviceHandler.getDevicesList();
        checkForActiveDevices();

        outputText = findViewById(R.id.incoming_data);
        outputText.setMovementMethod(new ScrollingMovementMethod());

        Bundle b = getIntent().getExtras();
        String classDevice = b.get("protocol").toString();

        bluetoothDataThreadForArduinoList = new ArrayList<>();
        outputText.append("\n"+ "Подключено " + devicesList.size() + " из " + (devicesList.size()+disconnectedDevicesList.size()) + " устройств;");
        outputText.append("\n"+ "Список успешных подключений:");
        for(int i = 0; i < devicesList.size(); i++){
            outputText.append("\n"+ "Устройство " + devicesList.get(i).getDevName() + " подключено;");
            BluetoothDataThread bluetoothDataThreadForArduino = new BluetoothDataThread(this, devicesList.get(i));
            bluetoothDataThreadForArduinoList.add(bluetoothDataThreadForArduino);
            bluetoothDataThreadForArduinoList.get(i).start();
        }

        res = getResources();
        isHoldCommand = false;
        String protocolName = b.getString("protocol");
        getDevicesID = new ProtocolRepo(getApplicationContext(), protocolName);
        ProtocolDBHelper protocolDBHelper = ProtocolDBHelper.getInstance(getApplicationContext());
        lengthMes = protocolDBHelper.getLength(classDevice);
        message = new byte[lengthMes];
        countCommands = 0;

        for(DeviceItemType currentDevice: devicesList) {
            if (!BluetoothAdapter.checkBluetoothAddress(currentDevice.getDeviceMAC())) {
                showToast("Wrong MAC address");
                BluetoothDeviceActivity.this.finish();
            }
        }

        findViewById(R.id.button_up).setOnTouchListener(touchListener);
        findViewById(R.id.button_down).setOnTouchListener(touchListener);
        findViewById(R.id.button_left).setOnTouchListener(touchListener);
        findViewById(R.id.button_right).setOnTouchListener(touchListener);
        findViewById(R.id.button_stop).setOnClickListener(this);

        SwitchMaterial hold_command = findViewById(R.id.switch_hold_command_mm);
        hold_command.setOnCheckedChangeListener(this);
        hold_command.setChecked(false);

        Arrays.fill(message, (byte) 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data==null) return;
        String message = data.getStringExtra("message");
        Intent intent = new Intent();
        intent.putExtra("message", message);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public synchronized void printDataToTextView(String printData){
        Log.d(APP_LOG_TAG, "Печатаемое сообщение в BtDeviceActivity: " + printData);
        outputText.append("\n" + "---" + "\n" + printData);
    }

    public synchronized boolean isActive(){
        return active;
    }

    void checkForActiveDevices(){
        for (DeviceItemType currentDevice: devicesList){
            if(!currentDevice.isConnected()){
                disconnectedDevicesList.add(currentDevice);
            }
        }
        for (DeviceItemType currentDevice: disconnectedDevicesList){
            if(currentDevice.isConnected()){
                devicesList.add(currentDevice);
            }
        }
        devicesList.removeIf(currentDevice -> !currentDevice.isConnected());
        disconnectedDevicesList.removeIf(DeviceItemType::isConnected);
    }

    public synchronized void addDisconnectedDevice(DeviceItemType currentDevice){
        disconnectedDevicesList.add(currentDevice);
        //TODO
        //Диалог с предложением переподключить эти устройства
        Log.d(APP_LOG_TAG, "устройство отсоединилось");

    }

    @Override
    protected void onResume() {
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
        for(int i = 0; i < bluetoothDataThreadForArduinoList.size(); i++){
            Log.d(APP_LOG_TAG, "BtDeviceActivity в onPause");
            bluetoothDataThreadForArduinoList.get(i).sendData(message, lengthMes);
        }

        for(int i = 0; i < bluetoothDataThreadForArduinoList.size(); i++) {
            bluetoothDataThreadForArduinoList.get(i).Disconnect();
        }
    }

    @Override
    public void onClick(View v)
    {
        completeDevicesInfo();
        switch (v.getId()) {
            case R.id.button_stop:
                outputText.append("\n"+ "Отправляю команду стоп;");
                completeMessage("STOP");
                countCommands = 0;
                break;
        }
    }

    View.OnTouchListener touchListener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            completeDevicesInfo();
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                // если нажали на кнопку и не важно есть удержание команд или нет
                switch (v.getId()) {
                    case R.id.button_up:
                        Log.d(APP_LOG_TAG, "Отправляю команду движения вперёд;");
                        outputText.append("\n"+ "Отправляю команду движения вперёд;");
                        completeMessage("FORWARD");
                        countCommands = 0;
                        break;
                    case R.id.button_down:
                        outputText.append("\n"+ "Отправляю команду движения назад;");
                        Log.d(APP_LOG_TAG, "Отправляю команду движения назад;");
                        //Toast.makeText(getApplicationContext(), "Назад поехали", Toast.LENGTH_SHORT).show();
                        completeMessage("BACK");
                        countCommands = 0;
                        break;
                    case R.id.button_left:
                        outputText.append("\n"+ "Отправляю команду движения влево;");
                        //Toast.makeText(getApplicationContext(), "Влево поехали", Toast.LENGTH_SHORT).show();
                        Log.d(APP_LOG_TAG, "Отправляю команду движения влево;");
                        completeMessage("LEFT");
                        countCommands = 0;
                        break;
                    case R.id.button_right:
                        //Toast.makeText(getApplicationContext(), "Вправо поехали", Toast.LENGTH_SHORT).show();
                        outputText.append("\n"+ "Отправляю команду движения вправо;");
                        Log.d(APP_LOG_TAG, "Отправляю команду движения вправо;");
                        completeMessage("RIGHT");
                        countCommands = 0;
                        break;
                }
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                // если отпустили кнопку
                if(!isHoldCommand) {
                    // и нет удержания команд то все кнопки отправляют команду стоп
                    outputText.append("\n"+ "Кнопка отпущена, отправляю команду стоп;");
                    switch (v.getId())
                    {
                        case R.id.button_up:
                            completeMessage("FORWARD_STOP");
                            countCommands = 0;
                            break;
                        case R.id.button_down:
                            completeMessage("BACK_STOP");
                            countCommands = 0;
                            break;
                        case R.id.button_left:
                            completeMessage("LEFT_STOP");
                            countCommands = 0;
                            break;
                        case R.id.button_right:
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
                message[countCommands++] = (prevCommand == code)? getDevicesID.get("redo_command"): getDevicesID.get("new_command");
                prevCommand = code;
            }

            if (getDevicesID.getTag(res.getString(R.string.TAG_TYPE_COM)))
                message[countCommands++] = getDevicesID.get("type_move");
            message[countCommands++] = code;

            for(int i = 0; i < bluetoothDataThreadForArduinoList.size(); i++){
                bluetoothDataThreadForArduinoList.get(i).sendData(message, lengthMes);
            }
        }
        else {
            outputText.append("\n"+ "Недостаточно данных в используемом протоколе, сообщение не отправлено;");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_hold_command_mm:
                isHoldCommand = isChecked;
                if(isHoldCommand) {
                    outputText.append("\n"+ "Удерживание комманды включено...");
                    findViewById(R.id.button_stop).setEnabled(true);
                }
                else {
                    outputText.append("\n"+ "Удерживание комманды отключено...");
                    findViewById(R.id.button_stop).setEnabled(false);
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
        for (int i = 0; i < devicesList.size(); i++){
            Log.d(APP_LOG_TAG, "BtDeviceActivity в onDestroy, отключение устройств");
            devicesList.get(i).closeConnection();

        }
    }
}
