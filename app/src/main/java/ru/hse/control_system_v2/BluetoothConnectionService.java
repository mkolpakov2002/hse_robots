package ru.hse.control_system_v2;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.hse.control_system_v2.list_devices.DeviceItemType;

public class BluetoothConnectionService extends Service {
    BluetoothDevice device;
    String TAG = "ConnectionService";
    // SPP UUID сервиса согласно документации Android:
    /*
    Hint: If you are connecting to a Bluetooth serial board,
     then try using the well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB
     https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
     */
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter btAdapter;
    String classDevice;
    ExecutorService executorService;
    ArrayList<DeviceItemType> devicesList;
    ArrayList<DeviceItemType> devicesListConnected;
    ArrayList<MyRun> treadList;
    ArrayList<Boolean> resultOfConnection;
    boolean isSuccess = false;
    int numberOfEndedConnections;
    Intent intentService;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        intentService = intent;

        numberOfEndedConnections = 0;
        resultOfConnection = new ArrayList<>();
        treadList = new ArrayList<>();

        devicesList = new ArrayList<>();
        devicesListConnected = new ArrayList<>();


        devicesList.addAll(DeviceHandler.getDevicesList());
        
        Bundle arguments = intent.getExtras();
        classDevice = arguments.get("protocol").toString();

        executorService = Executors.newFixedThreadPool(devicesList.size());
        Log.d(TAG, "...Соединение начато...");
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        for(int i = 0; i < devicesList.size(); i++){
            Log.d(TAG, "...Создаю массивы данных...");
            MyRun mr = new MyRun(devicesList.get(i), i);
            resultOfConnection.add(i, false);
            treadList.add(i, mr);

        }
        for(int i = 0; i < devicesList.size(); i++){
            Log.d(TAG, "...Создаю потоки...");
            executorService.execute(treadList.get(i));
        }
        Intent serviceStarted;
        serviceStarted = new Intent("serviceStarted");
        Log.d(TAG, "...Соединение начато...");
        sendBroadcast(serviceStarted);
        return Service.START_NOT_STICKY;
    }

    class MyRun implements Runnable {
        String deviceMAC;
        int i;
        DeviceItemType currentDevice;

        public MyRun(DeviceItemType currentDevice, int i) {
            this.currentDevice = currentDevice;
            this.deviceMAC = currentDevice.getMAC();
            this.i = i;
        }

        public void run() {
            if (btIsEnabledFlagVoid()) {
                device = btAdapter.getRemoteDevice(deviceMAC);
                // Попытка подключиться к устройству
                try {
                    currentDevice.setBtSocket((BluetoothSocket) device.getClass().getMethod("createRfcommSocketToServiceRecord", UUID.class).invoke(device, MY_UUID));
                    Log.d(TAG, "...Создаю сокет...");
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    Log.d("BLUETOOTH", e.getMessage());
                    Log.d(TAG, "...Создание сокета неуспешно...");
                    e.printStackTrace();
                }
                if (currentDevice.isConnected()){
                    currentDevice.openBtConnection();
                    // Отключаем поиск устройств для сохранения заряда батареи
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    Log.d(TAG, "...Подключаюсь к сокету...");
                }
            }
            resultOfConnection(currentDevice);

            Log.d(TAG, "...Попытка подключения для текущего устройства завершена...");
        }
    }

    //возвращает true, если bluetooth включён
    public boolean btIsEnabledFlagVoid() {
        return btAdapter.isEnabled();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "...Сервис остановлен...");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Передаём данные о статусе соединения в Main Activity
    synchronized void resultOfConnection(DeviceItemType currentDevice) {
        numberOfEndedConnections++;
        if(currentDevice.isConnected()){
            devicesListConnected.add(currentDevice);
            isSuccess = true;

        }
        if(numberOfEndedConnections == devicesList.size()){
            Intent resultOfConnectionIntent;
            if(isSuccess){
                resultOfConnectionIntent = new Intent("success");
                resultOfConnectionIntent.putExtra("protocol", classDevice);
                SocketHandler.setDevicesList(devicesListConnected);
                SocketHandler.setNumberOfConnections(numberOfEndedConnections);
                Log.d(TAG, "...Соединение успешно, передаю результат в Main Activity...");
            } else{
                resultOfConnectionIntent = new Intent("not_success");
                Log.d(TAG, "...Соединение неуспешно, передаю результат в Main Activity...");
            }
            sendBroadcast(resultOfConnectionIntent);
            stopService(intentService);
        }
    }
}