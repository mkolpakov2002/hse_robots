package ru.hse.control_system_v2;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import ru.hse.control_system_v2.dbprotocol.ProtocolDBHelper;
import ru.hse.control_system_v2.dbprotocol.ProtocolRepo;
import ru.hse.control_system_v2.list_devices.DeviceItemType;

public class BluetoothDeviceActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener
{
    private static final String TAG = "DeviceActivity";
    boolean is_hold_command;

    byte[] message;      // комманда посылаемая на arduino
    byte prevCommand = 0;
    String classDevice;
    ArrayList<BluetoothDataThread> bluetoothDataThreadForArduinoList;

    ArrayList<DeviceItemType> devicesList;
    static ArrayList<DeviceItemType> disconnectedDevicesList;
    TextView outputText;
    SwitchMaterial hold_command;
    int numberOfEndedConnections;
    ProtocolRepo getDevicesID;
    int countCommands;
    int lengthMes;
    ProtocolDBHelper dbprotocol;
    public static boolean active;

    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_mode);
        showToast("Started Manual mode!");
        findViewById(R.id.button_stop).setEnabled(false);

        disconnectedDevicesList = new ArrayList<>();

        devicesList = SocketHandler.getDevicesList();
        numberOfEndedConnections = SocketHandler.getNumberOfConnections();
        outputText = findViewById(R.id.incoming_data);
        outputText.setMovementMethod(new ScrollingMovementMethod());

        Bundle b = getIntent().getExtras();
        classDevice = b.get("protocol").toString();

        bluetoothDataThreadForArduinoList = new ArrayList<>();
        outputText.append("\n"+ "Подключено " + devicesList.size() +" из " + numberOfEndedConnections + " устройств;");
        outputText.append("\n"+ "Список успешных подключений:");
        for(int i = 0; i < devicesList.size(); i++){
            outputText.append("\n"+ "Устройство " + devicesList.get(i).getName() + " подключено;");
            BluetoothDataThread bluetoothDataThreadForArduino = new BluetoothDataThread(this, devicesList.get(i));
            bluetoothDataThreadForArduinoList.add(bluetoothDataThreadForArduino);
            bluetoothDataThreadForArduinoList.get(i).start();

        }


        res = getResources();
        is_hold_command = false;
        String protocolName = b.getString("protocol");
        getDevicesID = new ProtocolRepo(getApplicationContext(), protocolName);
        dbprotocol = ProtocolDBHelper.getInstance(getApplicationContext());


        lengthMes = dbprotocol.getLength(classDevice);
        message = new byte[lengthMes];
        countCommands = 0;

        for(int i = 0; i < devicesList.size(); i++) {
            if (!BluetoothAdapter.checkBluetoothAddress(devicesList.get(i).getMAC())) {
                showToast("Wrong MAC address");
                BluetoothDeviceActivity.this.finish();
            }
        }

        findViewById(R.id.button_up).setOnTouchListener(touchListener);
        findViewById(R.id.button_down).setOnTouchListener(touchListener);
        findViewById(R.id.button_left).setOnTouchListener(touchListener);
        findViewById(R.id.button_right).setOnTouchListener(touchListener);
        findViewById(R.id.button_stop).setOnClickListener(this);

        hold_command = findViewById(R.id.switch_hold_command_mm);
        hold_command.setOnCheckedChangeListener(this);
        hold_command.setChecked(false);

        Arrays.fill(message, (byte) 0);
        Log.d("DeviceActivity", String.valueOf(message.length));
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
        Log.d(TAG, "Output message " + printData);
        outputText.append("\n" + "---" + "\n" + printData);
    }

    public static synchronized void addDisconnectedDevice(DeviceItemType currentDevice){
        disconnectedDevicesList.add(currentDevice);
        //TODO
        //Диалог с предложением переподключить эти устройства
        Log.d("BtDevActivity", "disconnected");

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        active = true;
        //arduino.BluetoothConnectionServiceVoid();     // соединяемся с bluetooth
        //TODO - вызывает вылет приложения
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        active = false;
        completeDevicesInfo();

        if (getDevicesID.getTag(res.getString(R.string.TAG_TURN_COM)))
            message[countCommands++] = getDevicesID.get("new_command");

        if (getDevicesID.getTag(res.getString(R.string.TAG_TYPE_COM)))
            message[countCommands++] = getDevicesID.get("type_move");

        message[countCommands++] = getDevicesID.get("STOP");
        for(int i = 0; i < bluetoothDataThreadForArduinoList.size(); i++){
            Log.d("Logg", "DeviceActivity onPause");
            bluetoothDataThreadForArduinoList.get(i).sendData(message, lengthMes);
        }


        try
        {
            for(int i = 0; i < bluetoothDataThreadForArduinoList.size(); i++){
                bluetoothDataThreadForArduinoList.get(i).Disconnect();
            }
        }
        catch (Exception e)
        {}
    }

    @Override
    public void onClick(View v)
    {
        completeDevicesInfo();
        switch (v.getId())
        {
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
            if(event.getAction() == MotionEvent.ACTION_DOWN)                        // если нажали на кнопку и не важно есть удержание команд или нет
            {
                switch (v.getId())
                {
                    case R.id.button_up:
                        //Toast.makeText(getApplicationContext(), "Вперед поехали", Toast.LENGTH_SHORT).show();
                        Log.d("Вперед поехали", "********************************************");
                        outputText.append("\n"+ "Отправляю команду движения вперёд;");
                        completeMessage("FORWARD");
                        countCommands = 0;
                        break;
                    case R.id.button_down:
                        outputText.append("\n"+ "Отправляю команду движения назад;");
                        Log.d("Назад поехали", "********************************************");
                        //Toast.makeText(getApplicationContext(), "Назад поехали", Toast.LENGTH_SHORT).show();
                        completeMessage("BACK");
                        countCommands = 0;
                        break;
                    case R.id.button_left:
                        outputText.append("\n"+ "Отправляю команду движения влево;");
                        //Toast.makeText(getApplicationContext(), "Влево поехали", Toast.LENGTH_SHORT).show();
                        Log.d("Влево поехали", "********************************************");
                        completeMessage("LEFT");
                        countCommands = 0;
                        break;
                    case R.id.button_right:
                        //Toast.makeText(getApplicationContext(), "Вправо поехали", Toast.LENGTH_SHORT).show();
                        outputText.append("\n"+ "Отправляю команду движения вправо;");
                        Log.d("Вправо поехали", "********************************************");
                        completeMessage("RIGHT");
                        countCommands = 0;
                        break;
                }
            }
            else if(event.getAction() == MotionEvent.ACTION_UP)             // если отпустили кнопку
            {
                if(!is_hold_command)    // и нет удержания команд то все кнопки отправляют команду стоп
                {
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
                    Log.d("mLog", String.valueOf(countCommands));
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

    public void completeMessage (String command) {

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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        switch (buttonView.getId())
        {
            case R.id.switch_hold_command_mm:
                is_hold_command = isChecked;
                if(is_hold_command)
                {
                    outputText.append("\n"+ "Удерживание комманды включено...");
                    findViewById(R.id.button_stop).setEnabled(true);
                }
                else
                {
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
            Log.d("BLUETOOTH", "Отсоединение от устройства");
            devicesList.get(i).closeConnection();

        }
    }
}
