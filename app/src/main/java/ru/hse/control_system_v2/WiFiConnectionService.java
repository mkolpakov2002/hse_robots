package ru.hse.control_system_v2;

import static ru.hse.control_system_v2.Constants.APP_LOG_TAG;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.hse.control_system_v2.list_devices.DeviceItemType;

public class WiFiConnectionService extends Service {
    private ArrayList<DeviceItemType> devicesList;
    private ArrayList<DeviceItemType> devicesListConnected;
    private static final String host_address = "192.168.1.138";
    private String classDevice;
    private boolean isSuccess = false;
    private Intent intentService;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        intentService = intent;
        ArrayList<ConnectingThread> treadList = new ArrayList<>();

        devicesList = new ArrayList<>();
        devicesListConnected = new ArrayList<>();
        devicesList.addAll(DeviceHandler.getDevicesList());
        ExecutorService executorService = Executors.newFixedThreadPool(devicesList.size());

        devicesList.addAll(DeviceHandler.getDevicesList());

        for (int i = 0; i < devicesList.size(); i++) {
            Log.d(APP_LOG_TAG, "Создаю потоки для подключений...");
            ConnectingThread mr = new ConnectingThread(devicesList.get(i));
            treadList.add(i, mr);
            executorService.execute(treadList.get(i));
        }

        Log.d(APP_LOG_TAG, "WiFi соединение начато...");
        return Service.START_NOT_STICKY;
    }

    class ConnectingThread implements Runnable {
        DeviceItemType currentDevice;

        public ConnectingThread(DeviceItemType device) {
            this.currentDevice = device;
        }

        public void run() {
            try {
                currentDevice.setWifiSocket(new Socket(currentDevice.getDevIp(), currentDevice.getDevPort()));
            } catch (IOException e) {
                e.printStackTrace();
                currentDevice.closeConnection();
            }
            resultOfConnection(currentDevice);

            Log.d(APP_LOG_TAG, "...Попытка подключения для текущего устройства завершена...");
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
        if (currentDevice.isConnected()) {
            isSuccess = true;
        }
        if (devicesListConnected.size() == devicesList.size()) {
            Intent resultOfConnectionIntent;
            if (isSuccess) {
                resultOfConnectionIntent = new Intent("success");
                resultOfConnectionIntent.putExtra("protocol", classDevice);
                Log.d(APP_LOG_TAG, "WiFi соединение успешно, передаю результат в Main Activity...");
            } else {
                resultOfConnectionIntent = new Intent("not_success");
                Log.d(APP_LOG_TAG, "WiFi соединение неуспешно, передаю результат в Main Activity...");
            }
            sendBroadcast(resultOfConnectionIntent);
            stopService(intentService);
        }
    }
}