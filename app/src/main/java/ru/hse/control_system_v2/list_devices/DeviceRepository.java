package ru.hse.control_system_v2.list_devices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import ru.hse.control_system_v2.dbdevices.DeviceDBHelper;

public class DeviceRepository implements Serializable {
    final Set<DeviceItemType> mData;

    public static DeviceRepository getInstance(Context context) {
        DeviceRepository mInstance;
        synchronized (DeviceRepository.class) {
            mInstance = new DeviceRepository(context);
        }
        return mInstance;
    }

    public DeviceRepository(Context context) {
        mData = initializeData(context);
    }

    public Set<DeviceItemType> list() {
        return mData;
    }

    private Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    protected Set<DeviceItemType> initializeData(Context context) {
        Set<DeviceItemType> data = new HashSet<>();

        SQLiteDatabase deviceDB = DeviceDBHelper.getInstance(getActivity(context)).getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = deviceDB.query(DeviceDBHelper.TABLE_DEVICES, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DeviceDBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DeviceDBHelper.KEY_NAME);
            int MacIndex = cursor.getColumnIndex(DeviceDBHelper.KEY_MAC);
            int protocolIndex = cursor.getColumnIndex(DeviceDBHelper.KEY_PROTO);
            int classIndex = cursor.getColumnIndex(DeviceDBHelper.KEY_CLASS);
            int typeIndex = cursor.getColumnIndex(DeviceDBHelper.KEY_TYPE);
            do {
                DeviceItemType item = new DeviceItemType(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getString(MacIndex), cursor.getString(protocolIndex), cursor.getString(classIndex), cursor.getString(typeIndex));
                data.add(item);
            } while (cursor.moveToNext());
        }
        return data;
    }
}
