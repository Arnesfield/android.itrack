package com.systematix.itrack.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.systematix.itrack.database.daos.AttendanceDao;
import com.systematix.itrack.database.daos.NotificationDao;
import com.systematix.itrack.database.daos.ReportDao;
import com.systematix.itrack.database.daos.UserDao;
import com.systematix.itrack.database.daos.ViolationDao;
import com.systematix.itrack.items.Attendance;
import com.systematix.itrack.items.Notification;
import com.systematix.itrack.items.Report;
import com.systematix.itrack.items.User;
import com.systematix.itrack.items.Violation;

@Database(version = 1, exportSchema = false, entities = {
        User.class,
        Violation.class,
        Report.class,
        Attendance.class,
        Notification.class
})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract ViolationDao violationDao();
    public abstract ReportDao reportDao();
    public abstract AttendanceDao attendanceDao();
    public abstract NotificationDao notificationDao();

    private static AppDatabase db;

    public static AppDatabase getInstance(Context context) {
        if (db == null) {
            db = Room.databaseBuilder(context, AppDatabase.class, "itrack-v3.db").build();
        }
        return db;
    }
}
