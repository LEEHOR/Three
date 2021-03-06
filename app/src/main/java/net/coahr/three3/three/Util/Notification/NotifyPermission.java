package net.coahr.three3.three.Util.Notification;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NotifyPermission {
    private Context mContext;
    public NotifyPermission(Context context) {
        mContext = context;
    }
    /**
     * 检测通知栏是否开启
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    public boolean isNotificationEnabled() {



        String CHECK_OP_NO_THROW = "checkOpNoThrow";

        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";



        AppOpsManager mAppOps = (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);

        ApplicationInfo appInfo = mContext.getApplicationInfo();

        String pkg = mContext.getApplicationContext().getPackageName();

        int uid = appInfo.uid;



        Class appOpsClass = null;

        /* Context.APP_OPS_MANAGER */

        try {

            appOpsClass = Class.forName(AppOpsManager.class.getName());

            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,

                    String.class);

            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);



            int value = (Integer) opPostNotificationValue.get(Integer.class);

            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);



        } catch (Exception e) {

            e.printStackTrace();

        }

        return false;

    }
    public Intent toSetting() {

        Intent localIntent = new Intent();

        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT >= 9) {

            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");

            localIntent.setData(Uri.fromParts("package",mContext.getPackageName(), null));

        } else if (Build.VERSION.SDK_INT <= 8) {

            localIntent.setAction(Intent.ACTION_VIEW);

            localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");

            localIntent.putExtra("com.android.settings.ApplicationPkgName", mContext.getPackageName());

        }

        // startActivity(localIntent);
        return localIntent;
    }
}
