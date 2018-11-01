package com.systematix.itrack.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

public class Task<T> extends AsyncTask<Void, Void, T> {
    private OnTaskExecuteListener<T> executingListener;
    private OnTaskFinishListener<T> finishListener;

    public interface OnTaskExecuteListener<T> {
        T execute();
    }

    public interface OnTaskFinishListener<T> {
        void finish(T result);
    }

    public Task(@NonNull OnTaskExecuteListener<T> executingListener) {
        this(executingListener, null);
    }

    public Task(@NonNull OnTaskExecuteListener<T> executingListener, OnTaskFinishListener<T> finishListener) {
        this.executingListener = executingListener;
        this.finishListener = finishListener;
    }

    @Override
    protected T doInBackground(Void... voids) {
        return executingListener.execute();
    }

    @Override
    protected void onPostExecute(T taskResult) {
        super.onPostExecute(taskResult);
        // execute all taskResult methods
        if (finishListener != null) {
            finishListener.finish(taskResult);
        }
    }
}
