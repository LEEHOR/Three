package net.coahr.three3.three.Module;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.DeleteMultipleObjectRequest;
import com.alibaba.sdk.android.oss.model.DeleteMultipleObjectResult;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.alibaba.sdk.android.oss.model.OSSObjectSummary;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import net.coahr.three3.three.Base.BaseFragment;
import net.coahr.three3.three.Base.BaseModel;
import net.coahr.three3.three.Base.BasePara;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.CommonViewHolder;
import net.coahr.three3.three.DBbase.AnswersDB;
import net.coahr.three3.three.DBbase.ImagesDB;
import net.coahr.three3.three.DBbase.ProjectsDB;
import net.coahr.three3.three.DBbase.RecorderFilesDB;
import net.coahr.three3.three.DBbase.SubjectsDB;
import net.coahr.three3.three.DBbase.UsersDB;
import net.coahr.three3.three.NetWork.RetrofitManager;
import net.coahr.three3.three.Popupwindow.AlertDialogs.CommomDialog;
import net.coahr.three3.three.R;
import net.coahr.three3.three.RecyclerViewAdapter.UploadAdapter;
import net.coahr.three3.three.Util.ALiYunOSUtils.Config;
import net.coahr.three3.three.Util.ALiYunOSUtils.OssClient;
import net.coahr.three3.three.Util.ALiYunOSUtils.UIDisplayer;
import net.coahr.three3.three.Util.JDBC.DataBaseWork;
import net.coahr.three3.three.Util.LogUtils;
import net.coahr.three3.three.Util.OtherUtils.ToastUtils;
import net.coahr.three3.three.Util.Preferences.PreferencesTool;
import net.coahr.three3.three.customView.TopTitleBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by yuwei on 2018/4/3.
 */

public class UploadFragment extends BaseFragment {
    private UploadAdapter uploadAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private TopTitleBar topTitleBar;
    private RecyclerView recyclerView;
    private CheckBox cb1;
    private String TAG = "UploadFragment";
    private Button btnpiup, btnall, btnpi;
    private TextView textCance, tNotice, uptv;
    private CommonViewHolder.onItemCommonClickListener itemCommonClickListener;
    private boolean upload_pi = true;
    private LinearLayout Lo, Lt, up;
    private List<ProjectsDB> projectsDBSList;
    private PreferencesTool mPreferencesTool;
    private List<ProjectsDB> uploadList;
    private List<Integer> uploaded_Pro;
    private ProgressDialog progressDialog;
    private String sessionId;
    private ProgressBar bar;
    private Map<Integer,Integer> mapMap;
    private SparseArray<RecorderFilesDB> recorderSparse;
    private SparseArray<ProjectsDB> sparseArray_Count;
    private SparseArray<Integer> sparseArray_success;
    private IdentityHashMap<String,Integer> identityHashMap;
    private Map<String,Integer> map_up;

    private  StringBuffer buffer;
    private int upCount=0;
    private int Count=0;
    private int runs=0;
    private boolean isCanUpload_one=true ,touchOne=true;
    //OSS的上传下载
    private OSS oss;
    //负责界面更新
    private UIDisplayer mUIDisplayer;
    private  OssClient client;
    private ExecutorService threadPool;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        mPreferencesTool = new PreferencesTool(getActivity());
        sessionId = mPreferencesTool.getSessionId("sessionId");
        // credentialProvider = new OSSAuthCredentialsProvider(Config.STSSERVER);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sparseArray_Count=new SparseArray<>();
        sparseArray_success=new SparseArray<>();
        mapMap=new HashMap<>();
        uploaded_Pro=new ArrayList<>();
        identityHashMap=new IdentityHashMap<>();
        tNotice = view.findViewById(R.id.up_notice);
        topTitleBar = view.findViewById(R.id.fragmentupload_topbar);
        recyclerView = view.findViewById(R.id.upload_recycleview);
        //得到显示的控件id
        Lo = view.findViewById(R.id.upload_topStatusBarOne);
        //得到隐藏控件id
        Lt = view.findViewById(R.id.upload_topStatusBarTwo);
        /*批量操作*/
        btnall = view.findViewById(R.id.upload_upAll);
        btnpi = view.findViewById(R.id.upload_batchOperation);
        /*全选*/
        cb1 = view.findViewById(R.id.upload_selectedAll);
        btnpiup = view.findViewById(R.id.upload_upall_pi);
        textCance = view.findViewById(R.id.upload_upcance);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        //上传
        up = view.findViewById(R.id.upfragment_upline);
        bar = view.findViewById(R.id.upfragment_bar);
        uptv = view.findViewById(R.id.upfragment_uptv);

        //初始化
         client = OssClient.getInstance(getActivity());
        oss = client.getOss();
        mUIDisplayer = new UIDisplayer(bar, uptv,up, getActivity());
        mRefreshLayout = view.findViewById(R.id.upload_refresh);
        mRefreshLayout.setOnRefreshListener(new refreshListener());
        recyclerView.setLayoutManager(manager);

        /*点击跳转到搜索页面*/
        topTitleBar.setOnTitleClickListener(new TopTitleBar.TitleOnClickListener() {
            @Override
            public void search_top() {
            }
        });
        // loadingDialog = WaitDialog.createLoadingDialog(getActivity(), "正在上传...");

        /*按钮监听事件*/
        btnall.setOnClickListener(new myButton());
        btnpi.setOnClickListener(new myButton());

        cb1.setOnClickListener(new myButton());
        btnpiup.setOnClickListener(new myButton());
        textCance.setOnClickListener(new myButton());
        setAdapterData(getProjectsDBListAll());
        /**
         * 日志工具类
         */
        LogUtils.tagPrefix="上传页面";
        LogUtils.showD=false;
        LogUtils.showE=false;

    }


    /*复选框监听*/
    class MyCbBox implements UploadAdapter.CbInteface {

        @Override
        public void CbClick(int position, Map<Integer, Boolean> map) {

            map.put(position, !map.get(position));
            cb1.setChecked(false);
            uploadAdapter.setMap(map, position);
        }
    }

    /*按钮监听*/
    public class myButton implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.upload_upAll:
                    if (touchOne){   //只能点击一次上传，防止多次点击
                    boolean netWork = getNetWork();
                    if (netWork){  //为WIFI
                        getUploadData(getProjectsDBListAll());
                    } else {
                        new CommomDialog(getActivity(), R.style.dialog, "当前为4G网络", true, false, null, new CommomDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {
                                    dialog.dismiss();
                                    getUploadData(getProjectsDBListAll());
                                } else {
                                    dialog.dismiss();
                                }
                            }
                        }).setTitle("提示").setPositiveButton("确定上传").setNegativeButton("取消").show();
                     }
                        touchOne=false;
                    }
                    break;
                case R.id.upload_batchOperation:
                    isCanUpload_one=false;
                    uploadAdapter.checked_true();
                    //upload_pi=false;
                    Lo.setVisibility(View.GONE);
                    Lt.setVisibility(View.VISIBLE);
                    uploadAdapter.notifyDataSetChanged();
                    break;
                case R.id.upload_selectedAll:
                    if (!cb1.isChecked()) {
                        uploadAdapter.initCheck(false);
                    } else {
                        uploadAdapter.initCheck(true);
                    }
                    uploadAdapter.notifyDataSetChanged();
                    break;
                case R.id.upload_upall_pi:
                    if (touchOne) {
                        boolean netWork_pi = getNetWork();
                        if (netWork_pi) {  //为WIFI
                            getUploadData(getSelectItem());
                        } else {
                            new CommomDialog(getActivity(), R.style.dialog, "当前为4G网络", true, false, null, new CommomDialog.OnCloseListener() {
                                @Override
                                public void onClick(Dialog dialog, boolean confirm) {
                                    if (confirm) {
                                        dialog.dismiss();
                                        getUploadData(getSelectItem());
                                    } else {
                                        dialog.dismiss();
                                    }

                                }
                            }).setTitle("提示").setPositiveButton("确定上传").setNegativeButton("取消").show();
                        }
                        touchOne=false;
                    }
                    break;
                case R.id.upload_upcance:
                    isCanUpload_one=true;
                    uploadAdapter.checked_false();
                    uploadAdapter.initCheck(false);
                    Lt.setVisibility(View.GONE);
                    Lo.setVisibility(View.VISIBLE);
                    uploadAdapter.notifyDataSetChanged();
                    if (threadPool !=null){
                        threadPool.shutdown();

                    }
                    break;
            }
        }
    }

    /*获取选中的item、上传操作*/
    private List<ProjectsDB> getSelectItem() {
        Map<Integer, Boolean> map = uploadAdapter.getMap();
        if (map == null || map.size() < 0) {
            Toast.makeText(getActivity(), "没有选中", Toast.LENGTH_LONG).show();
        } else {
            uploadList = new ArrayList<>();
            for (Object o : map.keySet()) {
                boolean p = map.get(o);
                if (p) {
                    ProjectsDB pro = projectsDBSList.get((int) o);
                    uploadList.add(pro);
                }
            }

        }
        return uploadList;
    }

    private List<ProjectsDB> getProjectsDBListAll() {
        String sessionId = mPreferencesTool.getSessionId("sessionId");
        int proSize = 0;
        int subSize = 0;
        int imageSize = 0;
        int recorSize = 0;
        List<UsersDB> usersDBS = DataBaseWork.DBSelectByTogether_Where(UsersDB.class, "sessionid = ?", sessionId);
        if (usersDBS != null && usersDBS.size() > 0) {
            int usersdb_id = usersDBS.get(0).getId();
            projectsDBSList = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "usersdb_id =? and puploadstatus = ?", String.valueOf(usersdb_id), String.valueOf(0));

            if (projectsDBSList != null && projectsDBSList.size() > 0) {
                for (int i = 0; i < projectsDBSList.size(); i++) {
                    proSize++;
                    List<SubjectsDB> subjectsDBList = projectsDBSList.get(i).getSubjectsDBList();
                    if (subjectsDBList != null && subjectsDBList.size() > 0) {

                        for (int j = 0; j < subjectsDBList.size(); j++) {
                            if (subjectsDBList.get(j).getCensor() == 0 && subjectsDBList.get(j).getIsComplete() == 1) {
                                subSize++;
                                List<ImagesDB> imagesDBList = subjectsDBList.get(j).getImagesDBList();
                                if (imagesDBList != null && imagesDBList.size() > 0) {
                                    imageSize += imagesDBList.size();
                                }
                                List<RecorderFilesDB> recorderFiles = subjectsDBList.get(j).getRecorderFiles();
                                if (recorderFiles != null && recorderFiles.size() > 0) {
                                    recorSize++;
                                }
                            }

                        }
                    }

                }
            }

        }
        tNotice.setText(proSize + "个项目,共" + subSize + "条访问数据，" + (imageSize + recorSize) + "个附件");
        return projectsDBSList;

    }


    /*滑动适配器监听 单题上传*/
    public class myAdapterListener implements CommonViewHolder.onItemCommonClickListener {
        @Override
        public void onItemClickListener(final int position) {
            if (touchOne) {
                if (isCanUpload_one) {
                    boolean netWork = getNetWork();
                    if (netWork) {
                        getUploadDateOne(position);
                    } else {
                        new CommomDialog(getActivity(), R.style.dialog, "当前为4G网络", true, false, null, new CommomDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {
                                    dialog.dismiss();
                                    getUploadDateOne(position);
                                } else {
                                    dialog.dismiss();
                                }

                            }
                        }).setTitle("提示").setPositiveButton("确定上传").setNegativeButton("取消").show();
                    }
                }
                touchOne=false;
            }
        }

        @Override
        public void onItemLongClickListener(int position) {
            Toast.makeText(getActivity(), "选中的是(长按):" + position, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 单题上传
     * @param position
     */
    private void getUploadDateOne(int position){
        if (projectsDBSList.get(position).getpUploadStatus() == 0 && projectsDBSList.get(position).getIsDeletes()==0) {
            if (projectsDBSList.get(position).getSubjectsDBList() != null && projectsDBSList.get(position).getSubjectsDBList().size() > 0) {
                if (recorderSparse==null){
                    threadPool = Executors.newSingleThreadExecutor();
                }
                recorderSparse=new SparseArray<>();
                upCount=0;
                Count=0;
                uploaded_Pro.clear();
                sparseArray_Count.clear();
                sparseArray_success.clear();
                identityHashMap.clear();
                mapMap.clear();
                final List<SubjectsDB> subjectsDBList = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "projectsdb_id=? ", String.valueOf(projectsDBSList.get(position).getId()));
                if (subjectsDBList != null && subjectsDBList.size() > 0) {
                    sparseArray_Count.put(projectsDBSList.get(position).getId(),projectsDBSList.get(position));
                    for (int i = 0; i < subjectsDBList.size(); i++) {
                        if (subjectsDBList.get(i).getsUploadStatus()==0 && subjectsDBList.get(i).getCensor()==0 && subjectsDBList.get(i).getIsComplete()==1){
                            mUIDisplayer.setVisible();
                            checkStage(projectsDBSList.get(position), subjectsDBList.get(i), projectsDBSList.get(position).getStage(),recorderSparse);
                        } else {
                            ToastUtils.showLong(getActivity(), "没有题目上传");
                        }
                    }
                } else {
                    ToastUtils.showLong(getActivity(), "没有题目上传");
                }

            } else {
                ToastUtils.showLong(getActivity(), "没有题目上传");
            }

        } else {

            ToastUtils.showLong(getActivity(), "没有项目要上传");
        }
    }
    /**
     * 全部上传或批量上传
     *
     * @param upLs
     */
    private void getUploadData(List<ProjectsDB> upLs) {
        final List<ProjectsDB> UpList = upLs;

        if (UpList != null && !UpList.isEmpty() && UpList.size() > 0) {
            if (recorderSparse==null){
                threadPool = Executors.newSingleThreadExecutor();
            }
            uploaded_Pro.clear();
            upCount=0;
            Count=0;
            sparseArray_Count.clear();
            sparseArray_success.clear();
            identityHashMap.clear();
            mapMap.clear();
            for (int i = 0; i < UpList.size(); i++) {
                MutiUpload(UpList.get(i));
            }

        }

    }

    public void MutiUpload(final ProjectsDB projectsDB) {
        //判断当前项目是否上传过了
        if (projectsDB.getpUploadStatus() == 0 && projectsDB.getIsDeletes()==0) {
            recorderSparse=new SparseArray<>();
            final List<SubjectsDB> subjectsDBList = projectsDB.getSubjectsDBList();
            if (subjectsDBList != null && subjectsDBList.size() > 0 && !subjectsDBList.isEmpty()) {
                sparseArray_Count.put(projectsDB.getId(),projectsDB);
                mUIDisplayer.setVisible();
                for (int i = 0; i < subjectsDBList.size(); i++) {
                    //如果题目下可以上传
                    if (subjectsDBList.get(i).getIsComplete() == 1 ) {//   && subjectsDBList.get(i).getCensor()==0 && subjectsDBList.get(i).getsUploadStatus()==0
                        checkStage(projectsDB, subjectsDBList.get(i), projectsDB.getStage(),recorderSparse);
                    } else  {
                        ToastUtils.showShort(getActivity(),"没有题目上传");
                    }
                }
            }
        } else {
            ToastUtils.showShort(getActivity(),"没有题目上传");
            mUIDisplayer.display_uploaded();
        }

    }


    class refreshListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            if (mPreferencesTool.getSessionId("sessionId").equals(sessionId)) {
                setAdapterData(getProjectsDBListAll());
            } else {
                uploadAdapter.Update(getProjectsDBListAll());
            }
            mRefreshLayout.setRefreshing(false);

        }
    }

    private void setCensorStatus( int type,ProjectsDB projectsDB) {
        if (projectsDB != null) {
            List<SubjectsDB> subjectsDBList = projectsDB.getSubjectsDBList();
            if (subjectsDBList != null && subjectsDBList.size() > 0) {
                for (int i = 0; i < subjectsDBList.size(); i++) {
                    if (subjectsDBList.get(i).getsUploadStatus() == 1) {
                        subjectsDBList.get(i).setCensor(2);
                        subjectsDBList.get(i).update(subjectsDBList.get(i).getId());
                        ToastUtils.showShort(getActivity(), "更改censor" + subjectsDBList.get(i).getCensor());
                    }
                }

            }
        }
        if (type == 1) {

        }
        if (type == 2) {

        }

        uploadAdapter.Update(getProjectsDBListAll());
    }

    private void setAdapterData(List list) {
        uploadAdapter = new UploadAdapter(getActivity(), list, R.layout.item_root_upload_recycleview, new myAdapterListener());
        uploadAdapter.setCbInteface(new MyCbBox());
        recyclerView.setAdapter(uploadAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        setAdapterData(getProjectsDBListAll());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private boolean getNetWork() {
        // 判断是否处于wifi环境

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager

                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (networkInfo.isConnected()) {

            return true;

        }
        return false;
    }



    /**
     * 回调业务服务器
     *
     *
     * @return
     */
    private Map<String, Integer> get_up(IdentityHashMap<String,Integer> identityHashMap) {
        map_up=new HashMap<>();
        for (IdentityHashMap.Entry<String,Integer> entry: identityHashMap.entrySet()) {
            if (map_up.containsKey(entry.getKey())){
                map_up.put(entry.getKey(), map_up.get(entry.getKey()).intValue() + 1);
            } else {
                map_up.put(entry.getKey(), new Integer(1));
            }
        }
        return map_up;
    }

    /**
     * 检查是否第一次上传
     * @param projectsDB
     * @param subjectsDB
     * @param stage
     */
    private void checkStage(ProjectsDB projectsDB,SubjectsDB subjectsDB,String stage,SparseArray<RecorderFilesDB> recorderSparse){

     if (subjectsDB !=null){
         List<RecorderFilesDB> recorderFiles = subjectsDB.getRecorderFiles();
         if (recorderFiles !=null && recorderFiles.size()>0){
             recorderSparse.put(subjectsDB.getNumber(),recorderFiles.get(0));
         } else {
             recorderSparse.put(subjectsDB.getNumber(),null);
         }
         if (stage.equals("1")){
             LogUtils.d("首次");
             mUIDisplayer.display_uploading();
             //首次上传
             upLoadPart(projectsDB,subjectsDB,stage,recorderSparse) ;
         } else {
             //审核之后上传/查询并删除oss端存储的数据
             LogUtils.d("非首次");
             mUIDisplayer.display_uploading();
             getOSSObjects(projectsDB.getPid()+"/pictures/"+subjectsDB.getNumber(),projectsDB,subjectsDB,stage,recorderSparse);
         }
     }

    }

    private void upLoadPart(ProjectsDB projectsDB, SubjectsDB finalSubjectsDB, String stage,SparseArray<RecorderFilesDB> recorderSparse) {
        if (finalSubjectsDB != null) {
            int imageSize = 0;
            int audioSize = 0;
            int answersSize = 0;

            //图片
            List<ImagesDB> imagesDBList = finalSubjectsDB.getImagesDBList();
            if (imagesDBList != null) {
                imageSize = imagesDBList.size();
            }
            //录音
            RecorderFilesDB recorderFilesDB = recorderSparse.get(finalSubjectsDB.getNumber());
          //  List<RecorderFilesDB> recorderFiles = finalSubjectsDB.getRecorderFiles();
            if (recorderFilesDB!=null){
                if(recorderFilesDB.getRecorderName() !=null && stage.equals("1") ){
                    audioSize=1;
                }else {
                    audioSize=0;
                }
           }
            //答案
            List<AnswersDB> answers = finalSubjectsDB.getAnswers();
            if (answers != null && answers.size() > 0) {
                answersSize = 1;
            }
            if (imageSize==0 && audioSize==0){
                answersSize=1;
            } else {
                answersSize=0;
            }
            sparseArray_success.put(finalSubjectsDB.getId(),(imageSize+audioSize+answersSize));
            LogUtils.d(finalSubjectsDB.getNumber()+"/"+(imageSize+audioSize+answersSize));
            //图片
            if (imagesDBList != null && imagesDBList.size() > 0) {
                if (answers !=null){
                    for (int i = 0; i < imagesDBList.size(); i++) {
                        LogUtils.d("题号"+finalSubjectsDB.getNumber()+"/上传图片名"+finalSubjectsDB.getNumber() + "_" + (i + 1)+"."+"jpg");
                        asyncPutUpload( projectsDB.getPid() + "/pictures/" +finalSubjectsDB.getNumber()+ "/" + finalSubjectsDB.getNumber() + "_" + (i + 1)+"."+"jpg", imagesDBList.get(i).getZibImagePath(), projectsDB, finalSubjectsDB,  imageSize, audioSize, 0,imagesDBList.get(i).getImageName());
                     }
                 }

            }
                    //录音不为空
                    if (recorderFilesDB !=null) {

                        if (answers !=null){
                            if (stage.equals("1")){  //首次上传
                                if (finalSubjectsDB.getNumber()==1){  //如果当前仅为一个录音对象。无法比较
                                    LogUtils.d("题号"+finalSubjectsDB.getNumber()+"录音首次第一个"+recorderFilesDB.getRecorderName());
                                    asyncPutUpload( projectsDB.getPid() + "/audios/" +recorderFilesDB.getRecorderName(),recorderFilesDB.getRecorderPath(), projectsDB, finalSubjectsDB, imageSize,audioSize, 0,recorderFilesDB.getRecorderName());
                                } else {  //如果recorderSparse数组数量大于一就要比较是否是同一个录音文件
                                    RecorderFilesDB recorderFilesDB_last = recorderSparse.get((finalSubjectsDB.getNumber() - 1));
                                    if (recorderFilesDB_last !=null){
                                        if (recorderFilesDB_last.getRecorderName().equals(recorderFilesDB.getRecorderName())){  //如果当前录音文件和上一题录音文件相同则不上传（上传空文件）
                                            LogUtils.d("题号"+finalSubjectsDB.getNumber()+"录音首次重复"+recorderFilesDB.getRecorderName());
                                            asyncPutUpload( projectsDB.getPid() + "/test", null, projectsDB, finalSubjectsDB, imageSize,audioSize, 0,recorderFilesDB.getRecorderName());
                                        } else {    //如果当前录音文件和上一题录音文件不同则上传
                                            LogUtils.d("题号"+finalSubjectsDB.getNumber()+"录音首次不重复"+recorderFilesDB.getRecorderName());
                                               asyncPutUpload( projectsDB.getPid() + "/audios/" +recorderFilesDB.getRecorderName(), recorderFilesDB.getRecorderPath(), projectsDB, finalSubjectsDB,  imageSize,audioSize, 0,recorderFilesDB.getRecorderName());
                                        }
                                    }
                                }
                            } else {           //审核时无需修改录音，不需要上传文件(非首次上传)
                                   // asyncPutUpload(projectsDB.getPid() + "/test"  , answer, explain, null, recorderFilesDB.getRecorderName(), projectsDB, finalSubjectsDB,  imageSize, audioSize, 0, stage, number);

                            }
                        }

            }

            if (recorderFilesDB ==null  && (imagesDBList == null && imagesDBList.size() < 0)) {
                if (answers !=null){
                    asyncPutUpload(Config.bucket + "/test", null, projectsDB, finalSubjectsDB, 0, 0, answersSize ,null);
                }
            }

        }

    }

    //异步上传

    public void asyncPutUpload(String object, String localFile, final ProjectsDB projectsDB, final SubjectsDB subjectsDB, int imageSize, int recorderSize, int remarksSize,String fileName) {
        long upload_start = System.currentTimeMillis();

        if (object.equals("")) {
            return;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                String threadName = Thread.currentThread().getName();
                // 构造上传回调请求
                PutObjectRequest put = new PutObjectRequest(Config.bucket, object, localFile);

                ObjectMetadata objectMetadata = new ObjectMetadata();

                objectMetadata.setContentEncoding("UTF-8");
                put.setMetadata(objectMetadata);
                put.setCRC64(OSSRequest.CRC64Config.YES);
     //   Log.e("callback", "asyncPutUpload: "+(Count+=1) );

        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                int progress = (int) (100 * currentSize / totalSize);
                mUIDisplayer.updateProgress(progress);

                if (up.getVisibility()==View.VISIBLE){
                    mUIDisplayer.displayInfo(projectsDB.getPname()+"第"+subjectsDB.getNumber()+"题："+fileName+"上传进度: " + String.valueOf(progress) + "%");
                }
            }
        });
                OSSAsyncTask putTask = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                    @Override
                    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                        identityHashMap.put(new String(subjectsDB.getHt_id()),subjectsDB.getId());
                        Map<String, Integer> up = get_up(identityHashMap);
                        Integer integer = up.get(subjectsDB.getHt_id());
                        LogUtils.d("onSuccess"+"题号"+subjectsDB.getNumber()+"/上传数"+integer+"/当前题目总上传数"+sparseArray_success.get(subjectsDB.getId()));
                          if (sparseArray_success.get(subjectsDB.getId()) == integer) {
                                callback(projectsDB,subjectsDB);
                          }


                    }

                    @Override
                    public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {

                            String rawMessage = serviceException.getRawMessage();
                            LogUtils.e("onFailure"+rawMessage);


                    }

                });
                putTask.waitUntilFinished();

            }
        });
            }


    /**
     *
     * @param Prefix
     *              查询前缀
     * @param projectsDB
     *              项目
     * @param subjectsDB
     *              题目
     * @param stage
     *              是否首次上传标志
     */
    private void  getOSSObjects(String Prefix,ProjectsDB projectsDB,SubjectsDB subjectsDB,String stage,SparseArray<RecorderFilesDB> recorderSparse){
        ListObjectsRequest listObjects = new ListObjectsRequest(Config.bucket);
        listObjects.setPrefix(Prefix);
        // 设置成功、失败回调，发送异步罗列请求
        OSSAsyncTask getObjectTask = oss.asyncListObjects(listObjects, new OSSCompletedCallback<ListObjectsRequest, ListObjectsResult>() {
            @Override
            public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {
                List<String> deleteList =new ArrayList<>();
                List<OSSObjectSummary> objectSummaries = result.getObjectSummaries();
                if (objectSummaries !=null && objectSummaries.size()>0){  //可以查询出
                    for (int i = 0; i < objectSummaries.size(); i++) {
                        deleteList.add(objectSummaries.get(i).getKey());
                        LogUtils.d("onSuccess:查询"+objectSummaries.get(i).getKey());
                    }
                    deleteMultipleObject(deleteList,true,projectsDB,subjectsDB,stage,recorderSparse,1);
                } else {                  //查询结果为空 直接上传
                    upLoadPart(projectsDB,subjectsDB,stage,recorderSparse) ;
                }
            }

            @Override
            public void onFailure(ListObjectsRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
               // mUIDisplayer.display_uploaded();

                if (serviceException != null && clientExcepion != null) {
                    // 服务异常
                    Log.e(TAG, serviceException.getErrorCode());
                    Log.e(TAG, serviceException.getRequestId());
                    Log.e(TAG, serviceException.getHostId());
                    Log.e(TAG, serviceException.getRawMessage());
                    LogUtils.e("onFailure:查询"+"serviceException"+ serviceException.getErrorCode()+"\n"+serviceException.getRequestId()+"\n"+serviceException.getHostId()+"\n"+ serviceException.getRawMessage()+"\n"+"clientExcepion"+clientExcepion.toString());
                }

            }
        });
        getObjectTask.waitUntilFinished();

    }

    /**
     *
     * @param objectKeys
     *          key
     * @param isQuiet
     *          是否精简模式
     * @param projectsDB
     *          项目
     * @param subjectsDB
     *          题目
     * @param stage
     *          是否为首次上传标志
     * @param recorderSparse
     *           录音名Map
     * @param type
     *            是否为回调服务器失败标志
     */
        private void deleteMultipleObject(List<String> objectKeys,boolean isQuiet,ProjectsDB projectsDB,SubjectsDB subjectsDB,String stage,SparseArray<RecorderFilesDB> recorderSparse,int type){
            DeleteMultipleObjectRequest deleteMultipleObjectRequest = new DeleteMultipleObjectRequest(Config.bucket, objectKeys, isQuiet);
            OSSAsyncTask deleteTask = oss.asyncDeleteMultipleObject(deleteMultipleObjectRequest, new OSSCompletedCallback<DeleteMultipleObjectRequest, DeleteMultipleObjectResult>() {
               @Override
               public void onSuccess(DeleteMultipleObjectRequest request, DeleteMultipleObjectResult result) {
                   List<String> deletedObjects = result.getDeletedObjects();
                   if (deletedObjects !=null && deletedObjects.size()>0){
                       for (int i = 0; i < deletedObjects.size(); i++) {

                           LogUtils.d("onSuccess:删除:题号"+subjectsDB.getNumber()+"/objectKey"+deletedObjects.get(i));
                       }
                   }
                   if (type==1){
                       objectKeys.clear();
                       upLoadPart(projectsDB,subjectsDB,stage,recorderSparse) ;
                   }

               }

               @Override
               public void onFailure(DeleteMultipleObjectRequest request, ClientException clientException, ServiceException serviceException) {
                   Boolean quiet = request.getQuiet();
                   String bucketName = request.getBucketName();
                   if (clientException !=null && serviceException !=null){
                       LogUtils.e("onFailure:删除：错误信息"+clientException.getMessage()+"要删除的"+request.getBucketName());
                   }

                   objectKeys.clear();

               }
           });
            deleteTask.waitUntilFinished();

        }
    //得到后缀
    private String getNamePostfix(String fileName){
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        return suffix;
    }



    /**
     * 回调
     */
    private void callback(ProjectsDB projectsDB,SubjectsDB subjectsDB){
        List<RecorderFilesDB> recorderFiles = subjectsDB.getRecorderFiles();
        int recorderSize=0;
        int imageSize=0;
        String answer=null;
        String remakes=null;
        String recorderName=null;
        if (recorderFiles !=null && recorderFiles.size()>0){
            if (projectsDB.getStage().equals("1")){
                recorderSize=1;
                recorderName= recorderFiles.get(0).getRecorderName();
            } else {
                recorderSize=0;
                recorderName=null;
            }

        }
        List<ImagesDB> imagesDBList = subjectsDB.getImagesDBList();
        if (imagesDBList !=null){
            imageSize= imagesDBList.size();
        }

        List<AnswersDB> answers = subjectsDB.getAnswers();
        if (answers !=null && answers.size()>0){
            answer=  answers.get(0).getAnswer();
            remakes= answers.get(0).getRemakes();
        }
        BasePara para = new BasePara();
        Map<String, Object> map = new HashMap<>();
        map.put("projectId", projectsDB.getPid());
        map.put("answerId", subjectsDB.getHt_id());
        if (answer != null) {

            map.put("answer", answer);
        } else {
            map.put("answer", null);
        }

        if (remakes != null) {
            map.put("description", remakes);
        } else {
            map.put("description", null);
        }
        if (imageSize > 0) {
            buffer = new StringBuffer();
            for (int i = 0; i < imageSize; i++) {
                if (imageSize == 1) {
                    buffer.append(subjectsDB.getNumber() + "_" + (i + 1) + "." + "jpg");
                } else {
                    if (i == (imageSize - 1)) {
                        buffer.append(subjectsDB.getNumber() + "_" + (i + 1) + "." + "jpg");
                    } else {
                        buffer.append(subjectsDB.getNumber() + "_" + (i + 1) + "." + "jpg" + ";");
                    }
                }
            }
        }
        String ObjectKey=buffer.toString();
        map.put("pictureCount", imageSize);
        map.put("audioCount", recorderSize);
        map.put("number", subjectsDB.getNumber());
        map.put("stage", projectsDB.getStage());
        map.put("audio", recorderName);
        map.put("picture", ObjectKey);
        para.setData(map);

        for (Object o : map.keySet()) {
            LogUtils.d("callback"+o+"/"+map.get(o));
        }

            Subscription subscription = RetrofitManager.getInstance()
                .createService(para)
                .uploadDate(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                       ToastUtils.showLong(getActivity(),"上传失败"+projectsDB.getPname()+"\n"+subjectsDB.getNumber());
                       LogUtils.e("onError:上传失败",e);
                        mapMap.put(subjectsDB.getId(),subjectsDB.getNumber());
                        if (mapMap.size()==sparseArray_success.size()){
                           setProjectStatus();
                       }

                    }

                    @Override
                    public void onNext(BaseModel baseModel) {
                        LogUtils.d("onNext:上传成功"+projectsDB.getPname()+"/题号："+subjectsDB.getNumber());
                            subjectsDB.setCensor(2);
                            subjectsDB.setsUploadStatus(1);
                            subjectsDB.setDh("0");
                            subjectsDB.update(subjectsDB.getId());
                            mapMap.put(subjectsDB.getId(),subjectsDB.getNumber());
                            if (mapMap.size()==sparseArray_success.size()){
                                setProjectStatus();
                            }
                    }
                });
        addSubscription(subscription);
    }

    private void setProjectStatus() {
        ProjectsDB projectsDB;
        StringBuffer buffer=null;
        if (sparseArray_Count != null && sparseArray_Count.size() > 0) {
            for (int i = 0; i < sparseArray_Count.size(); i++) {
                 projectsDB = sparseArray_Count.valueAt(i);
                List<SubjectsDB> subjectsDBList1 = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "projectsdb_id=? and suploadstatus=?", String.valueOf(projectsDB.getId()), String.valueOf(1));
                if (projectsDB.getSubjectsDBList().size() == subjectsDBList1.size()) {
                    projectsDB.setIsComplete(1);
                    projectsDB.setpUploadStatus(1);
                    int update_p = projectsDB.update(projectsDB.getId());
                    if (update_p > 0) {
                        LogUtils.d("setProjectStatus"+projectsDB.getPname());
                        uploaded_Pro.add(projectsDB.getId());

                    }
                     buffer = new StringBuffer();
                    buffer.append(projectsDB.getPname());
                    for (int j = 0; j < subjectsDBList1.size(); j++) {
                        buffer.append("\n第" + subjectsDBList1.get(j).getNumber() + "题");
                    }

                } else {
                     uploaded_Pro.add(projectsDB.getId());
                   /*  List<SubjectsDB> subjectsDBList_fail = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "projectsdb_id=? and suploadstatus=?", String.valueOf(projectsDB.getId()), String.valueOf(0));
                     if (subjectsDBList_fail != null && subjectsDBList_fail.size() > 0) {
                         LogUtils.e("setProjectStatus"+projectsDB.getPname());
                        StringBuffer buffer = new StringBuffer();
                        buffer.append(projectsDB.getPname());
                        for (int j = 0; j < subjectsDBList_fail.size(); j++) {
                            buffer.append("\n第" + subjectsDBList_fail.get(j).getNumber() + "题");
                        }
                        mUIDisplayer.uploadFail(buffer.toString());
                    }*/
                }
            }
            if (uploaded_Pro.size()==sparseArray_Count.size()){
                mUIDisplayer.setGone();
                mUIDisplayer.uploadComplete(buffer.toString());
                mUIDisplayer.display_uploaded();
                touchOne=true;
                setAdapterData(getProjectsDBListAll());
            }
        }

    }

}
