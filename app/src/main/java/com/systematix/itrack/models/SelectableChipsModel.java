package com.systematix.itrack.models;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.systematix.itrack.components.chip.Chip;
import com.systematix.itrack.components.chip.Chipable;

import java.util.List;

public final class SelectableChipsModel<T extends Chipable> {
    private List<T> chips;
    private int selected;

    public interface OnModelInitListener<T extends Chipable> {
        List<T> getChips(List<T> chips);
    }

    public SelectableChipsModel(List<T> chips) {
        this.chips = chips;
        this.selected = -1;
    }

    private int getVisibleSize(List<T> chips) {
        return getVisibleSize(chips, -1);
    }

    private int getVisibleSize(List<T> chips, int maxSize) {
        // use either maxSize or chips length
        // choose chip length if maxSize is negative, or if chip length is less than maxSize
        int length = maxSize < 0 ? chips.size() : maxSize;
        return chips.size() < length ? chips.size() : length;
    }

    public void init(FlexboxLayout layout) {
        init(layout, -1, new OnModelInitListener<T>() {
            @Override
            public List<T> getChips(List<T> chips) {
                return chips;
            }
        });
    }

    public void init(FlexboxLayout layout, @NonNull OnModelInitListener<T> listener) {
        init(layout, -1, listener);
    }

    public void init(FlexboxLayout layout, int maxSize, @NonNull OnModelInitListener<T> listener) {
        final List<T> chips = listener.getChips(this.chips);
        // remove all views in the layout
        layout.removeAllViews();
        if (chips.isEmpty()) {
            // hide layout when there are no chips
            layout.setVisibility(View.GONE);
            return;
        }

        layout.setVisibility(View.VISIBLE);
        final int visibleSize = getVisibleSize(chips, maxSize);

        // then start adding the views hehe
        for (int i = 0; i < visibleSize; i++) {
            final Chipable chip = chips.get(i);
            final TextView tvChip = new TextView(layout.getContext());

            chip.setChipView(tvChip);

            FlexboxLayout.LayoutParams tvChipLayoutParams = (FlexboxLayout.LayoutParams) tvChip.getLayoutParams();
            if (tvChipLayoutParams == null) {
                tvChipLayoutParams = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
            }

            final int mx = 12;
            final int my = 18;
            tvChipLayoutParams.setMargins(mx, my, mx, my);
            tvChip.setLayoutParams(tvChipLayoutParams);

            // set listener and then add to layout
            tvChip.setOnClickListener(buildClickListener(chip));
            layout.addView(tvChip);
        }
    }

    private View.OnClickListener buildClickListener(final Chipable chip) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // should be clickable and must be the same view
                if (!chip.isChipClickable() || chip.getChipView() != v) {
                    return;
                }

                // when chip is clicked, unselect everything
                // assert that layout.getChildCount() is the same as getVisibleSize()
                int newSelected = -1;
                for (int i = 0; i < chips.size(); i++) {
                    final Chipable lChip = chips.get(i);
                    // if this is the current chip
                    // and if it is not yet selected, select it!
                    final boolean selectIt = chip == lChip && !lChip.isChipSelected();
                    lChip.setChipSelected(selectIt);
                    newSelected = selectIt ? i : newSelected;
                }
                // set selected
                selected = newSelected;

                final Chip.OnChipClickListener listener = chip.getOnChipClickListener();
                if (listener != null) {
                    listener.onChipClick(chip);
                }
            }
        };
    }

    // useful stuff
    public void collectionSetListener(Chip.OnChipClickListener listener) {
        for (final Chipable chip : chips) {
            chip.setOnChipClickListener(listener);
        }
    }

    public T getSelectedChip() {
        return hasSelectedChip() ? chips.get(selected) : null;
    }

    public boolean hasSelectedChip() {
        return selected >= 0;
    }
}
