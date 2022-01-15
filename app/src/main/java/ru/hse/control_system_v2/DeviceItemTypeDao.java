package ru.hse.control_system_v2;

import android.content.Context;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

import ru.hse.control_system_v2.list_devices.DeviceItemType;

@Dao
public interface DeviceItemTypeDao {
    // Добавление Note в бд
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(DeviceItemType... item);

    // Удаление Note из бд
    @Query("DELETE FROM devices WHERE devId = :id")
    void delete(int id);

    @Query("DELETE FROM devices")
    void deleteAll();

    @Update
    void update(DeviceItemType item);

    @Query("UPDATE devices SET devProtocol=:newProto WHERE devProtocol = :deletingProto")
    void deleteProto(String deletingProto, String newProto);

    // Получение всех Person из бд
    @Query("SELECT * FROM devices")
    List<DeviceItemType> getAll();

    // Получение всех DeviceItem из бд с условием
    @Query("SELECT * FROM devices WHERE devProtocol LIKE :suchProto")
    List<DeviceItemType> getAllDevicesWithSuchProto(String suchProto);

    @Query("SELECT * FROM devices WHERE deviceMAC LIKE :suchMAC")
    List<DeviceItemType> getAllDevicesWithSuchMAC(String suchMAC);

    @Query("SELECT * FROM devices WHERE devId = :suchId")
    DeviceItemType getById(int suchId);
}