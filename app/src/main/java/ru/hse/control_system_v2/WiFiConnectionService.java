package ru.hse.control_system_v2;

import android.app.Service;
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
    ArrayList<BluetoothConnectionService.MyRun> treadList;
    ExecutorService executorService;
    ArrayList<DeviceItemType> devicesList;
    public static  String host_address="192.168.1.138";// адрес вашего устройства

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        devicesList = new ArrayList<>();
        devicesList.addAll(DeviceHandler.getDevicesList());
        //new HTTP_client(40000);
        //new Udp_client();

        for(int i = 0; i < devicesList.size(); i++){
            Log.d(TAG, "...Создаю потоки...");
            executorService.execute(treadList.get(i));
        }
        return Service.START_NOT_STICKY;
    }


    class MyRun implements Runnable {
        String deviceMAC;
        int i;
        int port;
        public  String Greetings_from_S;

        public MyRun(String deviceMAC, int i, int port) {
            this.deviceMAC = deviceMAC;
            this.i = i;
            this.port = port;
        }

        public void run() {
            try (Socket socket = new Socket(host_address,port)){

                //PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //pw.println("stop data\r");//на всякий случай, вдруг вышли некорректно

                //pw.println("data\r");// Greetings with SERVER


                Greetings_from_S = br.readLine();

                //if (Greetings_from_S.equals("ready")) {

                    //new Udp_client();

                //}


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