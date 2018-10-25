package net.coahr.three3.three.Util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;

public class getDrawable {
    public  String getResourcesUri(Context context, @DrawableRes int id) {

        Resources resources = context.getResources();

        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +

                resources.getResourcePackageName(id) + "/" +

                resources.getResourceTypeName(id) + "/" +

                resources.getResourceEntryName(id);

        return uriPath;

    }
}
