package com.systematix.itrack.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.systematix.itrack.items.MinorReport;

import java.util.List;

@Dao
public interface MinorReportDao {
    @Query("SELECT * FROM minor_report")
    List<MinorReport> getAll();

    @Query("SELECT * FROM minor_report WHERE id = :id")
    MinorReport findById(int id);

    @Insert
    void insertAll(MinorReport... minorReports);

    @Update
    void update(MinorReport minorReport);

    @Delete
    void delete(MinorReport minorReport);

    @Query("DELETE FROM minor_report WHERE id = :id")
    void deleteById(int id);
}
