package ru.hse.control_system_v2.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import ru.hse.control_system_v2.list_devices.DeviceItemType;

@Database(entities = {DeviceItemType.class /*, AnotherEntityType.class, AThirdEntityType.class */}, version = 2)
public abstract class AppDataBase extends RoomDatabase {

    public abstract DeviceItemTypeDao getDeviceItemTypeDao();
}
