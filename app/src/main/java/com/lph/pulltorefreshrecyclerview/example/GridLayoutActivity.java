package com.lph.pulltorefreshrecyclerview.example;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;


public class GridLayoutActivity extends BaseExampleActivity {


    @Override
    protected RecyclerView.LayoutManager getLayoutManagerManager() {
        return new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle("GridLayout");
    }
}
