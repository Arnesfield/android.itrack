package com.systematix.itrack.models;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.systematix.itrack.R;
import com.systematix.itrack.items.User;

public final class UserInfoViewModel {
    public static void init(View userView, final User user) {
        final boolean hasNoLevel = user.getLevelStr() == null;
        final boolean hasNoCourse = user.getCourse() == null;

        final View vName = userView.findViewById(R.id.user_info_name);
        final View vNumber = userView.findViewById(R.id.user_info_number);
        // final View divider = userView.findViewById(R.id.user_info_divider);
        final View vLevel = userView.findViewById(R.id.user_info_level);
        final View vCourse = userView.findViewById(R.id.user_info_course);

        final TextView vNameTitle = vName.findViewById(R.id.component_info_title);
        final TextView vNameSubtitle = vName.findViewById(R.id.component_info_subtitle);
        final ImageView vNameImage = vName.findViewById(R.id.component_info_image);

        final TextView vNumberTitle = vNumber.findViewById(R.id.component_info_title);
        final TextView vNumberSubtitle = vNumber.findViewById(R.id.component_info_subtitle);
        final ImageView vNumberImage = vNumber.findViewById(R.id.component_info_image);

        final TextView vLevelTitle = vLevel.findViewById(R.id.component_info_title);
        final TextView vLevelSubtitle = vLevel.findViewById(R.id.component_info_subtitle);
        final ImageView vLevelImage = vLevel.findViewById(R.id.component_info_image);

        final TextView vCourseTitle = vCourse.findViewById(R.id.component_info_title);
        final TextView vCourseSubtitle = vCourse.findViewById(R.id.component_info_subtitle);
        final ImageView vCourseImage = vCourse.findViewById(R.id.component_info_image);

        vNameTitle.setText(user.getName());
        vNameSubtitle.setText(R.string.user_info_name);
        vNameImage.setImageResource(R.drawable.ic_id_number);

        vNumberTitle.setText(user.getNumber());
        vNumberSubtitle.setText(R.string.user_info_number);
        vNumberImage.setImageResource(R.drawable.ic_account_box);

        // divider.setVisibility(hasNoLevel && hasNoCourse ? View.GONE : View.VISIBLE);

        vLevel.setVisibility(hasNoLevel ? View.GONE : View.VISIBLE);
        if (!hasNoLevel) {
            vLevelTitle.setText(userView.getResources().getString(R.string.user_info_level_number, user.getOrdinalLevel()));
            vLevelSubtitle.setText(R.string.user_info_level);
            vLevelImage.setImageResource(R.drawable.ic_year_level);
        }

        vCourse.setVisibility(hasNoCourse ? View.GONE : View.VISIBLE);
        if (!hasNoCourse) {
            vCourseTitle.setText(user.getCourse());
            vCourseSubtitle.setText(R.string.user_info_course);
            vCourseImage.setImageResource(R.drawable.ic_school);
        }
    }
}
