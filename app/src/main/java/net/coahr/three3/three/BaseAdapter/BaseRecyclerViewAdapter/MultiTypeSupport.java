package net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter;

/**
 * Created by 李浩 on 2018/4/4.
 */

public interface MultiTypeSupport<T> {

    int getLayoutId(T item, int position);

}
