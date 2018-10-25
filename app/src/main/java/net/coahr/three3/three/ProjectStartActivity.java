package net.coahr.three3.three;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andsync.xpermission.XPermissionUtils;
import com.andsync.xpermission.XPermissionUtils.OnPermissionListener;

import net.coahr.three3.three.Base.BaseActivity;
import net.coahr.three3.three.Base.BaseModel;
import net.coahr.three3.three.Base.BasePara;
import net.coahr.three3.three.DBbase.AnswersDB;
import net.coahr.three3.three.DBbase.ImagesDB;
import net.coahr.three3.three.DBbase.ProjectsDB;
import net.coahr.three3.three.DBbase.RecorderFilesDB;
import net.coahr.three3.three.DBbase.SubjectsDB;
import net.coahr.three3.three.Model.HomeSearchModel;
import net.coahr.three3.three.Model.SubjectListModel;
import net.coahr.three3.three.NetWork.RetrofitManager;
import net.coahr.three3.three.Popupwindow.AlertDialogs.CommomDialog;
import net.coahr.three3.three.Popupwindow.AlertDialogs.Dialog_bottomListener;
import net.coahr.three3.three.Popupwindow.AlertDialogs.Dialog_bottomView;
import net.coahr.three3.three.Popupwindow.AlertDialogs.ProjectDialogLast;
import net.coahr.three3.three.Popupwindow.AlertDialogs.ProjectStartDialog;
import net.coahr.three3.three.Popupwindow.PopuWindows;
import net.coahr.three3.three.Popupwindow.PopuWindowsListener;
import net.coahr.three3.three.RecyclerViewAdapter.ProjectStartAdapter;
import net.coahr.three3.three.Util.AudioRecorder.AudioRecordService;
import net.coahr.three3.three.Util.AudioRecorder.AudioRecorder;
import net.coahr.three3.three.Util.AudioRecorder.MediaPlayManage;
import net.coahr.three3.three.Util.AudioRecorder.RecordStreamListener;
import net.coahr.three3.three.Util.ImageFactory.LubanZip;
import net.coahr.three3.three.Util.JDBC.DataBaseWork;
import net.coahr.three3.three.Util.Notification.NotifyPermission;
import net.coahr.three3.three.Util.OtherUtils.ActivityCollector;
import net.coahr.three3.three.Util.OtherUtils.FileUtils;
import net.coahr.three3.three.Util.OtherUtils.OrderSortByGroup;
import net.coahr.three3.three.Util.OtherUtils.ToastUtils;
import net.coahr.three3.three.Util.Preferences.PreferencesTool;
import net.coahr.three3.three.Util.camera2library.camera.CameraActivity;
import net.coahr.three3.three.Util.imageselector.utils.ImageSelectorUtils;
import net.coahr.three3.three.Verify.BrowseActivity;
import net.coahr.three3.three.customView.BrowseSubjectImagesView;
import net.coahr.three3.three.customView.BrowseSubjectsView;
import net.coahr.three3.three.customView.CustomGridLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProjectStartActivity extends BaseActivity {
    //题目总数   当前题目 : 默认值
    public static final int REQUEST_CODE = 0x00000011; //图片选择回调代码

    private static final String TAG = "ProjectStartActivity";

    private int subjectCount;

    private PopuWindows mpopupWindows;

    private View txtLine;

    private RecyclerView recyclerView;

    private boolean move;

    private TextView last, next;

    private int mCurrentPage = 0;//跳转页码

    private int width;  //recycle长度

    private Dialog_bottomView mbottomView; //底部dialog

    private int totalDy = 0; //滑动item距离

    private ProjectStartAdapter startAdapter;//adapter

    private SharedPreferences.Editor subjectlist_SP; //题目列表标识spu

    private String FilePath;   //录音文件地址

    private PreferencesTool mPreferencesTool;  //spu工具类

    private ProjectsDB infoList; //Info页面传来首页的数据

    private HomeSearchModel.SearchListBean infoSearch; //Info页面传来搜索页的数据

    private String project_id; //项目id

    private Handler handler;

    private int ProjectrecordType;  //录音方式  1.不录音 2.单体录音  3.全程录音

    private String recordTitle;  //录音类别标题

    private List<Integer> list_subjects = new ArrayList<>();

    private List<SubjectsDB> subjectsDBList; //查询数据库

    private ProjectsDB projectsDB;       //项目类

    private int savePage;    //保存的item

    private boolean isSingle = true,isFist=true;   //是否是单题录音保存

    private    CustomGridLayoutManager customGridLayoutManager;

    private boolean isFinish;



    private Handler timeHandler;
    private MyThress timeThread;
    private boolean isCloseThread = false;//是否关闭线程，默认为false
    private boolean isStartThread=true,isFirstDialog=true,isStartTimeHandler=true;
    private int recorderTime=0;

    private AudioRecordService.AudioRecordBinder audioRecordBinders; //录音服务
    ServiceConnection connection1 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            audioRecordBinders = (AudioRecordService.AudioRecordBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("rr", "录音时绑定服务失败");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_start);
        findUI();

        /*绑定并开启录音服务*/
        ActivityCollector.addActivity(this);
        BindRecordService();
        final NotifyPermission notifyPermission=new NotifyPermission(this);
        boolean notificationEnabled = notifyPermission.isNotificationEnabled();
        if (notificationEnabled){
        }else{
            new CommomDialog(ProjectStartActivity.this, R.style.dialog, "我们需要通知权限权限", true, new CommomDialog.OnCloseListener() {
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
        handler = new Handler();
        mPreferencesTool = new PreferencesTool(ProjectStartActivity.this);
        //获取Info页面传来的信息
        infoList = (ProjectsDB) getIntent().getSerializableExtra("InfoHome");
        infoSearch = (HomeSearchModel.SearchListBean) getIntent().getSerializableExtra("InfoSearch");
        mPreferencesTool.setRecorderModel("RecorderModel",1);
        mPreferencesTool.setAllRecorder("IsRecorderAll",false);
        if (infoList != null) { //项目ID
            project_id = infoList.getPid();
            ProjectrecordType = infoList.getRecord();
            List<ProjectsDB> projectsDBS = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "Pid=?", project_id);
            if (projectsDBS != null && projectsDBS.size() > 0) {
                projectsDB = projectsDBS.get(0);
                if (projectsDB != null) {
                    subjectsDBList = projectsDB.getSubjectsDBList();
                    if (subjectsDBList != null && subjectsDBList.size() > 0 && !subjectsDBList.isEmpty()) {
                        setAdapter(subjectsDBList);
                    } else {
                        RequestIntenet(project_id);
                    }
                }

            }

        }

        if (infoSearch != null) {
            project_id = infoSearch.getId();
            ProjectrecordType = infoSearch.getRecord();
            List<ProjectsDB> projectsDBS = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "Pid=?", project_id);
            if (projectsDBS != null && projectsDBS.size() > 0) {
                projectsDB = projectsDBS.get(0);
                if (projectsDB != null) {
                    subjectsDBList = projectsDB.getSubjectsDBList();
                    if (subjectsDBList != null && subjectsDBList.size() > 0 && !subjectsDBList.isEmpty()) {
                        setAdapter(subjectsDBList);
                    } else {
                        RequestIntenet(project_id);
                    }
                }
            }


        }
        if (ProjectrecordType == 3) {
            recordTitle = "开启全局录音";
        } else if (ProjectrecordType == 1 || ProjectrecordType == 2) {
            recordTitle = "请选择录音模式";
        }


    }

    private void BindRecordService() {
        Intent in = new Intent(this, AudioRecordService.class);
        startService(in);      //开启服务
        bindService(in, connection1, BIND_AUTO_CREATE);  //绑定服务
    }
    @Override
    public void findUI() {
        super.findUI();
        //数据源
        configureNaviBar(naviBar.findViewById(R.id.left), naviBar.findViewById(R.id.right));
        txtLine = findViewById(R.id.project_startTopLine);

        //底部按钮
        last = findViewById(R.id.project_start_last);
        next = findViewById(R.id.project_start_next);
        //滑动
        recyclerView = findViewById(R.id.project_start_recycleView);
        customGridLayoutManager=new CustomGridLayoutManager(ProjectStartActivity.this,LinearLayoutManager.VERTICAL,false,false);
        //customGridLa.youtManager.setScrollEnabled(false);
        recyclerView.setLayoutManager(customGridLayoutManager);

        getLeftBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subjectlist_SP = getSharedPreferences("subject", 0).edit();
                if (subjectlist_SP != null) {
                    subjectlist_SP.clear().commit();
                }
                finish();
               /* isCloseThread=true;
                boolean single_recorder = mPreferencesTool.getSingleRecorder("single_recorder");
                boolean all_recorder = mPreferencesTool.getAllRecorder("all_recorder");
                int recorderModel = mPreferencesTool.getRecorderModel("RecorderModel");

                if (single_recorder){
                    ToastUtils.showLong(ProjectStartActivity.this,"录音已保存");
                    audioRecordBinders.stop(ProjectStartActivity.this,getAudioNameOne(),projectsDB.getPid());
                    isFinish=true;
                } else if(all_recorder) {
                    ToastUtils.showLong(ProjectStartActivity.this,"录音已保存");
                    list_subjects.add(mCurrentPage);
                    audioRecordBinders.stop(ProjectStartActivity.this,getAudioName(),projectsDB.getPid());
                } else {
                    finish();
                }
                if (recorderModel==2){
                    isCompleteD();
                }
                if (recorderModel==3){
                    isCompleteQ();
                }
                mPreferencesTool.setAllRecorder("all_recorder",false);
                mPreferencesTool.setSingleRecorder("single_recorder",false);
                mPreferencesTool.setRecorderModel("RecorderModel",1);
                mPreferencesTool.setProjectStartProgress("Progress",mCurrentPage);
            */
            }
        });
        Drawable drawable=getResources().getDrawable(R.drawable.menu);
        drawable.setBounds(0,0,20,20);
        getRightBtn().setBackground(drawable);
        getRightBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopupWindows = new PopuWindows();
                mpopupWindows.showPopupWindow(txtLine, ProjectStartActivity.this);
                //popup弹窗监听事件
                XPermissionUtils.requestPermissions(ProjectStartActivity.this, 100, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, new OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        mpopupWindows.setOnButtonClickListener(new PopuWindowsListener() {

                            @Override
                            public void subjectItem(PopupWindow popupWindow) {

                                if (projectsDB != null) {

                                    final List<SubjectsDB> subjectsDBList = projectsDB.getSubjectsDBList();
                                    final BrowseSubjectsView browseSubjectsView = new BrowseSubjectsView(ProjectStartActivity.this, subjectsDBList);
                                    browseSubjectsView.show();
                                    browseSubjectsView.setBrowseSubjectDialogListener(new BrowseSubjectsView.BrowseSubjectDialogListener() {
                                        @Override
                                        public void OnImageViewBack() {
                                            browseSubjectsView.dismiss();
                                        }

                                        @Override
                                        public void OnItemClick(int position, Map map,String qu3,int id) {

                                            //新版改动屏蔽掉
                                            subjectlist_SP = getSharedPreferences("subject", 0).edit();
                                            for (Object o : map.keySet()) {
                                                subjectlist_SP.putBoolean((String) o, (boolean) map.get(o));
                                            }

                                                    subjectlist_SP.commit();
                                         /*   if (P_RecorderHave(id)){ //没有录音
                                                ToastUtils.showLong(ProjectStartActivity.this,"当前题目下没有录音，请开启或暂停录音");
                                            } else {
                                                mCurrentPage=position;
                                                moveToPosition(position);
                                            }*/

                                            mCurrentPage=position;
                                            moveToPosition(position);
                                            browseSubjectsView.dismiss();
                                        }
                                    });


                                } else {
                                }
                                popupWindow.dismiss();

                            }

                            @Override
                            public void singleRecording(final PopupWindow popupWindow) {
                           /*     boolean single_recorder = mPreferencesTool.getSingleRecorder("single_recorder");
                                boolean all_recorder = mPreferencesTool.getAllRecorder("all_recorder");
                               // mPreferencesTool.setRecorderModel("RecorderModel",2);
                                if (ProjectrecordType == 2) {
                                    if (all_recorder) {
                                        ToastUtils.showShort(ProjectStartActivity.this, "当前为全局录音");
                                    } else {
                                        if (!single_recorder) {
                                            String systemTime_str = FileUtils.getSystemTime_str();
                                            if (isFirstDialog){
                                                recorderTime=0;
                                                setTime();
                                            } else {
                                                recorderTime=0;
                                                setTimeHandlerStart();
                                            }
                                            audioRecordBinders.startRecords(systemTime_str, ProjectStartActivity.this);
                                            mPreferencesTool.setSingleRecorder("single_recorder", true);
                                            mPreferencesTool.setRecorderModel("RecorderModel", 2);
                                            savePage=mCurrentPage;
                                            //开始计时

                                        } else {
                                            ToastUtils.showLong(ProjectStartActivity.this,"当前题目有录音或正在录音");

                                        }
                                    }
                                }
                                if (ProjectrecordType == 3) {
                                    ToastUtils.showShort(ProjectStartActivity.this, "请开启全局录音");
                                }
                               // popupWindow.dismiss();
                              handler.postDelayed(new Runnable() {
                                  @Override
                                  public void run() {
                                      popupWindow.dismiss();
                                  }
                              },300);*/
                            }

                            @Override
                            public void totalRecording(final PopupWindow popupWindow) {
                  /*              boolean single_recorder = mPreferencesTool.getSingleRecorder("single_recorder");
                                boolean all_recorder = mPreferencesTool.getAllRecorder("all_recorder");
                                boolean isRecorderAll = mPreferencesTool.getIsRecorderAll("IsRecorderAll");

                                if (ProjectrecordType == 2) {
                                    if (single_recorder) {
                                        ToastUtils.showShort(ProjectStartActivity.this, "当前为单体录音");
                                    } else {
                                        if (all_recorder) {
                                            if (isRecorderAll) {
                                                audioRecordBinders.jixu(ProjectStartActivity.this);
                                                mPreferencesTool.setIsRecorderAll("IsRecorderAll", false);
                                                setTimeHandlerStart();
                                            }
                                        } else {
                                            String systemTime_str = FileUtils.getSystemTime_str();
                                            if (isFirstDialog){
                                                recorderTime=0;
                                                setTime();
                                            } else {
                                                recorderTime=0;
                                                setTimeHandlerStart();
                                            }
                                            audioRecordBinders.startRecords(systemTime_str, ProjectStartActivity.this);
                                            mPreferencesTool.setAllRecorder("all_recorder", true);
                                            mPreferencesTool.setRecorderModel("RecorderModel", 3);


                                        }
                                    }
                                }
                                if (ProjectrecordType == 3) {
                                    if (all_recorder) {
                                        if (isRecorderAll) {
                                            audioRecordBinders.jixu(ProjectStartActivity.this);
                                            setTimeHandlerStart();
                                            mPreferencesTool.setIsRecorderAll("IsRecorderAll", false);
                                        }
                                    } else {
                                        list_subjects.add(mCurrentPage);
                                        String systemTime_str = FileUtils.getSystemTime_str();
                                        if (isFirstDialog){
                                            recorderTime=0;
                                            setTime();
                                        } else {
                                            recorderTime=0;
                                            setTimeHandlerStart();
                                        }
                                        audioRecordBinders.startRecords(systemTime_str, ProjectStartActivity.this);
                                        mPreferencesTool.setAllRecorder("all_recorder", true);
                                        mPreferencesTool.setRecorderModel("RecorderModel", 3);
                                        list_subjects.add(mCurrentPage);


                                    }
                                }
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        popupWindow.dismiss();
                                    }
                                },300);*/
                            }

                            @Override
                            public void stopRecording(final PopupWindow popupWindow) {
                   /*             boolean single_recorder = mPreferencesTool.getSingleRecorder("single_recorder");
                                boolean all_recorder = mPreferencesTool.getAllRecorder("all_recorder");


                                if (ProjectrecordType == 2) {
                                    if (single_recorder) {
                                        isSingle=true;
                                        recorderTime=0;
                                        setTimeHandlerStop();
                                        mPreferencesTool.setSingleRecorder("single_recorder", false);
                                        mPreferencesTool.setRecorderModel("RecorderModel", 1);
                                        audioRecordBinders.stop(ProjectStartActivity.this,getAudioNameOne(),projectsDB.getPid());
                                    }
                                    if (all_recorder) {
                                        setTimeHandlerStop();
                                        audioRecordBinders.pause();
                                        mPreferencesTool.setIsRecorderAll("IsRecorderAll", true);

                                    }
                                }
                                if (ProjectrecordType == 3) {
                                    if (all_recorder) {
                                       setTimeHandlerStop();
                                        audioRecordBinders.pause();
                                         mPreferencesTool.setIsRecorderAll("IsRecorderAll", true);
                                    }
                                }

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        popupWindow.dismiss();
                                    }
                                },300);*/
                            }

                            @Override
                            public void exitAccess(PopupWindow popupWindow) {
                                boolean single_recorder = mPreferencesTool.getSingleRecorder("single_recorder");
                                boolean all_recorder = mPreferencesTool.getAllRecorder("all_recorder");
                                isCloseThread=true;
                                recorderTime=0;
                                if (subjectlist_SP != null) {
                                    subjectlist_SP.clear().commit();
                                }

                                if (ProjectrecordType == 2) {
                                    if (single_recorder) {
                                        isFinish = true;
                                        audioRecordBinders.stop(ProjectStartActivity.this,getAudioNameOne(),projectsDB.getPid());
                                        mPreferencesTool.setSingleRecorder("single_recorder", false);
                                        mPreferencesTool.setRecorderModel("RecorderModel", 1);

                                    } else if (all_recorder){
                                        isFinish = true;
                                        audioRecordBinders.stop(ProjectStartActivity.this,getAudioName(),projectsDB.getPid());
                                        mPreferencesTool.setAllRecorder("all_recorder", false);
                                        mPreferencesTool.setRecorderModel("RecorderModel", 1);

                                    }else {
                                        mPreferencesTool.setRecorderModel("RecorderModel", 1);
                                        popupWindow.dismiss();
                                        finish();
                                    }
                                }

                                if (ProjectrecordType == 3) {
                                    if (all_recorder){
                                        isFinish = true;
                                        list_subjects.add(mCurrentPage);
                                        audioRecordBinders.stop(ProjectStartActivity.this,getAudioName(),projectsDB.getPid());
                                        mPreferencesTool.setAllRecorder("all_recorder", false);
                                        mPreferencesTool.setRecorderModel("RecorderModel", 1);
                                        mPreferencesTool.setAllRecorder("IsRecorderAll",false);
                                    }else {
                                        popupWindow.dismiss();
                                    finish();

                                    }
                                }
                               popupWindow.dismiss();

                                mPreferencesTool.setProjectStartProgress("Progress",mCurrentPage);
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(final String[] deniedPermissions, boolean alwaysDenied) {

                        if (alwaysDenied) { // 拒绝后不再询问 -> 提示跳转到设置
                            // DialogUtil.showPermissionManagerDialog(MainActivity.this, "相机");
                        } else {    // 拒绝 -> 提示此公告的意义，并可再次尝试获取权限
                            new AlertDialog.Builder(ProjectStartActivity.this).setTitle("温馨提示")
                                    .setMessage("我们需要录音权限才能正常使用该功能")
                                    .setNegativeButton("取消", null)
                                    .setPositiveButton("验证权限", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            XPermissionUtils.requestPermissionsAgain(ProjectStartActivity.this, deniedPermissions, 100);
                                        }
                                    }).show();
                        }
                    }
                });
            }
        });
        setListener();
    }

    private void setListener() {
        last.setOnClickListener(new MyButton());
        next.setOnClickListener(new MyButton());
        //录音回调监听
        AudioRecorder.getInstance().setAudioRecorderListener(new MyAudioRecord());


    }

    //=============================底部按钮点击事件==================================

    class MyButton implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int page = mCurrentPage;
            switch (view.getId()) {

                case R.id.project_start_last:
                    if (subjectsDBList !=null && subjectsDBList.size()>0){
                     //   MediaPlayManage.stopMedia();

                        if (mCurrentPage != 0) {
                            if (!audioRecordBinders.getStatus().equals("STATUS_NO_READY")){
                                mCurrentPage -= 1;
                                moveToPosition(mCurrentPage);
                            } else {
                                Toast.makeText(ProjectStartActivity.this, "答题开始无法返回上一题", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(ProjectStartActivity.this, "已经是第一题", Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;

                case R.id.project_start_next:
            if (subjectsDBList !=null && subjectsDBList.size()>0){
               // MediaPlayManage.stopMedia();
                //InfoNext();
                infoNext_2();
                    }
                    break;
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
                mPreferencesTool.setProjectId("Pid", project_id);
                Intent intent = new Intent(ProjectStartActivity.this, CameraActivity.class);
                mPreferencesTool.setProjectStartPhotoPage("subject_id", subjectsDBList.get(mCurrentPage).getHt_id());//保存当前position
                startActivity(intent);
                dialog.dismiss();
            }

            @Override
            public void choosePhotos(Dialog dialog) {
                int id = subjectsDBList.get(mCurrentPage).getId();
                SubjectsDB SubjectsDB = DataBaseWork.DBSelectById(SubjectsDB.class, id);
                if (SubjectsDB != null) {
                    List<ImagesDB> imagesDBList = SubjectsDB.getImagesDBList();
                    int count = 0;
                    if (imagesDBList != null && imagesDBList.size() > 0) {
                        int size = imagesDBList.size();
                        if (size < 10) {
                            count = 10 - size;
                            ImageSelectorUtils.openPhoto(ProjectStartActivity.this, REQUEST_CODE, false, count);
                        } else {
                            ToastUtils.showLong(ProjectStartActivity.this, "图片数量已足够");
                        }
                    } else {
                        ImageSelectorUtils.openPhoto(ProjectStartActivity.this, REQUEST_CODE, false, 9);
                    }
                }
                dialog.dismiss();
            }
        });

    }

    //===============================================图片选择器回调======================================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            //结果：list
            final List<SubjectsDB> subjectsDBListImg = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "ht_id=?", subjectsDBList.get(mCurrentPage).getHt_id());
            ArrayList<String> images = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
            File bitPhotos = FileUtils.createFileDir(ProjectStartActivity.this, "BitPhotos");
            if (images != null && images.size() > 0) {
                LubanZip.getInstance().getZip(ProjectStartActivity.this,images, bitPhotos, subjectsDBListImg.get(0), projectsDB, new LubanZip.LuBanZip() {
                    @Override
                    public void ZipSuccess() {
                        startAdapter.notifyItemChanged(mCurrentPage);
                    }
                });
            }

        }

    }
    //===============================================图片+相机回调======================================

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection1);
        MediaPlayManage.stopMedia();
        ActivityCollector.removeActivity(this);
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isCloseThread=true;
          //  int recorderModel = mPreferencesTool.getRecorderModel("RecorderModel");
         //   boolean single_recorder = mPreferencesTool.getSingleRecorder("single_recorder");
          //  boolean all_recorder = mPreferencesTool.getAllRecorder("all_recorder");
            if (subjectlist_SP != null) {
                subjectlist_SP.clear().commit();
            }
            finish();
       /*     if (recorderModel==2){
                isCompleteD();
            }
            if (recorderModel==3){
                isCompleteQ();
            }
            if (single_recorder){
                ToastUtils.showLong(ProjectStartActivity.this,"录音已保存");
                audioRecordBinders.stop(ProjectStartActivity.this,getAudioNameOne(),projectsDB.getPid());
                isFinish=true;
            }else {

                finish();
            }
            mPreferencesTool.setAllRecorder("all_recorder",false);
            mPreferencesTool.setSingleRecorder("single_recorder",false);
            mPreferencesTool.setRecorderModel("RecorderModel",1);
            mPreferencesTool.setProjectStartProgress("Progress",mCurrentPage);*/

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    //重写
    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (startAdapter != null) {
            startAdapter.notifyItemChanged(mCurrentPage);
        }




    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    //录音回调
    class MyAudioRecord implements RecordStreamListener {
        @Override
        public void recordOfByte(byte[] data, int begin, int end) {

        }

        @Override
        public void OnSuccess(String fileNames) {
          /*  FilePath = fileNames;
            int recorderModel = mPreferencesTool.getRecorderModel("RecorderModel");
            if (ProjectrecordType == 1) {   //项目录音为不录音
                if (recorderModel == 2) {      //  单体录音模式
                    if (FilePath != null) {
                        String fileName = getFileName(FilePath);
                        int id = subjectsDBList.get(savePage).getId();
                        boolean b = P_RecorderHave(id);
                        if (b){
                            SubjectsDB SubjectsDB = DataBaseWork.DBSelectById(SubjectsDB.class, id);
                            RecorderFilesDB recorderFilesDB = new RecorderFilesDB();
                            recorderFilesDB.setRecorderPath(FilePath);
                            recorderFilesDB.setRecorderName(fileName);
                            recorderFilesDB.setProjectsDB(projectsDB);
                            recorderFilesDB.setSubjectsDB(SubjectsDB);
                            recorderFilesDB.save();
                            isCompleteD();
                        } else {
                            SubjectsDB SubjectsDB = DataBaseWork.DBSelectById(SubjectsDB.class, id);
                            List<RecorderFilesDB> recorderFiles = SubjectsDB.getRecorderFiles();
                            if (recorderFiles !=null && recorderFiles.size()>0){
                                recorderFiles.get(0).setRecorderPath(FilePath);
                                recorderFiles.get(0).update( recorderFiles.get(0).getId());
                            }

                        }

                    }
                }
                if (recorderModel == 3) {
                    if (FilePath != null) {
                                List<SubjectsDB> subject = getSubject(list_subjects);
                                for (int i = 0; i < subject.size(); i++) {
                                    RecorderFilesDB recorderFilesDB = new RecorderFilesDB();
                                    recorderFilesDB.setProjectsDB(projectsDB);
                                    recorderFilesDB.setRecorderName(getFileName(FilePath));
                                    recorderFilesDB.setRecorderPath(FilePath);
                                    recorderFilesDB.setSubjectsDB(subject.get(i));
                                    recorderFilesDB.save();
                                }
                                isCompleteQ();
                            }

                }
            }

            if (ProjectrecordType == 2) {
                if (recorderModel == 1) {
                    if (FilePath != null) {
                        String fileName = getFileName(FilePath);
                        int id = subjectsDBList.get(savePage).getId();
                        boolean b = P_RecorderHave(id);
                        if (b){
                            SubjectsDB SubjectsDB = DataBaseWork.DBSelectById(SubjectsDB.class, id);
                            RecorderFilesDB recorderFilesDB = new RecorderFilesDB();
                            recorderFilesDB.setRecorderPath(FilePath);
                            recorderFilesDB.setRecorderName(fileName);
                            recorderFilesDB.setProjectsDB(projectsDB);
                            recorderFilesDB.setSubjectsDB(SubjectsDB);
                            recorderFilesDB.save();
                            isCompleteD();
                        }else {
                            isCompleteD();
                        }
                        startAdapter.notifyItemChanged(savePage);

                    }
                } else if (recorderModel == 2) {
                    if (FilePath != null) {
                        if (isSingle) {  //单题保存
                            String fileName = getFileName(FilePath);
                            int id = subjectsDBList.get(mCurrentPage).getId();
                            boolean b = P_RecorderHave(id);
                            if (b){
                                SubjectsDB SubjectsDB = DataBaseWork.DBSelectById(SubjectsDB.class, id);
                                RecorderFilesDB recorderFilesDB = new RecorderFilesDB();
                                recorderFilesDB.setRecorderPath(FilePath);
                                recorderFilesDB.setRecorderName(fileName);
                                recorderFilesDB.setProjectsDB(projectsDB);
                                recorderFilesDB.setSubjectsDB(SubjectsDB);
                                recorderFilesDB.save();
                                isCompleteD();
                            }else {
                                isCompleteD();
                            }
                                startAdapter.notifyItemChanged(savePage);
                        } else {      //全局保存

                                    List<SubjectsDB> subject = getSubject(list_subjects);
                                    if (subject !=null && subject.size()>0){
                                        for (int i = 0; i < subject.size(); i++) {
                                            RecorderFilesDB recorderFilesDB = new RecorderFilesDB();
                                            recorderFilesDB.setProjectsDB(projectsDB);
                                            recorderFilesDB.setRecorderName(getFileName(FilePath));
                                            recorderFilesDB.setRecorderPath(FilePath);
                                            recorderFilesDB.setSubjectsDB(subject.get(i));
                                            recorderFilesDB.save();
                                        }
                                        isCompleteQ();
                                        startAdapter.notifyDataSetChanged();
                                    }




                        }
                    }
                } else if (recorderModel == 3) {
                            List<SubjectsDB> subject = getSubject(list_subjects);
                            if (subject !=null && subject.size()>0){
                                for (int i = 0; i < subject.size(); i++) {
                                    RecorderFilesDB recorderFilesDB = new RecorderFilesDB();
                                    recorderFilesDB.setProjectsDB(projectsDB);
                                    recorderFilesDB.setRecorderName(getFileName(FilePath));
                                    recorderFilesDB.setRecorderPath(FilePath);
                                    recorderFilesDB.setSubjectsDB(subject.get(i));
                                    recorderFilesDB.save();
                                }
                                isCompleteQ();
                    }

                }
            }
            if (ProjectrecordType == 3) {
                if (recorderModel == 3) {
                            List<SubjectsDB> subject = getSubject(list_subjects);
                            if(subject !=null && subject.size()>0){
                            for (int i = 0; i < subject.size(); i++) {
                                RecorderFilesDB recorderFilesDB = new RecorderFilesDB();
                                recorderFilesDB.setProjectsDB(projectsDB);
                                recorderFilesDB.setRecorderName(getFileName(FilePath));
                                recorderFilesDB.setRecorderPath(FilePath);
                                recorderFilesDB.setSubjectsDB(subject.get(i));
                                recorderFilesDB.save();
                            }
                            isCompleteQ();
                            }

                }
            }
            startAdapter.notifyDataSetChanged();*/
            if (isFinish) {
                finish();
                isFinish = false;
            }
        }

        @Override
        public void OnFail() {


        }
    }

    //图片浏览和adatper点击处理
    class MyAdapter implements ProjectStartAdapter.projectStartOnclickInterFace {

        @Override
        public void R_YES(int id, SparseArray<String> sparseArray, String answer, int position) {
            SubjectsDB SubjectsDB = DataBaseWork.DBSelectById(SubjectsDB.class, id);
            List<AnswersDB> answersdb = SubjectsDB.getAnswers();
            if (answersdb != null && answersdb.size() > 0 && !answersdb.isEmpty()) {  //如果存在就更新
                answersdb.get(0).setSubjectsDB(SubjectsDB);
                answersdb.get(0).setAnswer(answer);
                answersdb.get(0).update(answersdb.get(0).getId());
                sparseArray.put(id, answer);
            } else {                                            //不存在就保存
                AnswersDB answersDB = new AnswersDB();
                answersDB.setAnswer(answer);
                answersDB.setSubjectsDB(SubjectsDB);
                answersDB.save();
                sparseArray.put(id, answer);
            }
            startAdapter.setMap(sparseArray, position);
            //startAdapter.notifyItemChanged(mCurrentPage);
        }

        @Override
        public void R_NO(int id,  SparseArray<String> sparseArray, String answer,int position) {
            SubjectsDB SubjectsDB = DataBaseWork.DBSelectById(SubjectsDB.class, id);
            List<AnswersDB> answersdb = SubjectsDB.getAnswers();
            if (answersdb != null && answersdb.size() > 0 && !answersdb.isEmpty()) {  //如果存在就更新
                answersdb.get(0).setSubjectsDB(SubjectsDB);
                answersdb.get(0).setAnswer(answer);
                answersdb.get(0).update(answersdb.get(0).getId());
                sparseArray.put(id, answer);
            } else {                                            //不存在就保存
                AnswersDB answersDB = new AnswersDB();
                answersDB.setAnswer(answer);
                answersDB.setSubjectsDB(SubjectsDB);
                answersDB.save();
                sparseArray.put(id, answer);
            }
            startAdapter.setMap(sparseArray, position);
        }

        @Override
        public void NineViewOnClick(int NinePosition, final int position, String url, List<String> urlList, ImageView imageView, final List<ImagesDB> imagesDBList, Context context
        ) {

            if ( NinePosition==0){
                    //拍照
                mPreferencesTool.setProjectId("Pid", project_id);
                Intent intent = new Intent(ProjectStartActivity.this, CameraActivity.class);
                mPreferencesTool.setProjectStartPhotoPage("subject_id", subjectsDBList.get(mCurrentPage).getHt_id());//保存当前position
                startActivity(intent);
            }else {
                 final BrowseSubjectImagesView browseSubjectImagesView = new BrowseSubjectImagesView(context, imagesDBList, NinePosition, url);
                    browseSubjectImagesView.show();

                 browseSubjectImagesView.setBrowseSubjectImageInteface(new BrowseSubjectImagesView.BrowseSubjectImagesInteface() {

                @Override
                public void DialogDismiss() {
                    browseSubjectImagesView.dismiss();
                }

                @Override
                public void delete(int id, String ImageName) {
                /*    List<ImagesDB> imagesDBS = DataBaseWork.DBSelectByTogether_Where(ImagesDB.class, "imagename=?", ImageName);
                    if (imagesDBS !=null && imagesDBS.size()>0){
                        String imagePath = imagesDBS.get(0).getImagePath();
                        File file=new File(imagePath);
                        if (file!=null && file.exists()){
                            file.delete();
                        }
                    }
                    int i = DataBaseWork.DBDeleteByConditions(ImagesDB.class, "imagename=?", ImageName);
                    if (i>0){
                        startAdapter.notifyItemChanged(position);
                    }*/
                }

            });
            }
        }

        @Override
        public void NineViewOnLongClick(final int NinePosition, final int position, String url, List<String> urlList, ImageView imageView, final List<ImagesDB> imagesDBList, Context context) {

            if (NinePosition >0) {
                new CommomDialog(ProjectStartActivity.this, R.style.dialog, imagesDBList.get(NinePosition-1).getImageName(), true, false, null, new CommomDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {

                           // int i = DataBaseWork.DBDeleteById(ImagesDB.class, imagesDBList.get(NinePosition-1).getId());

                            int i = DataBaseWork.DBDeleteByConditions(ImagesDB.class, "imagepath=?", url);
                            File file=new File(url);
                            if (file!=null && file.exists()){
                                file.delete();
                            }
                            if (i> 0) {
                                startAdapter.notifyItemChanged(position);
                            }
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                        }
                    }
                }).setTitle("确定删除").show();
            }
        }

        @Override
        public void startRecorder( int nowposition, ImageView iv) {
           // int id = subjectsDBList.get(savePage).getId();
            final int recorderModel = mPreferencesTool.getRecorderModel("RecorderModel");
            boolean isRecorderAll = mPreferencesTool.getIsRecorderAll("IsRecorderAll");
            boolean single_recorder = mPreferencesTool.getSingleRecorder("single_recorder");
            boolean all_recorder = mPreferencesTool.getAllRecorder("all_recorder");
            if (recorderModel==2){
                if (single_recorder){
                    audioRecordBinders.stop(ProjectStartActivity.this,getAudioNameOne(),projectsDB.getPid());
                }
                if (all_recorder){
                    audioRecordBinders.stop(ProjectStartActivity.this,getAudioName(),projectsDB.getPid());
                }
                setTimeHandlerStop();
                recorderTime=0;
                mPreferencesTool.setRecorderModel("RecorderModel",1);
            }else if(recorderModel==3){
                if (isRecorderAll){  //继续
                    audioRecordBinders.jixu(ProjectStartActivity.this);
                    setTimeHandlerStart();
                    mPreferencesTool.setIsRecorderAll("IsRecorderAll", false);
                } else {  //暂停
                    setTimeHandlerStop();
                    mPreferencesTool.setIsRecorderAll("IsRecorderAll", true);
                }

            }else {
                RecorderDialog();
            }
        }

        @Override
        public void EditeText(int position, SparseArray<String> sparseArray, int id) {
            new MaterialDialog.Builder(ProjectStartActivity.this)
                    .title("输入框")
                    .iconRes(R.mipmap.ic_launcher)
                    .content("请填写您对本题的看法：")
//                                .widgetColor(Color.BLUE)//输入框光标的颜色
                    .inputType(InputType.TYPE_CLASS_TEXT)//可以输入的类型-电话号码
            .input("我的看法", "", new MaterialDialog.InputCallback() {
                @Override
                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                    SubjectsDB SubjectsDB = DataBaseWork.DBSelectById(SubjectsDB.class, id);
                    List<AnswersDB> answersdb = SubjectsDB.getAnswers();
                    if (answersdb != null && answersdb.size() > 0 && !answersdb.isEmpty()) {  //如果存在就更新
                        answersdb.get(0).setSubjectsDB(SubjectsDB);
                        answersdb.get(0).setRemakes( input.toString());
                        int update = answersdb.get(0).update(answersdb.get(0).getId());
                        if (update>0){
                            sparseArray.put(id, input.toString());
                        }
                    } else {                                            //不存在就保存
                        AnswersDB answersDB = new AnswersDB();
                        answersDB.setRemakes( input.toString());
                        answersDB.setSubjectsDB(SubjectsDB);
                        boolean save = answersDB.save();
                        if (save) {

                            sparseArray.put(id,  input.toString());
                        }
                    }
                }
            })
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    if (dialog.getInputEditText().length() <=10) {
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                    }else {
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    }
                    startAdapter.setMapRemark(sparseArray,position);
                }
            }).show();

        }
    }

    public void RequestIntenet(String projectId) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectId", projectId);
        BasePara para = new BasePara();
        para.setData(map);
        Subscription subscription = RetrofitManager.getInstance()
                .createService(para)
                .getSubjects(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(BaseModel baseModel) {
                        SubjectListModel data = (SubjectListModel) baseModel.getData();
                        List<SubjectListModel.QuestionListBean> questionList = data.getQuestionList();
                        for (int i = 0; i < questionList.size(); i++) {
                            SubjectsDB questionListbean = new SubjectsDB();
                            questionListbean.setTitle(questionList.get(i).getTitle());
                            questionListbean.setHt_id(questionList.get(i).getId());
                            questionListbean.setType(questionList.get(i).getType());
                            questionListbean.setOptions(questionList.get(i).getOptions());
                            questionListbean.setDescription(questionList.get(i).getDescription());
                            questionListbean.setPhotoStatus(questionList.get(i).getPhotoStatus());
                            questionListbean.setRecordStatus(questionList.get(i).getRecordStatus());
                            questionListbean.setDescribeStatus(questionList.get(i).getDescribeStatus());
                            questionListbean.setCensor(questionList.get(i).getCensor());
                            questionListbean.setIsComplete(0);
                            questionListbean.setDh("0");
                            questionListbean.setNumber(i + 1);
                            questionListbean.setsUploadStatus(0);
                            if (questionList.get(i).getQuota1() != null) {
                                questionListbean.setQuota1(questionList.get(i).getQuota1());
                                if (questionList.get(i).getQuota2() != null) {
                                    questionListbean.setQuota2(questionList.get(i).getQuota2());
                                    if (questionList.get(i).getQuota3() != null) {
                                        questionListbean.setQuota3(questionList.get(i).getQuota3());
                                    } else {
                                        questionListbean.setQuota3(null);
                                    }
                                } else {
                                    questionListbean.setQuota2(null);
                                    questionListbean.setQuota3(null);
                                }

                            } else {
                                questionListbean.setQuota1(null);
                                questionListbean.setQuota2(null);
                                questionListbean.setQuota3(null);
                            }
                            questionListbean.setProjectsDB(projectsDB);
                            questionListbean.save();
                        }

                            subjectsDBList = projectsDB.getSubjectsDBList();
                            setAdapter(subjectsDBList);


                    }
                });
        addSubscription(subscription);
    }

    /*====================================首次进入录音dialog提示==========================*/
    private void RecorderDialog() {
        XPermissionUtils.requestPermissions(ProjectStartActivity.this, 100, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, new OnPermissionListener() {
            @Override
            public void onPermissionGranted() {
                if (ProjectrecordType == 3) {
                    int id = subjectsDBList.get(mCurrentPage).getId();
                    if (!P_RecorderHave(id)){
                        ToastUtils.showLong(ProjectStartActivity.this,"当前题目下有录音");
                        return;
                    }
                    new CommomDialog(ProjectStartActivity.this, R.style.dialog, recordTitle, false, false, null, new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                String systemTime_str = FileUtils.getSystemTime_str();
                                if (isFirstDialog){
                                    recorderTime=0;
                                    setTime();
                                    isFirstDialog=false;
                                } else {
                                    recorderTime=0;
                                    setTimeHandlerStart();
                                }
                                audioRecordBinders.startRecords(systemTime_str, ProjectStartActivity.this);
                                mPreferencesTool.setAllRecorder("all_recorder", true);
                                mPreferencesTool.setRecorderModel("RecorderModel", 3);
                                list_subjects.add(mCurrentPage);

                                dialog.dismiss();
                            } else {

                                dialog.dismiss();
                            }
                        }
                    }).setTitle("").setPositiveButton("确定").show();
                }
                if (ProjectrecordType == 2 || ProjectrecordType == 1) {
                    new ProjectStartDialog(ProjectStartActivity.this, new ProjectStartDialog.OnStartCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm, int status) {
                            if (confirm) {
                                if (status == 3) {
                                    String systemTime_str = FileUtils.getSystemTime_str();
                                    if (isFirstDialog){
                                        recorderTime=0;
                                        setTime();
                                        isFirstDialog=false;
                                    } else {
                                        recorderTime=0;
                                       setTimeHandlerStart();
                                    }

                                    audioRecordBinders.startRecords(systemTime_str, ProjectStartActivity.this);
                                    mPreferencesTool.setAllRecorder("all_recorder", true);
                                    // mPreferencesTool.setIsRecorderAll("IsAllRecorder", true);
                                    mPreferencesTool.setRecorderModel("RecorderModel", 3);
                                    list_subjects.add(mCurrentPage);

                                } else if (status == 2) {
                                    String systemTime_str = FileUtils.getSystemTime_str();
                                    if (isFirstDialog){
                                        recorderTime=0;
                                        setTime();
                                        isFirstDialog=false;
                                    } else {
                                        recorderTime=0;
                                      setTimeHandlerStart();
                                    }
                                    audioRecordBinders.startRecords(systemTime_str, ProjectStartActivity.this);
                                    mPreferencesTool.setSingleRecorder("single_recorder", true);
                                    mPreferencesTool.setRecorderModel("RecorderModel", 2);

                                } else {
                                    ToastUtils.showLong(ProjectStartActivity.this, "请选择录音模式");
                                }
                                dialog.dismiss();


                            } else {
                                mPreferencesTool.setRecorderModel("RecorderModel", 1);
                                dialog.dismiss();
                            }
                        }
                    }).show();
                }
            }

            @Override
            public void onPermissionDenied(final String[] deniedPermissions, boolean alwaysDenied) {

                if (alwaysDenied) { // 拒绝后不再询问 -> 提示跳转到设置
                    // DialogUtil.showPermissionManagerDialog(MainActivity.this, "相机");
                } else {    // 拒绝 -> 提示此公告的意义，并可再次尝试获取权限
                    new AlertDialog.Builder(ProjectStartActivity.this).setTitle("温馨提示")
                            .setMessage("我们需要相机权限才能正常使用该功能")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("验证权限", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    XPermissionUtils.requestPermissionsAgain(ProjectStartActivity.this, deniedPermissions, 100);
                                }
                            }).show();
                }
            }
        });


    }

    //判断当前题目下有无录音文件
    private boolean IsHaveRecorder() {
        List<RecorderFilesDB> recorderFiles = subjectsDBList.get(mCurrentPage).getRecorderFiles();
        if (recorderFiles != null&& recorderFiles.size() > 0  && !recorderFiles.isEmpty()) {
            RecorderFilesDB recorderFilesDB = recorderFiles.get(0);
            if (recorderFilesDB != null) {
                String recorderPath = recorderFilesDB.getRecorderPath();
                if (recorderPath != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否可以进入下一题
     *
     * @return
     */
    private boolean isCanNext() {
        int id = subjectsDBList.get(mCurrentPage).getId();
        SubjectsDB SubjectsDB = DataBaseWork.DBSelectById(SubjectsDB.class, id);
        int recordStatus = SubjectsDB.getRecordStatus();  //是否需要强制录音
        int describeStatus = SubjectsDB.getDescribeStatus(); //是否需要强制填写说明
        int photoStatus = SubjectsDB.getPhotoStatus();  //是否需要强制拍照
        List<AnswersDB> answers = SubjectsDB.getAnswers();
        String answer = null;
        String remakes = null;
        if (answers != null && answers.size() > 0) {
            answer = answers.get(0).getAnswer();
            remakes = answers.get(0).getRemakes();
        }
        List<ImagesDB> imagesDBList = SubjectsDB.getImagesDBList();   //图片
        List<RecorderFilesDB> recorderFiles = SubjectsDB.getRecorderFiles();  //录音
        int recorderModel = mPreferencesTool.getRecorderModel("RecorderModel");      //录音模式
        boolean isAllRecorder = mPreferencesTool.getIsRecorderAll("IsAllRecorder"); //全局录音是否暂停
        if (ProjectrecordType == 1) {  //项目标签不录音
            if (recorderModel == 1) {   //如果没有选择录音模式
                if (describeStatus == 1 && photoStatus == 1) {
                    if (answer != null && remakes != null && imagesDBList != null && imagesDBList.size() > 0 && !imagesDBList.isEmpty()) {
                        return true;
                    }
                } else if (describeStatus == 1 && photoStatus == -1) {
                    if (answer != null && remakes != null) {
                        return true;
                    }

                } else if (describeStatus == -1 && photoStatus == 1) {
                    if (answer != null && imagesDBList != null && imagesDBList.size() > 0 && !imagesDBList.isEmpty()) {
                        return true;
                    }
                } else  if (describeStatus == -1 && photoStatus == -1) {
                    if (answer != null) {
                        return true;
                    }
                }

            } else if (recorderModel == 2) {  //如果选择了单题录音
                if (describeStatus == 1 && photoStatus == 1) {
                    if (answer != null && remakes != null && imagesDBList.size() > 0 && imagesDBList != null && !imagesDBList.isEmpty() && recorderFiles != null && recorderFiles.size() > 0 && !recorderFiles.isEmpty()) {
                        return true;
                    }
                } else if (describeStatus == 1 && photoStatus == -1) {
                    if (answer != null && remakes != null && recorderFiles != null && recorderFiles.size() > 0 && !recorderFiles.isEmpty()) {
                        return true;
                    }

                } else if (describeStatus == -1 && photoStatus == 1) {
                    if (answer != null && imagesDBList.size() > 0 && imagesDBList != null && !imagesDBList.isEmpty() && recorderFiles != null && recorderFiles.size() > 0 && !recorderFiles.isEmpty()) {
                        return true;
                    }
                } else  if (describeStatus == -1 && photoStatus == -1) {
                    if (answer != null && recorderFiles != null && recorderFiles.size() > 0 && !recorderFiles.isEmpty()) {
                        return true;
                    }
                }
            } else if (recorderModel == 3) { //如果选择了全局录音
                if (describeStatus == 1 && photoStatus == 1) {
                    if (answer != null && remakes != null && imagesDBList.size() > 0 && imagesDBList != null && !imagesDBList.isEmpty() && !isAllRecorder) {
                        return true;
                    }
                } else if (describeStatus == 1 && photoStatus == -1) {
                    if (answer != null && remakes != null && !isAllRecorder) {
                        return true;
                    }

                } else if (describeStatus == -1 && photoStatus == 1) {
                    if (answer != null && imagesDBList.size() > 0 && imagesDBList != null && !imagesDBList.isEmpty() && !isAllRecorder) {
                        return true;
                    }
                } else  if (describeStatus == -1 && photoStatus == -1) {
                    if (answer != null && !isAllRecorder) {
                        return true;
                    }
                }
            }

        }

        if (ProjectrecordType == 2) {   //如果项目为单体录音
            if (recorderModel == 1) {      //如果选择没有录音模式
                if (recordStatus == 1) {    //如果为强制录音
                    if (IsHaveRecorder()){   //如果有录音文件
                       return  true;
                    }else {
                        if (describeStatus == 1 && photoStatus == 1) {
                            if (answer != null && remakes != null && imagesDBList != null && imagesDBList.size() > 0  && !imagesDBList.isEmpty() && recorderFiles.size() > 0 && recorderFiles != null && !recorderFiles.isEmpty()) {
                                return true;
                            }
                        } else if (describeStatus == 1 && photoStatus == -1) {
                            if (answer != null && remakes != null &&recorderFiles != null  && recorderFiles.size() > 0 && !recorderFiles.isEmpty()) {
                                return true;
                            }

                        } else if (describeStatus == -1 && photoStatus == 1) {
                            if (answer != null &&imagesDBList != null  && imagesDBList.size() > 0 &&recorderFiles != null &&  !imagesDBList.isEmpty()  && recorderFiles.size() > 0 && !recorderFiles.isEmpty()) {
                                return true;
                            }
                        } else if (describeStatus == -1 && photoStatus == -1){
                            if (answer != null && recorderFiles.size() > 0 && recorderFiles != null && !recorderFiles.isEmpty()) {
                                return true;
                            }
                        }
                    }


                } else {   //如果为不强制录音
                    if (describeStatus == 1 && photoStatus == 1) {
                        if (answer != null && remakes != null && imagesDBList.size() > 0 && imagesDBList != null && !imagesDBList.isEmpty()) {
                            return true;
                        }
                    } else if (describeStatus == 1 && photoStatus == -1) {
                        if (answer != null && remakes != null) {
                            return true;
                        }

                    } else if (describeStatus == -1 && photoStatus == 1) {
                        if (answer != null && imagesDBList.size() > 0 && imagesDBList != null && !imagesDBList.isEmpty()) {
                            return true;
                        }
                    } else if (describeStatus == -1 && photoStatus == -1){
                        if (answer != null) {
                            return true;
                        }
                    }
                }
            } else if (recorderModel == 2) {     //选择单体录音模式
                if (describeStatus == 1 && photoStatus == 1) {
                    if (answer != null && remakes != null && imagesDBList.size() > 0 && imagesDBList != null && !imagesDBList.isEmpty() && recorderFiles.size() > 0 && recorderFiles != null && !recorderFiles.isEmpty()) {
                        return true;
                    }
                } else if (describeStatus == 1 && photoStatus == -1) {
                    if (answer != null && remakes != null && recorderFiles.size() > 0 && recorderFiles != null && !recorderFiles.isEmpty()) {
                        return true;
                    }

                } else if (describeStatus == -1 && photoStatus == 1) {
                    if (answer != null && imagesDBList.size() > 0 && imagesDBList != null && !imagesDBList.isEmpty() && recorderFiles.size() > 0 && recorderFiles != null && !recorderFiles.isEmpty()) {
                        return true;
                    }
                } else if (describeStatus == -1 && photoStatus == -1){
                    if (answer != null && recorderFiles.size() > 0 && recorderFiles != null && !recorderFiles.isEmpty()) {
                        return true;
                    }
                }

            } else if (recorderModel == 3) {  //选择全局录音模式
                if (recordStatus == 1) {    //强制单体录音
                    if (describeStatus == 1 && photoStatus == 1) {
                        if (answer != null && remakes != null && imagesDBList.size() > 0 && imagesDBList != null && !imagesDBList.isEmpty()) {
                            return true;
                        }
                    } else if (describeStatus == 1 && photoStatus == -1) {
                        if (answer != null && remakes != null && recorderFiles.size() > 0 ) {
                            return true;
                        }

                    } else if (describeStatus == -1 && photoStatus == 1) {
                        if (answer != null && imagesDBList != null &&  imagesDBList.size() > 0&& !imagesDBList.isEmpty()) {
                            return true;
                        }
                    } else  if (describeStatus == -1 && photoStatus == -1) {
                        if (answer != null) {
                            return true;
                        }
                    }
                } else {  //不是强制单体录音
                    if (describeStatus == 1 && photoStatus == 1) {
                        if (answer != null && remakes != null && imagesDBList.size() > 0 && imagesDBList != null && !imagesDBList.isEmpty() && !isAllRecorder) {
                            return true;
                        }
                    } else if (describeStatus == 1 && photoStatus == -1) {
                        if (answer != null && remakes != null && !isAllRecorder) {
                            return true;
                        }

                    } else if (describeStatus == -1 && photoStatus == 1) {
                        if (answer != null && imagesDBList.size() > 0 && imagesDBList != null && !imagesDBList.isEmpty() && !isAllRecorder) {
                            return true;
                        }
                    } else  if (describeStatus == -1 && photoStatus == -1) {
                        if (answer != null && !isAllRecorder) {
                            return true;
                        }
                    }
                }


            }

        }

        if (ProjectrecordType == 3) {  //如果为全局录音
            if (IsHaveRecorder()) {
                if (describeStatus == 1 && photoStatus == 1) {
                    if (answer != null && remakes != null && imagesDBList != null &&  imagesDBList.size() > 0 && !imagesDBList.isEmpty()) {
                        return true;
                    } else {
                        ToastUtils.showShort(ProjectStartActivity.this, "请将答案填写完整，并开启全局录音");
                    }
                } else if (describeStatus == 1 && photoStatus == -1) {
                    if (answer != null && remakes != null) {
                        return true;
                    } else {
                        ToastUtils.showShort(ProjectStartActivity.this, "请将答案填写完整，并开启全局录音");
                    }

                } else if (describeStatus == -1 && photoStatus == 1) {
                    if (answer != null &&  imagesDBList != null  && imagesDBList.size()> 0 && !imagesDBList.isEmpty()) {
                        return true;
                    } else {
                        ToastUtils.showShort(ProjectStartActivity.this, "请将答案填写完整，并开启全局录音");
                    }
                } else {
                    if (answer != null) {
                        return true;
                    } else {
                        ToastUtils.showShort(ProjectStartActivity.this, "请将答案填写完整，并开启全局录音");
                    }
                }
            } else {
                if (recorderModel == 3) {
                    if (describeStatus == 1 && photoStatus == 1) {
                        if (answer != null && remakes != null && imagesDBList != null &&   imagesDBList.size() > 0 && !imagesDBList.isEmpty() && !isAllRecorder) {
                            return true;
                        } else {
                            ToastUtils.showShort(ProjectStartActivity.this, "请将答案填写完整，并开启全局录音");
                        }
                    } else if (describeStatus == 1 && photoStatus == -1) {
                        if (answer != null && remakes != null && !isAllRecorder) {
                            return true;
                        } else {
                            ToastUtils.showShort(ProjectStartActivity.this, "请将答案填写完整，并开启全局录音");
                        }

                    } else if (describeStatus == -1 && photoStatus == 1) {
                        if (answer != null && imagesDBList.size() > 0 && imagesDBList != null && !imagesDBList.isEmpty() && !isAllRecorder) {
                            return true;
                        } else {
                            ToastUtils.showShort(ProjectStartActivity.this, "请将答案填写完整，并开启全局录音");
                        }
                    } else {
                        if (answer != null && !isAllRecorder) {
                            return true;
                        } else {
                            ToastUtils.showShort(ProjectStartActivity.this, "请将答案填写完整，并开启全局录音");
                        }
                    }
                } else {
                    ToastUtils.showShort(ProjectStartActivity.this, "请开启全局录音");
                }

            }


        }
        return false;
    }

    /**
     * 根据录音状态和有无录音判断录音状态
     */
    private void ByRecordChanceReModel() {
        int id = subjectsDBList.get(mCurrentPage).getId();
        SubjectsDB SubjectsDB = DataBaseWork.DBSelectById(SubjectsDB.class, id);
        int recordStatus = SubjectsDB.getRecordStatus();    //是否需要强制录音
        // List<RecorderFilesDB> recorderFiles = SubjectsDB.getRecorderFiles();     //录音
        final int recorderModel = mPreferencesTool.getRecorderModel("RecorderModel");    //录音模式
        boolean isAllRecorder = mPreferencesTool.getIsRecorderAll("IsAllRecorder");   //全局录音是否暂停
        boolean single_recorder = mPreferencesTool.getSingleRecorder("single_recorder"); //是否开始单题录音
        boolean all_recorder = mPreferencesTool.getAllRecorder("all_recorder");   //是否开始全局录音
        /**
         * 单体录音
         */
        if (ProjectrecordType==2){
            if (recorderModel==1){
                if(IsHaveRecorder()){
                   // audioRecordBinders.canceled();
                }else {
                    if (recordStatus == 1) {   //如果为强制录音

                       if (isFirstDialog){
                           recorderTime=0;
                           setTime();
                       } else {
                           recorderTime=0;
                           setTimeHandlerStart();
                       }
                        String systemTime_str = FileUtils.getSystemTime_str();
                        mPreferencesTool.setSingleRecorder("single_recorder", true);
                        mPreferencesTool.setRecorderModel("RecorderModel",2);
                        audioRecordBinders.startRecords(systemTime_str, ProjectStartActivity.this);
                    }
                }
            } else if(recorderModel==2){
                if (IsHaveRecorder()){
                   // audioRecordBinders.canceled();

                } else {
                    if (isFirstDialog){
                        recorderTime=0;
                        setTime();
                    } else {
                        recorderTime=0;
                        setTimeHandlerStart();
                    }
                    String systemTime_str = FileUtils.getSystemTime_str();
                    audioRecordBinders.startRecords(systemTime_str, ProjectStartActivity.this);
                    mPreferencesTool.setSingleRecorder("single_recorder", true);
                }

            } else if (recorderModel==3){
                if (IsHaveRecorder()){  //有录音
                    if (all_recorder){   //录音开启过
                        if (isAllRecorder){  //当前为暂停

                        }else {
                            setTimeHandlerStop();
                            audioRecordBinders.pause();
                            mPreferencesTool.setIsRecorderAll("IsRecorderAll", true);
                        }

                    }
                } else {
                    if (all_recorder){
                        if (isAllRecorder){
                            setTimeHandlerStart();
                            audioRecordBinders.jixu(ProjectStartActivity.this);
                            mPreferencesTool.setIsRecorderAll("IsRecorderAll", false);
                        } else {

                        }
                    } else {  //没有录音且没开录音
                        if (isFirstDialog){
                            recorderTime=0;
                            setTime();
                        } else {
                            recorderTime=0;
                            setTimeHandlerStart();
                        }
                         String systemTime_str = FileUtils.getSystemTime_str();
                         mPreferencesTool.setRecorderModel("RecorderModel",3);
                         audioRecordBinders.startRecords(systemTime_str, ProjectStartActivity.this);
                         mPreferencesTool.setAllRecorder("all_recorder", true);
                    }
                }

            }
        }

        /**
         * 全局录音
         */
        if (ProjectrecordType==3){
          if (recorderModel==1){
               if (IsHaveRecorder()){ //有录音
                   if (all_recorder){  //开启过录音
                       if (isAllRecorder){  //在暂停

                       } else {   //非暂停中就暂停
                           setTimeHandlerStop();
                           audioRecordBinders.pause();
                           mPreferencesTool.setIsRecorderAll("IsRecorderAll", true);
                       }
                   }
               } else {  //没录音
                   if (all_recorder){
                       if (isAllRecorder){ //当前为暂停录音
                           setTimeHandlerStart();
                           audioRecordBinders.jixu(ProjectStartActivity.this);
                           mPreferencesTool.setAllRecorder("all_recorder", false);
                       }
                   } else {
                       if (isFirstDialog){
                           recorderTime=0;
                           setTime();
                       } else {
                           recorderTime=0;
                           setTimeHandlerStart();
                       }
                       String systemTime_str = FileUtils.getSystemTime_str();
                       mPreferencesTool.setRecorderModel("RecorderModel",3);
                       audioRecordBinders.startRecords(systemTime_str, ProjectStartActivity.this);
                       mPreferencesTool.setAllRecorder("all_recorder", true);
                   }
               }
          } else if (recorderModel==3){
              if (IsHaveRecorder()){
                    if (all_recorder){
                        if (isAllRecorder){

                        } else {
                            setTimeHandlerStop();
                            audioRecordBinders.pause();
                            mPreferencesTool.setIsRecorderAll("IsRecorderAll", true);
                        }
                    }
              } else {
                  if (all_recorder){
                      if (isAllRecorder){ //暂停录音
                         setTimeHandlerStart();
                          audioRecordBinders.jixu(ProjectStartActivity.this);
                          mPreferencesTool.setAllRecorder("all_recorder", false);
                      }
                  } else {
                      if (isFirstDialog){
                          recorderTime=0;
                          setTime();
                      } else {
                          recorderTime=0;
                          setTimeHandlerStart();
                      }
                      String systemTime_str = FileUtils.getSystemTime_str();
                      mPreferencesTool.setRecorderModel("RecorderModel",3);
                      audioRecordBinders.startRecords(systemTime_str, ProjectStartActivity.this);
                      mPreferencesTool.setAllRecorder("all_recorder", true);
                  }
              }
          }
        }

    }

    /**
     * 翻页
     */
    private void InfoNext() {
        int recorderModel = mPreferencesTool.getRecorderModel("RecorderModel");    //录音模式
        boolean single_recorder = mPreferencesTool.getSingleRecorder("single_recorder");  //单体录音开启
        boolean all_recorder = mPreferencesTool.getAllRecorder("all_recorder");     //全局录音开启
        if (ProjectrecordType == 2) {
         if (recorderModel == 1) {
                   if (mCurrentPage < subjectsDBList.size() - 1) {

                    if (isCanNext()){
                        if (subjectsDBList != null && subjectsDBList.size() > 0) {
                            SubjectsDB subjectsDB = subjectsDBList.get(mCurrentPage);
                            subjectsDB.setIsComplete(1);
                            subjectsDB.update(subjectsDB.getId());
                        }
                        mCurrentPage += 1;
                        moveToPosition(mCurrentPage);
                        ByRecordChanceReModel();
                    } else {
                        ToastUtils.showLong(ProjectStartActivity.this, "请完善题目答案和录音");
                    }
                } else {
                    setTimeStop();
                    ToastUtils.showLong(ProjectStartActivity.this, "已经是最后一题");
                    if (single_recorder){
                        savePage = mCurrentPage;
                        audioRecordBinders.stop(ProjectStartActivity.this,getAudioNameOne(),projectsDB.getPid());
                        mPreferencesTool.setAllRecorder("all_recorder",false);
                        mPreferencesTool.setSingleRecorder("single_recorder", false);
                    }else {
                        isCompleteD();
                    }
                    lastDialog();

                }
           //  ToastUtils.showLong(ProjectStartActivity.this,"请开启录音");
            } else if (recorderModel == 2) {
                if (mCurrentPage < subjectsDBList.size() - 1) {
                    if (single_recorder) {
                        savePage = mCurrentPage;
                        recorderTime=0;
                        setTimeHandlerStop();
                        audioRecordBinders.stop(ProjectStartActivity.this,getAudioNameOne(),projectsDB.getPid());
                        mPreferencesTool.setSingleRecorder("single_recorder", false);
                    }
                    if (isCanNext()) {
                        if (subjectsDBList != null && subjectsDBList.size() > 0) {
                            SubjectsDB subjectsDB = subjectsDBList.get(mCurrentPage);
                            subjectsDB.setIsComplete(1);
                            subjectsDB.update(subjectsDB.getId());
                        }
                        mCurrentPage += 1;
                        moveToPosition(mCurrentPage);
                        ByRecordChanceReModel();
                    }else {
                        ToastUtils.showLong(ProjectStartActivity.this, "请完善题目答案和录音");
                    }
                } else {
                   setTimeStop();
                    ToastUtils.showLong(ProjectStartActivity.this, "已经是最后一题");
                    if (single_recorder){
                        savePage = mCurrentPage;
                        audioRecordBinders.stop(ProjectStartActivity.this,getAudioNameOne(),projectsDB.getPid());
                        mPreferencesTool.setAllRecorder("all_recorder",false);
                        mPreferencesTool.setSingleRecorder("single_recorder", false);
                    }else {
                        isCompleteD();
                    }
                    lastDialog();
                }
            } else if (recorderModel == 3) {
                if (mCurrentPage < subjectsDBList.size() - 1) {
                    if (isCanNext()) {
                        list_subjects.add(mCurrentPage);
                        mCurrentPage+=1;
                        moveToPosition(mCurrentPage);
                        ByRecordChanceReModel();
                    }else {
                        ToastUtils.showLong(ProjectStartActivity.this, "请完善题目答案和录音");
                    }

                } else {
                   setTimeStop();
                    list_subjects.add(mCurrentPage);
                    ToastUtils.showLong(ProjectStartActivity.this, "已经是最后一题");
                    if (all_recorder){
                        audioRecordBinders.stop(ProjectStartActivity.this,getAudioName(),projectsDB.getPid());
                        mPreferencesTool.setIsRecorderAll("IsRecorderAll", false);
                        mPreferencesTool.setSingleRecorder("single_recorder",false);
                        mPreferencesTool.setAllRecorder("all_recorder", false);
                    } else {
                        isCompleteQ();
                    }
                       lastDialog();
                }

            }/*else {
                if (mCurrentPage < subjectsDBList.size() - 1) {
                    if (isCanNext()) {
                        mCurrentPage += 1;
                        moveToPosition(mCurrentPage);
                        ByRecordChanceReModel();
                    }else {
                        ToastUtils.showLong(ProjectStartActivity.this, "请完善题目答案和录音");
                    }

                }else {
                    isCloseThread=true;
                    recorderTime=0;
                    ToastUtils.showLong(ProjectStartActivity.this, "已经是最后一题");
                        audioRecordBinders.stop(ProjectStartActivity.this);
                        mPreferencesTool.setIsRecorderAll("IsRecorderAll", false);
                        mPreferencesTool.setSingleRecorder("single_recorder",false);
                        mPreferencesTool.setAllRecorder("all_recorder", false);
                        isCompleteQ();
                    lastDialog();
                }
            }*/
        }
        if (ProjectrecordType == 3) {
            if (recorderModel==1){
                if (mCurrentPage < subjectsDBList.size() - 1) {
                    if (isCanNext()) {
                        list_subjects.add(mCurrentPage);
                        mCurrentPage += 1;
                        moveToPosition(mCurrentPage);
                        ByRecordChanceReModel();
                    }else {
                        ToastUtils.showLong(ProjectStartActivity.this, "请完善题目答案和录音");
                    }
                 } else {
                   setTimeStop();
                    list_subjects.add(mCurrentPage);
                    audioRecordBinders.stop(ProjectStartActivity.this,getAudioName(),projectsDB.getPid());
                    mPreferencesTool.setIsRecorderAll("IsRecorderAll", false);
                    mPreferencesTool.setAllRecorder("all_recorder", false);
                    mPreferencesTool.setSingleRecorder("single_recorder",false);
                    isCompleteQ();
                    lastDialog();

                }
              // ToastUtils.showLong(ProjectStartActivity.this,"请开启录音");
            } else if (recorderModel==3) {
                if (mCurrentPage < subjectsDBList.size() - 1) {
                    if (isCanNext()) {
                        list_subjects.add(mCurrentPage);
                        mCurrentPage += 1;
                        moveToPosition(mCurrentPage);
                        ByRecordChanceReModel();
                    } else {
                        ToastUtils.showLong(ProjectStartActivity.this, "请完善题目答案和录音");
                    }

                } else {
                   setTimeStop();
                    list_subjects.add(mCurrentPage);
                    ToastUtils.showLong(ProjectStartActivity.this, "已经是最后一题");
                    audioRecordBinders.stop(ProjectStartActivity.this,getAudioName(),projectsDB.getPid());
                    mPreferencesTool.setIsRecorderAll("IsRecorderAll", false);
                    mPreferencesTool.setAllRecorder("all_recorder", false);
                    mPreferencesTool.setSingleRecorder("single_recorder", false);
                    isCompleteQ();
                    lastDialog();
                }
            }

        }
    }

    /**
     * 新需求改进判断是否进入
     * @return
     */
    private boolean isCanNext_2(){
        int id = subjectsDBList.get(mCurrentPage).getId();
        SubjectsDB SubjectsDB = DataBaseWork.DBSelectById(SubjectsDB.class, id);
        List<ImagesDB> imagesDBList = SubjectsDB.getImagesDBList();   //图片
        if (imagesDBList !=null && imagesDBList.size()>0){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 新需求下一题
     */
 private void  infoNext_2(){
     if (mCurrentPage < subjectsDBList.size() - 1) {
         if (isCanNext_2()){
             if (subjectsDBList != null && subjectsDBList.size() > 0) {
                 SubjectsDB subjectsDB = subjectsDBList.get(mCurrentPage);
                 subjectsDB.setIsComplete(1);
                 subjectsDB.update(subjectsDB.getId());
             }
             mCurrentPage += 1;
             moveToPosition(mCurrentPage);
         } else {
             ToastUtils.showLong(ProjectStartActivity.this, "图片不能为空");
         }
     } else {
         isAllComplete();
     }
 }

    /**
     * 新需求是否完成
     */

    private boolean isAllComplete(){
        List<Integer> integerList=new ArrayList<>();
        integerList.clear();
        List<SubjectsDB> subjectsDBList = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "projectsdb_id=?", String.valueOf(project_id));
        if (subjectsDBList !=null && subjectsDBList.size()>0){
            for (int i = 0; i <subjectsDBList.size() ; i++) {
                if (subjectsDBList.get(i).getImagesDBList() !=null && subjectsDBList.get(i).getImagesDBList().size()>0){

                } else {
                    integerList.add(subjectsDBList.get(i).getId());
                }
            }
        } else {
            return false;
        }

        if (integerList.size()>0){
            return  false;
        } else {

            return true;
        }
    }


    private void setAdapter(List list) {
        setTitle((TextView) naviBar.findViewById(R.id.title), "访问中(1/" + list.size() + ")");
        startAdapter = new ProjectStartAdapter(ProjectStartActivity.this, list, R.layout.item_recycleview_projectstart, null);
        startAdapter.setWindowWidth(getWindowWidth());
        startAdapter.setWindowHeight(getWindowHeight());
        recyclerView.setAdapter(startAdapter);
        recyclerView.addOnScrollListener(new Recycle());
        startAdapter.setProjectStartOnclick(new MyAdapter());

        if (isFist){
           // customGridLayoutManager.setScrollEnabled(true);
            int progress = mPreferencesTool.getProjectStartProgress("Progress");
            mCurrentPage=progress;
            if (subjectsDBList.get(mCurrentPage).getIsComplete()==1){
                if (mCurrentPage<subjectsDBList.size()-1){
                    moveToPosition(mCurrentPage);
                } else {
                    moveToPosition((mCurrentPage+1));
                }
            }else {
                moveToPosition(mCurrentPage);
            }

            isFist=false;
        }
    }

    private String getFileName(String filePath) {
        if (filePath != null) {
            int start = filePath.lastIndexOf("/") + 1;
            String fileName = filePath.substring(start);
            return fileName;
        } else {
            return null;
        }

    }

    private List<SubjectsDB> getSubject(List list) {
        List<Integer> integers = OrderSortByGroup.removeDuplicate(list);

        List<Integer> mids = new ArrayList<>();
        mids.clear();
        if (integers != null && integers.size() > 0) {
            for (int i = 0; i < integers.size(); i++) {
                int id = subjectsDBList.get(integers.get(i)).getId();
                mids.add(id);
            }
        }
        List<SubjectsDB> subjectsDBListQ = DataBaseWork.DBSelectByIds(SubjectsDB.class, mids);

        List<SubjectsDB> msubjects = new ArrayList<>();
        msubjects.clear();
        if (subjectsDBListQ != null && subjectsDBListQ.size() > 0) {
            for (int i = 0; i < subjectsDBListQ.size(); i++) {
                List<RecorderFilesDB> recorderFiles = subjectsDBListQ.get(i).getRecorderFiles();
                if (recorderFiles == null || recorderFiles.isEmpty()) {
                    msubjects.add(subjectsDBListQ.get(i));
                }
            }
        }
        return msubjects;
    }

    private List<SubjectsDB> getIsCompleteList(List list) {
        List<Integer> integers = OrderSortByGroup.removeDuplicate(list);

        List<Integer> midss = new ArrayList<>();
        midss.clear();
        if (integers != null && integers.size() > 0) {
            for (int i = 0; i < integers.size(); i++) {
                int id = subjectsDBList.get(integers.get(i)).getId();
                midss.add(id);
            }
        }
        List<SubjectsDB> subjectsDBListQ = DataBaseWork.DBSelectByIds(SubjectsDB.class, midss);
        List<SubjectsDB> msubjects = new ArrayList<>();
        msubjects.clear();
        if (subjectsDBListQ != null && subjectsDBListQ.size() > 0) {
            for (int i = 0; i < subjectsDBListQ.size(); i++) {
                List<RecorderFilesDB> recorderFiles = subjectsDBListQ.get(i).getRecorderFiles();
                if (recorderFiles !=null || recorderFiles.size()>0) {
                    msubjects.add(subjectsDBListQ.get(i));
                }
            }
        }
        return msubjects;
    }


    /**
     * 过期
     */
    private void isCompleteQ() {
        boolean a = false;
        boolean b = false;
        boolean c = false;
        boolean d = false;
        List<SubjectsDB> subject = getIsCompleteList(list_subjects);
        if (subject != null && subject.size() > 0) {
            for (int i = 0; i < subject.size(); i++) {

                /*图片*/
                if (subject.get(i).getPhotoStatus() == 1) {
                    List<ImagesDB> imagesDBList = subject.get(i).getImagesDBList();
                    if (imagesDBList != null && imagesDBList.size() > 0) {
                        a = true;
                    }
                }
                if (subject.get(i).getPhotoStatus() == -1) {
                    a = true;
                }
                /*答案*/
                List<AnswersDB> answersList = subject.get(i).getAnswers();
                if (answersList != null && answersList.size() > 0) {
                    String answer = answersList.get(0).getAnswer();
                    if (answer != null) {
                        b = true;
                    }
                }
                /*描述*/
                if (subject.get(i).getDescribeStatus() == 1) {
                    String remakes = answersList.get(0).getRemakes();
                    if (remakes != null) {
                        c = true;
                    }
                }
                if (subject.get(i).getDescribeStatus() == -1) {
                    c = true;
                }
                /*录音*/
                if (ProjectrecordType == 3) {
                    List<RecorderFilesDB> recorderFiles = subject.get(i).getRecorderFiles();
                    if (recorderFiles != null && recorderFiles.size() > 0) {
                        d = true;
                    }
                }
                if (ProjectrecordType == 2) {
                    if (subject.get(i).getRecordStatus() == 1) {
                        List<RecorderFilesDB> recorderFiles = subject.get(i).getRecorderFiles();
                        if (recorderFiles != null && recorderFiles.size() > 0) {
                            String recorderPath = recorderFiles.get(0).getRecorderPath();
                            if (recorderPath != null) {

                                d = true;
                            }
                        }
                    }
                    if (subject.get(i).getRecordStatus() == -1) {
                        d = true;
                    }
                }
                if (a && b && c && d) {
                    subject.get(i).setIsComplete(1);
                    subject.get(i).update(subject.get(i).getId());
                }
            }
        }
    }


    /**
     * 过期
     */
    private void isCompleteD() {
        boolean e = false;
        boolean f = false;
        boolean g = false;
        boolean h = false;
        SubjectsDB subjectsDB = subjectsDBList.get(mCurrentPage);
        if (subjectsDB != null) {
            if (subjectsDB.getPhotoStatus() == 1) {
                /*图片*/
                List<ImagesDB> imagesDBList = subjectsDB.getImagesDBList();
                if (imagesDBList != null && imagesDBList.size() > 0) {
                    e = true;
                }
            } else if (subjectsDB.getPhotoStatus() == -1) {
                e = true;
            }
            /*答案*/
            List<AnswersDB> answersList = subjectsDB.getAnswers();
            if (answersList != null && answersList.size() > 0) {
                String answer = answersList.get(0).getAnswer();
                if (answer != null) {
                    f = true;
                }
                if (subjectsDB.getDescribeStatus() == 1) {
                    String remakes = answersList.get(0).getRemakes();
                    if (remakes != null) {
                        g = true;
                    }

                } else if (subjectsDB.getDescribeStatus() == -1) {
                    g = true;
                }
            }
            /*录音*/
            if (subjectsDB.getRecordStatus() == 1) {
                List<RecorderFilesDB> recorderFiles = subjectsDB.getRecorderFiles();
                if (recorderFiles != null && recorderFiles.size() > 0) {
                    String recorderPath = recorderFiles.get(0).getRecorderPath();
                    if (recorderPath != null) {
                        h = true;
                    }
                }
            } else if (subjectsDB.getRecordStatus() == -1) {
                h = true;
            }
            if (e && f && g && h) {
                subjectsDB.setIsComplete(1);
                subjectsDB.update(subjectsDB.getId());
            }

        }
    }
    private boolean P_RecorderHave(int id){
        List<RecorderFilesDB> recorderFilesDBS = DataBaseWork.DBSelectByTogether_Where(RecorderFilesDB.class, "subjectsdb_id=?", String.valueOf(id));
        if (recorderFilesDBS !=null && recorderFilesDBS.size()>0){
            String recorderPath = recorderFilesDBS.get(0).getRecorderPath();
            if (recorderPath!=null){
                return false;
            }
        }
        return true;
    }
   private void lastDialog(){
       List<ProjectsDB> projectsDBS_d = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "pid=?", project_id);
       boolean browseMethod = mPreferencesTool.getBrowseMethod("browseMethod");
       long startTime=0;
       int size=0;
       int upSub=0;
       int imageSize = 0;
       int recorSize=0;
       if (projectsDBS_d!=null && projectsDBS_d.size()>0){
            startTime = projectsDBS_d.get(0).getStartTime();
       }
       String name = mPreferencesTool.getUserName("name");
       if (subjectsDBList !=null && subjectsDBList.size()>0){
            size = subjectsDBList.size();
       }
       List<SubjectsDB> subjectsDBList_d = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "censor=?", String.valueOf(0));
       if (subjectsDBList_d !=null && subjectsDBList_d.size()>0){
           upSub= subjectsDBList_d.size();
           for (int i = 0; i < subjectsDBList_d.size(); i++) {
               if (subjectsDBList_d.get(i).getImagesDBList() !=null && subjectsDBList_d.get(i).getImagesDBList().size()>0){
                   List<ImagesDB> imagesDBList_d = subjectsDBList_d.get(i).getImagesDBList();
                   if (imagesDBList_d !=null && imagesDBList_d.size()>0){
                       for (int j = 0; j <imagesDBList_d.size() ; j++) {
                           imageSize++;
                       }
                   }

               }
               if (subjectsDBList_d.get(i).getRecorderFiles() !=null && subjectsDBList_d.get(i).getRecorderFiles().size()>0){
                           recorSize++;
               }
           }
       }
       new ProjectDialogLast(ProjectStartActivity.this,startTime,browseMethod,name,String.valueOf(size)+"/"+String.valueOf(size),upSub+"条数据，"+(imageSize+recorSize)+"个附件未上传", new ProjectDialogLast.OnProjectCloseListener() {
           @Override
           public void onClick(Dialog dialog, boolean isBrowser) {
               if (isBrowser){
                   Intent intent = new Intent(ProjectStartActivity.this , BrowseActivity.class);
                   intent.putExtra("projectId" ,project_id);
                   intent.putExtra("status" , -1);
                   startActivity(intent);
               }else {

               }
               dialog.dismiss();
           }
       }).show();
   }


    private void moveToPosition(int n) {
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = customGridLayoutManager.findFirstVisibleItemPosition();
        int lastItem = customGridLayoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (n <= firstItem ){
            customGridLayoutManager.setScrollEnabled(true);
            //当要置顶的项在当前显示的第一个项的前面时
            recyclerView.scrollToPosition(n);
            customGridLayoutManager.setScrollEnabled(false);

        }else if ( n <= lastItem ){
            customGridLayoutManager.setScrollEnabled(true);
            //当要置顶的项已经在屏幕上显示时
            int left = recyclerView.getChildAt(n - firstItem).getLeft();
            recyclerView.scrollBy(0, left);
            customGridLayoutManager.setScrollEnabled(false);
        }else{
            customGridLayoutManager.setScrollEnabled(true);
            //当要置顶的项在当前显示的最后一项的后面时
            recyclerView.scrollToPosition(n);
            //这里这个变量是用在RecyclerView滚动监听里面的
            move = true;
        }
        String title = String.format(getResources().getString(R.string.ProjectStartTitle), (mCurrentPage+1) , subjectsDBList.size());
        getTitleView().setText(title);
    }

    class Recycle extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //在这里进行第二次滚动（最后的100米！）
            if (move ){
                move = false;
                //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                int n =mCurrentPage-customGridLayoutManager.findFirstVisibleItemPosition();
                if ( 0 <= n && n < recyclerView.getChildCount()){
                    //获取要置顶的项顶部离RecyclerView顶部的距离
                    int left = recyclerView.getChildAt(n).getLeft();
                    //最后的移动
                    recyclerView.scrollBy(0, left);
                }
                customGridLayoutManager.setScrollEnabled(false);
            }
        }

    }

    /*计时类*/
    private void setTime(){
            isCloseThread=false;
            if (timeThread==null){
                timeThread=new MyThress();
            }
            if (isStartThread){
                timeThread.start();
                isStartThread=false;
            }
            if (timeHandler==null){
                timeHandler=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what==1){
                            boolean all_recorder = mPreferencesTool.getAllRecorder("all_recorder");
                            if (isStartTimeHandler){
                                String s = FileUtils.msToss((recorderTime++)*1000);
                                if (startAdapter.getMyViewHolderList() !=null &&startAdapter.getMyViewHolderList().size()>0  && isCloseThread==false)
                                    for (int i = 0; i <startAdapter.getMyViewHolderList().size() ; i++) {
                                        // BaseViewHolder holder = (BaseViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                                        ((TextView)startAdapter.getMyViewHolderList().get(i).getView(R.id.start_recorder_time)).setText(s);
                                    }
                            }else {
                              if (all_recorder){
                                  String s = FileUtils.msToss(recorderTime*1000);
                                  if (startAdapter.getMyViewHolderList() !=null &&startAdapter.getMyViewHolderList().size()>0){
                                      for (int i = 0; i <startAdapter.getMyViewHolderList().size() ; i++) {
                                          // BaseViewHolder holder = (BaseViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                                          ((TextView)startAdapter.getMyViewHolderList().get(i).getView(R.id.start_recorder_time)).setText(s);
                                      }
                                  }
                              } else {
                                  String s = FileUtils.msToss(0*1000);
                                  if (startAdapter.getMyViewHolderList() !=null &&startAdapter.getMyViewHolderList().size()>0){
                                      for (int i = 0; i <startAdapter.getMyViewHolderList().size() ; i++) {
                                          // BaseViewHolder holder = (BaseViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                                          ((TextView)startAdapter.getMyViewHolderList().get(i).getView(R.id.start_recorder_time)).setText(s);
                                      }
                                  }
                              }
                            }
                        }
                    }
                };
            }
    }

    private void setTimeStop(){
        isCloseThread=true;
       isStartTimeHandler=false;
        recorderTime=0;

    }

    private void setTimeHandlerStart(){
        isStartTimeHandler=true;
    }
    private void setTimeHandlerStop(){
        isStartTimeHandler=false;
    }
    /**
     * 时间线程
     */
    class MyThress extends Thread{
        @Override
        public void run() {
            super.run();
            do{
                try {
                    timeThread.sleep(1000);
                    Message message=new Message();
                    message.what=1;
                    if (timeHandler !=null){
                        timeHandler.sendMessage(message);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }while (!isCloseThread);
        }
    }
    private String getAudioName(){
        StringBuilder audioName=new StringBuilder();
        List<SubjectsDB> subject = getSubject(list_subjects);
        if (subject !=null && subject.size()>0){
            for (int i = 0; i <subject.size() ; i++) {
            if (subject.size()==1){
                audioName.append(subject.get(0).getNumber()+"_"+subject.get(0).getNumber());
            }else {
                if (i==0){
                    audioName.append(subject.get(i).getNumber()+"_");
                }
                if (i==subject.size()-1){
                    audioName.append(subject.get(i).getNumber());
                }
            }
            }
        }
  /*      if (list_subjects !=null && list_subjects.size()>0){
            for (int i = 0; i <list_subjects.size() ; i++) {
                if (list_subjects.size()==1){
                    SubjectsDB subjectsDB = subjectsDBList.get(list_subjects.get(i));
                    audioName.append(subjectsDB.getNumber()+"_"+subjectsDB.getNumber());
                }else {
                    if (i==0){
                        SubjectsDB subjectsDB = subjectsDBList.get(list_subjects.get(i));
                        audioName.append(subjectsDB.getNumber()+"_");
                    }
                    if (i==list_subjects.size()-1){
                        SubjectsDB subjectsDB = subjectsDBList.get(list_subjects.get(i));
                        audioName.append(subjectsDB.getNumber());
                    }

                }
            }
        }*/
        return  audioName.toString();
    }

    private String getAudioNameOne(){
        StringBuilder audioName=new StringBuilder();
        audioName.append(subjectsDBList.get(mCurrentPage).getNumber()+"_"+subjectsDBList.get(mCurrentPage).getNumber());
        return audioName.toString();
    }

}


