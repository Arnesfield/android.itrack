package com.systematix.itrack.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.systematix.itrack.database.daos.UserDao;
import com.systematix.itrack.database.daos.ViolationDao;
import com.systematix.itrack.items.User;
import com.systematix.itrack.items.Violation;

@Database(entities = { User.class, Violation.class }, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract ViolationDao violationDao();

    private static AppDatabase db;

    public static AppDatabase getInstance(Context context) {
        if (db == null) {
            db = Room.databaseBuilder(context, AppDatabase.class, "itrack-db").build();
        }
        return db;
    }
}
