package com.systematix.itrack.models;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.systematix.itrack.R;

public final class ProgressTextModel {
    private final TextView title;
    private final TextView subtitle;
    private final ProgressBar progressBar;

    public ProgressTextModel(View view) {
        this.title = view.findViewById(R.id.component_progress_text_title);
        this.subtitle = view.findViewById(R.id.component_progress_text_subtitle);
        this.progressBar = view.findViewById(R.id.component_progress_text_progress);
    }

    public void setProgress(final float value, final float total) {
        setProgress(value, total, 0);
    }

    public void setProgress(final float value, final float total, int startAt) {
        setSubtitleValue(total);
        // some animation

        // make sure startAt is less than value!
        final int targetValue = Math.round(value);
        final boolean increment = startAt <= targetValue;

        for (int i = startAt; increment ? i <= targetValue : i >= targetValue; i += increment ? 1 : -1) {
            final int currValue = i;
            final int progress = getProgress(i, total);

            final int delayProgress = getProgress(Math.abs(i - startAt), total);

            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    title.setText(String.valueOf(currValue));
                    title.setTextColor(getProgressColor(title.getContext(), progress));
                    setProgressBar(progress);
                }
                // slow down when higher
            };

            if (i == startAt) {
                runnable.run();
            } else {
                new Handler().postDelayed(runnable, (delayProgress * (delayProgress / 8)) + 100);
            }
        }
    }

    private void setProgressBar(int progress) {
        progressBar.setProgress(progress);
        // set also color
        final Drawable drawable = progressBar.getProgressDrawable();

        if (!(drawable instanceof LayerDrawable)) {
            return;
        }

        final Drawable shape = ((LayerDrawable) drawable).getDrawable(1);
        final int color = getProgressColor(progressBar.getContext(), progress);
        shape.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    private void setSubtitleValue(float fValue) {
        final int value = Math.round(fValue);
        final String str = subtitle.getResources().getString(R.string.attendance_view_subtitle, value);
        subtitle.setText(str);
    }

    private int getProgress(float value, float total) {
        int progress = Math.round(value / total * 100);
        // if progress is greater than 100 or less than 0 :D
        progress = progress < 0 ? 0 : progress;
        progress = progress > 100 ? 100 : progress;
        return progress;
    }

    private int getProgressColor(Context context, int progress) {
        return ContextCompat.getColor(context, getProgressColorRes(progress));
    }

    @ColorRes private int getProgressColorRes(int progress) {
        if (progress < 20) {
            return R.color.colorProgress1;
        } else if (progress < 40) {
            return R.color.colorProgress2;
        } else if (progress < 60) {
            return R.color.colorProgress3;
        } else if (progress < 80) {
            return R.color.colorProgress4;
        } else {
            return R.color.colorProgress5;
        }
    }
}
