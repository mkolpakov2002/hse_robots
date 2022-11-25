package ru.hse.control_system_v2.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DeviceItemType.class /*, AnotherEntityType.class, AThirdEntityType.class */}, version = 2)
public abstract class AppDataBase extends RoomDatabase {

    public abstract DeviceItemTypeDao getDeviceItemTypeDao();
}
