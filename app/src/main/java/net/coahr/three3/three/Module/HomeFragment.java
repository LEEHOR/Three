package net.coahr.three3.three.Module;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.andsync.xpermission.XPermissionUtils;
import com.baidu.location.BDLocation;

import net.coahr.three3.three.Base.BaseCheckPermissionFragment;
import net.coahr.three3.three.Base.BaseModel;
import net.coahr.three3.three.Base.BasePara;
import net.coahr.three3.three.Base.LocationApplication;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.CommonViewHolder;
import net.coahr.three3.three.DBbase.ProjectsDB;
import net.coahr.three3.three.DBbase.UsersDB;
import net.coahr.three3.three.Model.HomeDataListModle;
import net.coahr.three3.three.NetWork.RetrofitManager;
import net.coahr.three3.three.Popupwindow.AlertDialogs.CommomDialog;
import net.coahr.three3.three.Project.ProjectInfoActivity;
import net.coahr.three3.three.R;
import net.coahr.three3.three.RecyclerViewAdapter.HomeAdapter;
import net.coahr.three3.three.SearchActivity;
import net.coahr.three3.three.Util.BaiduSDk.BDLocationUtils;
import net.coahr.three3.three.Util.BaiduSDk.BaiDuSdkListener;
import net.coahr.three3.three.Util.JDBC.DataBaseWork;
import net.coahr.three3.three.Util.OtherUtils.OrderSortByGroup;
import net.coahr.three3.three.Util.OtherUtils.ToastUtils;
import net.coahr.three3.three.Util.Preferences.PreferencesTool;
import net.coahr.three3.three.customView.TopTitleBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by yuwei on 2018/4/3.
 */


public class HomeFragment extends BaseCheckPermissionFragment {
    public static HomeFragment homeFragment;
    public String Info1 = "04";
    public String Info2;
    private TopTitleBar topTitleBar;
    public  CompositeSubscription mCompositeSubscription; //解除订阅, RX
    private BaiDuSdkListener baiDuSdkListener;
    private RadioButton Rb1,Rb2,Rb3,Rb4;
    private LinearLayout homeSelectBar;
    private  List<ProjectsDB> projects;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static Bundle bundle;
    public Double longitude,latitude;
    private PreferencesTool mPreferencesTool;
    private Map<String,String> homeLocation;
    private HomeDataListModle data;
    private HomeAdapter homeAdapter;
    private BDLocationUtils bdLocationUtils;
    private   LocationApplication application;
    private int user_id;
    private  boolean rb1,rb2,rb3,desc,First=true,FirstFai=true,FirstHomeAdapter=false;
    private   boolean networkAvailable,isPerssoin;
    private String TAG="HomeFragment";
    private  String sessionId;
    private TextView home_StartTime,home_EndTime,home_distance;
    private View home_line_l,home_line_lc,home_line_rc,home_line_r;
    private LinearLayout LineaTop;
    private int mIndex=0,topIndex=0;
    private int ids=0,topId=0;
    private int[] iconNormal = {R.drawable.sort};
    private int[] iconSelected ={R.drawable.sort_c};
    public static HomeFragment getInstance(boolean b) {
            homeFragment=new HomeFragment();
        bundle=new Bundle();
        bundle.putBoolean("HomeFragment",b);
        homeFragment.setArguments(bundle);
        return homeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferencesTool = new PreferencesTool(getActivity());
        isPerssoin = mPreferencesTool.getHomeFragmentPermission("HomeFragment");
        sessionId = mPreferencesTool.getSessionId("sessionId");
        //getDingwei();
        List<UsersDB> usersDBS = DataBaseWork.DBSelectByTogether_Where(UsersDB.class, "sessionid=?", sessionId);
        if (usersDBS !=null && usersDBS.size()>0){
            user_id= usersDBS.get(0).getId();

        }
        if (getArguments()!=null){

            boolean b = getArguments().getBoolean("HomeFragment");
            if (b){

                if (mPreferencesTool.getHomeFragmentPermission("HomeFragment")){
                  getDingwei();
                }else {
                    getPermission();
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        networkAvailable = isNetworkAvailable(getActivity());
        if(getActivity().getWindow().getAttributes().softInputMode== WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        {
            //隐藏软键盘
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        if (mPreferencesTool.getHomeFragmentPermission("HomeFragment")){
            getDingwei();
        }else {
            getPermission();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //-----------获取控件--------------/

        /*搜索栏*/
        topTitleBar = view.findViewById(R.id.fragmenthome_topbar);
        /*单选框上*/
        Rb1=view.findViewById(R.id.fragmenthome_topRb1);  //新项目
        Rb2=view.findViewById(R.id.fragmenthome_topRb2);  //已完成
        Rb3=view.findViewById(R.id.fragmenthome_topRb3);  //未完成
        Rb4=view.findViewById(R.id.fragmenthome_topRb4);   //全部
        Rb4.setChecked(true);
        /*头部白线*/
        home_line_l= view.findViewById(R.id.home_topline_l);
        home_line_lc=  view.findViewById(R.id.home_topline_lc);
        home_line_rc= view.findViewById(R.id.home_topline_rc);
        home_line_r=view.findViewById(R.id.home_topline_r);
        LineaTop= view.findViewById(R.id.fragmenthome_top_line);
        setItemClickTopLineBg(LineaTop);

        /*单选框下*/
        homeSelectBar = view.findViewById(R.id.fragmenthome_bottom_Radio);
        home_StartTime= view.findViewById(R.id.home_sT);
        home_EndTime= view.findViewById(R.id.home_eT);
        home_distance= view.findViewById(R.id.home_dis);
        setItemClickEven(homeSelectBar);
        /*设置图片大小*/

       /*获取 SwipeRefreshLayout 控件*/
        swipeRefreshLayout = view.findViewById(R.id.fragmenthome_pullDownScrollView);
        // 设置下拉进度的背景颜色，默认就是白色的
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);

        /*获取recycleView控件*/
        recyclerView= view.findViewById(R.id.fragmenthome_recycle);
        //-----------以下是监听事件--------------/
        /*搜索栏监听事件*/
        topTitleBar.setOnTitleClickListener(new TopTitleBar.TitleOnClickListener() {
            @Override
            public void search_top() {
                Intent intent=new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
        /* 下拉刷新监听 */

        swipeRefreshLayout.setOnRefreshListener(new MySwipelayout());
        /*单选按钮上监听事件*/
        Rb1.setOnClickListener(new MyRD1());
        Rb2.setOnClickListener(new MyRD1());
        Rb3.setOnClickListener(new MyRD1());
        Rb4.setOnClickListener(new MyRD1());

        /*单选按钮下监听事件*/
        home_StartTime.setOnClickListener(new MyRD1());
        home_EndTime.setOnClickListener(new MyRD1());
        home_distance.setOnClickListener(new MyRD1());
    }
   public class MyRD1 implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Log.e("networkAvailable", "networkAvailable" +networkAvailable);
            switch (view.getId()) {
                case R.id.fragmenthome_topRb1:
                    Info1 = "01";
                    topId=0;
                    projects = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "usersdb_id=? and completestatus=? and isdeletes =? and downloadtime !=? ",String.valueOf(user_id), String.valueOf(1),String.valueOf(0),String.valueOf(0));

                    break;
                case R.id.fragmenthome_topRb2:
                    Info1 = "02";
                    topId=1;
                    projects = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "usersdb_id=? and completestatus=? and isdeletes =?",String.valueOf(user_id), String.valueOf(3),String.valueOf(0));

                    break;
                case R.id.fragmenthome_topRb3:
                    Info1 = "03";
                    topId=2;
                    projects = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "usersdb_id=? and completestatus=? and isdeletes =?",String.valueOf(user_id), String.valueOf(2),String.valueOf(0));

                    break;
                case R.id.fragmenthome_topRb4:
                    Info1 = "04";
                    topId=3;
                    projects = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, " usersdb_id=? and isdeletes =?",String.valueOf(user_id),String.valueOf(0));

                    break;
                case R.id.home_sT:
                    Info2 = "1";
                    ids=0;
                    rb1 = mPreferencesTool.getTob_rb1("rb1");
                    if (rb1) {
                        mPreferencesTool.setTop_rb1("rb1", false);
                        desc = false;
                    } else {
                        mPreferencesTool.setTop_rb1("rb1", true);
                        desc = true;
                    }
                    break;
                case R.id.home_eT:
                    Info2 = "2";
                    ids=1;
                    rb2 = mPreferencesTool.getTob_rb2("rb2");
                    if (rb2) {
                        mPreferencesTool.setTop_rb2("rb2", false);
                        desc = false;
                    } else {
                        mPreferencesTool.setTop_rb2("rb2", true);
                        desc = true;
                    }
                    break;
                case R.id.home_dis:
                    Info2 = "3";
                    ids=2;
                    rb3 = mPreferencesTool.getTob_rb3("rb3");
                    if (rb3) {
                        mPreferencesTool.setTop_rb3("rb3", false);
                        desc = false;
                    } else {
                        mPreferencesTool.setTop_rb3("rb3", true);
                        desc = true;
                    }
                    break;

            }
            chanceItem(ids,homeSelectBar);
            chanceItemTopLine(topId,LineaTop);
            if (Info2 != null) {
                if (Info2.equals("3")) {
                     OrderSortByGroup.ListSortByDestance(projects, desc);
                }
                if (Info2.equals("2")) {
                    OrderSortByGroup.ListSortByEndTime(projects, desc);
                }
                if (Info2.equals("1")) {
                    OrderSortByGroup.ListSortByStartTime(projects, desc);
                }
            }

                if (FirstHomeAdapter){
                    if (homeAdapter.getAdapterMap()){
                        homeAdapter.Update(projects);
                    }else {
                    }
                }else {
                }


        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }


    /*滑动适配器监听*/
    public class myAdapterListener implements CommonViewHolder.onItemCommonClickListener {
        @Override
        public void onItemClickListener(int position) {
            String id = projects.get(position).getPid();
                if (projects.get(position).getDownloadTime()==-1){
                    Toast.makeText(getActivity(),"选中的是:"+position+"/状态："+projects.get(position).getCompleteStatus(),Toast.LENGTH_LONG).show();

                    download(id,position);
                } else {
                     Intent intent=new Intent(getActivity(), ProjectInfoActivity.class);
                     Bundle bundle=new Bundle();
                      bundle.putSerializable("homeFragment",  projects.get(position));
                     intent.putExtras(bundle);
                     startActivity(intent);
                }
        }

        @Override
        public void onItemLongClickListener(final int position) {
            String id = projects.get(position).getPid();
            List<ProjectsDB> projectsDBSs = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "pid=?", id);
            String mesage=null;
          //  Log.e("rr","项目Id"+id+"/position"+position+"/状态"+projects.get(position).getCompleteStatus());

                if(projects.get(position).getDownloadTime()==-1 && projects.get(position).getCompleteStatus()==1){
                        download(id,position);

                }else if(projects.get(position).getCompleteStatus()==1&&projects.get(position).getDownloadTime()!=-1) {

                    new CommomDialog(getActivity(), R.style.dialog,projects.get(position).getPname(), false,true,"新项目！", new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm){
                                if (projectsDBSs !=null && projectsDBSs.size()>0) {
                                    projectsDBSs.get(0).setIsDeletes(1);
                                    projectsDBSs.get(0).update(projectsDBSs.get(0).getId());
                                }
                                Toast.makeText(getActivity(),"删除完成",Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }
                    }).setTitle("确定删除").show();
                } else if (projects.get(position).getCompleteStatus()==2){

                    if (projectsDBSs !=null && projectsDBSs.size()>0){
                        int i = projectsDBSs.get(0).getpUploadStatus();
                        if (i==0){
                            mesage="此项目中有未提交数据";
                        }
                    }
                    new CommomDialog(getActivity(), R.style.dialog,projects.get(position).getPname(), false,true,mesage, new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm){
                                if (projectsDBSs !=null && projectsDBSs.size()>0) {
                                    projectsDBSs.get(0).setIsDeletes(1);
                                    projectsDBSs.get(0).update(projectsDBSs.get(0).getId());
                                }
                                Toast.makeText(getActivity(),"删除完成",Toast.LENGTH_LONG).show();

                                dialog.dismiss();
                            }
                        }
                    }).setTitle("确定删除").show();
                } else{
                    new CommomDialog(getActivity(), R.style.dialog, projects.get(position).getPname(),true,false,null, new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm){
                                Log.e(TAG, "onClick: "+projects.get(position).getId() );
                                  if (projectsDBSs !=null && projectsDBSs.size()>0){
                                      projectsDBSs.get(0).setIsDeletes(1);
                                     projectsDBSs.get(0).update(projectsDBSs.get(0).getId());
                                  }
                                Toast.makeText(getActivity(),"删除完成",Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }
                    }).setTitle("确定删除").show();

                }
            projects = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "usersdb_id=? and  isdeletes =?  ",String.valueOf(user_id), String.valueOf(0));
            homeAdapter.Update(projects);
          //  homeAdapter.notifyDataSetChanged();
        }
    }

    class MySwipelayout implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            if (isNetworkAvailable(getActivity())){
                networkAvailable=true;
                if (mPreferencesTool.getHomeFragmentPermission("HomeFragment")){
                    if (!mPreferencesTool.getSessionId("sessionId").equals(sessionId)){
                        FirstHomeAdapter=false;
                        FirstFai=true;

                        //RequestIntenet(longitude,latitude);
                        First=true;
                        getDingwei();
                    }else {
                        First=true;
                        getDingwei();
                       // RequestIntenet(longitude,latitude);
                    }

                } else {

                    if (!mPreferencesTool.getSessionId("sessionId").equals(sessionId)){
                       First=true;
                       // FirstFai=true;
                       FirstHomeAdapter=false;
                        List<UsersDB> usersDBS = DataBaseWork.DBSelectByTogether_Where(UsersDB.class, "sessionid=?", mPreferencesTool.getSessionId("sessionId"));
                        if (usersDBS !=null && usersDBS.size()>0){
                            user_id= usersDBS.get(0).getId();
                        }
                      getPermission();
                    }else {
                       getPermission();
                    }

                }
            }else {
                networkAvailable=false;
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
    private  void RequestIntenet(Double longitudes,Double latitudes){

        sessionId = mPreferencesTool.getSessionId("sessionId");

        Map<String, Object> map = new HashMap<>();
      //  if (longitudes !=null && latitudes!=null){
            // dd1 =new Double(latitudes);
           // dd1= Double.valueOf(latitudes);
            // dd = Double.valueOf(longitudes);
      //  }

        map.put("latitude",latitudes);
        map.put("longitude",longitudes);
        map.put("sessionId", sessionId);
        BasePara para = new BasePara();
        para.setData(map);
        Subscription subscription= RetrofitManager.getInstance()
                .createService(para)
                .getHomeData(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseModel>() {
                               @Override
                               public void onCompleted() {

                                   swipeRefreshLayout.setRefreshing(false);
                               }

                               @Override
                               public void onError(Throwable e) {
                                   ToastUtils.showShort(getActivity(), "服务器忙，请稍后");
                                   networkAvailable = false;

                                   if (FirstFai) {
                                       FirstFai = false;
                                       projects = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "usersdb_id=? and isdeletes =? ",String.valueOf(user_id), String.valueOf(0));
                                       setRecyclerViewAdapterDataSource(projects);
                                       FirstHomeAdapter=true;
                                   } else {
                                       switch (Info1) {

                                           case "01":
                                               projects = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "usersdb_id=? and completestatus=? and isdeletes =? and downloadtime !=?",String.valueOf(user_id), String.valueOf(1), String.valueOf(0),String.valueOf(0));


                                               break;
                                           case "02":
                                               projects = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, " usersdb_id=? and completestatus=? and isdeletes =?",String.valueOf(user_id), String.valueOf(3), String.valueOf(0));

                                               break;
                                           case "03":
                                               projects = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, " usersdb_id=? and  completestatus=? and isdeletes =?",String.valueOf(user_id), String.valueOf(2), String.valueOf(0));


                                               break;
                                           case "04":
                                               projects = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "usersdb_id=? and  isdeletes =?",String.valueOf(user_id), String.valueOf(0));

                                               break;
                                       }
                                       homeAdapter.Update(projects);
                                   }
                                   swipeRefreshLayout.setRefreshing(false);
                               }

                               @Override
                               public void onNext(BaseModel  baseModel) {
                                   networkAvailable=true;
                                   data= (HomeDataListModle) baseModel.getData();
                                   if (data!=null){
                                       //把数据缓存到本地
                                       if (data.getAllList() !=null && data.getAllList().size()>0){
                                           for (int i = 0; i <data.getAllList().size() ; i++) {
                                               SaveProject(data.getAllList().get(i).getId(),data.getAllList().get(i));
                                           }

                                       }
                                      // projects = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "usersdb_id=?  and isdelete =? ", String.valueOf(user_id),String.valueOf(0));

                                       switch (Info1)
                                       {
                                           case "01":
                                               projects = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "usersdb_id=? and completestatus=? and isdeletes =? and downloadtime !=? ", String.valueOf(user_id), String.valueOf(1), String.valueOf(0), String.valueOf(0));


                                               break;
                                           case "02":
                                               projects = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "usersdb_id=? and completestatus=? and isdeletes =?  ",String.valueOf(user_id), String.valueOf(3),String.valueOf(0));

                                               break;
                                           case "03":
                                               projects = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "usersdb_id=? and completestatus=? and isdeletes =?  ",String.valueOf(user_id), String.valueOf(2),String.valueOf(0));


                                               break;
                                           case "04":
                                               projects = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "usersdb_id=? and isdeletes =?  ",String.valueOf(user_id),String.valueOf(0));

                                               break;

                                       }

                                       if (FirstHomeAdapter){
                                           homeAdapter.Update(projects);
                                       }else {
                                           setRecyclerViewAdapterDataSource(projects);
                                           FirstHomeAdapter=true;
                                       }

                                       swipeRefreshLayout.setRefreshing(false);
                                   }else {

                                   }


                               }
                           });

                        addSubscription(subscription);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
        application. bdLocationUtils.mLocationClient.stop();
        application. bdLocationUtils.unRegisterLocationListener();
    }


    private void setRecyclerViewAdapterDataSource(List list)
    {
        LinearLayoutManager manager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        if (list!=null){
            homeAdapter=new HomeAdapter(getActivity(),list,new myAdapterListener());
        }else {
            homeAdapter=new HomeAdapter(getActivity(),null,new myAdapterListener());
        }

        recyclerView.setAdapter(homeAdapter);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
//如果仅仅是用来判断网络连接
  //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private  void download( String projectId, final int position) {
        Log.e("sessionId", "" + sessionId);
        sessionId = mPreferencesTool.getSessionId("sessionId");
        Map<String, Object> map = new HashMap<>();
        map.put("sessionId", sessionId);
        map.put("projectId", projectId);
        BasePara para = new BasePara();
        para.setData(map);
        Subscription subscription = RetrofitManager.getInstance()
                .createService(para)
                .download(map)
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
                      if (baseModel.getResult().equals("1")){
                          RequestIntenet(longitude,latitude);
                      }

                    }

                });
        addSubscription(subscription);
    }
        private  void SaveProject(String Pid, HomeDataListModle.AllListBean listBean){
            String sessionId = mPreferencesTool.getSessionId("sessionId");
            List<ProjectsDB> ProjectDBList = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class,"pid=?",Pid);
            List<UsersDB>  usersDBS = DataBaseWork.DBSelectByTogether_Where(UsersDB.class, "sessionId=?", sessionId);
            if (ProjectDBList!=null&&!ProjectDBList.isEmpty()&&ProjectDBList.size()>0){
                ProjectDBList.get(0).setRecord(listBean.getRecord()); //录音方式
                ProjectDBList.get(0).setInspect(listBean.getInspect()); //检验方式
                ProjectDBList.get(0).setPname(listBean.getPname());  //项目名
                ProjectDBList.get(0).setAddress(listBean.getAreaAddress());
                ProjectDBList.get(0).setDownloadTime(listBean.getDownloadTime());
                ProjectDBList.get(0).setcName(listBean.getCname());
                ProjectDBList.get(0).setCode(listBean.getCode());
                ProjectDBList.get(0).setGrade(listBean.getGrade());
                ProjectDBList.get(0).setManager(listBean.getManager());
                ProjectDBList.get(0).setStartTime(listBean.getStartTime());
                ProjectDBList.get(0).setCompleteStatus(listBean.getCompleteStatus());
                if (listBean.getDistance()!=null){
                    ProjectDBList.get(0).setDistance(listBean.getDistance());

                }else {
                    ProjectDBList.get(0).setDistance(String.valueOf(0));
                }
                ProjectDBList.get(0).setEndTime(listBean.getEndTime());
                ProjectDBList.get(0).setdName(listBean.getDname());
                ProjectDBList.get(0).setLatitude(listBean.getLatitude());
                ProjectDBList.get(0).setLocation(listBean.getLocation());
                ProjectDBList.get(0).setLongitude(listBean.getLongitude());
                ProjectDBList.get(0).setProgress(listBean.getProgress());
                ProjectDBList.get(0).setManager(listBean.getManager());
                ProjectDBList.get(0).setGrade(listBean.getGrade());
                ProjectDBList.get(0).setModifyTime(listBean.getModifyTime());
                ProjectDBList.get(0).setNotice(listBean.getNotice());
                ProjectDBList.get(0).setUser(usersDBS.get(0));
                int update = ProjectDBList.get(0).update(ProjectDBList.get(0).getId());
                if (update>0){

                }


            }else {
                ProjectsDB projectsDB = new ProjectsDB();
                projectsDB.setPid(listBean.getId());
                projectsDB.setRecord(listBean.getRecord()); //录音方式
                projectsDB.setInspect(listBean.getInspect()); //检验方式
                projectsDB.setPname(listBean.getPname());  //项目名
                projectsDB.setAddress(listBean.getAreaAddress());
                projectsDB.setStartTime(listBean.getStartTime());
                projectsDB.setcName(listBean.getCname());
                projectsDB.setCode(listBean.getCode());
                projectsDB.setDownloadTime(listBean.getDownloadTime());
                projectsDB.setCompleteStatus(listBean.getCompleteStatus());
                if (listBean.getDistance()!=null){
                    projectsDB.setDistance(listBean.getDistance());

                }else {
                    projectsDB.setDistance(String.valueOf(0));
                }
                projectsDB.setProgress(listBean.getProgress());
                projectsDB.setdName(listBean.getDname());
                projectsDB.setLatitude(listBean.getLatitude());
                projectsDB.setLocation(listBean.getLocation());
                projectsDB.setLongitude(listBean.getLongitude());
                projectsDB.setModifyTime(listBean.getModifyTime());
                projectsDB.setNotice(listBean.getNotice());
                projectsDB.setEndTime(listBean.getEndTime());
                projectsDB.setIsComplete(0);
                projectsDB.setStage("1");
                projectsDB.setUser(usersDBS.get(0));
                boolean save = projectsDB.save();
                if (save){
                }
            }
           // projects = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "usersdb_id=? and isdelete !=?  ",String.valueOf(user_id),String.valueOf(0));

            /*删除*/
            }

    public void setItemClickEven(ViewGroup viewGroup)
    {

        for (int i = 0 ; i < viewGroup.getChildCount() ; i++)
        {
            TextView textView = (TextView) viewGroup.getChildAt(i);
            Drawable drawable = getResources().getDrawable(iconNormal[0]);
            drawable.setBounds(0,0,30,35);
            textView.setCompoundDrawables(null,null,drawable,null);
            textView.setCompoundDrawablePadding(5);
        }
    }
    public void setItemClickTopLineBg(ViewGroup viewGroup)
    {

        for (int i = 0 ; i < viewGroup.getChildCount() ; i++)
        {
            View view = (View) viewGroup.getChildAt(i);
            if (i==3){
                view.setBackgroundColor(getResources().getColor(R.color.top_radio_check));
            }else {
                view.setBackgroundColor(getResources().getColor(R.color.titleBar));
            }

        }
    }
    private void chanceItemTopLine(int id,ViewGroup viewGroup){

        if (topIndex == id)
        {


            return;
        }
        else
        {
            for (int i = 0 ; i < viewGroup.getChildCount() ; i ++)
            {
                View view = (View) viewGroup.getChildAt(i);



                if (i == id)
                {
                    view.setBackgroundColor(getResources().getColor(R.color.top_radio_check));
                }
                else
                {
                    view.setBackgroundColor(getResources().getColor(R.color.titleBar));
                }
            }
            topIndex=id;
        }
    }
    private void chanceItem(int id,ViewGroup viewGroup){

        if (mIndex == id)
        {


            return;
        }
        else
        {
            for (int i = 0 ; i < viewGroup.getChildCount() ; i ++)
            {
                TextView textView = (TextView) viewGroup.getChildAt(i);
                Drawable drawable = null;


                if (i == id)
                {

                    drawable = getResources().getDrawable(iconSelected[0]);
                    drawable.setBounds(0, 0, 30, 35);
                    textView.setCompoundDrawables(null,null,drawable,null);
                    textView.setTextColor(Color.parseColor("#ff8103"));
                    textView.setCompoundDrawablePadding(5);

                }
                else
                {
                    drawable = getResources().getDrawable(iconNormal[0]);
                    drawable.setBounds(0, 0, 30, 35);
                    textView.setCompoundDrawables(null,null,drawable,null);
                    textView.setTextColor(Color.parseColor("#666666"));
                    textView.setCompoundDrawablePadding(5);
                }
            }
            mIndex=id;
        }
    }
    private void getDingwei(){
        application = LocationApplication.getInstance();
        application.bdLocationUtils.doLocation();//开启定位
        application. bdLocationUtils.mLocationClient.start();//开始定位
        application. bdLocationUtils.setSdkListener(new BaiDuSdkListener() {
            @Override
            public void getLongLatAddress(Double Longitude, Double Latitude, String address, StringBuffer stringBuffer, BDLocation location) {
                latitude=Latitude;
                longitude=Longitude;
               // mPreferencesTool.setHomeLocation("latitude","longitude",Latitude,Longitude);
                if (First){
                    if (networkAvailable){
                        RequestIntenet(Longitude,Latitude);
                    }else {
                        List<UsersDB> usersDBS = DataBaseWork.DBSelectByTogether_Where(UsersDB.class, "sessionid=?", mPreferencesTool.getSessionId("sessionId"));
                        if (usersDBS !=null && usersDBS.size()>0){
                            user_id= usersDBS.get(0).getId();
                        }
                        projects = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "usersdb_id=? and  isdeletes =?  ",String.valueOf(user_id), String.valueOf(0));
                        setRecyclerViewAdapterDataSource(projects);
                    }

                }
                    swipeRefreshLayout.setRefreshing(false);
                First=false;
                application. bdLocationUtils.mLocationClient.stop();
                application.bdLocationUtils.unRegisterLocationListener();

            }

        });
    }

    /**
     * 动态获取权限
     */
    private void getPermission(){
        if(Build.VERSION.SDK_INT>=23){
            XPermissionUtils.requestPermissions(getActivity(), 100, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, new XPermissionUtils.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    mPreferencesTool.setHomeFragmentPermission("HomeFragment",true);
                    getDingwei();
                }

                @Override
                public void onPermissionDenied(final String[] deniedPermissions, boolean alwaysDenied) {
                    mPreferencesTool.setHomeFragmentPermission("HomeFragment",false);
                    if (alwaysDenied) { // 拒绝后不再询问 -> 提示跳转到设置
                        // DialogUtil.showPermissionManagerDialog(MainActivity.this, "相机");
                    } else {    // 拒绝 -> 提示此公告的意义，并可再次尝试获取权限
                        new AlertDialog.Builder(getActivity()).setTitle("温馨提示")
                                .setMessage("我们需要定位权限才能正常使用该功能")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("验证权限", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        XPermissionUtils.requestPermissionsAgain(getActivity(), deniedPermissions, 100);
                                    }
                                }).show();
                    }

                }
            });
        }else {
            getDingwei();
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        application. bdLocationUtils.unRegisterLocationListener();
        application. bdLocationUtils.mLocationClient.stop();
        First=true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (  application !=null){
            application. bdLocationUtils.mLocationClient.stop();
            application. bdLocationUtils.unRegisterLocationListener();
        }



    }

    @Override
    public void onResume() {
        super.onResume();
       // getDingwei();
        //First=true;
        if (mPreferencesTool.getHomeFragmentPermission("HomeFragment")){
            Log.e(TAG, "onResume:sessionId "+sessionId );
            if(!mPreferencesTool.getSessionId("sessionId").equals(sessionId)){
                FirstHomeAdapter=false;
                FirstFai=true;
                getDingwei();
            }else {
                getDingwei();
            }
        }else {
            if(!mPreferencesTool.getSessionId("sessionId").equals(sessionId)){
               First=true;
               getPermission();
            }else {
                getPermission();
            }
        }

    }



    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    protected String[] getNeedPermissions() {
        return new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    @Override
    protected void permissionGrantedSuccess() {
        isPerssoin=true;
        mPreferencesTool.setHomeFragmentPermission("HomeFragment",true);
        getDingwei();
    }

    @Override
    protected void permissionGrantedFail() {
        isPerssoin=false;
        mPreferencesTool.setHomeFragmentPermission("HomeFragment",false);
        getPermission();
    }
}
