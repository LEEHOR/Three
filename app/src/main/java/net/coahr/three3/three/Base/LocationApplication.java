package net.coahr.three3.three.Base;


import android.annotation.TargetApi;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.baidu.mapapi.SDKInitializer;

import net.coahr.three3.three.Util.BaiduSDk.BDLocationUtils;

import org.litepal.LitePal;


/**
 * 主Application，所有百度定位SDK的接口说明请参考线上文档：http://developer.baidu.com/map/loc_refer/index.html
 *
 * 百度定位SDK官方网站：http://developer.baidu.com/map/index.php?title=android-locsdk
 * 
 * 直接拷贝com.baidu.location.service包到自己的工程下，简单配置即可获取定位结果，也可以根据demo内容自行封装
 */
public class LocationApplication extends Application {
	//public LocationService locationService;
     public BDLocationUtils bdLocationUtils;
  //  public Vibrator mVibrator;
    public static LocationApplication instance;



    public static LocationApplication getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        /***
         * 初始化定位sdk，建议在Application中创建
         */
     //   locationService = new LocationService(getApplicationContext());

        LitePal.initialize(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());
        bdLocationUtils=new BDLocationUtils(getApplicationContext());
        //mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = "Recorder";

            String channelName = "录音";

            int importance = NotificationManager.IMPORTANCE_HIGH;

            createNotificationChannel(channelId, channelName, importance);



            channelId = "PlayRecorder";

            channelName = "播放录音";

            importance = NotificationManager.IMPORTANCE_DEFAULT;

            createNotificationChannel(channelId, channelName, importance);

        }

       
    }
    @TargetApi(Build.VERSION_CODES.O)

    private void createNotificationChannel(String channelId, String channelName, int importance) {

        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);

        NotificationManager notificationManager = (NotificationManager) getSystemService(

                NOTIFICATION_SERVICE);

        notificationManager.createNotificationChannel(channel);

    }
}
