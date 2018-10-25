package net.coahr.three3.three.customView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.coahr.three3.three.R;

/**
 * Created by yuwei on 2018/4/3.
 */


public class Tarbar extends LinearLayout {
    private int mIndex = 0;
    private changeItem listenner;

    private int[] iconNormal = {R.drawable.home , R.drawable.upload , R.drawable.audit , R.drawable.center};
    private int[] iconSelected = {R.drawable.home_c , R.drawable.upload_c , R.drawable.audit_c , R.drawable.center_c};

    public Tarbar(Context context) {
        super(context);
    }

    public Tarbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public Tarbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Tarbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setItemClickEven(ViewGroup viewGroup)
    {

        System.out.println(viewGroup.getChildCount());
        for (int i = 0 ; i < viewGroup.getChildCount() ; i++)
        {
            TextView  textView = (TextView) viewGroup.getChildAt(i);

            textView.setOnClickListener(new ItemClickListener(i , viewGroup));
        }

    }

    class ItemClickListener implements OnClickListener {
        private int         index;
        private ViewGroup   viewGroup;
        public ItemClickListener(int index , ViewGroup viewGroup)
        {
            this.index      = index;
            this.viewGroup  = viewGroup;
        }
        @Override
        public void onClick(View v) {

            if (mIndex == index)
            {

                return;
            }
            else
            {
                for (int i = 0 ; i < viewGroup.getChildCount() ; i ++)
                {
                    TextView textView = (TextView) viewGroup.getChildAt(i);
                    Drawable drawable = null;


                    if (i == index)
                    {

                        drawable = getResources().getDrawable(iconSelected[i]);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        textView.setCompoundDrawables(null,drawable,null,null);
                        textView.setTextColor(Color.parseColor("#0092ff"));

                    }
                    else
                    {
                        drawable = getResources().getDrawable(iconNormal[i]);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        textView.setCompoundDrawables(null,drawable,null,null);
                        textView.setTextColor(Color.parseColor("#6689a4"));

                    }
                }

                mIndex = index;
            }

            listenner.changeItem(mIndex);

        }
    }


    public  interface changeItem
    {
        void changeItem(int index);
    }

    public void setListenner(changeItem changeItem)
    {
        this.listenner = changeItem;
    }

}
