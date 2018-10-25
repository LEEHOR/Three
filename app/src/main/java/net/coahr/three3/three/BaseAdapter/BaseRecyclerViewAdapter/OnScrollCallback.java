package net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter;

import android.support.v7.widget.RecyclerView;

/**
 * Created by 李浩
 * 2018/4/17
 */
public interface OnScrollCallback {
   // 状态变化时
    void onStateChanged(RecyclerView recycler, int state);
    //上滑
    void onScrollUp(RecyclerView recycler, int dy);
    //到底
    void onScrollToBottom();
    //下滑
    void onScrollDown(RecyclerView recycler, int dy);
    //最上面
    void onScrollToTop();
    //左滑
    void onScrollToLeft(RecyclerView recycler, int dx);
     //最右边
     void onScrollToFarRight();
    //右滑
    void onScrollToRight(RecyclerView recycler, int dx);
    //最左边
    void onScrollToFarLeft();

}
