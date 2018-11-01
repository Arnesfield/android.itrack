package com.systematix.itrack.helpers;

import android.content.Context;
import android.support.annotation.MenuRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.systematix.itrack.R;
import com.systematix.itrack.interfaces.OnNavItemChangeListener;
import com.systematix.itrack.items.User;

public final class NavDrawerHelper {
    public static void setHeader(Context context, NavigationView navigationView, User user) {
        // from here, set also the name of user
        final View headerView = navigationView.getHeaderView(0);
        final TextView tvTitle = headerView.findViewById(R.id.nav_title);
        final TextView tvSubtitle = headerView.findViewById(R.id.nav_subtitle);
        final ImageView imageView = headerView.findViewById(R.id.nav_image_view);
        final TextView textView = headerView.findViewById(R.id.nav_no_image_text);

        tvTitle.setText(user.getName());
        tvSubtitle.setText(user.getNumber());
        user.loadImage(context, imageView, textView);
    }

    public static void setMenu(NavigationView navigationView, @MenuRes int menu) {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(menu);
    }

    public static void setNavItemSelected(NavigationView navigationView, Fragment fragment) {
        // update nav selected
        if (navigationView != null && fragment instanceof OnNavItemChangeListener) {
            navigationView.setCheckedItem(((OnNavItemChangeListener) fragment).getNavId());
        }
    }

    public static void setNavItemSelected(NavigationView navigationView, FragmentHelper fragmentHelper) {
        if (fragmentHelper != null) {
            setNavItemSelected(navigationView, fragmentHelper.getCurrFragment());
        }
    }
}
