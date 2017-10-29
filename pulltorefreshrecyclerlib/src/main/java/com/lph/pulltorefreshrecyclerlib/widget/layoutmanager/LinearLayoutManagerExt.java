package com.lph.pulltorefreshrecyclerlib.widget.layoutmanager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by lph on 2017/10/29.
 */

public class LinearLayoutManagerExt extends LinearLayoutManager {


    public LinearLayoutManagerExt(Context context) {
        super(context);
    }

    public LinearLayoutManagerExt(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LinearLayoutManagerExt(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
