package com.lph.pulltorefreshrecyclerlib.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by lph on 2017/10/29.
 * 当刷新的headerview添加到其余所有的headerview下面的时候，如果上下滑动添加的headerview，也会触发刷新headerview的刷新
 * 这里通过重写onTouchEvent 拦截父控件的onTouchEvent，来避免这个问题
 */

public class InterceptOnTouchEventHeaderView extends FrameLayout {

    private boolean mShouldIntercept = true;

    public InterceptOnTouchEventHeaderView(@NonNull Context context) {
        super(context);
    }

    public InterceptOnTouchEventHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptOnTouchEventHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public InterceptOnTouchEventHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(mShouldIntercept);
        return super.onTouchEvent(event);
    }

    public void setShouldIntercept(boolean mShouldIntercept) {
        this.mShouldIntercept = mShouldIntercept;
    }
}
