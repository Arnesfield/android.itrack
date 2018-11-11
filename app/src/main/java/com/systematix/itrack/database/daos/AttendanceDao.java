package com.systematix.itrack.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.systematix.itrack.items.Attendance;

import java.util.List;

@Dao
public interface AttendanceDao {
    @Query("SELECT * FROM attendance")
    List<Attendance> getAll();

    @Query("SELECT * FROM attendance WHERE id = :id")
    Attendance findById(int id);

    @Insert
    void insertAll(Attendance... attendances);

    @Update
    void update(Attendance attendance);

    @Delete
    void delete(Attendance attendance);

    @Query("DELETE FROM attendance WHERE id = :id")
    void deleteById(int id);
}
