package com.systematix.itrack.components.chip;

import android.arch.persistence.room.Ignore;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.systematix.itrack.R;

public abstract class Chip implements Chipable {
    @Ignore private TextView view;
    @Ignore private OnChipClickListener listener;
    @Ignore private boolean selected;

    public interface OnChipClickListener {
        void onChipClick(Chipable chip);
    }

    @Override
    public int getChipTextColor() {
        return android.R.color.primary_text_light;
    }

    @Override
    public int getChipSelectedTextColor() {
        return android.R.color.primary_text_dark;
    }

    @Override
    public int getChipDrawable() {
        return R.drawable.chip_rounded;
    }

    @Override
    public int getChipSelectedDrawable() {
        return R.drawable.chip_selected_rounded;
    }

    @Override
    public void styleChip() {
        if (view == null) {
            return;
        }

        final int drawable = selected ? getChipSelectedDrawable() : getChipDrawable();
        final int colorId = selected ? getChipSelectedTextColor() : getChipTextColor();
        final int color = ContextCompat.getColor(view.getContext(), colorId);

        view.setText(getChipText());
        view.setTextSize(16F);
        view.setClickable(isChipClickable());
        view.setTextColor(color);
        view.setBackgroundResource(drawable);
    }

    @Override
    public boolean isChipSelected() {
        return selected;
    }

    @Override
    public void setChipSelected(boolean selected) {
        this.selected = selected;
        // style it
        styleChip();
    }

    @Override
    public void setChipView(TextView view) {
        this.view = view;
        styleChip();
    }

    @Override
    public TextView getChipView() {
        return view;
    }

    @Override
    public void setOnChipClickListener(OnChipClickListener listener) {
        this.listener = listener;
    }

    @Override
    public OnChipClickListener getOnChipClickListener() {
        return listener;
    }
}
