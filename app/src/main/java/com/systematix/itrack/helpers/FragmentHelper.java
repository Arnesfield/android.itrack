package com.systematix.itrack.helpers;

import android.content.res.Resources;
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
        String getTitle(Resources resources);
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
        setFragment(currFragment, false);
    }

    public void setFragment(Fragment newFragment) {
        setFragment(newFragment, true);
    }

    public void setFragment(Fragment newFragment, boolean strict) {
        // do not continue if fragment is the same as current
        if (newFragment == null || (strict && currFragment.getClass().equals(newFragment.getClass()))) {
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

        try {
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // new then becomes the current! :)
        currFragment = newFragment;

        // set dat title
        if (setActivityTitle && activity != null) {
            activity.setTitle(((TitleableFragment) newFragment).getTitle(activity.getResources()));
        }
    }

    // getters
    public Fragment getCurrFragment() {
        return currFragment;
    }
}
