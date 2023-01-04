package ru.hse.control_system_v2.connection;

import static ru.hse.control_system_v2.Constants.TAG;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BluetoothConnection extends ConnectionClass {

    // SPP UUID сервиса согласно документации Android:
    /*
    Hint: If you are connecting to a Bluetooth serial board,
     then try using the well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB
     https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
     */
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothAdapter btAdapter;
    //сокет для передачи данных
    BluetoothSocket clientSocket;
    //mac address
    String mac;

    public BluetoothConnection(String mac){
        this.mac = mac;
    }

    @Override
    void sentData() {

    }

    @Override
    void closeConnection() {
        Observable.fromCallable(() -> {
            // В случае ошибки пытаемся закрыть соединение
            clientSocket.close();
            // RxJava does not accept null return value. Null will be treated as a failure.
            // So just make it return true.
            return true;
        })
        // Execute in IO thread, i.e. background thread.
        .subscribeOn(Schedulers.io())
        // report or post the result to main thread.
        .observeOn(AndroidSchedulers.mainThread())
        // execute this RxJava
        .subscribe();
        try {

        } catch (IOException closeException) {
            //запись логов ошибки
            Log.d(TAG, closeException.getMessage());
        }
    }

    @Override
    @NonNull
    Observable<Object> openConnection() {

//получаем локальный Bluetooth адаптер устройства
//            btAdapter = BluetoothAdapter.getDefaultAdapter();
//            //устройство с выбранным MAC как объект
//            var device = btAdapter.getRemoteDevice(mac);
//            // Попытка подключиться к устройству
//            try {
//                clientSocket = (BluetoothSocket) device.getClass()
//                        .getMethod("createRfcommSocketToServiceRecord", UUID.class)
//                        .invoke(device, MY_UUID);
//            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
//                Log.d(TAG, ex.getMessage());
//            }
//            try {
//                if (clientSocket != null) {
//                    clientSocket.connect();
//                }
//            } catch (IOException exception) {
//                // В случае ошибки пытаемся закрыть соединение
//                closeConnection();
//
//                //запись логов ошибки
//                Log.d(TAG, exception.getMessage());
//            }

    }

}
