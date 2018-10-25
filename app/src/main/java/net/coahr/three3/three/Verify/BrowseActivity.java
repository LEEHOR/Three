package net.coahr.three3.three.Verify;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import net.coahr.three3.three.Base.BaseActivity;
import net.coahr.three3.three.Base.BaseModel;
import net.coahr.three3.three.Base.BasePara;
import net.coahr.three3.three.Model.SubjectListModel;
import net.coahr.three3.three.NetWork.RetrofitManager;
import net.coahr.three3.three.Popupwindow.AlertDialogs.Dialog_bottomListener;
import net.coahr.three3.three.Popupwindow.AlertDialogs.Dialog_bottomView;
import net.coahr.three3.three.Popupwindow.PopuWindows;
import net.coahr.three3.three.Popupwindow.PopuWindowsListener;
import net.coahr.three3.three.R;
import net.coahr.three3.three.RecyclerViewAdapter.BrowseAdapter;
import net.coahr.three3.three.Util.AudioRecorder.AudioRecordService;
import net.coahr.three3.three.Util.AudioRecorder.MediaPlayManage;
import net.coahr.three3.three.Util.OtherUtils.ToastUtils;
import net.coahr.three3.three.Util.imageselector.utils.ImageSelectorUtils;

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
 * Created by yuwei on 2018/4/10.
 */

public class BrowseActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private float downX = 0; // 手指按下的X轴坐标
    private float slideDistance = 0; // 滑动的距离
    private float scrollX = 0; // X轴当前的位置
    public EditFcous fcous; //访问者说明编辑
    private View deleteView;
    private Button deleteBtn;
    private TextView upBtn;
    private TextView nextBtn;
    private int mPosition;
    private List mImageList;
    private DeleteImageInterface deleteImageInterface;
    private View mEditToolView;
    private int  mCurrentPage;
    private List mList;
    private Context mContext;
    private ImageView camear,introductions,recorder;
    private WindowManager mWindowManager = null;
    private View mView = null;
    private PopuWindows mpopupWindows;
    private SharedPreferences.Editor subjectlist;
    private Dialog_bottomView mbottomView;
    //题目总数   当前题目 : 默认值
    private static final int REQUEST_CODE = 0x00000011; //图片选择代码
    private boolean verifyFlag;
    private BrowseAdapter mBrowseAdapter;
    private int index;
    String projectId;
    private SubjectListModel mQuestionList;
    private LinearLayoutManager layoutManager;
    private Timer myTimer;

    private TimerTask timerTask;

    private SeekBar mSeekBar;//接收adapter 控件
    private Handler mHandler;
    private TextView imageChange;
  /*  private boolean isChanging = false;//是否正在拖拽seekbar
    public void setChanging(boolean changing) {
        isChanging = changing;
    }*/

    private AudioRecordService.AudioRecordBinder audioRecordBinders; //录音服务
    ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            audioRecordBinders=(AudioRecordService.AudioRecordBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("rr","录音时绑定服务失败");
        }
    };



    private void BindRecordService(){
        Intent in=new Intent(this,AudioRecordService.class);
        startService(in);      //开启服务
        bindService(in,connection,BIND_AUTO_CREATE);  //绑定服务
    }





    public interface EditFcous {

        void fcous();
    }

    public void setFcous(EditFcous fcous) {
        this.fcous = fcous;
    }

    public interface DeleteImageInterface {
        void delete(int position, View v , List imageList);
    }

    public void setDeleteImageInterface(DeleteImageInterface deleteImageInterface) {
        this.deleteImageInterface = deleteImageInterface;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_revise);
        int status = getIntent().getIntExtra("status" , -1);
        projectId = getIntent().getStringExtra("projectId");
        index = getIntent().getIntExtra("index" , 0);
        verifyFlag = true;
        requestRemote(projectId);
        Log.e("BA", "onCreate: "+projectId );
        findUI();
        BindRecordService();
    }

    class ItemClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            fcous.fcous();
        }
    }

    @Override
    public void findUI() {
        super.findUI();

        FrameLayout viewById = findViewById(R.id.medio_contr);
        viewById.setVisibility(View.GONE);
        configureNaviBar(naviBar.findViewById(R.id.left), naviBar.findViewById(R.id.right));
        getLeftBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subjectlist != null) {
                    subjectlist.clear().commit();
                }
                finish();
            }
        });
        getRightBtn().setBackgroundResource(R.drawable.menu);
        getRightBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mpopupWindows=new PopuWindows();
                mpopupWindows.showPopupWindow(findViewById(R.id.line),BrowseActivity.this , false);
                mpopupWindows.setOnButtonClickListener(new PopuWindowsListener() {
                    @Override
                    public void subjectItem(PopupWindow popupWindow) {
                        popupWindow.dismiss();
                       /* final BrowseSubjectsView browseSubjectsView = new BrowseSubjectsView(VerifyReviseActivity.this);
                        browseSubjectsView.show();

                               *//* Window dialogWindow =browseSubjectsView.getWindow();

                                Window dialogWindow = browseSubjectsView.getWindow();

                                dialogWindow.setGravity(Gravity.CENTER);
                                dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
                                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                                dialogWindow.setAttributes(lp);*//*

                        browseSubjectsView.setBrowseSubjectDialogListener(new BrowseSubjectsView.BrowseSubjectDialogListener() {
                            @Override
                            public void OnImageViewBack() {
                                browseSubjectsView.dismiss();
                            }

                            @Override
                            public void OnItemClick(int position, Map map) {
                                subjectlist = getSharedPreferences("subject", 0).edit();
                                for (Object o : map.keySet()) {
                                    subjectlist.putBoolean((String) o, (boolean) map.get(o));
                                    Log.e("rr", "ker:" + o + "/values:" + map.get(o));
                                }

                                int p;
                                if (mCurrentPage <= 0) {
                                    p = mCurrentPage = 0;
                                } else {
                                    p = mCurrentPage;
                                }
                                subjectlist.commit();

                                browseSubjectsView.dismiss();

                            }
                        });*/
                    }

                    @Override
                    public void singleRecording(PopupWindow popupWindow) {

                    }

                    @Override
                    public void totalRecording(PopupWindow popupWindow) {

                    }

                    @Override
                    public void stopRecording(PopupWindow popupWindow) {

                    }

                    @Override
                    public void exitAccess(PopupWindow popupWindow) {
                        popupWindow.dismiss();
                    }
                });

            }
        });




        final TextView titleBtn = naviBar.findViewById(R.id.title);
        if (!verifyFlag)
        {
            Drawable drawable = getResources().getDrawable(R.drawable.lock);
            drawable.setBounds(0, 0, 80, 80);
            titleBtn.setCompoundDrawables(null, null, drawable, null);
            titleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                /*    if(Fab.getVisibility() == View.INVISIBLE) {
                        Fab.setVisibility(View.VISIBLE);
                        Drawable drawable = getResources().getDrawable(R.drawable.unlock);
                        drawable.setBounds(0, 0, 80, 80);
                        titleBtn.setCompoundDrawables(null, null, drawable, null);
                    }
                    else
                    {
                        Fab.setVisibility(View.INVISIBLE);
                        Drawable drawable = getResources().getDrawable(R.drawable.lock);
                        drawable.setBounds(0, 0, 80, 80);
                        titleBtn.setCompoundDrawables(null, null, drawable, null);
                    }*/
                }
            });
        }

        mRecyclerView = findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(BrowseActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        upBtn = findViewById(R.id.up);
        nextBtn = findViewById(R.id.next);
        upBtn.setOnClickListener(new UpNextBtnClickListener());
        nextBtn.setOnClickListener(new UpNextBtnClickListener());
        /**/
        deleteView = findViewById(R.id.delete);
        deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImageInterface.delete(mPosition, deleteView , mImageList);
            }
        });


    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {


        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                System.out.println("----------dispatchTouchEvent--ACTION_DOWN");
                break;

            case MotionEvent.ACTION_MOVE:
//                System.out.println("x:"+ev.getRawX());
//                System.out.println("y:"+ev.getRawY());
                break;
        }


        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        System.out.println("onTouchEventonTouchEventonTouchEventonTouchEventonTouchEvent");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                System.out.println("-----onTouchEvent----ACTION_DOWN");

                break;

            case MotionEvent.ACTION_UP:

                System.out.println("-----onTouchEvent----ACTION_UP");
                break;

        }


        return super.onTouchEvent(event);
    }


    class  UpNextBtnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.up)
                if (mCurrentPage != 0) {
                if (mQuestionList !=null){
                    mRecyclerView.smoothScrollToPosition(mCurrentPage - 1);
                    MediaPlayManage.stopMedia();
                    if (myTimer != null)
                        myTimer.cancel();
                    myTimer = null;
                }

                }
            if (v.getId() == R.id.next)//这里还需要判断list长度
            {
                if (mQuestionList !=null)
                {
                    if (mCurrentPage != mQuestionList.getQuestionList().size()) {
                        mRecyclerView.smoothScrollToPosition(mCurrentPage+1);
                        MediaPlayManage.stopMedia();
                        if (myTimer != null)
                            myTimer.cancel();
                        myTimer = null;
                    }
                }

            }

        }
    }

    /**
     * -------------------底部dialog弹窗监听-----------------------------------------------------------
     **/
    private void showPopupWindow(Context context) {
        mbottomView = new Dialog_bottomView();
        mbottomView.showMydialog(context);
        mbottomView.setOnButtonClickListener(new Dialog_bottomListener() {
            @Override
            public void takePhotos(Dialog dialog) {
                ToastUtils.showLong(BrowseActivity.this, "拍照22");
                dialog.dismiss();
            }

            @Override
            public void choosePhotos(Dialog dialog) {
                ImageSelectorUtils.openPhoto(BrowseActivity.this, REQUEST_CODE, false, 9);
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayManage.stopMedia();
        unbindService(connection);
    }

    @Override
    protected void onResume() {
       // MediaPlayManage.resumeMedia();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayManage.pauseMedia();

    }

    protected void requestRemote(String projectId) {
        super.requestRemote();
        Map<String, Object> map = new HashMap<>();
        map.put("projectId", projectId);
        BasePara para = new BasePara();
        para.setData(map);
        Subscription subscription= RetrofitManager.getInstance()
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

                       if(model.getResult().equals("1"))
                       {
                           SubjectListModel data = model.getData();
//                           mQuestionListBean = (SubjectListModel.QuestionListBean) data.getQuestionList();
                           //mBrowseAdapter.Update(data.getQuestionList());
                           setAdapter(data.getQuestionList());
                           mQuestionList = model.getData();

                           setTitle((TextView) naviBar.findViewById(R.id.title), verifyFlag ? "浏览中(1/"+data.getQuestionList().size() +")" : "修改中1/"+data.getQuestionList().size() +")");
                           mRecyclerView.smoothScrollToPosition(index);

                       }


                   }
               });
        addSubscription(subscription);

    }
    private void setAdapter(List list){
        mBrowseAdapter =new BrowseAdapter(BrowseActivity.this, list, R.layout.item_recyclerview_verify_revise , null);
        mBrowseAdapter.setWindowWidth(getWindowWidth());
        mBrowseAdapter.setWindowHeight(getWindowHeight());
        mBrowseAdapter.setVerifyFlag(verifyFlag);
        //mBrowseAdapter.setIB_PlayListenerInterFace((BrowseAdapter.IB_PlayListenerInterFace) new MyAudioRecordPlay());
        mBrowseAdapter.setShowDeleteViewInterface(new showDelete()) ;
        mRecyclerView.setAdapter(mBrowseAdapter);
        OnScrollListener();
    }
    class showDelete implements BrowseAdapter.ShowDeleteViewInterface{
        @Override
        public void show(int position, List imageList) {
            deleteView.setVisibility(View.VISIBLE);
            mPosition = position;
            mImageList = imageList;
        }
    }
    private void  OnScrollListener() {
        mRecyclerView.addOnScrollListener(new OnScrollListener() {
            private int totalDy = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {

                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        break;

                    case RecyclerView.SCROLL_STATE_SETTLING:


                        break;

                    case RecyclerView.SCROLL_STATE_IDLE:


                        int width = recyclerView.getWidth();
                        mCurrentPage = totalDy / width;
//                        System.out.println("=============::::::"+page);
//                        System.out.println(Math.abs(totalDy % width));
                        if (width * 0.5 <= Math.abs(totalDy % width)) {

                            mCurrentPage += 1;
//                            System.out.println("---------------page:" + page);
                        }
                        @SuppressLint("StringFormatMatches")
                        String title = String.format(verifyFlag ? getResources().getString(R.string.browseTitle) : getResources().getString(R.string.ReviseTitle), mCurrentPage + 1, mQuestionList.getQuestionList().size());
                        getTitleView().setText(title);
                        recyclerView.smoothScrollToPosition(mCurrentPage);

                        break;

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                System.out.println("onScrolled"+dx);
                Log.e("rr","onScrolled"+dx);
                totalDy += dx;

            }
        });
    }
}
