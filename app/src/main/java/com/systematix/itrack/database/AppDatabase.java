package com.systematix.itrack.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

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

@Database(version = 2, exportSchema = false, entities = {
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
            db = Room.databaseBuilder(context, AppDatabase.class, "itrack-v1.db")
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return db;
    }

    // migrations
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE report "
                + " ADD COLUMN imgSrc TEXT");
        }
    };
}
