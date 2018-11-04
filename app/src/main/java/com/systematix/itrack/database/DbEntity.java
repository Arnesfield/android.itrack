package com.systematix.itrack.database;

import android.content.Context;
import android.support.annotation.Nullable;

import com.systematix.itrack.utils.Task;

public interface DbEntity {
    void save(Context context, @Nullable Task.OnTaskPreExecuteListener preExecuteListener, @Nullable Task.OnTaskFinishListener<Void> finishListener);
    void delete(Context context, @Nullable Task.OnTaskPreExecuteListener preExecuteListener, @Nullable Task.OnTaskFinishListener<Void> finishListener);
}
