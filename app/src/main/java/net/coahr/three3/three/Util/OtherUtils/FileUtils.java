package net.coahr.three3.three.Util.OtherUtils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static net.coahr.three3.three.Util.OtherUtils.VersionUtils.getPackageName;

/**
 * Created by ZHT on 2017/4/25.
 * 文件(文件夹)工具类
 */

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static String ROOT_PATH = null;

    private static String getRoot(Context context) {
        ROOT_PATH = Environment.getExternalStorageDirectory()
                + File.separator
                + getPackageName(context);
        return ROOT_PATH;
    }

    private static final String CACHE = "/cache/";

    /**
     * 在初始化时创建APP所需要的基础文件夹
     * 在6.0以上版本时需要进行权限申请
     *
     * @param context 上下文
     */
    public static File init(Context context, String FileName) {
        LogUtils.d(TAG, "Root path is " + getRoot(context));
        File fileDir = createFileDir(context, FileName);
        return fileDir;
    }

    /**
     * 创建文件夹
     * 在6.0以上版本时需要进行权限申请
     *
     * @param context     上下文
     * @param fileDirName 文件夹名字
     */
    public static File createFileDir(Context context, String fileDirName) {

        File rootDir = new File(getRoot(context));
        boolean isRootSuccess = false;
        if (!rootDir.exists()) {
            isRootSuccess = rootDir.mkdirs();
        }

        File fileDir;
        if (isExistSDCard()) {
            fileDir = new File(rootDir, fileDirName);
        } else {
            fileDir = new File(getInternalPath(context), fileDirName);
        }

        boolean isFileSuccess = false;
        if (!fileDir.exists()) {
            isFileSuccess = fileDir.mkdirs();
        }

        LogUtils.d(TAG, "is root dir create success? " + isRootSuccess);
        LogUtils.d(TAG, "is file dir create success? " + isFileSuccess);
        return fileDir;
    }

    /**
     * 在没有sdcard时获取内部存储路径
     *
     * @return
     */
    public static String getInternalPath(Context context) {
        LogUtils.d(TAG, "internal path is " + context.getFilesDir().getPath());
        return context.getFilesDir().getPath() + context.getPackageName();
    }

    /**
     * 检测是否SDCard是否存在
     *
     * @return true：存在 false：不存在
     */
    public static boolean isExistSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static long getSystemTime_long() {
        //("yyyy年MM月dd日 HH时MM分ss秒"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM_ddHHmmss");
        long times = System.currentTimeMillis();
        System.out.println(times);
        Date date = new Date(times);
        String time = sdf.format(date);
        Log.e("timeintimet", "timeint: " + time);
        long timeint = 0;
        try {

            timeint = Long.valueOf(time);

        } catch (Exception e) {
            Log.e("exception", "getSystemTime: " + e.toString());
        }
        return timeint;
    }

    public static String getSystemTime_str() {
        //("yyyy年MM月dd日 HH时MM分ss秒"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");
        long times = System.currentTimeMillis();
        // System.out.println(times);
        Date date = new Date(times);
        String time = sdf.format(date);
        Log.e("timeintimet", "timeint: " + time);

        return time;
    }


    /**
     * 毫秒转换成hhmmss
     * @param ms 毫秒
     * @return hh:mm:ss
     */
    public static String msToss(long ms) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String ss = formatter.format(ms);
        Log.e(TAG, "msToss: "+ss );
        return ss;
    }
}
