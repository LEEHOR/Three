package net.coahr.three3.three.Project;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import net.coahr.three3.three.Base.BaseActivity;
import net.coahr.three3.three.Base.BaseModel;
import net.coahr.three3.three.Base.BasePara;
import net.coahr.three3.three.DBbase.ProjectsDB;
import net.coahr.three3.three.Model.HomeSearchModel;
import net.coahr.three3.three.Model.SubjectListModel;
import net.coahr.three3.three.NetWork.RetrofitManager;
import net.coahr.three3.three.Popupwindow.AlertDialogs.CommomDialog;
import net.coahr.three3.three.R;
import net.coahr.three3.three.RecyclerViewAdapter.ProjectAttachmentAdapter;
import net.coahr.three3.three.Util.AudioRecorder.MediaPlayManage;
import net.coahr.three3.three.Util.Notification.NotifyPermission;
import net.coahr.three3.three.Util.OtherUtils.ToastUtils;
import net.coahr.three3.three.Util.Preferences.PreferencesTool;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yuwei on 2018/4/23.
 */

public class ProjectAttachmentActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private List mList;
    private RadioGroup  mFilterGroup;
    private RadioButton image;
    private RadioButton audio;
    private RadioButton all;
    private PreferencesTool mPreferencesTool;
    private FilterInterFace mFilterInterFace;
    private ProjectsDB infoList; //Info页面传来首页的数据
    private  HomeSearchModel.SearchListBean infoSearch; //Info页面传来搜索页的数据
    private boolean isChanging = false;//是否正在拖拽seekbar
    private SeekBar mSeekBar;//接收adapter 控件
    private Timer myTimer;
    private TimerTask timerTask;
    private Handler mHandler;
    private Message messages;
    private LinearLayout linearLayout;
    private TextView recorderName,PlayPoints,PlayCount;
    private TextView imageChange;

    public void setChanging(boolean changing) {
        isChanging = changing;
    }


    public void setmFilterInterFace(FilterInterFace mFilterInterFace) {
        this.mFilterInterFace = mFilterInterFace;
    }

    public FilterInterFace getmFilterInterFace() {
        return mFilterInterFace;
    }

    public  interface FilterInterFace
    {
        void  filter(boolean flag1, boolean flag2 , boolean flag3 );
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_attachment);
        final NotifyPermission notifyPermission=new NotifyPermission(this);
        boolean notificationEnabled = notifyPermission.isNotificationEnabled();
        if (notificationEnabled){

        }else{
            new CommomDialog(ProjectAttachmentActivity.this, R.style.dialog, "我们需要通知权限权限", true, new CommomDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm){
                        Intent intent = notifyPermission.toSetting();
                        startActivity(intent);
                    }else {

                    }
                    dialog.dismiss();
                }
            }).setTitle("通知权限").setPositiveButton("去设置").setNegativeButton("忽略").show();
        }
        findUI();

        mPreferencesTool=new PreferencesTool(ProjectAttachmentActivity.this);
        //获取Info页面传来的信息
        infoList = (ProjectsDB) getIntent().getSerializableExtra("InfoHome");
        infoSearch = (HomeSearchModel.SearchListBean) getIntent().getSerializableExtra("InfoSearch");

        if (infoList!=null){
            String  project_id = infoList.getPid();
          //  List<ProjectsDB> projectsDBS = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "Pid=?", project_id);
            requestRemote(project_id);
        }
        if (infoSearch!=null){
            String  project_id = infoSearch.getId();
            //  List<ProjectsDB> projectsDBS = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "Pid=?", project_id);
            requestRemote(project_id);
        }
    }

    @Override
    public void findUI() {
        super.findUI();
        setTitle((TextView)naviBar.findViewById(R.id.title), "项目附件");
        configureNaviBar(naviBar.findViewById(R.id.left) , null);
        mRecyclerView = findViewById(R.id.recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(ProjectAttachmentActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        linearLayout= findViewById(R.id.liu_Players);
        recorderName= findViewById(R.id.recorderName);
        mSeekBar=findViewById(R.id.liu_SeekBar);
       PlayPoints= findViewById(R.id.PlayPoint);
       PlayCount= findViewById(R.id.PlayCount);
      image= findViewById(R.id.image);
      audio=findViewById(R.id.audio);
      all=findViewById(R.id.all);

        imageChange=findViewById(R.id.imageChange);
        imageChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              if (MediaPlayManage.isPlay){
                  MediaPlayManage.stopMedia();
              }

             mSeekBar.setProgress(0);
              PlayPoints.setText("00:00:00");
              PlayCount.setText("00:00:00");
                if (timerTask!=null){
                    timerTask.cancel();

                }
                if (myTimer!=null){

                    myTimer.cancel();
                }
                if (mHandler !=null){
                    mHandler=null;
                }
                timerTask=null;
                myTimer=null;
                messages=null;
            linearLayout.setVisibility(View.INVISIBLE);

            }
        });

        mFilterGroup = findViewById(R.id.filterGroup);
        mFilterGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mList !=null){

                switch (checkedId)
                {
                    case R.id.image:
                       image.setTextColor(getResources().getColor(R.color.white));
                       audio.setTextColor(getResources().getColor(R.color.black));
                       all.setTextColor(getResources().getColor(R.color.black));
                        mFilterInterFace.filter(true , false , true);

                        break;

                    case R.id.audio:
                        image.setTextColor(getResources().getColor(R.color.black));
                        audio.setTextColor(getResources().getColor(R.color.white));
                        all.setTextColor(getResources().getColor(R.color.black));
                        mFilterInterFace.filter(false , true , false);

                        break;

                    case R.id.all:
                        image.setTextColor(getResources().getColor(R.color.black));
                        audio.setTextColor(getResources().getColor(R.color.black));
                        all.setTextColor(getResources().getColor(R.color.white));
                        mFilterInterFace.filter(true , true , true);
                        break;
                }


                Collections.replaceAll(mList ,true , false);

                } else {
                    ToastUtils.showLong(ProjectAttachmentActivity.this,"联网失败，无法获取数据");

                }
            }
        });

    }


    //播放录音的回调
    class MyAudioRecordPlay implements ProjectAttachmentAdapter.IB_PlayListenerInterFace{
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void OnPlayRecord(int position,String name, String recorderFile, final int PlayPoint) {
            if (recorderFile != null) {   //题目下有录音文件
                linearLayout.setVisibility(View.VISIBLE);
                //mSeekBar = seekbar;
                if (PlayPoint==0){
                    MediaPlayManage.stopMedia();
                    mSeekBar.setProgress(0);
                    recorderName.setText(name);
                    if (mHandler !=null){
                        mHandler=null;
                    }
                }
                mSeekBar.setOnSeekBarChangeListener(new setSeekBar());
                String s = net.coahr.three3.three.Util.OtherUtils.FileUtils.msToss(PlayPoint);
                PlayPoints.setText(s);
                if (timerTask!=null){
                    timerTask.cancel();

                }
                if (myTimer!=null){

                    myTimer.cancel();
                }

                timerTask=null;
                myTimer=null;
                messages=null;
                MediaPlayManage.playPrepare(recorderFile, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        MediaPlayManage.stopMedia();
                        mSeekBar.setProgress(0);
                        String s = net.coahr.three3.three.Util.OtherUtils.FileUtils.msToss(0);
                         PlayPoints.setText(s);
                        if (timerTask!=null){
                            timerTask.cancel();

                        }
                        if (myTimer!=null){

                            myTimer.cancel();
                        }
                        timerTask=null;
                        myTimer=null;
                        messages=null;
                        linearLayout.setVisibility(View.INVISIBLE);
                    }
                });
                mSeekBar.setMax(MediaPlayManage.getDuration());
                String count = net.coahr.three3.three.Util.OtherUtils.FileUtils.msToss(MediaPlayManage.getDuration());
                PlayCount.setText(count);
                MediaPlayManage.playMedia(ProjectAttachmentActivity.this);
                setProgress();

               // mSeekBar.getProgress();
              //  Log.e("", "OnPlayRecord: 播放录音" + recorderFile + "时长：" + MediaPlayManage.getCurrentPosition());



            }


        }
    }

    class setSeekBar implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (MediaPlayManage.getmMediaPlayer() != null)
                MediaPlayManage.getmMediaPlayer().seekTo(progress );
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

            if (MediaPlayManage.getmMediaPlayer()!=null){
                MediaPlayManage.pauseMedia();

            }
             setChanging(true);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
             setChanging(false);
             if (MediaPlayManage.getmMediaPlayer()!=null){
                 MediaPlayManage.resumeMedia();

             }

        }
    }

    protected void requestRemote(String projectId) {
        super.requestRemote();
        Map<String, Object> map = new HashMap<>();
        map.put("projectId", projectId);
        BasePara para = new BasePara();
        para.setData(map);
        Subscription subscription = RetrofitManager.getInstance()
                .createService(para)
                .getSubjects(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseModel<SubjectListModel>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseModel<SubjectListModel> model) {

                        if (model.getResult().equals("1")) {
                            SubjectListModel data = model.getData();
                            mList = data.getQuestionList();
                            setAdapters(data.getQuestionList());


                        }


                    }
                });
        addSubscription(subscription);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayManage.stopMedia();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timerTask!=null){
            timerTask.cancel();
            timerTask=null;
        }
        if (myTimer!=null){

            myTimer.cancel();

            myTimer=null;
        }
        MediaPlayManage.pauseMedia();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaPlayManage.resumeMedia();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setAdapters(List list){
        setAdapter(new ProjectAttachmentAdapter(ProjectAttachmentActivity.this , list , R.layout.item_project_attachment , null));
        adapter.setWindowWidth(getWindowWidth());
        adapter.setWindowHeight(getWindowHeight());
        ((ProjectAttachmentAdapter)adapter).setIB_PlayListenerInterFace(new MyAudioRecordPlay());
        mRecyclerView.setAdapter(adapter);
    }

    private void setProgress(){

        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String s = net.coahr.three3.three.Util.OtherUtils.FileUtils.msToss(( msg.arg1));
                PlayPoints.setText(s);
            }
        };
        if (myTimer == null) {

            myTimer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (isChanging)
                        return;
                   mSeekBar.setProgress(MediaPlayManage.getCurrentPosition());
                    //String s = net.coahr.three3.three.Util.OtherUtils.FileUtils.msToss(MediaPlayManage.getCurrentPosition()*1000);
                    messages=new Message();
                    messages.arg1=MediaPlayManage.getCurrentPosition();
                    mHandler.sendMessage(messages);
                }
            };

            myTimer.schedule(timerTask, 0, 1000);
        }


    }
}
