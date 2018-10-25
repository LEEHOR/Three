package net.coahr.three3.three.Verify;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.coahr.three3.three.Base.BaseActivity;
import net.coahr.three3.three.Base.BaseModel;
import net.coahr.three3.three.Base.BasePara;
import net.coahr.three3.three.DBbase.AnswersDB;
import net.coahr.three3.three.DBbase.ImagesDB;
import net.coahr.three3.three.DBbase.ProjectsDB;
import net.coahr.three3.three.DBbase.RecorderFilesDB;
import net.coahr.three3.three.DBbase.SubjectsDB;
import net.coahr.three3.three.Model.VerifyInfoDetailModel;
import net.coahr.three3.three.NetWork.RetrofitManager;
import net.coahr.three3.three.Popupwindow.AlertDialogs.CommomDialog;
import net.coahr.three3.three.Popupwindow.AlertDialogs.Dialog_bottomListener;
import net.coahr.three3.three.Popupwindow.AlertDialogs.Dialog_bottomView;
import net.coahr.three3.three.Popupwindow.PopuWindows;
import net.coahr.three3.three.Popupwindow.PopuWindowsListener;
import net.coahr.three3.three.R;
import net.coahr.three3.three.RecyclerViewAdapter.VerifyReviseAdapter;
import net.coahr.three3.three.Util.AudioRecorder.AudioRecordService;
import net.coahr.three3.three.Util.AudioRecorder.MediaPlayManage;
import net.coahr.three3.three.Util.ImageFactory.LubanZip;
import net.coahr.three3.three.Util.JDBC.DataBaseWork;
import net.coahr.three3.three.Util.Notification.NotifyPermission;
import net.coahr.three3.three.Util.OtherUtils.FileUtils;
import net.coahr.three3.three.Util.OtherUtils.ToastUtils;
import net.coahr.three3.three.Util.Preferences.PreferencesTool;
import net.coahr.three3.three.Util.camera2library.camera.CameraActivity;
import net.coahr.three3.three.Util.imageselector.utils.ImageSelectorUtils;
import net.coahr.three3.three.customView.BrowseSubjectsView;
import net.coahr.three3.three.customView.CustomGridLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yuwei on 2018/4/10.
 */

public class VerifyReviseActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    public EditFcous fcous; //访问者说明编辑
    private View deleteView;
    private Button deleteBtn;
    private TextView upBtn;
    private TextView nextBtn;
    private int mPosition;
    private List mImageList;
    private DeleteImageInterface deleteImageInterface;
    private int  mCurrentPage ;
    private PopuWindows mpopupWindows;
    private Dialog_bottomView mbottomView;
    private SharedPreferences.Editor subjectlist_SP; //题目列表标识spu
    //题目总数   当前题目 : 默认值
    private static final int REQUEST_CODE = 0x00000011; //图片选择代码
    private boolean verifyFlag;
    private VerifyReviseAdapter verifyReviseAdapter;
    private  VerifyInfoDetailModel mVerifyInfoDetailModel;
    private int index;
    private LinearLayoutManager layoutManager;
    private PreferencesTool mPreferencesTool;
    private  List<VerifyInfoDetailModel.VerifyDetailBean> list;
    private Timer myTimer;
    private  String projectId;
    private  ProjectsDB projectsDB;
    private    CustomGridLayoutManager customGridLayoutManager;
    private boolean move;


   /* private boolean isChanging = false;//是否正在拖拽seekbar

    public void setChanging(boolean changing) {
        isChanging = changing;
    }*/

   private ImageView camera,introductions,recorder;

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
        final NotifyPermission notifyPermission=new NotifyPermission(this);
        boolean notificationEnabled = notifyPermission.isNotificationEnabled();
        if (notificationEnabled){

        }else{
            new CommomDialog(VerifyReviseActivity.this, R.style.dialog, "我们需要通知权限权限", true, new CommomDialog.OnCloseListener() {
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
        mPreferencesTool=new PreferencesTool(this);
        int status = getIntent().getIntExtra("status" , -1);
        projectId = getIntent().getStringExtra("projectId");
        index = getIntent().getIntExtra("index" , 0);
        List<ProjectsDB> projectsDBS = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "pid=?", projectId);
        if (projectsDBS !=null && projectsDBS.size()>0){
             projectsDB = projectsDBS.get(0);
        }
        verifyFlag = false;
        findUI();
        if (status==1){
                verifyReviseAdapter=null;
        }
        requestRemote(projectId , status);


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
//        mEditToolView = findViewById(R.id.EditView);
//        mEditToolView.setOnClickListener(new ItemClick());
        verifyFlag = getIntent().getBooleanExtra("flag" , false);
        configureNaviBar(naviBar.findViewById(R.id.left), naviBar.findViewById(R.id.right));

        getLeftBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subjectlist_SP != null) {
                    subjectlist_SP.clear().commit();
                }
                isCompleteD();
                finish();
            }
        });
        getRightBtn().setBackgroundResource(R.drawable.menu);
        getRightBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopupWindows=new PopuWindows();
                mpopupWindows.showPopupWindow(findViewById(R.id.line),VerifyReviseActivity.this,verifyFlag);
                mpopupWindows.setOnButtonClickListener(new PopuWindowsListener() {
                    @Override
                    public void subjectItem(PopupWindow popupWindow) {
                        List<SubjectsDB> subjectsDBList=new ArrayList<>();
                        if (list !=null && list.size()>0){
                            for (int i = 0; i <list.size() ; i++) {
                                String id = list.get(i).getId();

                                List<SubjectsDB> subjectsDBS = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "ht_id=?", id);
                                if (subjectsDBS !=null && subjectsDBS.size()>0){
                                    subjectsDBList.add(subjectsDBS.get(0));
                                }
                            }
                        }
                        final BrowseSubjectsView browseSubjectsView = new BrowseSubjectsView(VerifyReviseActivity.this, subjectsDBList);
                        browseSubjectsView.show();
                        browseSubjectsView.setBrowseSubjectDialogListener(new BrowseSubjectsView.BrowseSubjectDialogListener() {
                            @Override
                            public void OnImageViewBack() {
                                browseSubjectsView.dismiss();
                            }

                            @Override
                            public void OnItemClick(int position, Map map, String qu3,int id) {
                                subjectlist_SP = getSharedPreferences("subject", 0).edit();
                                for (Object o : map.keySet()) {
                                    subjectlist_SP.putBoolean((String) o, (boolean) map.get(o));
                                    Log.e("rr", "ker:" + o + "/values:" + map.get(o));
                                }

                                subjectlist_SP.commit();
                                if (P_RecorderHave(id)){ //没有录音
                                    ToastUtils.showLong(VerifyReviseActivity.this,"当前题目下没有录音，请开启或暂停录音");
                                } else {
                                    mCurrentPage=position;
                                    moveToPosition(position);
                                }

                            }
                        });
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

                    }
                });
            }
        });
        /*拍照*/
        camera=findViewById(R.id.shenhe_camera);
        recorder=findViewById(R.id.shenhe_recorder);
        introductions=findViewById(R.id.shenhe_introductions);

        final TextView titleBtn = naviBar.findViewById(R.id.title);
        upBtn = findViewById(R.id.up);
        nextBtn = findViewById(R.id.next);
        upBtn.setOnClickListener(new UpNextBtnClickListener());
        nextBtn.setOnClickListener(new UpNextBtnClickListener());
        mRecyclerView = findViewById(R.id.recyclerView);
      /*  layoutManager = new LinearLayoutManager(VerifyReviseActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);*/
      //  mRecyclerView.setLayoutManager(layoutManager);
        customGridLayoutManager=new CustomGridLayoutManager(VerifyReviseActivity.this,LinearLayoutManager.VERTICAL,false,false);
        //customGridLa.youtManager.setScrollEnabled(false);
        mRecyclerView.setLayoutManager(customGridLayoutManager);

        deleteView = findViewById(R.id.delete);
        deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImageInterface.delete(mPosition, deleteView , mImageList);
            }
        });

        camera.setOnClickListener(new caidan());
        recorder.setOnClickListener(new caidan());
        introductions.setOnClickListener(new caidan());
                //录音回调监听
                // AudioRecorder.getInstance().setAudioRecorderListener(new MyAudioRecords());
    }
    private void moveToPosition(int n) {
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = customGridLayoutManager.findFirstVisibleItemPosition();
        int lastItem = customGridLayoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (n <= firstItem ){
            customGridLayoutManager.setScrollEnabled(true);
            //当要置顶的项在当前显示的第一个项的前面时
            mRecyclerView.scrollToPosition(n);
            customGridLayoutManager.setScrollEnabled(false);

        }else if ( n <= lastItem ){
            customGridLayoutManager.setScrollEnabled(true);
            //当要置顶的项已经在屏幕上显示时
            int left = mRecyclerView.getChildAt(n - firstItem).getLeft();
            mRecyclerView.scrollBy(0, left);
            customGridLayoutManager.setScrollEnabled(false);
        }else{
            customGridLayoutManager.setScrollEnabled(true);
            //当要置顶的项在当前显示的最后一项的后面时
            mRecyclerView.scrollToPosition(n);
            //这里这个变量是用在RecyclerView滚动监听里面的
            move = true;
        }
        String title = String.format(getResources().getString(R.string.ProjectStartTitle), (mCurrentPage+1) , list.size());
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

    /*拍照图片监听*/
    class  caidan implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.shenhe_camera:
                    showPopupWindow(VerifyReviseActivity.this);
                    break;
                case R.id.shenhe_recorder:

                    break;
                case R.id.shenhe_introductions:

                    break;
            }
        }
    }
    class  UpNextBtnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.up){

                if (mVerifyInfoDetailModel !=null && mVerifyInfoDetailModel.getList().size()>=0){
                if (mCurrentPage != 0)
                {
                   // mRecyclerView.smoothScrollToPosition(mCurrentPage-1);
                    moveToPosition(mCurrentPage-=1);
                  //  MediaPlayManage.stopMedia();
                    if (myTimer != null)
                        myTimer.cancel();
                    myTimer = null;

                }
                 isCompleteD();
                }
            }

            if (v.getId() == R.id.next)//这里还需要判断list长度
                if (mVerifyInfoDetailModel !=null && mVerifyInfoDetailModel.getList().size()>=0){


                if (mCurrentPage < mVerifyInfoDetailModel.getList().size()-1)
                {
                    if (isCanNext()){
                        moveToPosition(mCurrentPage+=1);
                        isCompleteD();
                    } else {
                        ToastUtils.showShort(VerifyReviseActivity.this,"请完善当前题目的图片或说明");
                    }

                } else {

                    ToastUtils.showShort(VerifyReviseActivity.this,"已经是最后一题");
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
                ToastUtils.showLong(VerifyReviseActivity.this, "拍照22");
                mPreferencesTool.setProjectId("Pid", projectId);
                Intent intent = new Intent(VerifyReviseActivity.this, CameraActivity.class);
                mPreferencesTool.setProjectStartPhotoPage("subject_id", list.get(mCurrentPage).getId());//保存当前position
                startActivity(intent);
                dialog.dismiss();
            }

            @Override
            public void choosePhotos(Dialog dialog) {
                List<SubjectsDB> subjectsDBList = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "ht_id=?", list.get(mCurrentPage).getId());

                if (subjectsDBList != null && subjectsDBList.size()>0) {
                    List<ImagesDB> imagesDBList = subjectsDBList.get(0).getImagesDBList();
                    int count = 0;
                    if (imagesDBList != null && imagesDBList.size() > 0) {
                        int size = imagesDBList.size();
                        if (size < 10) {
                            count = 10 - size;
                            ImageSelectorUtils.openPhoto(VerifyReviseActivity.this, REQUEST_CODE, false, count);
                        } else {
                            ToastUtils.showLong(VerifyReviseActivity.this, "图片数量已足够");
                        }
                    } else {
                        ImageSelectorUtils.openPhoto(VerifyReviseActivity.this, REQUEST_CODE, false, 9);
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
             final List<SubjectsDB> subjectsDBList = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "ht_id=?", list.get(mCurrentPage).getId());
             ArrayList<String> images = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
            File bitPhotos = FileUtils.createFileDir(VerifyReviseActivity.this, "BitPhotos");
            if (images != null && images.size() > 0) {
                LubanZip.getInstance().getZip(VerifyReviseActivity.this,images, bitPhotos, subjectsDBList.get(0), projectsDB, new LubanZip.LuBanZip() {
                    @Override
                    public void ZipSuccess() {
                        verifyReviseAdapter.notifyItemChanged(mCurrentPage);
                    }
                });

            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        MediaPlayManage.stopMedia();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayManage.pauseMedia();

       // MediaPlayManage.pauseMedia();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("tt", "onResume:Verif " );
        if (verifyReviseAdapter!=null)
            verifyReviseAdapter.notifyItemChanged(mCurrentPage);
      //  MediaPlayManage.resumeMedia();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("tt", "onResume:Verif " );
    }

    /*网络请求*/
    protected void requestRemote(String projectId , final int status) {

        Map<String, Object> map = new HashMap<>();
        map.put("projectId" , projectId);
        map.put("status" , status);
        BasePara para = new BasePara();
        para.setData(map);
        Subscription subscription= RetrofitManager.getInstance()
                .createService(para)
                .getVerifyDetailData(map)
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
                    public void onNext(BaseModel model) {

                        if(model.getResult().equals("1"))
                        {
                             mVerifyInfoDetailModel = (VerifyInfoDetailModel) model.getData();


                           list = mVerifyInfoDetailModel.getList();

                           // verifyReviseAdapter.Update(list);

                                setVerifyReviseAdapter(list);
                              //  setAdapter();
                            setTitle((TextView) naviBar.findViewById(R.id.title), verifyFlag ? "浏览中(1/"+mVerifyInfoDetailModel.getList().size() +")" : "(修改中1/"+mVerifyInfoDetailModel.getList().size() +")");
                            mRecyclerView.smoothScrollToPosition(index);
                        }
                    }
                });

        addSubscription(subscription);


    }




    class Mydelete implements VerifyReviseAdapter.ShowDeleteViewInterface {
        @Override
        public void show(int position, List imageList) {
            deleteView.setVisibility(View.VISIBLE);
            mPosition = position;
            mImageList = imageList;
        }
    }

    class MyVewiAdapterListerer implements VerifyReviseAdapter.VerifyReAdapter{

        @Override
        public void R_YES(String id,int position, Map<String,String> map,String answer) {
           // String id = mVerifyInfoDetailModel.getList().get(position).getId();
            List<SubjectsDB> subjectsDBList = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "ht_id=?", id);
            if (subjectsDBList!=null && subjectsDBList.size()>0){
                List<AnswersDB> answers = subjectsDBList.get(0).getAnswers();
                if (answers !=null && answers.size()>0){
                    answers.get(0).setSubjectsDB(subjectsDBList.get(0));
                    answers.get(0).setAnswer(answer);
                    answers.get(0).update(answers.get(0).getId());
                    map.put(id,answer);
                } else {                                            //不存在就保存
                    AnswersDB answersDB=new AnswersDB();
                    answersDB.setAnswer(answer);
                    answersDB.setSubjectsDB(subjectsDBList.get(0));
                    answersDB.save();
                    map.put(id,answer);
                }
            }
            verifyReviseAdapter.setMap(map,position);
        }

        @Override
        public void R_NO(String id,int position, Map<String,String> map,String answer) {
           // String id = mVerifyInfoDetailModel.getList().get(position).getId();
         //   SubjectsDB SubjectsDB = DataBaseWork.DBSelectById(SubjectsDB.class, id);
            List<SubjectsDB> subjectsDBList = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "ht_id=?", id);
           if (subjectsDBList!=null && subjectsDBList.size()>0){
               List<AnswersDB> answers = subjectsDBList.get(0).getAnswers();
               if (answers !=null && answers.size()>0){
                   answers.get(0).setSubjectsDB(subjectsDBList.get(0));
                   answers.get(0).setAnswer(answer);
                   answers.get(0).update(answers.get(0).getId());
                       map.put(id,answer);
                   } else {                                            //不存在就保存
                       AnswersDB answersDB=new AnswersDB();
                       answersDB.setAnswer(answer);
                       answersDB.setSubjectsDB(subjectsDBList.get(0));
                       answersDB.save();
                       map.put(id,answer);
                   }
               }
            verifyReviseAdapter.setMap(map,position);

           }

        @Override
        public void R_introductions(int position, String id, Map map) {
            new MaterialDialog.Builder(VerifyReviseActivity.this)
                    .title("输入框")
                    .iconRes(R.mipmap.ic_launcher)
                    .content("请填写您对本题的看法：")
//                                .widgetColor(Color.BLUE)//输入框光标的颜色
                    .inputType(InputType.TYPE_CLASS_TEXT)//可以输入的类型-电话号码
                    .inputRange(10,100)
                    .input("我的看法", "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            List<SubjectsDB> subjectsDBList = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "ht_id=?", id);
                            if (subjectsDBList != null && subjectsDBList.size() > 0) {

                                List<AnswersDB> answersdb = subjectsDBList.get(0).getAnswers();
                                if (answersdb != null && answersdb.size() > 0 && !answersdb.isEmpty()) {  //如果存在就更新
                                    answersdb.get(0).setSubjectsDB(subjectsDBList.get(0));
                                    answersdb.get(0).setRemakes(input.toString());
                                    int update = answersdb.get(0).update(answersdb.get(0).getId());
                                    if (update > 0) {
                                        map.put(id, input.toString());
                                    }
                                    Log.e("审核", "EditTextView: " + position + "/" + input + "/" + "更新");
                                } else {                                            //不存在就保存
                                    AnswersDB answersDB = new AnswersDB();
                                    answersDB.setRemakes(input.toString());
                                    answersDB.setSubjectsDB(subjectsDBList.get(0));
                                    boolean save = answersDB.save();
                                    if (save) {

                                        map.put(id, input.toString());
                                    }
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
                                verifyReviseAdapter.setMapRemake(map,position);
                            }
                        }
                    }).show();
        }


    }


    /*====================================首次进入录音dialog提示==========================*/
    private void RecorderDialog() {

            new CommomDialog(VerifyReviseActivity.this, R.style.dialog, "请开启录音", false, false, null, new CommomDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {
                        String systemTime_str = FileUtils.getSystemTime_str();
                        Log.e("rr", "单题录音" + systemTime_str);
                        audioRecordBinders.startRecords(systemTime_str, VerifyReviseActivity.this);
                        mPreferencesTool.setSingleRecorder("single_recorder",true);
                        dialog.dismiss();
                    } else {

                        dialog.dismiss();
                    }
                }
            }).setTitle("").setPositiveButton("确定").show();


    }

    //录音回调
   /* class MyAudioRecords implements RecordStreamListener{

        @Override
        public void recordOfByte(byte[] data, int begin, int end) {

        }

        @Override
        public void OnSuccess(final String filePath) {
             if (list !=null){
                 final String fileName = getFileName(filePath);

                         VerifyInfoDetailModel.VerifyDetailBean verifyDetailBean = list.get(mCurrentPage);
                 Log.e("题目ID", "OnSuccess: "+verifyDetailBean.getId() );
                         List<SubjectsDB> subjectsDBList = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "ht_id=?", verifyDetailBean.getId());
                         if (subjectsDBList !=null && subjectsDBList.size()>0){
                             List<RecorderFilesDB> recorderFiles = subjectsDBList.get(0).getRecorderFiles();
                             if (recorderFiles !=null && recorderFiles.size()>0){
                                 recorderFiles.get(0).setRecorderPath(filePath);
                                 recorderFiles.get(0).setRecorderName(fileName);
                                 recorderFiles.get(0).update(recorderFiles.get(0).getId());
                                 Log.e("审核", "run: "+fileName );
                             }

                         }else {
                             ToastUtils.showLong(VerifyReviseActivity.this,"题目不存在");
                         }

                     }

                 if (isFinish){
                     finish();
                 }

        }

        @Override
        public void OnFail() {

        }
    }*/
    /*获取文件名*/
    private String getFileName(String filePath) {
        if (filePath != null) {
            int start = filePath.lastIndexOf("/") + 1;
            String fileName = filePath.substring(start);
            return fileName;
        } else {
            return null;
        }

    }


    private void isCompleteD(){
        boolean e=false;
        boolean f=false;
        boolean g=false;
        boolean h=false;
        if (list !=null && list.size()>0){

        String id = list.get(mCurrentPage).getId();
        List<SubjectsDB> subjectsDBList = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "ht_id=?", id);
        if (subjectsDBList !=null && subjectsDBList.size()>0){
            SubjectsDB subjectsDB = subjectsDBList.get(0);
            if (subjectsDB!=null){
                if (subjectsDB.getPhotoStatus()==1){
                    /*图片*/
                    List<ImagesDB> imagesDBList = subjectsDB.getImagesDBList();
                    if (imagesDBList !=null && imagesDBList.size()>0){
                        e=true;
                    }
                }else if (subjectsDB.getPhotoStatus()==-1){
                    e=true;
                }
                /*答案*/
                List<AnswersDB> answersList = subjectsDB.getAnswers();
                if (answersList !=null && answersList.size()>0){
                    String answer = answersList.get(0).getAnswer();
                    if (answer !=null){
                        f=true;
                    }
                    if (subjectsDB.getDescribeStatus()==1){
                        String remakes = answersList.get(0).getRemakes();
                        if (remakes!=null){
                            g=true;
                        }

                    }else if(subjectsDB.getDescribeStatus()==-1){
                        g=true;
                    }
                }
                /*录音*/
//                if (subjectsDB.getRecordStatus()==1){
//                    List<RecorderFilesDB> recorderFiles = subjectsDB.getRecorderFiles();
//                    if (recorderFiles !=null && recorderFiles.size()>0){
//                        String recorderPath = recorderFiles.get(0).getRecorderPath();
//                        if (recorderPath !=null){
//                            h=true;
//                        }
//                    }
//                } else if (subjectsDB.getRecordStatus()==-1){
//                    h=true;
//                }
                if (e && f && g ){
                    subjectsDB.setIsComplete(1);
                    subjectsDB.update(subjectsDB.getId());
                }
            }

            }
        }

    }
    private void setVerifyReviseAdapter(List list){
        verifyReviseAdapter =new VerifyReviseAdapter(VerifyReviseActivity.this, list, R.layout.item_recyclerview_verify_revise , null);
        verifyReviseAdapter.setWindowWidth(getWindowWidth());
        verifyReviseAdapter.setWindowHeight(getWindowHeight());
        verifyReviseAdapter.setVerifyFlag(verifyFlag);
        mRecyclerView.setAdapter(verifyReviseAdapter);
        mRecyclerView.addOnScrollListener(new Recycle());
        //  verifyReviseAdapter.setIB_PlayListenerInterFace(new MyAudioRecordPlay());
        verifyReviseAdapter.setShowDeleteViewInterface(new Mydelete());
        verifyReviseAdapter.setVerifyAdater(new MyVewiAdapterListerer());
    }

    private boolean isCanNext(){
        if (list !=null){
            int describeStatus=0;
            int photoStatus=0;
            String answer = null;
            String remakes = null;
            List<ImagesDB> imagesDBList=null;
            String id = list.get(mCurrentPage).getId();
            List<SubjectsDB> subjectsDBList = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "ht_id=?", id);
            if (subjectsDBList !=null && subjectsDBList.size()>0){
                 describeStatus = subjectsDBList.get(0).getDescribeStatus();
                 photoStatus = subjectsDBList.get(0).getPhotoStatus();
                List<AnswersDB> answers = subjectsDBList.get(0).getAnswers();
                if (answers != null && answers.size() > 0) {
                    answer = answers.get(0).getAnswer();
                    remakes = answers.get(0).getRemakes();
                }
                imagesDBList = subjectsDBList.get(0).getImagesDBList();   //图片
            }
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
        }
        return  false;
    }

    }


