package com.systematix.itrack.fragments;


import android.app.Activity;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.systematix.itrack.R;
import com.systematix.itrack.models.FragmentModel;
import com.systematix.itrack.models.ViewFlipperModel;
import com.systematix.itrack.interfaces.OnNavItemChangeListener;
import com.systematix.itrack.models.NfcEnabledStateModel;
import com.systematix.itrack.models.NfcNoPermissionStateModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class NfcFragment extends Fragment
        implements FragmentModel.TitleableFragment, NfcNoPermissionStateModel.Model, OnNavItemChangeListener {

    private NfcAdapter nfc;
    private ViewFlipperModel viewFlipperModel;

    public NfcFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FrameLayout rootView = (FrameLayout) inflater.inflate(R.layout.fragment_nfc, container, false);
        final ViewFlipper viewFlipper = rootView.findViewById(R.id.nfc_view_flipper);
        final View vNoPermission = rootView.findViewById(R.id.nfc_no_permission_state_view);
        nfc = NfcAdapter.getDefaultAdapter(getContext());
        viewFlipperModel = new ViewFlipperModel(viewFlipper);

        NfcEnabledStateModel.init(getContext(), nfc);
        NfcNoPermissionStateModel.init(this, vNoPermission);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
        // start the timer to check for nfc here
        NfcNoPermissionStateModel.startTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        // stop the timer obviously
        NfcNoPermissionStateModel.stopTimer();
    }

    private void updateView() {
        final boolean isNfcEnabled = nfc.isEnabled();
        final int newView = isNfcEnabled ? R.id.nfc_enabled_state_view : R.id.nfc_no_permission_state_view;

        // if these are different, then show dat toast!
        if (isNfcEnabled && viewFlipperModel.isNotCurrent(newView)) {
            Toast.makeText(getContext(), R.string.msg_nfc_enabled, Toast.LENGTH_SHORT).show();
            NfcEnabledStateModel.onResume(getActivity());
        }

        viewFlipperModel.switchTo(newView);
    }

    // NfcNoPermissionStateModel
    @Override
    public void noPermissionStateUpdateView() {
        updateView();
    }

    @Override
    public Activity noPermissionStateGetActivity() {
        return getActivity();
    }

    // TitleableFragment
    @Override
    public String getTitle(Resources resources) {
        return resources.getString(R.string.nav_nfc);
    }

    // OnNavItemChangeListener
    @Override
    public int getNavId() {
        return R.id.nav_nfc;
    }
}
