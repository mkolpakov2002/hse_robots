package ru.hse.control_system_v2.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import ru.hse.control_system_v2.data.DeviceItemType;

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
    LiveData<List<DeviceItemType>> getAll();
}