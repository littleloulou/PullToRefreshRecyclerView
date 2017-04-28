package com.lph.pulltorefreshrecyclerview.example;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by lph on 2017/4/28.
 */

public class StaggeredLayoutActivity extends BaseExampleActivity {
    @Override
    protected RecyclerView.LayoutManager getLayoutManagerManager() {
        return new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle("StaggeredLayout");
    }
}
