package ru.hse.control_system_v2.data;

import static ru.hse.control_system_v2.Constants.APP_LOG_TAG;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.hse.control_system_v2.AppMain;

public class ConnectionService extends Service {
    private ArrayList<DeviceItemType> devicesList;
    private ArrayList<DeviceItemType> devicesListConnected;
    private boolean isSuccess = false;
    private Intent intentService;
    boolean isBtService;
    // SPP UUID сервиса согласно документации Android:
    /*
    Hint: If you are connecting to a Bluetooth serial board,
     then try using the well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB
     https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
     */
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        intentService = intent;
        Bundle b = intentService.getExtras();
        isBtService = b.getBoolean("isBtService");
        ArrayList<ConnectingThread> treadList = new ArrayList<>();

        devicesList = new ArrayList<>();
        devicesListConnected = new ArrayList<>();
        devicesList.addAll(AppMain.getDevicesList());
        ExecutorService executorService = Executors.newFixedThreadPool(devicesList.size());

        for (int i = 0; i < devicesList.size(); i++) {
            Log.d(APP_LOG_TAG, "Создаю потоки для подключений...");
            ConnectingThread mr = new ConnectingThread(devicesList.get(i));
            treadList.add(i, mr);
            executorService.execute(treadList.get(i));
        }

        Log.d(APP_LOG_TAG, "Соединение начато...");
        return Service.START_NOT_STICKY;
    }

    class ConnectingThread implements Runnable {
        DeviceItemType currentDevice;

        public ConnectingThread(DeviceItemType device) {
            this.currentDevice = device;
        }

        public void run() {
            try {
                if(!isBtService)
                    currentDevice.setWifiSocket(new Socket(currentDevice.getDevIp(), currentDevice.getDevPort()));
                else {
                    BluetoothDevice device = AppMain.getBtAdapter().getRemoteDevice(currentDevice.getDeviceMAC());
                    currentDevice.setBtSocket((BluetoothSocket) device.getClass().getMethod("createRfcommSocketToServiceRecord", UUID.class).invoke(device, MY_UUID));
                }
            } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                Log.d(APP_LOG_TAG, "Попытка подключения для " + currentDevice.getDevName() + " неуспешна");
                currentDevice.closeConnection();
            }
            resultOfConnection(currentDevice);

            Log.d(APP_LOG_TAG, "Попытка подключения для текущего устройства завершена...");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // Передаём данные о статусе соединения в Main Activity
    synchronized void resultOfConnection(DeviceItemType currentDevice) {
        devicesListConnected.add(currentDevice);
        if (currentDevice.isWiFiBtConnected()!=null) {
            isSuccess = true;
        }
        if (devicesListConnected.size() == devicesList.size()) {
            Intent resultOfConnectionIntent;
            if (isSuccess) {
                resultOfConnectionIntent = new Intent("success");
                AppMain.setDevicesList(devicesList);
            } else {
                resultOfConnectionIntent = new Intent("not_success");
            }
            sendBroadcast(resultOfConnectionIntent);
            stopService(intentService);
        }
    }
}