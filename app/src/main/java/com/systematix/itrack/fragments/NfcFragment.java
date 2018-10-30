package com.systematix.itrack.fragments;


import android.app.Activity;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.systematix.itrack.R;
import com.systematix.itrack.helpers.FragmentHelper;
import com.systematix.itrack.models.NfcNoPermissionStateModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class NfcFragment extends Fragment
        implements FragmentHelper.TitleableFragment, NfcNoPermissionStateModel.Model {

    private NfcAdapter nfc;
    private FrameLayout rootView;
    private View vWaiting;
    private View vNoPermission;
    private View currView;

    public NfcFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (FrameLayout) inflater.inflate(R.layout.fragment_nfc, container, false);
        vWaiting = inflater.inflate(R.layout.nfc_waiting_state, container, false);
        vNoPermission = inflater.inflate(R.layout.nfc_no_permission_state, container, false);
        nfc = NfcAdapter.getDefaultAdapter(getContext());

        NfcNoPermissionStateModel.init(this, vNoPermission);

        updateView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    private void updateView() {
        View currView;
        if (nfc.isEnabled()) {
            currView = vWaiting;
            // if these are different, then show dat toast!
            if (this.currView != currView) {
                Toast.makeText(getContext(), "NFC enabled!", Toast.LENGTH_SHORT).show();
            }
        } else {
            currView = vNoPermission;
        }

        // if this.currView is different from local currView,
        // then remove all da views and set
        if (this.currView != currView) {
            this.currView = currView;
            rootView.removeAllViews();
            rootView.addView(currView);
        }
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
}
