package ru.hse.control_system_v2;

import static ru.hse.control_system_v2.Constants.APP_LOG_TAG;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.hse.control_system_v2.list_devices.DeviceItemType;

public class ConnectionThread extends Thread {
    private final DeviceItemType deviceItemType;
    private Context c;
    private OutputStream mmOutStream;
    boolean isBtService;


    public ConnectionThread(@NonNull Context context, DeviceItemType deviceItemType, boolean isBtService) {
        if (context instanceof Activity) {
            c = context;
        }
        this.deviceItemType = deviceItemType;
        this.isBtService = isBtService;
        Log.d(APP_LOG_TAG, "Поток запущен");
    }

    @Override
    public void run() {
        OutputStream tmpOut = null;
        InputStream tmpIn = null;
        try {
            if(!isBtService){
                tmpOut = deviceItemType.getWiFiSocket().getOutputStream();
                tmpIn = deviceItemType.getWiFiSocket().getInputStream();
            } else {
                tmpOut = deviceItemType.getBtSocket().getOutputStream();
                tmpIn = deviceItemType.getBtSocket().getInputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(APP_LOG_TAG, e.getMessage());
            Disconnect();
        }

        mmOutStream = tmpOut;
        InputStream mmInStream = tmpIn;
        StringBuilder str = new StringBuilder();

        while (deviceItemType.isWiFiBtConnected()!=null) {
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
            if (deviceItemType.isWiFiBtConnected()!=null) {
                //успешно считываем данные
                StringBuilder incomingDataBuffer = new StringBuilder();
                String incomingMessage = new String(buffer, 0, bytes);
                //incomingMessage - текущие входящие данные (в текущем заходе цикла while); содержат символы \r, которые не нужны
                incomingMessage = incomingMessage.replaceAll("\r", "");
                //str - переменная для формирования итоговой строки
                str.append(incomingMessage);
                int j = 0;
                boolean isComplete = false;
                while (!isComplete) {
                    //обрабатываем str, выделяем строки с символом \n в конце
                    if (str.charAt(j) == '\n' && j + 1 <= str.length() - 1) {
                        //substring копирует до второго параметра НЕ включительно, но включая с первого
                        incomingDataBuffer.append(str.substring(0, j + 1));
                        incomingData(incomingDataBuffer.toString());
                        //incomingDataBuffer.toString() - подходящая строка
                        //записываем в str остаток старой строки (str без incomingDataBuffer.toString())
                        incomingDataBuffer.setLength(0);
                        String bufferStr = str.substring(j + 1);
                        str.setLength(0);
                        str.append(bufferStr);
                        j = -1;
                    } else if (str.charAt(j) == '\n') {
                        //нету элемента j+1, рассматриваемый символ \n последний в str
                        //просто копируем (без остатка, его нет)
                        incomingDataBuffer.append(str);
                        incomingData(incomingDataBuffer.toString());
                        j = -1;
                        str.setLength(0);
                    }
                    if (str.indexOf("\n") == -1) {
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

        Log.d(APP_LOG_TAG, "Конец работы цикла потока");
    }

    synchronized void incomingData(String incomingData) {
        ((ConnectionActivity) c).runOnUiThread(new Runnable() {
            public void run() {
                ((ConnectionActivity) c).printDataToTextView(incomingData.replaceAll("\n", ""));
            }
        });
        SystemClock.sleep(100);
    }

    public void sendData(byte[] message, int len) {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            StringBuilder logMessage = new StringBuilder("Отправляем данные: ");
            for (int i = 0; i < len; i++)
                logMessage.append(message[i]).append(" ");
            Log.d(APP_LOG_TAG, logMessage + "***");
            try {
                mmOutStream.write(message);
            } catch (IOException e) {
                Disconnect();
            }
        }
    }

    public void sendData(String message) {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                mmOutStream.write(message.getBytes());
            } catch (IOException e) {
                Disconnect();
            }
        }
    }

    public void sendData(byte message) {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            StringBuilder logMessage = new StringBuilder("Отправляем данные: ");
            logMessage.append(message).append(" ");
            Log.d(APP_LOG_TAG, logMessage + "***");
            try {
                mmOutStream.write(message);
            } catch (IOException e) {
                Disconnect();
            }
        }
    }

    public void Disconnect() {
        deviceItemType.closeConnection();
        Thread.currentThread().interrupt();
    }


}
