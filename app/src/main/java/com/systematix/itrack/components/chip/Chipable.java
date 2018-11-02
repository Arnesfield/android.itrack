package com.systematix.itrack.components.chip;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.widget.TextView;

public interface Chipable {
    String getChipText();
    boolean isChipClickable();
    @ColorRes int getChipTextColor();
    @ColorRes int getChipSelectedTextColor();
    @DrawableRes int getChipDrawable();
    @DrawableRes int getChipSelectedDrawable();
    void styleChip();
    boolean isChipSelected();
    void setChipSelected(boolean selected);
    void setChipView(TextView view);
    TextView getChipView();
    void setOnChipClickListener(Chip.OnChipClickListener listener);
    Chip.OnChipClickListener getOnChipClickListener();
}
