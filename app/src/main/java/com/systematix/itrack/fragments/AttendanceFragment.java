package com.systematix.itrack.fragments;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.systematix.itrack.R;
import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.interfaces.OnNavItemChangeListener;
import com.systematix.itrack.items.Attendance;
import com.systematix.itrack.items.Auth;
import com.systematix.itrack.models.FragmentModel;
import com.systematix.itrack.models.ProgressTextModel;
import com.systematix.itrack.models.ViewFlipperModel;
import com.systematix.itrack.models.api.GetAttendanceApiModel;
import com.systematix.itrack.utils.Api;
import com.systematix.itrack.utils.Callback;
import com.systematix.itrack.utils.Task;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment
        implements FragmentModel.TitleableFragment, OnNavItemChangeListener, Api.OnApiRespondListener {


    private ViewFlipperModel viewFlipperModel;
    private ProgressTextModel progressTextModel;
    private SwipeRefreshLayout viewSwipeLayout;
    private SwipeRefreshLayout emptySwipeLayout;
    private SwipeRefreshLayout errorSwipeLayout;
    private Callback<Attendance> fetchCallback;
    private Callback<Attendance> emptyCallback;
    private Callback<Attendance> errorCallback;
    private Task.OnTaskFinishListener<Void> fetchListener;

    public AttendanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        final ViewFlipper viewFlipper = view.findViewById(R.id.attendance_view_flipper);
        final View vAttendance = viewFlipper.findViewById(R.id.attendance_view);
        final View vEmpty = viewFlipper.findViewById(R.id.attendance_empty_state_view);
        final View vError = viewFlipper.findViewById(R.id.attendance_error_state_view);

        viewSwipeLayout = (SwipeRefreshLayout) vAttendance;
        emptySwipeLayout = (SwipeRefreshLayout) vEmpty;
        errorSwipeLayout = (SwipeRefreshLayout) vError;

        buildCallbacks();

        // refresh listener
        final SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                attemptToShowAttendance(fetchCallback);
            }
        };

        viewSwipeLayout.setOnRefreshListener(refreshListener);
        emptySwipeLayout.setOnRefreshListener(refreshListener);
        errorSwipeLayout.setOnRefreshListener(refreshListener);

        // only show loading on create view
        viewFlipperModel = new ViewFlipperModel(viewFlipper, R.id.attendance_loading_layout);
        progressTextModel = new ProgressTextModel(vAttendance);

        return view;
    }

    private void buildCallbacks() {
        // make callback
        fetchCallback = new Callback<Attendance>() {
            @Override
            public void call(Attendance attendance) {
                // do some requesting here!
                fetchAttendances();
            }
        };
        emptyCallback = new Callback<Attendance>() {
            @Override
            public void call(Attendance obj) {
                doRefresh(false);
                if (obj == null) {
                    viewFlipperModel.switchTo(R.id.attendance_empty_state_view);
                }
            }
        };
        errorCallback = new Callback<Attendance>() {
            @Override
            public void call(Attendance obj) {
                doRefresh(false);
                if (obj == null) {
                    viewFlipperModel.switchTo(R.id.attendance_error_state_view);
                }
            }
        };
        fetchListener = new Task.OnTaskFinishListener<Void>() {
            @Override
            public void finish(Void result) {
                // display stuff
                attemptToShowAttendance(emptyCallback);
            }
        };
    }

    private void doRefresh(boolean refresh) {
        viewSwipeLayout.setRefreshing(refresh);
        emptySwipeLayout.setRefreshing(refresh);
        errorSwipeLayout.setRefreshing(refresh);
    }

    private void fetchAttendances() {
        final Context context = getContext();
        final int uid = Auth.getSavedUserId(context);

        if (uid == -1) {
            // do not proceed
            doRefresh(false);
            errorCallback.call(null);
            return;
        }

        // execute stuff finish listener
        GetAttendanceApiModel.setOnFetchListener(fetchListener);

        // TODO: remove handler
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GetAttendanceApiModel.fetch(context, AttendanceFragment.this);
            }
        }, 5000);
    }

    private void attemptToShowAttendance(final Callback<Attendance> callback) {
        final Context context = getContext();
        final AppDatabase db = AppDatabase.getInstance(context);
        final int uid = Auth.getSavedUserId(context);

        if (uid == -1) {
            doRefresh(false);
            errorCallback.call(null);
            return;
        }

        new Task<>(new Task.OnTaskListener<Attendance>() {
            @Override
            public void preExecute() {

            }

            @Override
            public Attendance execute() {
                return db.attendanceDao().findById(uid);
            }

            @Override
            public void finish(Attendance result) {
                setAttendance(result);
                if (callback != null) {
                    callback.call(result);
                }
            }
        }).execute();
    }

    private void setAttendance(@Nullable Attendance attendance) {
        if (attendance != null) {
            doRefresh(false);
            viewFlipperModel.switchTo(R.id.attendance_view);
            progressTextModel.setProgress(attendance.getHoursRendered(), attendance.getViolationHours());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // try to show attendance first then fetch secretly
        attemptToShowAttendance(fetchCallback);
    }

    // TitleableFragment
    @Override
    public String getTitle(Resources resources) {
        return resources.getString(R.string.nav_attendance);
    }

    // OnNavItemChangeListener
    @Override
    public int getNavId() {
        return R.id.nav_attendance;
    }

    // OnApiSuccessListener
    @Override
    public void onApiSuccess(String tag, JSONObject response, boolean success, String msg) {
        if (!success) {
            doRefresh(false);
            attemptToShowAttendance(errorCallback);
        }
    }

    // OnApiErrorListener
    @Override
    public void onApiError(String tag, VolleyError error) {
        attemptToShowAttendance(errorCallback);
    }

    // OnApiExceptionListener
    @Override
    public void onApiException(String tag, JSONException e) {
        attemptToShowAttendance(errorCallback);
    }
}
