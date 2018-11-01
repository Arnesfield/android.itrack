package com.systematix.itrack.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.systematix.itrack.items.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user WHERE user_id != :exceptId")
    List<User> getAll(int exceptId);

    @Query("SELECT * FROM user WHERE user_id = :id")
    User findById(int id);

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);

    @Query("DELETE FROM user WHERE user_id = :id")
    void deleteById(int id);
}
