package net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 *
 */
public class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    private SparseArray<View> views;
    public boolean flag = false;
    public int position;
    public T data;


    public BaseViewHolder(View view) {
        super(view);
        this.views = new SparseArray<>();

    }

    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }else {
            view= views.get(viewId);
        }
        return (T) view;
    }

    public View getRootView() {
        return itemView;
    }

}
