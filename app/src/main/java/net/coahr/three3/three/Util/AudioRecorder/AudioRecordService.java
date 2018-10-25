package net.coahr.three3.three.Util.AudioRecorder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.Notification.NotifyUtils;


/**
 * Created by 李浩 on 2018/3/11.
 */

public class AudioRecordService extends Service {
    private static final String tag = "PhoneListenService";
    private AudioRecordBinder audioRecordBinder=new AudioRecordBinder();
    private AudioRecorder audioRecorder;
    private TelephonyManager tm;
    private Context mcontext;
    //空闲状态
    private boolean CALL_STATE_IDLE=false;

    // 动态监听去电的广播接收器
    private MyPhoneListener myPhoneListener;
    //通知类
    private NotifyUtils mNotifyUtils;

    @Override
    public void onCreate() {
        super.onCreate();

        //获取电话管理器
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        //对通话进行监听,传入回调函数
        myPhoneListener=new MyPhoneListener();
        tm.listen(myPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * 自定义内部类对来电的电话状态进行监听
     */
  public  class MyPhoneListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
           switch (state){
               //电话处于空闲状态
               case TelephonyManager.CALL_STATE_IDLE:
                   CALL_STATE_IDLE=true;
                   audioRecordBinder.jixu(mcontext);
                   Log.e(tag, "onCallStateChanged: "+"空闲" );
                   break;
               //电话处于响铃状态
               case TelephonyManager.CALL_STATE_RINGING:
                   CALL_STATE_IDLE=false;
                   audioRecordBinder.pause();
                   Log.e(tag, "onCallStateChanged: "+"响铃" );
                   break;
               //电话处于接通状态
               case TelephonyManager.CALL_STATE_OFFHOOK:
                   CALL_STATE_IDLE=false;
                 audioRecordBinder.pause();
                   Log.e(tag, "onCallStateChanged: "+"接听或拨打" );
                   break;
           }
        }

    }
   public class AudioRecordBinder extends Binder{

        public void startRecords(String fileName, final Context context){ //开始录音
            mcontext=context;
                    if(audioRecorder==null){
                        audioRecorder=AudioRecorder.getInstance();
                    }
               if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_NO_READY) {
                   //初始化录音
                   audioRecorder.createDefaultAudio(fileName);
                   Log.d("FFF",""+fileName);
                   //   true                  1
                   if (CALL_STATE_IDLE){ //是否是空闲状态
                       audioRecorder.startRecord(null,context);
                     mNotifyUtils=new NotifyUtils(AudioRecordService.this,1,"Recorder");
                     mNotifyUtils.notify_normal_singline(null,R.drawable.ic_shortcut_smallnotifi,"正在录音","斯锐","开始录音",false,false,false);

                   }

               }

        }

        public  void  pause(){  //暂停录音
            if(audioRecorder==null){
                audioRecorder=AudioRecorder.getInstance();
            }
            if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_START) {
                //暂停录音
                audioRecorder.pauseRecord();
                mNotifyUtils.notify_normal_singline(null,R.drawable.ic_shortcut_smallnotifi,"暂停录音","斯锐","暂停录音",false,false,false);
               // getNotification("取消录音",mcontext);
            }
        }

        public  void  jixu(Context context){  //继续录音
            mcontext=context;
            if(audioRecorder==null){
                audioRecorder=AudioRecorder.getInstance();
            }
            if(audioRecorder.getStatus()==AudioRecorder.Status.STATUS_PAUSE){
                audioRecorder.startRecord(null,context);
                mNotifyUtils.notify_normal_singline(null,R.drawable.ic_shortcut_smallnotifi,"开始录音","斯锐","开始录音",false,false,false);

                // getNotification("继续录音",mcontext);
            }
        }
        public  void stop(Context context,String audioName,String FileNames){  //停止录音
            mcontext=context;
            if(audioRecorder==null){
                audioRecorder=AudioRecorder.getInstance();
            }
            if(audioRecorder.getStatus()!=AudioRecorder.Status.STATUS_NO_READY&&audioRecorder.getStatus()!=AudioRecorder.Status.STATUS_STOP){
                audioRecorder.stopRecord(context,audioName,FileNames);
                mNotifyUtils.clearAllNotificationById(1);
               // stopForeground(true);
               // getNotificationManager().cancel(1);
            }
        }

        public  void  canceled(){  //取消录音
            if(audioRecorder.getStatus()!=AudioRecorder.Status.STATUS_NO_READY){
                audioRecorder.cancel();
               //getNotification("取消录音",mcontext);
               // stopForeground(true);
            }
        }
        public AudioRecorder.Status getStatus(){ //获取录音状态
            return audioRecorder.getStatus();
        }


    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return audioRecordBinder;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消电话状态的监听
        if (tm != null && myPhoneListener != null) {
            tm.listen(myPhoneListener, PhoneStateListener.LISTEN_NONE);

        }
    }
}
