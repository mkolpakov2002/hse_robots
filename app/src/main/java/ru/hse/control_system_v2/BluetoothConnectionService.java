package ru.hse.control_system_v2;

import static ru.hse.control_system_v2.Constants.APP_LOG_TAG;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.hse.control_system_v2.list_devices.DeviceItemType;

public class BluetoothConnectionService extends Service {
    // SPP UUID сервиса согласно документации Android:
    /*
    Hint: If you are connecting to a Bluetooth serial board,
     then try using the well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB
     https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
     */
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter btAdapter;
    private ArrayList<DeviceItemType> devicesList;
    private int devicesListConnectedSize;
    private boolean isSuccess = false;
    private Intent intentService;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        intentService = intent;
        ArrayList<MyRun> treadList = new ArrayList<>();
        devicesList = new ArrayList<>();
        devicesListConnectedSize = 0;
        devicesList.addAll(DeviceHandler.getDevicesList());
        Bundle arguments = intent.getExtras();

        ExecutorService executorService = Executors.newFixedThreadPool(devicesList.size());
        Log.d(APP_LOG_TAG, "Соединение по Bt начато...");
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        for(int i = 0; i < devicesList.size(); i++){
            Log.d(APP_LOG_TAG, "Создаю потоки для подключений...");
            MyRun mr = new MyRun(devicesList.get(i));
            treadList.add(i, mr);
            executorService.execute(treadList.get(i));
        }
        Intent serviceStarted;
        serviceStarted = new Intent("serviceStarted");
        sendBroadcast(serviceStarted);
        return Service.START_NOT_STICKY;
    }

    private class MyRun implements Runnable {
        String deviceMAC;
        DeviceItemType currentDevice;

        public MyRun(DeviceItemType currentDevice) {
            this.currentDevice = currentDevice;
            this.deviceMAC = currentDevice.getDeviceMAC();
        }

        public void run() {
            if (btIsEnabledFlagVoid()) {
                BluetoothDevice device = btAdapter.getRemoteDevice(deviceMAC);
                // Попытка подключиться к устройству
                try {
                    currentDevice.setBtSocket((BluetoothSocket) device.getClass().getMethod("createRfcommSocketToServiceRecord", UUID.class).invoke(device, MY_UUID));
                    Log.d(APP_LOG_TAG, "Создаю Bt сокет...");
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    Log.d("BLUETOOTH", e.getMessage());
                    Log.d(APP_LOG_TAG, "Создание Bt сокета неуспешно...");
                    e.printStackTrace();
                }
                if (currentDevice.isConnected()){
                    currentDevice.openBtConnection();
                    // Отключаем поиск устройств для сохранения заряда батареи
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    Log.d(APP_LOG_TAG, "Подключаюсь к Bt сокету...");
                }
            }
            resultOfConnection(currentDevice);
            Log.d(APP_LOG_TAG, "Попытка подключения для текущего устройства завершена...");
        }
    }

    //возвращает true, если bluetooth включён
    private boolean btIsEnabledFlagVoid() {
        return btAdapter.isEnabled();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(APP_LOG_TAG, "Bt сервис остановлен...");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Передаём данные о статусе соединения в Main Activity
    synchronized void resultOfConnection(DeviceItemType currentDevice) {
        devicesListConnectedSize++;
        if(currentDevice.isConnected()){
            isSuccess = true;
        }
        if(devicesListConnectedSize == devicesList.size()){
            Intent resultOfConnectionIntent;
            if(isSuccess){
                resultOfConnectionIntent = new Intent("success");
                DeviceHandler.setDevicesList(devicesList);
                Log.d(APP_LOG_TAG, "getter " +String.valueOf(DeviceHandler.getDevicesList().size()));
                Log.d(APP_LOG_TAG, "Bt соединение успешно, передаю результат в Main Activity...");
            } else {
                resultOfConnectionIntent = new Intent("not_success");
                Log.d(APP_LOG_TAG, "Bt соединение неуспешно, передаю результат в Main Activity...");
            }
            sendBroadcast(resultOfConnectionIntent);
            stopService(intentService);
        }
    }
}