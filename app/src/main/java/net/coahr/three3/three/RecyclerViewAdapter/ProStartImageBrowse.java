package net.coahr.three3.three.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseRecyclerViewAdapter;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseViewHolder;
import net.coahr.three3.three.DBbase.ImagesDB;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.GlideCache.GlideApp;

import java.util.List;

public class ProStartImageBrowse extends BaseRecyclerViewAdapter<ImagesDB> {
    private ImageView imageView;
    private static final
    String TAG="ProStartImageBrowse";
    private Context mContext;
    private  int Id;
    private String url;
    private RequestOptions requestOptions;
    /**
     * 实例化具体实现
     *
     * @param context  父activity
     * @param datas    加载的数据
     * @param layoutId
     */
    public ProStartImageBrowse(Context context, List<ImagesDB> datas, int layoutId , Intent intent) {
        super(context, datas, layoutId , intent);
        this.mContext=context;
        requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    }

    @Override
    public void bindData(BaseViewHolder holder, ImagesDB data, int positions) {
        this.Id=data.getId();
        this.url=data.getImageName();
        Log.e(TAG, "bindData: 浏览图片"+data.getZibImagePath());
        imageView= (ImageView) holder.getView(R.id.browserImageView);
      //  Picasso.with(mContext).load("file://"+data.getZibImagePath()).into(imageView);
        GlideApp.with(mContext).asBitmap()
                .apply(requestOptions)
                .load(data.getZibImagePath()).into(imageView);

    }
    public  int getImagesPosition(){
        return Id;
    }
    public String getImageUrl(){
        return url;
    }

}
