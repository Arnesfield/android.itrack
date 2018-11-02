package com.systematix.itrack.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.systematix.itrack.items.Violation;

import java.util.List;

@Dao
public interface ViolationDao {
    @Query("SELECT * FROM violation WHERE violation_type = :type")
    List<Violation> getAll(String type);

    @Query("SELECT * FROM violation WHERE violation_id = :id")
    Violation findById(int id);

    @Insert
    void insertAll(Violation... violations);

    @Insert
    void insertAll(List<Violation> violations);

    @Delete
    void delete(Violation violation);

    @Query("DELETE FROM violation")
    void deleteAll();
}
