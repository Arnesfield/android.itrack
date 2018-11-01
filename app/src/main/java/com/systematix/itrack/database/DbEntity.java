package com.systematix.itrack.database;

import android.content.Context;

public interface DbEntity {
    void save(Context context);
    void delete(Context context);
}
