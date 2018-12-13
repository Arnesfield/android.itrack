package com.systematix.itrack.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.systematix.itrack.items.Notification;

import java.util.List;

@Dao
public interface NotificationDao {
    @Query("SELECT * FROM notification")
    List<Notification> getAll();

    @Query("SELECT * FROM notification WHERE user_id = :userId ORDER BY timestamp DESC")
    List<Notification> getAll(int userId);

    @Query("SELECT * FROM notification WHERE id = :id")
    Notification findById(int id);

    @Insert
    void insertAll(Notification... notifications);

    @Update
    void update(Notification notification);

    @Insert
    void insertAll(List<Notification> notifications);

    @Delete
    void delete(Notification notification);

    @Query("DELETE FROM notification")
    void deleteAll();
}
