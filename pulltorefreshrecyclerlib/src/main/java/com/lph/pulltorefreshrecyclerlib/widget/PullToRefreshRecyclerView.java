package com.lph.pulltorefreshrecyclerlib.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.lph.pulltorefreshrecyclerlib.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by lph on 2017/4/15.
 */
public class PullToRefreshRecyclerView extends RecyclerView implements RefreshHeaderView.OnRefreshListener {

    private static final int TYPE_BASE = 8888 * 6;
    /**
     * item 类型
     */
    public final static int TYPE_NORMAL = TYPE_BASE;
    public final static int TYPE_FOOTER = 1 + TYPE_BASE;
    public final static int TYPE_HEADER = 2 + TYPE_BASE;
    public final static int TYPE_EMPTY = 3 + TYPE_BASE;


    private boolean mIsLoadMoreEnable = true;//是否允许加载更多
    private boolean mIsRefreshEnable = true;//是否允许下拉刷新
    private boolean mIsRefreshHeaderViewFirst = false;
    private RefreshHeaderView.OnRefreshListener mOnRefreshListener;
    private loadMoreFooterView mLoadMoreFootView;

    //用来存放headerview的数量
    private List<View> mHeaders = new LinkedList<>();

    public void setRefreshEnable(boolean isRefreshEnable) {
        this.mIsRefreshEnable = isRefreshEnable;
    }

    private WrapperAdapter mWrapperAdapter;


    private int mLoadMorePosition;
    private LoadMoreListener mListener;
    private RefreshHeaderView mRefreshHeaderView;

    public PullToRefreshRecyclerView(Context context) {
        this(context, null);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    int mHeaderIndex = 0;

    private void initStateView() {

        if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            View placeholder = LayoutInflater.from(getContext()).inflate(R.layout.layout_placeholder, this, false);
            mHeaders.add(mHeaderIndex++, placeholder);
        }
        mRefreshHeaderView = ((RefreshHeaderView) LayoutInflater.from(getContext()).inflate(R.layout.layout_item_header_def, this, false));
        //创建headerview之后，添加到保存head的容器里面
        if (!mHeaders.contains(mRefreshHeaderView)) {
            //刷新的view应该是放在headerview的最后一个view
            mHeaders.add(mHeaderIndex++, mRefreshHeaderView);
        }

        mLoadMoreFootView = new loadMoreFooterView(getContext());
        mRefreshHeaderView.setOnRefreshListener(this);
    }


    /**
     * 添加出了刷新之外的头部布局
     * 这个方法需要在setLayoutManager之后调用
     *
     * @param view 头部布局
     */
    public void addHeader(View view) {
        //如果刷新的headerview放在了第一个位置,那么别的header事不允许放在第一个位置的,需要往后偏移
        if (mIsRefreshHeaderViewFirst) {
            mHeaders.add(mHeaderIndex++, view);
        } else {
            mHeaders.add(mHeaderIndex - 1, view);
            mHeaderIndex++;
        }
        if (mWrapperAdapter != null) {
            mWrapperAdapter.notifyDataSetChanged();
        }
    }

    public void removeHeader(int index) {
        mHeaders.remove(resetIndex(index));
        mWrapperAdapter.notifyDataSetChanged();
    }

    private void init() {
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (null != mListener && mIsLoadMoreEnable && !mLoadMoreFootView.isLoading() && dy > 0 && mShouldLoadMore) {
                    int lastVisiblePosition = getLastVisiblePosition();
                    if (lastVisiblePosition == mWrapperAdapter.getItemCount() - 1) {
                        setLoadingMore(true);
                        mLoadMorePosition = lastVisiblePosition;
                        mLoadMoreFootView.onLoading();
                        mListener.onLoadMore();
                    }
                }
            }
        });
    }


    /**
     * 设置加载更多的监听
     */
    public void setLoadMoreListener(LoadMoreListener listener) {
        mListener = listener;
    }

    /**
     * 设置正在加载更多
     */
    public void setLoadingMore(boolean loadingMore) {
        mLoadMoreFootView.setIsLoadingMore(loadingMore);
    }


    @Override
    public void onRefresh() {
        if (mIsRefreshEnable && mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }

    /**
     * 加载更多监听
     */
    public interface LoadMoreListener {
        /**
         * 加载更多
         */
        void onLoadMore();
    }


    /**
     *
     */
    public class WrapperAdapter extends Adapter<ViewHolder> {
        /**
         * 数据adapter
         */
        private Adapter mInternalAdapter;

        public WrapperAdapter(Adapter adapter) {
            mInternalAdapter = adapter;
            mInternalAdapter.registerAdapterDataObserver(mAdapterDataObserver);
        }

        private AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mWrapperAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                mWrapperAdapter.notifyItemRangeChanged(resetPosition(positionStart), itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);
                mWrapperAdapter.notifyItemRangeChanged(resetPosition(positionStart), itemCount, payload);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                mWrapperAdapter.notifyItemRangeInserted(resetPosition(positionStart), itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                mWrapperAdapter.notifyItemRangeRemoved(resetPosition(positionStart), itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                mWrapperAdapter.notifyItemMoved(resetPosition(fromPosition), resetPosition(toPosition));
            }
        };

        private int resetPosition(int positionStart) {
            return mIsRefreshEnable ? positionStart + mHeaders.size() : positionStart + mHeaders.size() - 1;
        }


        private int mCurrentHeaderPosition;

        @Override
        public int getItemViewType(int position) {
         /*   if (mInternalAdapter.getItemCount() == 0 && mHeaders.size() == 1) {
                return TYPE_EMPTY;
            }
*/
            if (mInternalAdapter.getItemCount() == 0) {
                if (getLayoutManager() instanceof StaggeredGridLayoutManager && mHeaders.size() == 2) {
                    return TYPE_EMPTY;
                } else if (mHeaders.size() == 1) {
                    return TYPE_EMPTY;
                }
            }

       /*     if (position == 0 && mIsRefreshEnable) {
                return TYPE_HEADER;
            }
          */

            int otherHeadCount = mHeaders.size() - 1;
            if (mIsRefreshEnable && position <= otherHeadCount + 1 - 1) {
                mCurrentHeaderPosition = position;
                return TYPE_HEADER;
            }
            if (!mIsRefreshEnable && position <= otherHeadCount - 1) {
                mCurrentHeaderPosition = position;
                return TYPE_HEADER;
            }

            if (isFooter(position)) {
                return TYPE_FOOTER;
            }
            return TYPE_NORMAL;
        }

        private boolean isFooter(int position) {
            return mIsLoadMoreEnable && getItemCount() - 1 == position;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_FOOTER) {
                return new FooterViewHolder(mLoadMoreFootView);
            } else if (viewType == TYPE_HEADER) {
                Logger.d("mCurrentHeaderPosition" + mCurrentHeaderPosition);
                HeaderViewHolder headerViewHolder = new HeaderViewHolder(mHeaders.get(mCurrentHeaderPosition));
                headerViewHolder.setIsRecyclable(false);
                return headerViewHolder;
            } else if (viewType == TYPE_EMPTY) {
                return new EmptyViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_item_empty_def, parent, false));
            } else {
                return mInternalAdapter.onCreateViewHolder(parent, viewType);
            }
        }


        class FooterViewHolder extends ViewHolder {

            FooterViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class HeaderViewHolder extends ViewHolder {

            HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        class EmptyViewHolder extends ViewHolder {

            public EmptyViewHolder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int type = getItemViewType(position);
            if (type != TYPE_FOOTER && type != TYPE_HEADER && type != TYPE_EMPTY) {
                //对应的位置要往后偏移，如果刷新要减去刷新的和自定义的view的数目,如果没有刷新，只减去自定义的header
                if (mIsRefreshEnable) {
                    position = position - mHeaders.size();
                } else {
                    position = position - mHeaders.size() + 1;
                }
                mInternalAdapter.onBindViewHolder(holder, position);
            }
        }

        @Override
        public final void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            if (holder instanceof HeaderViewHolder || holder instanceof FooterViewHolder || holder instanceof EmptyViewHolder) {
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                    ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
                }
            } else {
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                    ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(false);
                }
            }
            //避免由于数据不满一页，而显示加载更多
            if (holder.getItemViewType() == TYPE_FOOTER) {
                if (getFirstVisiblePosition() <= 1) {
                    holder.itemView.setVisibility(GONE);
                    mShouldLoadMore = false;
                } else {
                    holder.itemView.setVisibility(VISIBLE);
                    mShouldLoadMore = true;
                }
            }
            onViewAttachedToWindowCustom(holder);
        }

        protected void onViewAttachedToWindowCustom(ViewHolder holder) {

        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            if (holder.itemView instanceof RefreshHeaderView) {

            }
        }

        @Override
        public int getItemCount() {
            int count = mInternalAdapter.getItemCount();
            if (count == 0)
                return mHeaders.size() > 1 ? mHeaders.size() : 1; //当展示的内容为空时，需要显示一个空的item
            if (mIsLoadMoreEnable) count++;
//            if (mIsRefreshEnable) count++;
            count += mHeaders.size(); //加上所有的头view
            if (!mIsRefreshEnable) count--; //如果没有开启刷新,则要减掉刷新的item
            return count;
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            mInternalAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter != null) {
            mWrapperAdapter = new WrapperAdapter(adapter);
        }

        super.setAdapter(mWrapperAdapter);
    }

    /**
     * 获取最后一条展示的位置
     */
    private int getLastVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions =
                    layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得最大的位置
     */
    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    /**
     * 获得最大的位置
     */
    private int getMinPosition(int[] positions) {
        int size = positions.length;
        int minPosition = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            Logger.d("getMinPosition" + positions[i]);
            minPosition = Math.min(minPosition, positions[i]);
        }
        return minPosition;
    }

    /**
     * 设置是否支持自动加载更多
     */
    public void setLoadMoreEnable(boolean autoLoadMore) {
        mIsLoadMoreEnable = autoLoadMore;
    }

    public void onLoadCompleted(boolean hasMore) {
//        setLoadMoreEnable(hasMore);
        if (!hasMore) {
            mLoadMoreFootView.onFailure();
            setLoadingMore(false);
            return;
        }
        if (mLoadMoreFootView.isLoading()) {
            getAdapter().notifyItemRemoved(mLoadMorePosition);
        }
        setLoadingMore(false);
    }

    @Override
    public void onChildAttachedToWindow(View child) {
        super.onChildAttachedToWindow(child);
        if (!ViewCompat.hasOnClickListeners(child)) {
            child.setClickable(true);
            child.setOnClickListener(mOnItemOperateListenerInternalInternal);
        }
        child.setOnLongClickListener(mOnItemOperateListenerInternalInternal);
    }

    public interface onItemOperateListenerInternal extends OnClickListener, OnLongClickListener {

    }

    private final onItemOperateListenerInternal mOnItemOperateListenerInternalInternal = new onItemOperateListenerInternal() {
        @Override
        public void onClick(View v) {
            if (mOnItemOperateListenerUser != null) {
                int position = getChildAdapterPosition(v);
                if (invalidPosition(position)) return;
                mOnItemOperateListenerUser.onItemClickListener(v, getPosition(position));
            }
        }

        private int getPosition(int position) {
            position = mIsRefreshEnable ? position - mHeaders.size() : position - mHeaders.size() + 1;
            return position < 0 ? 0 : position;
        }

        @Override
        public boolean onLongClick(View v) {
            if (mOnItemOperateListenerUser != null) {
                int position = getChildAdapterPosition(v);
                return !invalidPosition(position) && mOnItemOperateListenerUser.onItemLongClickListener(v, getPosition(position));
            }
            return false;
        }
    };

    private boolean invalidPosition(int position) {
        if (mIsRefreshEnable || mHeaders.size() > 1) {
            int itemViewType = mWrapperAdapter.getItemViewType(position);
            if (itemViewType == TYPE_HEADER) {
                return true;
            }
        }
        if (mIsLoadMoreEnable) {
            int itemViewType = mWrapperAdapter.getItemViewType(position);
            if (itemViewType == TYPE_FOOTER) {
                return true;
            }
        }
        if (position == -1) return true;
        return mWrapperAdapter.getItemViewType(position) == TYPE_EMPTY;
    }

    public interface onItemOperateListenerUser {
        void onItemClickListener(View view, int position);

        boolean onItemLongClickListener(View view, int position);
    }

    private onItemOperateListenerUser mOnItemOperateListenerUser;


    public void setOnItemOperateListenerUser(onItemOperateListenerUser onItemOperateListenerUser) {
        this.mOnItemOperateListenerUser = onItemOperateListenerUser;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (layout instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layout;
            final int spanCount = gridLayoutManager.getSpanCount();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int itemViewType = mWrapperAdapter.getItemViewType(position);
                    return (itemViewType == TYPE_FOOTER || itemViewType == TYPE_HEADER || itemViewType == TYPE_EMPTY) ? spanCount : 1;
                }
            });
        }
        super.setLayoutManager(layout);
        initStateView();

    }

    @Override
    public boolean canScrollVertically(int direction) {
        return !mRefreshHeaderView.shouldDisableRecyclerViewScroll();
    }

    private float mLastY;
    private boolean mShouldLoadMore;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            //需要在这里记录最初的点击事件的位置,因为事件会被子view拦截onTouchEvent 中的 MotionEvent.ACTION_DOWN
            //是不会执行的
            mLastY = e.getRawY();
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = e.getRawY() - mLastY;
                Logger.d("shouldrefresh:" + shouldRefresh());
                //解决当刷新的headerview放在最后的位置时，再其它header区域滑动触发刷新的问题
                if (!mIsRefreshHeaderViewFirst && mHeaders.size() > 1) {
                    //获取当前移动的y轴相对于recyclerview的距离
                    float y = e.getY();
                    View view = mHeaders.get(mHeaders.size() - 2);
                    view.getBottom();
                    if (y < view.getBottom()) {
                        break;
                    }
                }
                if (shouldRefresh()) {
                    mRefreshHeaderView.updateHeight(deltaY);
                }
                mLastY = e.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                mRefreshHeaderView.onActionUp();
                break;
        }

        return super.onTouchEvent(e);
    }

    public boolean shouldRefresh() {
        Logger.d("getFirstVisiblePosition" + getFirstVisiblePosition());
        return mRefreshHeaderView.shouldRefresh(mIsRefreshEnable, getFirstVisiblePosition());
//        return mIsRefreshEnable && getFirstVisiblePosition() == 0;
    }

    private int getFirstVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        } else {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] firstPositions =
                    layoutManager.findFirstCompletelyVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMinPosition(firstPositions);
        }
        return position;
    }

    /**
     * 刷新完毕的回调
     *
     * @param hasRefresh 是否刷新
     */
    public void onRefreshComplete(boolean hasRefresh) {
        mRefreshHeaderView.onRefreshComplete(hasRefresh);
    }

    /**
     * 设置刷新监听
     *
     * @param listener 监听回调
     */
    public void setOnRefreshListener(RefreshHeaderView.OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    /**
     * 设置刷新的headview显示再recyclerview首个item的位置，还是显示在所有header最后的item位置
     *
     * @param mIsRefreshHeaderViewFirst
     */

    public void setIsRefreshHeaderViewFirst(boolean mIsRefreshHeaderViewFirst) {
        this.mIsRefreshHeaderViewFirst = mIsRefreshHeaderViewFirst;
    }

    public boolean isIsRefreshHeaderViewFirst() {
        return mIsRefreshHeaderViewFirst;
    }

    private int resetIndex(int initIndex) {
        if (mIsRefreshHeaderViewFirst) {
            if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
                return initIndex + 2;
            } else {
                return initIndex + 1;
            }
        } else {
            if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
                return initIndex + 1;
            }
            return initIndex;
        }
    }
}
