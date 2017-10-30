# PullToRefreshRecyclerView
## 简介

> 	基于andriod原生的recyclerview进行了扩展，实现了可选择的下拉刷新和上拉加载更多的基本功能，并且封装了对item的操作监听，提供了接口。实现了可以添加任意headerview的功能，可以动态添加，动态删除。
	
## 特性
*	支持下拉刷新
* 	支持上拉加载更多
*  支持添加自定义header
*  支持动态添加和删除header
*  支持更换RefreshHeader的展示位置,显示在所有headaer之前或者显示在所有header之后，可以进行配置
*  对itemview基本的操作，进行了接口的封装，提供了接口

## 如何使用(具体请看demo)

1. 在布局中使用此此实例
2. 在代码中进行配置

<pre><code>
 mRecyclerView = ((PullToRefreshRecyclerView) findViewById(R.id.recyclerView));
        //添加headerview
        mRecyclerView.setOnRefreshListener(this);
        mRecyclerView.setLoadMoreListener(this);
        mRecyclerView.setOnItemOperateListenerUser(this);
        mRecyclerView.setIsRefreshHeaderViewFirst(false);//刷新的headerview是否显示在第一个位置
        mAdapter = new ExampleAdapter();
        mLayoutManager = getLayoutManagerManager();
        mRecyclerView.setLayoutManager(mLayoutManager);
</code>
</pre>

> 需要注意的这几个set开头的方法，应该在 `mRecyclerView.setLayoutManager(mLayoutManager);`之前执行，否则会出问题
> 至于adapter按照自己要写的就行了，但是layoutmanager 需要使用后面带有Ext的，项目类库里面作者自己定义的，没有很大的改动，只是为了适配recyclerview的滑动，重写一个方法，如果你需要自己自定义，可以继承作者的，不会有影响。

## 应该注意的是

<pre>
	<code>
	//这个方法应该在recyclerView setLayoutManager之后再使用,否则会出问题
        mRecyclerView.addHeader(mHeader);
        mRecyclerView.setRefreshEnable(true); //是否开启刷新,默认为true
        mRecyclerView.setLoadMoreEnable(true);//是否开启加载更多,默认为true
        mRecyclerView.setIsRefreshHeaderViewFirst(false);//刷新的view是否显示在第一个位置,默认为false
	</code>
</pre>

## 效果图
* RefreshHeaderFirst
	
![image](https://github.com/littleloulou/PullToRefreshRecyclerView/blob/master/header_first.gif)
	

* RefreshHeaderLast

	
![image](https://github.com/littleloulou/PullToRefreshRecyclerView/blob/master/header_last.gif)
 
