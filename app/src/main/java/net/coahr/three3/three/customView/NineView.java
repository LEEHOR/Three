package net.coahr.three3.three.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import net.coahr.three3.three.Util.GlideCache.GlideApp;
import net.coahr.three3.three.customView.NineViewRoot.NineGridLayout;
import net.coahr.three3.three.customView.NineViewRoot.RatioImageView;

import java.util.List;

/**
 * Created by 李浩
 * 2018/5/10
 */
public class NineView extends NineGridLayout {
    private Context context;
    private int itemPosition;
    private OnClickNineView clickNineView;
    private  RequestOptions requestOptions;
    public NineView(Context context) {
        super(context);
    }

    public NineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
         requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    }

    @Override
    protected void displayImage(int position, RatioImageView imageView, final String url) {
        if(context!=null){
           // Picasso.with(context).load(url).config(Bitmap.Config.RGB_565).into(imageView);
            GlideApp.with(mContext).asBitmap()
                    .apply(requestOptions)
                    .load(url).into(imageView);
        }
    }

    @Override
    protected void onClickImage(int position, String url, List<String> urlList, ImageView imageView) {
        if (clickNineView!=null){
            clickNineView.OnClickImages(position,url,urlList,imageView);
        }
    }

    @Override
    protected void onLongClickImage(int position, String url, List<String> urlList, ImageView imageView) {
        if (clickNineView!=null){
            clickNineView.OnLongClickImages(position,url,urlList,imageView);
        }
    }

    public interface OnClickNineView{
        void OnClickImages(int position, String url, List<String> urlList, ImageView imageView);
        void OnLongClickImages(int position, String url, List<String> urlList, ImageView imageView);
    }
    public void setOnClickNineView(OnClickNineView clickNineView){
        this.clickNineView=clickNineView;
    }

}

