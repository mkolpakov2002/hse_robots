package ru.hse.control_system_v2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.UUID;

public class DataThread extends Thread{ // класс поток для приема и передачи данных
    public DataThread(@NonNull Context context){
        if (context instanceof Activity){
            c = context;
        }
        Log.d(TAG, "Поток запущен");
    }

    public void setSelectedDevice(String selectedDevice) {
        this.MAC = selectedDevice;
    }
    public void setProtocol(String classDevice) {
        this.classDevice = classDevice;
    }
    public void setSocket(BluetoothSocket clientSocket){
        this.clientSocket = clientSocket;
    }
    Context c;
    String MAC;
    String classDevice;
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothSocket clientSocket;
    private byte[] inputPacket;
    private static final String TAG = "Thread";
    OutputStream mmOutStream;
    InputStream mmInStream;
    private int[] my_data;
    int len;
    // SPP UUID сервиса
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean ready_to_request;         // флаг готовности принятия данных: true - высылай новый пакет   false - не высылай пакет
    @Override
    public void run()
    {
        Log.d("thread is running", "********************************************");
        OutputStream tmpOut = null;
        InputStream tmpIn = null;
        try
        {
            tmpOut = clientSocket.getOutputStream();
            tmpIn = clientSocket.getInputStream();
        } catch (IOException e)
        {
            e.printStackTrace();
            Log.d("BLUETOOTH", e.getMessage());
        }

        mmOutStream = tmpOut;
        mmInStream = tmpIn;
        inputPacket = new byte[12];
        StringBuilder str = new StringBuilder();
        int bufNum;
        int pacNum = 0;
        boolean flag = true;
        while(flag){
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            // Read from the InputStream
            try {
                bytes = mmInStream.read(buffer);
                flag = true;
            } catch (IOException e) {
                Log.e(TAG, "Ошибка чтения входящих данных в потоке " + e.getMessage());
                flag = false;
            }
            if(flag){
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
            } else if(DeviceActivity.active){
                //чтение входящей информации неуспешно при открытом приложении
                ((DeviceActivity) c).runOnUiThread(new Runnable() {
                    public void run() {

                        //((DeviceActivity) c).connectionFailed();
                    }
                });
            }
        }

        Log.d("Конец true", "********************************************");
    }

    synchronized void incomingData(String incomingData){
        if(DeviceActivity.active){
            Log.d(TAG, "Входящие данные: " + incomingData);
            ((DeviceActivity) c).runOnUiThread(new Runnable() {
                public void run() {
                    ((DeviceActivity) c).printDataToTextView(incomingData.replaceAll("\n",""));
                }
            });
            SystemClock.sleep(100);
        }
    }

    public void sendData(String message, int len)
    {
        Log.d("Send_Data 3", "********************************************");
        byte[] msgBuffer = message.getBytes();
        Log.d(TAG, "***Отправляем данные: " + message + "***");

        try
        {
            mmOutStream.write(msgBuffer);
        } catch (IOException e)
        {
        }
    }

    public void sendData(byte[] message, int len)
    {
        Log.d("Send_Data 2", "********************************************");
        String logMessage = "***Отправляем данные: ";
        for (int i=0; i < len; i++)
            logMessage += message[i] + " ";
        Log.d(TAG, logMessage + "***");
        try
        {
            mmOutStream.write(message);
        } catch (IOException e)
        {
        }
    }

    public void cancel()
    {
        try
        {
            clientSocket.close();
        } catch (IOException e)
        {
        }
    }

    public Object status_OutStrem()
    {
        if (mmOutStream == null)
        {

            return null;
        }
        return mmOutStream;
    }


    //возвращает true, если bluetooth включён
    public boolean btIsEnabledFlagVoid() {
        return btAdapter.isEnabled();
    }



    public void Send_Data(String message) { sendData(message, len);}

    public void Send_Data(byte[] message, int len) {
        Log.d("Send_Data", "********************************************");
        this.len = len;
        sendData(message, len);
    }

    public void Disconnect(Timer bt_timer) // для работы через определенные промежутки времени
    {
        Log.d(TAG, "...In onPause()...");

        if (status_OutStrem() != null)
        {
            cancel();
            bt_timer.cancel();
        }

        try
        {
            clientSocket.close();

        } catch (IOException e2)
        {
            //MyError("Fatal Error", "В onPause() Не могу закрыть сокет" + e2.getMessage() + ".", "Не могу закрыть сокет.");
        }

    }
    public void Disconnect() // при ручном управлении передачей пакетов
    {
        Log.d(TAG, "...In onPause()...");

        if (status_OutStrem() != null)
        {
            cancel();
        }

        try
        {
            clientSocket.close();

        } catch (IOException e2)
        {
            //MyError("Fatal Error", "В onPause() Не могу закрыть сокет" + e2.getMessage() + ".", "Не могу закрыть сокет.");
        }
    }

    public boolean isReady_to_request()
    {
        return ready_to_request;
    }

    public void setReady_to_request(boolean ready_to_request)
    {
        this.ready_to_request = ready_to_request;
    }

    public int[] getMy_data()
    {
        for (int i = 0; i < 5; i++) // 12 должно быть: 2 - префикс 7 - данные 3 - контр сумма.  ... ну или 7 - только данные
        {
            my_data[i] = (int) inputPacket[i +2];
            my_data[i] = my_data[i]*5;
        }
        return my_data;
    }

}
