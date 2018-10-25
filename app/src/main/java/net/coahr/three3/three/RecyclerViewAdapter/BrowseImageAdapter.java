package net.coahr.three3.three.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseRecyclerViewAdapter;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseViewHolder;
import net.coahr.three3.three.DBbase.ImagesDB;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.GlideCache.GlideApp;

import java.util.List;

/**
 * Created by yuwei on 2018/4/23.
 */

public class BrowseImageAdapter extends BaseRecyclerViewAdapter {
    private static final
    String TAG="BrowseImageAdapter";
    private Context mContext;
    private ImageView mImageView;
    private RequestOptions requestOptions;
    /**
     * 实例化具体实现
     *
     * @param context  父activity
     * @param datas    加载的数据
     * @param layoutId
     */
    public BrowseImageAdapter(Context context, List datas, int layoutId , Intent intent) {
        super(context, datas, layoutId , intent);
        this.mContext=context;
        requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);

    }

    @Override
    public void bindData(BaseViewHolder holder,  Object data, int position) {

        ImagesDB image = (ImagesDB) data;
        //mImageView.setImageBitmap(BitmapFactory.decodeFile(image.getZibImagePath()));
        GlideApp.with(mContext).asBitmap()
                .apply(requestOptions)
                .load(image.getZibImagePath()).into(mImageView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        findUI(holder.itemView);
        bindData((BaseViewHolder) holder, datas.get(position) , position);
    }

    @Override
    public void findUI(View v) {
        super.findUI(v);

        mImageView = v.findViewById(R.id.browserImageView);

    }
}
