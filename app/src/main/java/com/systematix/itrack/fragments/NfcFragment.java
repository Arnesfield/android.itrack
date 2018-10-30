package com.systematix.itrack.fragments;


import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.systematix.itrack.R;
import com.systematix.itrack.helpers.FragmentHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class NfcFragment extends Fragment implements FragmentHelper.TitleableFragment {


    public NfcFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_nfc, container, false);

        final NfcAdapter nfc = NfcAdapter.getDefaultAdapter(getContext());

        if (nfc.isEnabled()) {
            Toast.makeText(getContext(), "NFC enabled!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "NFC not enabled!", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    // TitleableFragment
    @Override
    public String getTitle() {
        return "Track";
    }
}
