package net.coahr.three3.three.customView;

import android.content.Context;
import android.widget.ImageView;

public class CustomImageView extends android.support.v7.widget.AppCompatImageView {
    private  String mPath;
    private  int   mIndex;
    public CustomImageView(Context context , String path , int index) {
        super(context);
        this.mPath = path;
        this.mIndex = index;
    }

    public String getmPath() {
        return mPath;
    }

    public int getmIndex() {
        return mIndex;
    }
}
