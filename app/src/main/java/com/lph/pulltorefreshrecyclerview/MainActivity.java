package com.lph.pulltorefreshrecyclerview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.lph.pulltorefreshrecyclerview.example.GridLayoutActivity;
import com.lph.pulltorefreshrecyclerview.example.LinearLayoutActivity;
import com.lph.pulltorefreshrecyclerview.example.StaggeredLayoutActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void linearLayout(View view) {
        startActivity(new Intent(this, LinearLayoutActivity.class));
    }

    public void gridLayout(View view) {
        startActivity(new Intent(this, GridLayoutActivity.class));
    }

    public void staggerLayout(View view) {
        startActivity(new Intent(this, StaggeredLayoutActivity.class));
    }


    public void noRefresh(View view) {
        Intent intent = new Intent(this, LinearLayoutActivity.class);
        intent.putExtra("enableRefresh", false);
        startActivity(intent);
    }

    public void noLoadMore(View view) {
        Intent intent = new Intent(this, LinearLayoutActivity.class);
        intent.putExtra("enableLoadMore", false);
        startActivity(intent);
    }

    public void normal(View view) {
        Intent intent = new Intent(this, LinearLayoutActivity.class);
        intent.putExtra("enableRefresh", false);
        intent.putExtra("enableLoadMore", false);
        startActivity(intent);
    }
}
