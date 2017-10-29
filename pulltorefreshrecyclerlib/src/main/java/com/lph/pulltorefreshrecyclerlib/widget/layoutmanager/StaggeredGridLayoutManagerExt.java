package com.lph.pulltorefreshrecyclerlib.widget.layoutmanager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by lph on 2017/10/29.
 */

public class StaggeredGridLayoutManagerExt extends StaggeredGridLayoutManager {
    public StaggeredGridLayoutManagerExt(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public StaggeredGridLayoutManagerExt(int spanCount, int orientation) {
        super(spanCount, orientation);
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
