package com.systematix.itrack.helpers;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public final class FragmentHelper {
    private Fragment currFragment;
    private AppCompatActivity activity;
    @IdRes private int res;
    private boolean setActivityTitle;

    // implement this to your fragment!
    public interface TitleableFragment {
        String getTitle();
    }

    public FragmentHelper(
            AppCompatActivity activity,
            Fragment fragment,
            @IdRes int res,
            boolean setActivityTitle
    ) {
        this.activity = activity;
        this.currFragment = fragment;
        this.res = res;
        this.setActivityTitle = setActivityTitle;
    }

    public void setCurrFragment() {
        setFragment(currFragment);
    }

    public void setFragment(Fragment newFragment) {
        if (newFragment == null) {
            return;
        }

        // newFragment should implement TitleableFragment first!
        if (!(newFragment instanceof TitleableFragment)) {
            throw new RuntimeException(newFragment.toString() + " must implement TitlableFragment");
        }

        final FragmentManager manager = activity.getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();

        // if no curr fragment, then add it
        // else, just replace it
        if (currFragment == null) {
            transaction.add(res, newFragment);
        } else {
            transaction.replace(res, newFragment);
        }

        transaction.commit();

        // new then becomes the current! :)
        currFragment = newFragment;

        // set dat title
        if (setActivityTitle) {
            activity.setTitle(((TitleableFragment) newFragment).getTitle());
        }
    }

    // getters
    public Fragment getCurrFragment() {
        return currFragment;
    }
}
