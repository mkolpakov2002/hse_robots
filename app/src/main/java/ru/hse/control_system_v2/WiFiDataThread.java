package ru.hse.control_system_v2;

import static ru.hse.control_system_v2.Constants.APP_LOG_TAG;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.hse.control_system_v2.list_devices.DeviceItemType;

public class WiFiDataThread extends Thread{

    private final DeviceItemType deviceItemType;
    private WiFiDeviceActivity wiFiDeviceActivity;
    private final NetworkInfo mWifi;
    private OutputStream mmOutStream;

    public WiFiDataThread(Context c, DeviceItemType deviceItemType){
        if (c instanceof WiFiDeviceActivity){
            wiFiDeviceActivity = ((WiFiDeviceActivity) c);
        }
        this.deviceItemType = deviceItemType;
        mWifi = ((ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        Log.d(APP_LOG_TAG, "Поток запущен");
    }

    @Override
    public void run() {
        Log.d(APP_LOG_TAG, "WiFi thread is running");
        OutputStream tmpOut = null;
        InputStream tmpIn = null;
        try {
            tmpOut = deviceItemType.getWiFiSocket().getOutputStream();
            tmpIn = deviceItemType.getWiFiSocket().getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(APP_LOG_TAG, e.getMessage());
            Disconnect();
        }
        mmOutStream = tmpOut;
        InputStream mmInStream = tmpIn;
        StringBuilder str = new StringBuilder();

        while(deviceItemType.isConnected()){
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            // Read from the InputStream
            try {
                bytes = mmInStream.read(buffer);
            } catch (IOException e) {
                Log.e(APP_LOG_TAG, "Ошибка чтения входящих данных в потоке " + e.getMessage());
                Disconnect();
            }
            if(deviceItemType.isConnected()){
                //успешно считываем данные
                StringBuilder incomingDataBuffer = new StringBuilder();
                String incomingMessage = new String(buffer, 0, bytes);
                //incomingMessage - текущие входящие данные (в текущем заходе цикла while); содержат символы \r, которые не нужны
                incomingMessage = incomingMessage.replaceAll("\r", "");
                //str - переменная для формирования итоговой строки
                str.append(incomingMessage);
                int j = 0;
                boolean isComplete = false;
                while(!isComplete){
                    //обрабатываем str, выделяем строки с символом \n в конце
                    if(str.charAt(j)=='\n' && j+1<=str.length()-1) {
                        //substring копирует до второго параметра НЕ включительно, но включая с первого
                        incomingDataBuffer.append(str.substring(0, j+1));
                        incomingData(incomingDataBuffer.toString());
                        //incomingDataBuffer.toString() - подходящая строка
                        //записываем в str остаток старой строки (str без incomingDataBuffer.toString())
                        incomingDataBuffer.setLength(0);
                        String bufferStr = str.substring(j + 1);
                        str.setLength(0);
                        str.append(bufferStr);
                        j = -1;
                    } else if(str.charAt(j)=='\n') {
                        //нету элемента j+1, рассматриваемый символ \n последний в str
                        //просто копируем (без остатка, его нет)
                        incomingDataBuffer.append(str);
                        incomingData(incomingDataBuffer.toString());
                        j = -1;
                        str.setLength(0);
                    }
                    if(str.indexOf("\n") == -1){
                        //более символов \n не найдено, завершаем обработку строки
                        isComplete = true;
                    }
                    j++;
                }
            } else {
                //чтение входящей информации неуспешно при открытом приложении
                Disconnect();
            }
        }
        Log.d(APP_LOG_TAG, "Конец работы цикла потока WiFi");
    }

    public void sendData(byte[] message, int lengthMes) {
        StringBuilder logMessage = new StringBuilder("Отправляем данные по WiFi: ");
        for (int i=0; i < lengthMes; i++)
            logMessage.append(message[i]).append(" ");
        Log.d(APP_LOG_TAG, logMessage + "***");
        try {
            mmOutStream.write(message);
        } catch (IOException e){
            Disconnect();
        }
    }

    //возвращает true, если wifi включён
    public boolean wifiIsEnabledFlagVoid() {
        return mWifi.isConnected();
    }

    public void Disconnect() {
        deviceItemType.closeConnection();
        if(wifiIsEnabledFlagVoid()){
            //TODO
            //Сделать проверку на сеть в Bt и WiFi активити
            wiFiDeviceActivity.addDisconnectedDevice(deviceItemType);
        }
        Thread.currentThread().interrupt();
    }

    synchronized void incomingData(String incomingData){
        if(wiFiDeviceActivity.isActive()){
            Log.d(APP_LOG_TAG, "Входящие данные WiFi: " + incomingData);
            //TODO
            //А если activity не активна?
            (wiFiDeviceActivity).runOnUiThread(new Runnable() {
                public void run() {
                    (wiFiDeviceActivity).printDataToTextView(incomingData.replaceAll("\n",""));
                }
            });
            SystemClock.sleep(100);
        }
    }
}
