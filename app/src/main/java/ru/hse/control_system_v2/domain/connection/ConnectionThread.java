package ru.hse.control_system_v2.domain.connection;

import static ru.hse.control_system_v2.utility.AppConstants.APP_LOG_TAG;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.OutputStream;

import ru.hse.control_system_v2.model.entities.Device;

/**
 * Поток для прослушивания данных
 */
public class ConnectionThread extends Thread {
    private final Device deviceItemType;
    private Context c;
    private OutputStream mmOutStream;
    boolean isBtService;


    public ConnectionThread(@NonNull Context context, Device deviceItemType, boolean isBtService) {
        if (context instanceof Activity) {
            c = context;
        }
        this.deviceItemType = deviceItemType;
        this.isBtService = isBtService;
        Log.d(APP_LOG_TAG, "Поток запущен");
    }

    @Override
    public void run() {

    }

}
