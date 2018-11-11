package com.systematix.itrack.fragments;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systematix.itrack.R;
import com.systematix.itrack.models.FragmentModel;
import com.systematix.itrack.interfaces.OnNavItemChangeListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment implements FragmentModel.TitleableFragment, OnNavItemChangeListener {


    public AttendanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attendance, container, false);
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
