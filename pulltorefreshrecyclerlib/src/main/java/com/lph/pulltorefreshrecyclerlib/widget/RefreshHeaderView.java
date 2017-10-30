package com.lph.pulltorefreshrecyclerlib.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lph.pulltorefreshrecyclerlib.R;


/**
 * Created by lph on 2017/4/15.
 * 下拉刷新的头部布局
 */

public class RefreshHeaderView extends LinearLayout {

    private int mMaxHeight;
    private int mChangeHeight;
    private float mCurrentHeight = 0;
    private View mPb;
    private View mIvArrow;
    private TextView mTvStatue;
    private ValueAnimator mHideAnimator;

    public RefreshHeaderView(Context context) {
        this(context, null);
    }

    public RefreshHeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
//        DisplayUtil.dip2px(getContext(), 120)
        mMaxHeight = Integer.MAX_VALUE;
        mChangeHeight = dip2px(getContext(), 60);
    }

    /**
     * 根据当前用户手指的移动,更新headView的高度
     *
     * @param deltaY 移动的高度差
     */
    public void updateHeight(float deltaY) {
        if (mCurrentState == State.REFRSHING) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        mCurrentHeight = mCurrentHeight + deltaY / 2;
        mCurrentHeight = mCurrentHeight > mMaxHeight ? mMaxHeight : mCurrentHeight;
        mCurrentHeight = mCurrentHeight < 0 ? 0 : mCurrentHeight;
        layoutParams.height = (int) mCurrentHeight;
        setLayoutParams(layoutParams);
        updateState();
    }

    /**
     * 随着高度的改变更改状态
     */
    private void updateState() {
        int ivHeight = mIvArrow.getHeight();
        int ivWidth = mIvArrow.getWidth();
        if (getHeight() > mChangeHeight) {
            if (mCurrentState != State.RELEASE_TO_REFRESH && mCurrentState != State.REFRSHING && mCurrentState != State.REFRESH_COMPLETE) {
                RotateAnimation rotateAnimation = new RotateAnimation(0, -180, ivHeight / 2, ivWidth / 2);
                rotateAnimation.setDuration(500);
                rotateAnimation.setFillAfter(true);
                mIvArrow.startAnimation(rotateAnimation);
                mTvStatue.setText(R.string.release_to_refresh);
                mCurrentState = State.RELEASE_TO_REFRESH;
            }
        } else {
            if (mCurrentState != State.PULL_TO_REFRESH && mCurrentState != State.REFRSHING && mCurrentState != State.REFRESH_COMPLETE) {
                RotateAnimation rotateAnimation = new RotateAnimation(-180, 0, ivHeight / 2, ivWidth / 2);
                rotateAnimation.setDuration(500);
                rotateAnimation.setFillAfter(true);
                mIvArrow.startAnimation(rotateAnimation);
                mTvStatue.setText(R.string.pull_to_refresh);
                mCurrentState = State.PULL_TO_REFRESH;
            }
        }

        if (getHeight() == 0) {
            mCurrentState = State.NO_STATE;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPb = findViewById(R.id.pb);
        mIvArrow = findViewById(R.id.iv_arrow);
        mTvStatue = ((TextView) findViewById(R.id.tv_statue));
    }


    private int mCurrentState = State.NO_STATE;

    public void showRefreshing() {
        int ivHeight = mIvArrow.getHeight();
        int ivWidth = mIvArrow.getWidth();
        //恢复箭头之前状态
        RotateAnimation rotateAnimation = new RotateAnimation(-180, 0, ivHeight / 2, ivWidth / 2);
        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);
        mIvArrow.startAnimation(rotateAnimation);
        mIvArrow.clearAnimation();
        mIvArrow.setVisibility(GONE);
        mPb.setVisibility(VISIBLE);
        mTvStatue.setText(R.string.refreshing);
        //更改当前状态为正在刷新
        mCurrentState = State.REFRSHING;
    }

    /**
     * 当执行actionUp时，更改状态
     */
    public void onActionUp() {
        switch (mCurrentState) {
            case State.PULL_TO_REFRESH:
                //如果是下拉刷新，则恢复到最初状态
                scrollHeader(0, 0, 500);
                break;
            case State.RELEASE_TO_REFRESH:
                //如果是释放刷新,则显示刷新
                showRefreshing();
                //迅速更改当前headerView高度到一定高度
                scrollHeader(0, mChangeHeight, 0);
                break;
            case State.REFRSHING:
                //缓慢更改当前headerView高度到一定高度
                scrollHeader(0, mChangeHeight, 500);
                break;
        }
    }

    public void scrollHeader(long delay, final int toHeight, final long duration) {
        if (getLayoutParams().height <= 0) return;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mHideAnimator = ValueAnimator.ofInt((int) mCurrentHeight, toHeight);
                mHideAnimator.setDuration(duration);
                mHideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        getLayoutParams().height = ((int) animation.getAnimatedValue());
                        requestLayout();
                    }
                });
                mHideAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (toHeight > 0) {
                            mCurrentHeight = toHeight;
                            if (mOnRefreshListener != null) {
                                mOnRefreshListener.onRefresh();
                            }
                        } else {
                            if (mCurrentState != State.NO_STATE) {
                                mCurrentState = State.NO_STATE;
                                mPb.setVisibility(GONE);
                                mIvArrow.setVisibility(VISIBLE);
                                mTvStatue.setText(R.string.pull_to_refresh);
                            }
                            mCurrentHeight = 0;
                        }

                    }
                });
                mHideAnimator.start();
            }
        }, delay);

    }

    /**
     * 是否应该执刷新操作
     *
     * @param mIsRefreshEnable     是否开启了刷新
     * @param firstVisiblePosition 第一个可见view的位置
     * @return 是否刷新
     */
    public boolean shouldRefresh(boolean mIsRefreshEnable, int firstVisiblePosition) {
        return mIsRefreshEnable && (firstVisiblePosition == 0 || (firstVisiblePosition == 1 && mCurrentState != State.NO_STATE));
    }

    /**
     * 刷新状态
     */
    public interface State {
        //没有进行过刷新
        int NO_STATE = -1;
        //下拉刷新
        int PULL_TO_REFRESH = 0;
        //释放刷新
        int RELEASE_TO_REFRESH = 1;
        //正在刷新
        int REFRSHING = 2;
        //刷新完成
        int REFRESH_COMPLETE = 3;
    }

    public void onRefreshComplete(boolean hasRefresh) {
        if (mCurrentState != State.REFRSHING) return;
        mCurrentState = State.REFRESH_COMPLETE;
        if (hasRefresh) {
            mTvStatue.setText(R.string.refresh_complete);
        } else {
            mTvStatue.setText(R.string.refresh_failure);
        }
        mPb.setVisibility(INVISIBLE);
        scrollHeader(500, 0, 500);
    }


    private OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.mOnRefreshListener = onRefreshListener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHideAnimator != null && mHideAnimator.isRunning()) {
            mHideAnimator.cancel();
            mHideAnimator = null;
        }
        requestLayout();
    }


    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public boolean shouldDisableRecyclerViewScroll() {
        return mCurrentState != State.NO_STATE;
    }
}
