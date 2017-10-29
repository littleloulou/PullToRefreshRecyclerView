package com.lph.pulltorefreshrecyclerlib.widget.layoutmanager;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by lph on 2017/10/29.
 */

public class GridLayoutManagerExt extends GridLayoutManager {
    public GridLayoutManagerExt(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public GridLayoutManagerExt(Context context, int spanCount) {
        super(context, spanCount);
    }

    public GridLayoutManagerExt(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    private RecyclerView mRecyclerView;

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mRecyclerView = view;
    }

    @Override
    public boolean canScrollVertically() {
        return mRecyclerView == null ? super.canScrollVertically() : mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        mRecyclerView = null;
    }
}
