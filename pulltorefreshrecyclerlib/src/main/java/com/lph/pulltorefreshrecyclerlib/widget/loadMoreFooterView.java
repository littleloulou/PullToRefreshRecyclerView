package com.lph.pulltorefreshrecyclerlib.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.lph.pulltorefreshrecyclerlib.R;


/**
 * Created by lph on 2017/4/28.
 */

public class loadMoreFooterView extends FrameLayout {

    protected View mLoadingView;
    protected View mFailureView;

    private boolean mIsloading;

    public loadMoreFooterView(Context context) {
        this(context, null);
    }

    public loadMoreFooterView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public loadMoreFooterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public loadMoreFooterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init() {
        mLoadingView = LayoutInflater.from(getContext()).inflate(R.layout.layout_foot_loading_def, this, false);
        mFailureView = LayoutInflater.from(getContext()).inflate(R.layout.layout_foot_fialure_def, this, false);
        addView(mFailureView);
        addView(mLoadingView);
    }

    public void onLoading() {
        mLoadingView.setVisibility(VISIBLE);
        mFailureView.setVisibility(INVISIBLE);
    }

    public void onFailure() {
        mLoadingView.setVisibility(INVISIBLE);
        mFailureView.setVisibility(VISIBLE);
    }

    public void setIsLoadingMore(boolean isLoading) {
        mIsloading = isLoading;
    }

    public boolean isLoading() {
        return mIsloading;
    }
}
