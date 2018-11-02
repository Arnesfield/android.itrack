package com.systematix.itrack.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Task<T> extends AsyncTask<Void, Void, T> {
    private OnTaskPreExecuteListener preExecuteListener;
    private OnTaskExecuteListener<T> executeListener;
    private OnTaskFinishListener<T> finishListener;

    public interface OnTaskPreExecuteListener {
        void preExecute();
    }

    public interface OnTaskExecuteListener<T> {
        T execute();
    }

    public interface OnTaskFinishListener<T> {
        void finish(T result);
    }

    public interface OnTaskListener<T> extends OnTaskPreExecuteListener, OnTaskExecuteListener<T>, OnTaskFinishListener<T> {

    }

    public Task(@NonNull OnTaskExecuteListener<T> executeListener) {
        this(executeListener, null);
    }

    public Task(@NonNull OnTaskExecuteListener<T> executeListener, OnTaskFinishListener<T> finishListener) {
        this(null, executeListener, finishListener);
    }

    public Task(@Nullable OnTaskPreExecuteListener preExecuteListener, @NonNull OnTaskExecuteListener<T> executeListener, OnTaskFinishListener<T> finishListener) {
        this.preExecuteListener = preExecuteListener;
        this.executeListener = executeListener;
        this.finishListener = finishListener;
    }

    public Task(OnTaskListener listener) {
        this(listener, listener, listener);
    }

    // setters
    public Task<T> setPreExecuteListener(OnTaskPreExecuteListener preExecuteListener) {
        this.preExecuteListener = preExecuteListener;
        return this;
    }

    public Task<T> setFinishListener(OnTaskFinishListener<T> finishListener) {
        this.finishListener = finishListener;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (preExecuteListener != null) {
            preExecuteListener.preExecute();
        }
    }

    @Override
    protected T doInBackground(Void... voids) {
        return executeListener != null ? executeListener.execute() : null;
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
