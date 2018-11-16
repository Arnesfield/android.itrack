package com.systematix.itrack.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.systematix.itrack.items.Report;

import java.util.List;

@Dao
public interface ReportDao {
    @Query("SELECT * FROM report")
    List<Report> getAll();

    @Query("SELECT * FROM report WHERE id = :id")
    Report findById(int id);

    @Insert
    void insertAll(Report... reports);

    @Update
    void update(Report report);

    @Delete
    void delete(Report report);

    @Query("DELETE FROM report WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM report")
    void deleteAll();
}
