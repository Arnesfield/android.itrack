package com.systematix.itrack.fragments;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.systematix.itrack.R;
import com.systematix.itrack.models.FragmentModel;
import com.systematix.itrack.interfaces.OnNavItemChangeListener;
import com.systematix.itrack.models.ProgressTextModel;
import com.systematix.itrack.models.ViewFlipperModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment implements FragmentModel.TitleableFragment, OnNavItemChangeListener {


    private ViewFlipperModel viewFlipperModel;
    private ProgressTextModel progressTextModel;
    private SwipeRefreshLayout emptySwipeLayout;
    private SwipeRefreshLayout errorSwipeLayout;

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

        emptySwipeLayout = vEmpty.findViewById(R.id.attendance_empty_swipe_layout);
        errorSwipeLayout = vError.findViewById(R.id.attendance_error_swipe_layout);
        // errorSwipeLayout.setRefreshing();

        viewFlipperModel = new ViewFlipperModel(viewFlipper, R.id.attendance_loading_layout);
        progressTextModel = new ProgressTextModel(vAttendance);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewFlipperModel.switchTo(R.id.attendance_view);
        progressTextModel.setProgress(100, 100);
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
}
