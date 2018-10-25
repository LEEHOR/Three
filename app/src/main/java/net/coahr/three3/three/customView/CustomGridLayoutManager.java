package net.coahr.three3.three.customView;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

public class CustomGridLayoutManager extends LinearLayoutManager {

    private boolean isScrollEnabled = true;

    public CustomGridLayoutManager(Context context, int orientation, boolean reverseLayout, boolean isScrollEnabled) {
        super(context, orientation, reverseLayout);
        this.isScrollEnabled = isScrollEnabled;
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }


    @Override
    public boolean canScrollHorizontally() {
        return isScrollEnabled && super.canScrollHorizontally();
    }

    @Override
    public boolean canScrollVertically() {
        return false && super.canScrollVertically();
    }
}
