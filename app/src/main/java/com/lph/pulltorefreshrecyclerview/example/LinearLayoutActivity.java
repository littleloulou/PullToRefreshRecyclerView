package com.lph.pulltorefreshrecyclerview.example;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lph.pulltorefreshrecyclerlib.widget.layoutmanager.LinearLayoutManagerExt;


public class LinearLayoutActivity extends BaseExampleActivity {

    @Override
    protected RecyclerView.LayoutManager getLayoutManagerManager() {

        return new LinearLayoutManagerExt(this, LinearLayoutManager.VERTICAL, false);
    }


    @Override
    protected void onStart() {
        super.onStart();
        boolean enableLoadMore = getIntent().getBooleanExtra("enableLoadMore", true);
        boolean enableRefresh = getIntent().getBooleanExtra("enableRefresh", true);
        String title = "LinearLayout";
        if (enableLoadMore && !enableRefresh) {
            title = "disableRefresh";
        } else if (!enableLoadMore && enableRefresh) {
            title = "disableLoadMore";
        } else if (!enableLoadMore && !enableRefresh) {
            title = "normal";
        }

        getSupportActionBar().setTitle(title);

        mRecyclerView.setLoadMoreEnable(enableLoadMore);
        mRecyclerView.setRefreshEnable(enableRefresh);
    }
}
