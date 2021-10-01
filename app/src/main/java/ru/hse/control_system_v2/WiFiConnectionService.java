package ru.hse.control_system_v2;

import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import ru.hse.control_system_v2.list_devices.DeviceItemType;

public class WiFiConnectionService extends Service {
    String TAG = "ConnectionService";
    ArrayList<ConnectingThread> treadList;
    ExecutorService executorService;
    ArrayList<DeviceItemType> devicesList;
    public static String host_address="192.168.1.138";// адрес вашего устройства
    ArrayList<Boolean> resultOfConnection;
    int port = 40000;
    ArrayList<BluetoothSocket> socketList;
    ArrayList<BluetoothSocket> socketListConnected;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        devicesList = new ArrayList<>();
        devicesList.addAll(DeviceHandler.getDevicesList());
        resultOfConnection = new ArrayList<>();
        socketList = new ArrayList<>();
        //new HTTP_client(40000);
        //new Udp_client();

        for(int i = 0; i < devicesList.size(); i++){
            Log.d(TAG, "...Создаю массивы данных...");
            WiFiConnectionService.ConnectingThread mr = new WiFiConnectionService.ConnectingThread(devicesList.get(i).getMAC(), i, port);
            resultOfConnection.add(i, false);
            treadList.add(i, mr);

            socketList.add(i, null);
        }


        for(int i = 0; i < devicesList.size(); i++){
            Log.d(TAG, "...Создаю потоки...");
            executorService.execute(treadList.get(i));
        }
        return Service.START_NOT_STICKY;
    }


    static class ConnectingThread implements Runnable {
        String deviceMAC;
        int i;
        int port;
        public  String Greetings_from_S;

        public ConnectingThread(String deviceMAC, int i, int port) {
            this.deviceMAC = deviceMAC;
            this.i = i;
            this.port = port;
        }

        public void run() {
            try (Socket socket = new Socket(host_address,port)){


                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));




                Greetings_from_S = br.readLine();


            } catch (Exception e) {

                e.printStackTrace();

            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}