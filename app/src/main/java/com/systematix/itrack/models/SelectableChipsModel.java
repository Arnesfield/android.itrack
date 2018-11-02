package com.systematix.itrack.models;

import android.view.View;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.systematix.itrack.components.chip.Chip;
import com.systematix.itrack.components.chip.Chipable;

import java.util.List;

public final class SelectableChipsModel<T> {
    private List<T> chips;
    private FlexboxLayout layout;
    private int size;
    private int selected;

    public SelectableChipsModel(FlexboxLayout layout, List<T> chips) {
        this(layout, chips, -1);
    }

    public SelectableChipsModel(FlexboxLayout layout, List<T> chips, int size) {
        this.layout = layout;
        this.chips = chips;
        this.size = size;
        this.selected = -1;
        init();
    }

    private int getVisibleSize() {
        // use either size or chips length
        // choose chip length if size is negative, or if chip length is less than size
        int length = size < 0 ? chips.size() : size;
        return chips.size() < length ? chips.size() : length;
    }

    private void init() {
        // remove all views in the layout
        layout.removeAllViews();
        if (chips.isEmpty()) {
            // hide layout when there are no chips
            layout.setVisibility(View.GONE);
            return;
        }

        layout.setVisibility(View.VISIBLE);

        // then start adding the views hehe
        for (int i = 0; i < getVisibleSize(); i++) {
            final T gChip = chips.get(i);
            // skip if not Chipable
            if (!(gChip instanceof Chipable)) {
                continue;
            }
            final Chipable chip = (Chipable) gChip;
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
                int newSelected = -1;
                for (int i = 0; i < layout.getChildCount(); i++) {
                    final T gChip = chips.get(i);
                    // skip if not Chipable
                    if (!(gChip instanceof Chipable)) {
                        continue;
                    }
                    final Chipable lChip = (Chipable) gChip;

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
        for (final T gChip : chips) {
            if (!(gChip instanceof Chipable)) {
                continue;
            }

            final Chipable chip = (Chipable) gChip;
            chip.setOnChipClickListener(listener);
        }
    }

    public Chipable getSelectedChip() {
        if (!hasSelectedChip()) {
            return null;
        }
        final T gChip = chips.get(selected);
        return gChip instanceof Chipable ? (Chipable) gChip : null;
    }

    public boolean hasSelectedChip() {
        return selected >= 0;
    }
}
