package com.systematix.itrack.fragments;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systematix.itrack.R;
import com.systematix.itrack.models.FragmentModel;
import com.systematix.itrack.interfaces.OnNavItemChangeListener;
import com.systematix.itrack.models.ProgressTextModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment implements FragmentModel.TitleableFragment, OnNavItemChangeListener {


    private ProgressTextModel progressTextModel;

    public AttendanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        final View vAttendance = view.findViewById(R.id.attendance_view);

        progressTextModel = new ProgressTextModel(vAttendance);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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
