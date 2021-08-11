package ru.hse.control_system_v2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WiFiConnectionService extends Service {
    public WiFiConnectionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}