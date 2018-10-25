package net.coahr.three3.three.customView;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.coahr.three3.three.R;


/**
 * Created by 李浩 on 2018/4/1.
 */

public class TopTitleBar extends LinearLayout {
   private RelativeLayout Rl, Rz;
   private TextView tv;
   private ImageView imageView;
    private TitleOnClickListener titleOnClickListener;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public TopTitleBar(Context context, AttributeSet attrs) {
        super(context,attrs);
        /**加载布局文件*/
        LayoutInflater.from(context).inflate(R.layout.layout_fragment_toolbar, this,true);
        /*得到父控件*/
        Rl= findViewById(R.id.top_bar);
           /*得到子Re*/
           Rz=findViewById(R.id.fragment_top);
           tv= findViewById(R.id.tv_top);
        imageView= findViewById(R.id.searchimage_top);
       Rz.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View view) {
               if (titleOnClickListener != null) {
                   titleOnClickListener.search_top();
               }
           }
       });
    }

    /**
     * 设置标题的点击监听
     *
     * @param titleOnClickListener
     */
    public void setOnTitleClickListener(TitleOnClickListener titleOnClickListener) {
        this.titleOnClickListener = titleOnClickListener;
    }

    /**
     * 监听标题点击接口
     */
    public interface TitleOnClickListener {

        /**头部跳转
         */
        void  search_top();
    }
}
