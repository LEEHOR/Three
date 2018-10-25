package net.coahr.three3.three.Util.AudioRecorder;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.Notification.NotifyUtils;

import java.io.IOException;

/**
 * Created by 李浩
 * 2018/5/4
 */
public class MediaPlayManage {
    private static MediaPlayer mMediaPlayer;
    public static boolean isPause;
    public static boolean isPlay=false;
    private static NotifyUtils mNotifyUtils;
    public static MediaPlayManage getInstance(){
        if (mMediaPlayer==null){

        }
        return null;
    }
    public static void playPrepare(String filePath, MediaPlayer.OnCompletionListener completionListener) {
        if (mMediaPlayer == null ) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);

            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.setOnCompletionListener(completionListener);
            mMediaPlayer.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void playMedia(Context context){
        if(mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
                isPlay=true;
            mNotifyUtils=new NotifyUtils(context,2,"PlayRecorder");
            mNotifyUtils.notify_normal_singline(null, R.drawable.ic_shortcut_smallnotifi,"开始播放","斯锐","开始播放",false,false,false);

            Log.e("播放器","开始播放");
        }
    }
    public static void pauseMedia(){
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            isPlay=false;
            isPause = true;
            Log.e("播放器","暂停播放");
        }
    }
    public static void resumeMedia(){
        if(mMediaPlayer != null && isPause){
            mMediaPlayer.start();
            isPause = false;
            isPlay=true;
            Log.e("播放器","继续播放");
        }
    }
    public static void stopMedia(){
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
            mNotifyUtils.notify_normal_singline(null, R.drawable.ic_shortcut_smallnotifi,"停止播放","斯锐","停止播放",false,false,false);
            releaseMedia();
            isPlay=false;
            Log.e("播放器","停止播放");
        }
    }
    public static void releaseMedia(){
        if(mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
            isPlay=false;
            Log.e("播放器","释放播放器");
        }
    }

    public static int getCurrentPosition(){
        int currentPosition=0;
        if(mMediaPlayer != null){
             currentPosition = mMediaPlayer.getCurrentPosition();
        }
        return currentPosition;
    }

    public static int getDuration(){
        int duration=0;
        if(mMediaPlayer != null){

             duration = mMediaPlayer.getDuration();
        }
        return duration;
    }

    public static MediaPlayer getmMediaPlayer() {
        return mMediaPlayer;
    }
    public  static void   seekTo(int point){
        if (mMediaPlayer !=null){
            mMediaPlayer.seekTo(point);
        }
    }
    public static  void BPrepare(String filePath){
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
