package net.coahr.three3.three.Util.imageselector.view;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatImageView;
/**
 * Created by 李浩 on 2018/4/19.
 *正方形的ImageView，统一显示界面
 */
/**
 * 正方形的ImageView
 */
public class SquareImageView extends AppCompatImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
