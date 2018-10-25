package net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class BaseRecyclerViewAdapter<T> extends  RecyclerView.Adapter<BaseViewHolder> {
protected Context context;
protected LayoutInflater inflater;
protected List<T> datas;
protected int layoutId;
protected OnItemClickListner onItemClickListner;//单击事件
protected OnItemLongClickListner onItemLongClickListner;//长按单击事件
protected boolean clickFlag = true;//单击事件和长单击事件的屏蔽标识
private   Context mContext;
private   int     windowWidth;
private   int     windowHeight;
private   Intent    mIntent;
private  int position;



    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public Context getContext() {
        return context;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public int getWindowHeight()
    {
        return windowHeight;
    }

    public Intent getmIntent() {
        return mIntent;
    }

    public void setmIntent(Intent mIntent) {
        this.mIntent = mIntent;
    }

    /**
     *          实例化具体实现
     * @param context
     *          父activity
     * @param datas
     *          加载的数据
     * @param layoutId
     *          布局资源ID
     */
    public BaseRecyclerViewAdapter(Context context, List<T> datas, int layoutId , Intent intent) {
        this.context = context;
        this.datas = datas;
        this.layoutId = layoutId;
        this.inflater = LayoutInflater.from(context);
        this.mIntent = intent;
    }
    public void Update( List<T> data){
        this.datas=data;
        notifyDataSetChanged();
    }
    public  void Updatesingle(List<T> data){
        this.datas.addAll(data);
        notifyDataSetChanged();
    }
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder holder = new BaseViewHolder(inflater.inflate(layoutId, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

        bindData(holder, datas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    /**
     * 子控件
     * @param holder
     *              视图管理器
     * @param data
     *               数据
     * @param position
     *                点击的item
     */
   public abstract void bindData(BaseViewHolder holder, T data, int position);

    public void setOnItemClickListener(OnItemClickListner onItemClickListener) {
        this.onItemClickListner = onItemClickListener;
    }

    public void setOnItemLongClickLisetner(OnItemLongClickListner onItemLongClickListener) {
        this.onItemLongClickListner = onItemLongClickListener;
    }

    public interface OnItemClickListner {
        void onItemClickListener(View v, int position , Intent intent,Context context);
    }

    public interface OnItemLongClickListner {
        void onItemLongClickListener(View v, int position);
    }
    //获取item子控件
    public void findUI(View v)
    {

    }
}
