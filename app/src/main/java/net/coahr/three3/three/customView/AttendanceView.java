package net.coahr.three3.three.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import net.coahr.three3.three.R;

/**
 * Created by yuwei on 2018/4/25.
 */

@SuppressLint("AppCompatCustomView")
public class AttendanceView extends TextView {

    private boolean inCircle;

    public void setInCircle(boolean inCircle) {
        this.inCircle = inCircle;
    }

    public boolean isInCircle() {
        return inCircle;
    }

    public AttendanceView(Context context) {
        super(context);
    }

    public AttendanceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs , 0);
    }

    public AttendanceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCustomAttrs(context , attrs);
    }

    public AttendanceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        ;


    }
//获取自定义属性
    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.attendance);
        inCircle = ta.getBoolean(R.styleable.attendance_inCircle , false);
        ta.recycle();
    }


}
