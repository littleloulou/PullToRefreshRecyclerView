package com.lph.pulltorefreshrecyclerview.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lph.pulltorefreshrecyclerlib.widget.InterceptOnTouchEventHeaderView;
import com.lph.pulltorefreshrecyclerlib.widget.Logger;
import com.lph.pulltorefreshrecyclerlib.widget.PullToRefreshRecyclerView;
import com.lph.pulltorefreshrecyclerlib.widget.RefreshHeaderView;
import com.lph.pulltorefreshrecyclerview.R;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by lph on 2017/4/27.
 */

public abstract class BaseExampleActivity extends AppCompatActivity implements PullToRefreshRecyclerView.LoadMoreListener, RefreshHeaderView.OnRefreshListener, PullToRefreshRecyclerView.onItemOperateListenerUser, View.OnClickListener {
    private SimpleDateFormat simpleDateFormat;
    protected PullToRefreshRecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<String> mDatas = new ArrayList<>();
    protected LinearLayoutActivity.ExampleAdapter mAdapter;
    private View mHeader;


    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_example_base_layout);
        mRecyclerView = ((PullToRefreshRecyclerView) findViewById(R.id.recyclerView));
        //添加headerview
        mRecyclerView.setOnRefreshListener(this);
        mRecyclerView.setLoadMoreListener(this);
        mRecyclerView.setOnItemOperateListenerUser(this);
        mAdapter = new ExampleAdapter();
        mLayoutManager = getLayoutManagerManager();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setIsRefreshHeaderViewFirst(false);
        addHeader();
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 添加出了刷新的headervie之外的header
     */
    protected void addHeader() {
        mHeader = LayoutInflater.from(this).inflate(R.layout.layout_header_first, mRecyclerView, false);
        mHeader.findViewById(R.id.iv_add).setOnClickListener(this);
        mHeader.findViewById(R.id.iv_sub).setOnClickListener(this);
        if (mRecyclerView.isIsRefreshHeaderViewFirst()) {
            index = 1;
        }
        mRecyclerView.addHeader(mHeader, index);

    }

    @Override
    public void onLoadMore() {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDatas.size() > 60) {
                    mRecyclerView.onLoadCompleted(false);
                    return;
                }
                for (int i = 0; i < 10; i++) {
                    mDatas.add("loadMore" + i);
                }
                mRecyclerView.onLoadCompleted(true);
                mAdapter.notifyDataSetChanged();
            }
        }, 1000);
    }

    @Override
    public void onRefresh() {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mDatas.size(); i++) {
                    mDatas.set(i, "Item refresh  " + getDateString());
                }
                mRecyclerView.onRefreshComplete(true);
                mAdapter.notifyDataSetChanged();
            }
        }, 2000);
    }

    @Override
    public void onItemClickListener(View view, int position) {
        Toast.makeText(view.getContext(), "onClick" + mDatas.get(position), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClickListener(View view, int position) {
        Toast.makeText(view.getContext(), "onLongClick" + mDatas.get(position), Toast.LENGTH_SHORT).show();
        return true;
    }

    protected abstract RecyclerView.LayoutManager getLayoutManagerManager();


    protected void initData() {
        for (int i = 0; i < 20; i++) {
            mDatas.add("item" + i);
        }
        mAdapter.notifyDataSetChanged();
    }


    public String getDateString() {
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat("hh:mm:ss", Locale.CHINA);
        }
        return simpleDateFormat.format(new Date());
    }

    int index = 0;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add:
                mHeader = LayoutInflater.from(this).inflate(R.layout.layout_header_first, mRecyclerView, false);
                mHeader.findViewById(R.id.iv_add).setOnClickListener(this);
                mHeader.findViewById(R.id.iv_sub).setOnClickListener(this);
                mRecyclerView.addHeader(mHeader, ++index);
                break;
            case R.id.iv_sub:
                mRecyclerView.removeHeader(index);
                index--;
                Logger.d("index:" + index);
                break;
        }
    }

    class ExampleAdapter extends RecyclerView.Adapter<ExampleVH> {
        @Override
        public ExampleVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return getViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ExampleVH holder, int position) {
            ((TextView) holder.itemView.findViewById(R.id.tv_title)).setText(mDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }


    }

    class ExampleVH extends RecyclerView.ViewHolder {
        ExampleVH(View itemView) {
            super(itemView);
        }
    }

    protected ExampleVH getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_example, parent, false);
        return new ExampleVH(view);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_operate, menu);
        return true;
    }

    private int mCurrent;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                mDatas.clear();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.menu_add:
                mDatas.add("addData" + (++mCurrent));
                mAdapter.notifyItemInserted(mAdapter.getItemCount());
                break;
            case R.id.menu_add_more:
                for (int i = 0; i < 10; i++) {
                    mDatas.add("addData" + i);
                }
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.menu_delete:
                mDatas.remove(mDatas.size() - 1);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.menu_delete_more:
                if (mDatas.size() > 10) {
                    for (int i = 0; i < 10; i++) {
                        mDatas.remove(i);
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
        }
        return true;
    }


}
