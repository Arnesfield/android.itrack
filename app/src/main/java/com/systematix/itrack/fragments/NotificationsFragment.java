package com.systematix.itrack.fragments;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.systematix.itrack.R;
import com.systematix.itrack.database.AppDatabase;
import com.systematix.itrack.interfaces.OnNavItemChangeListener;
import com.systematix.itrack.items.Auth;
import com.systematix.itrack.items.Notification;
import com.systematix.itrack.models.FragmentModel;
import com.systematix.itrack.models.ViewFlipperModel;
import com.systematix.itrack.models.api.GetNotificationsApiModel;
import com.systematix.itrack.utils.Api;
import com.systematix.itrack.utils.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment
        implements FragmentModel.TitleableFragment, OnNavItemChangeListener {


    private ViewFlipperModel viewFlipperModel;
    private View vEmptyState;
    private View vNotifications;

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        final ViewFlipper viewFlipper = rootView.findViewById(R.id.notifications_view_flipper);
        vEmptyState = viewFlipper.findViewById(R.id.notifications_empty_state_view);
        vNotifications = viewFlipper.findViewById(R.id.notifications_view);

        viewFlipperModel = new ViewFlipperModel(viewFlipper, R.id.notifications_loading_layout);

        // set stuff
        final Button btnEmpty = vEmptyState.findViewById(R.id.notifications_empty_reload_btn);
        btnEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchNotifications();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // fetch hereee
        fetchNotifications();
    }

    private void fetchNotifications() {
        // loading
        viewFlipperModel.switchTo(R.id.notifications_loading_layout);
        GetNotificationsApiModel.fetch(getContext(), new Api.OnApiRespondListener() {
            @Override
            public void onApiSuccess(String tag, JSONObject response, boolean success, String msg) throws JSONException {
                // get notifs even when no fetched :D
                getNotificationsFromDb();
            }

            @Override
            public void onApiError(String tag, VolleyError error) throws JSONException {
                // get notifs even when no fetched :D
                getNotificationsFromDb();
            }

            @Override
            public void onApiException(String tag, JSONException e) {
                // unlikely to pass here tho
                viewFlipperModel.switchTo(vEmptyState);
            }
        });
    }

    private void getNotificationsFromDb() {
        final Context context = getContext();
        final AppDatabase db = AppDatabase.getInstance(context);
        final int uid = Auth.getSavedUserId(context);

        // fetch notifications from db
        new Task<>(new Task.OnTaskListener<List<Notification>>() {
            @Override
            public void preExecute() {

            }

            @Override
            public List<Notification> execute() {
                return db.notificationDao().getAll(uid);
            }

            @Override
            public void finish(List<Notification> notifications) {
                setNotifications(notifications);
            }
        }).execute();
    }

    private void setNotifications(List<Notification> notifications) {
        if (notifications.isEmpty()) {
            // show empty here
            viewFlipperModel.switchTo(vEmptyState);
            return;
        }

        viewFlipperModel.switchTo(vNotifications);

        // display notifications here
        // TODO: make adapter for notifications
    }

    // TitleableFragment
    @Override
    public String getTitle(Resources resources) {
        return resources.getString(R.string.nav_notifications);
    }

    // OnNavItemChangeListener
    @Override
    public int getNavId() {
        return R.id.nav_notifications;
    }
}
