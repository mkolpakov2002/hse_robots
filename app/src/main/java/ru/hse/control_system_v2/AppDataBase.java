package ru.hse.control_system_v2;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ru.hse.control_system_v2.list_devices.DeviceItemType;
import ru.hse.control_system_v2.list_devices.ItemType;

@Database(entities = {DeviceItemType.class /*, AnotherEntityType.class, AThirdEntityType.class */}, version = 2)
public abstract class AppDataBase extends RoomDatabase {

    public abstract DeviceItemTypeDao getDeviceItemTypeDao();
}
