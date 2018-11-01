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

import com.systematix.itrack.R;
import com.systematix.itrack.helpers.FragmentHelper;
import com.systematix.itrack.helpers.ViewSwitcherHelper;
import com.systematix.itrack.interfaces.OnNavItemChangeListener;
import com.systematix.itrack.models.NfcEnabledStateModel;
import com.systematix.itrack.models.NfcNoPermissionStateModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class NfcFragment extends Fragment
        implements FragmentHelper.TitleableFragment, NfcNoPermissionStateModel.Model, OnNavItemChangeListener {

    private NfcAdapter nfc;
    private View vEnabled;
    private View vNoPermission;
    private ViewSwitcherHelper viewSwitcher;

    public NfcFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FrameLayout rootView = (FrameLayout) inflater.inflate(R.layout.fragment_nfc, container, false);
        vEnabled = inflater.inflate(R.layout.nfc_enabled_state, container, false);
        vNoPermission = inflater.inflate(R.layout.nfc_no_permission_state, container, false);
        nfc = NfcAdapter.getDefaultAdapter(getContext());
        viewSwitcher = new ViewSwitcherHelper(rootView);

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
        final View newView = isNfcEnabled ? vEnabled : vNoPermission;

        // if these are different, then show dat toast!
        if (isNfcEnabled && viewSwitcher.isNotCurrent(newView)) {
            Toast.makeText(getContext(), R.string.msg_nfc_enabled, Toast.LENGTH_SHORT).show();
            NfcEnabledStateModel.onResume(getActivity());
        }

        viewSwitcher.switchTo(newView);
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
