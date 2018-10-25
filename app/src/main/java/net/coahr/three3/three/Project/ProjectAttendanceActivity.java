package net.coahr.three3.three.Project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andsync.xpermission.XPermissionUtils;
import com.baidu.location.BDLocation;
import com.baidu.location.BDNotifyListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.SpatialRelationUtil;

import net.coahr.three3.three.Base.BaseCheckPermissionActivity;
import net.coahr.three3.three.Base.BaseModel;
import net.coahr.three3.three.Base.BasePara;
import net.coahr.three3.three.Base.LocationApplication;
import net.coahr.three3.three.Model.AttendanceInfoModel;
import net.coahr.three3.three.NetWork.RetrofitManager;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.BaiduSDk.BaiDuSdkListener;
import net.coahr.three3.three.Util.OtherUtils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yuwei on 2018/4/18.
 */

public class ProjectAttendanceActivity extends BaseCheckPermissionActivity {
    private android.support.v4.app.Fragment attendanceFragment,historyFragment;
    private TextView  mFragmentBtnLeft , mFragmentBtnRight;
    private android.support.v4.app.FragmentTransaction fragmentTransaction;
   // private NotiftLocationListener listener;
    private Vibrator mVibrator;
    public double longitude,latitude;
    private NotifyLister mNotifyLister;
    private   LocationApplication application;
    private MaterialDialog loadingDialog;
    private LatLng  mLatLng;
    private  LatLng currentLatLng;
    private  Boolean inCircle;
    private  int i=1;
    private int[] iconNormal = {R.drawable.attendance , R.drawable.histroy};
    private int[] iconSelected = {R.drawable.attendance_c , R.drawable.histroy_c};

    private RemoteDataInterface mRemoteDataInterface;
    private AttendanceInterface mAttendanceInterface;
    private BaseModel   projectInfo;


    public interface RemoteDataInterface
    {
        void remoteData(BaseModel model);

    }
    public interface AttendanceInterface
    {
        void attendaceHistory(BaseModel model);
    }

    public void setmRemoteDataInterface(RemoteDataInterface mRemoteDataInterface) {
        this.mRemoteDataInterface = mRemoteDataInterface;
    }

    public void setmAttendanceInterface(AttendanceInterface mAttendanceInterface) {
        this.mAttendanceInterface = mAttendanceInterface;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_attendance);
        findUI();
        initDialog();
        setAttendanceFragment(new ProjectAttendanceFragment());
        attendanceFragment = getAttendanceFragment();
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.fg_container, attendanceFragment, "home").commit();
       // listener = new NotiftLocationListener();
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
      //  mLocationClient  = new LocationClient(this);
      //  mLocationClient.registerLocationListener(listener);
        mNotifyLister = new NotifyLister();
       // mLocationClient.registerNotify(mNotifyLister);
       // getDingwei();
        requestRemote(getIntent().getStringExtra("projectId"));
        showLoading();

    }

    public void setAttendanceFragment(android.support.v4.app.Fragment attendanceFragment) {
        this.attendanceFragment = attendanceFragment;


    }

    public android.support.v4.app.Fragment getAttendanceFragment() {
        return attendanceFragment;
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void findUI() {
        super.findUI();
        setTitle((TextView) naviBar.findViewById(R.id.title), "考勤打卡");
        configureNaviBar(naviBar.findViewById(R.id.left) , null);
        mFragmentBtnLeft = findViewById(R.id.attendance);
        mFragmentBtnRight = findViewById(R.id.attendanceHistory);
        mFragmentBtnLeft.setOnClickListener(new FragmentChangeBtnListenner());
        mFragmentBtnRight.setOnClickListener(new FragmentChangeBtnListenner());
    }


    class FragmentChangeBtnListenner implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {

            String title = "";
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (attendanceFragment != null)
                fragmentTransaction.hide(attendanceFragment);

            if(historyFragment != null)
                fragmentTransaction.hide(historyFragment);
            switch (v.getId())
            {
                case R.id.attendance:
                    title = "考勤打卡";
                    if (attendanceFragment == null) {
                        setAttendanceFragment(new ProjectAttendanceFragment());

                        fragmentTransaction.add(R.id.fg_container, getAttendanceFragment());

                    } else {
                        fragmentTransaction.show(attendanceFragment);
                    }


                    changeItem(R.id.attendance);

                    break;

                case R.id.attendanceHistory:
                    title = "接触历史";
                    if (historyFragment == null) {
                        historyFragment = new ProjectAttendanceHistoryFragment();

                        fragmentTransaction.add(R.id.fg_container, historyFragment);

                    } else {
                        fragmentTransaction.show(historyFragment);
                    }
                    changeItem(R.id.attendanceHistory);
                    break;
            }
            fragmentTransaction.commit();
            setTitle((TextView) naviBar.findViewById(R.id.title), title);
        }
    }


    public  void changeItem(int id)
    {

        Drawable drawable = null;
        if (id == R.id.attendance)
        {

            drawable = getResources().getDrawable(R.drawable.attendance_c);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mFragmentBtnLeft.setCompoundDrawables(null , drawable , null , null);
            drawable = getResources().getDrawable(R.drawable.histroy);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mFragmentBtnRight.setCompoundDrawables(null , drawable , null , null);
        }
        else
        {
            drawable = getResources().getDrawable(R.drawable.histroy_c);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mFragmentBtnRight.setCompoundDrawables(null , drawable , null , null);
            drawable = getResources().getDrawable(R.drawable.attendance);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mFragmentBtnLeft.setCompoundDrawables(null , drawable , null , null);
            getHistory();
        }
    }


    /**
     * 显示请求字符串
     *
     * @param str
     */
    public void logMsg(String str) {
        final String s = str;
        try {
            if (s != null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                            ProjectAttendanceActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((ProjectAttendanceFragment)attendanceFragment).setLocationMsg(s);
                                }
                            });

                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (application !=null){
            application.  bdLocationUtils.unRegisterLocationListener();
            application.  bdLocationUtils.mLocationClient.removeNotifyEvent(mNotifyLister);
            application.  bdLocationUtils.mLocationClient.stop();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (application !=null){
            application. bdLocationUtils.unRegisterLocationListener();
            application. bdLocationUtils.mLocationClient.removeNotifyEvent(mNotifyLister);
            application. bdLocationUtils.mLocationClient.stop();
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
       // getDingwei();
    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
    }

    @Override
    protected void permissionGrantedSuccess() {
                getDingwei();
    }

    @Override
    protected void permissionGrantedFail() {
        showTipsDialog();
        getPermission();
    }


    private Handler notifyHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
        if (msg.what==0){

            mNotifyLister.SetNotifyLocation(latitude,longitude, 200,application.bdLocationUtils.mLocationClient.getLocOption().coorType);//4个参数代表要位置提醒的点的坐标，具体含义依次为：纬度，经度，距离范围，坐标系类型(gcj02,gps,bd09,bd09ll)mLocationClient.getLocOption().getCoorType()
        }

        }

    };


    public class NotifyLister extends BDNotifyListener{
        public void onNotify(BDLocation mlocation, float distance){
            if (inCircle){
                mVibrator.vibrate(2000);//振动提醒已到设定位置附近
                ToastUtils.showLong(ProjectAttendanceActivity.this,"已到达考勤范围");
            }
            i++;
            ((ProjectAttendanceFragment)attendanceFragment).setRangeMsg(inCircle);
        }
    }


    protected void requestRemote(String projectId) {
        super.requestRemote();

        Map<String, Object> map = new HashMap<>();

        String sessionId = mPreferencesTool.getSessionId("sessionId");
        //这里通过activity传值 将projectId透传过来
        map.put("projectId", projectId);
        map.put("sessionId",sessionId);
        BasePara para = new BasePara();
        para.setData(map);
        Subscription subscription= RetrofitManager.getInstance()
                .createService(para)
                .getAttendanceInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                        System.out.println(e);
                        mPreferencesTool.setAttendanceInter("AttendanceInter",false);
                    }

                    @Override
                    public void onNext(BaseModel baseModel) {


                        System.out.println(baseModel);
                            if (baseModel.getResult().equals("1"))
                            {

                                AttendanceInfoModel infoModel  = (AttendanceInfoModel) baseModel.getData();
                                mLatLng=new LatLng(infoModel.getLatitude(),infoModel.getLongitude());
                                getPermission();
                                projectInfo = baseModel;
                                mRemoteDataInterface.remoteData(baseModel);
                                mPreferencesTool.setAttendanceInter("AttendanceInter",true);
                                //getDingwei();
                            }



                    }
                });

    }

    protected void getHistory()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("projectId", getIntent().getStringExtra("projectId"));
        map.put("sessionId",mPreferencesTool.getSessionId("sessionId"));
        BasePara para = new BasePara();
        para.setData(map);
        Subscription subscription= RetrofitManager.getInstance()
                .createService(para)
                .getAttendanceHistory(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                        System.out.println(e);


                    }

                    @Override
                    public void onNext(BaseModel baseModel) {


                        System.out.println(baseModel);
                        if (baseModel.getResult().equals("1"))
                        {
                            mAttendanceInterface.attendaceHistory(baseModel);
                            ((ProjectAttendanceHistoryFragment)historyFragment).setProjectInfo(projectInfo);
                        }


                    }
                });
    }

    //fragment调用
    public void fragmentChangeAcitivity(){
       // locationService.stop();
       // locationService.start();
      // bdLocationUtils.mLocationClient.start();
        if (application !=null){
            application.bdLocationUtils.mLocationClient.stop();
            application.bdLocationUtils.unRegisterLocationListener();
            application.bdLocationUtils.mLocationClient.removeNotifyEvent(mNotifyLister);
            showLoading();
            getDingwei();
        }else {

        }


    }
    private void getDingwei(){

        application = LocationApplication.getInstance();
        //bdLocationUtils=new BDLocationUtils(application);
        application.bdLocationUtils.doLocation();//开启定位(间隔时间)
        //开始定位
        application. bdLocationUtils.mLocationClient.start();
        application. bdLocationUtils.mLocationClient.registerNotify(mNotifyLister);
        application. bdLocationUtils.setSdkListener(new BaiDuSdkListener() {
            @Override
            public void getLongLatAddress(Double Longitude, Double Latitude, String address,StringBuffer stringBuffer,BDLocation location) {
                latitude=Latitude;
                longitude=Longitude;
                if (location.getStreet()==null){
                    showLoading();
                } else {
                    dismissLoading();
                }
                    logMsg(location.getProvince()+location.getCity()+location.getDistrict()+location.getStreet());
                System.out.println(location.getProvince()+location.getCity()+location.getDistrict()+location.getStreet());
                // mPreferencesTool.setHomeLocation("latitude","longitude",Latitude,Longitude);
                currentLatLng = new LatLng(Latitude , Longitude);
                ((ProjectAttendanceFragment)attendanceFragment).setmLatLng(currentLatLng,location);
                inCircle = SpatialRelationUtil.isCircleContainsPoint(mLatLng ,200 , currentLatLng);
                ToastUtils.showLong(ProjectAttendanceActivity.this,"定位信息"+longitude+"/"+latitude);
                notifyHandler.sendEmptyMessage(0);
                ((ProjectAttendanceFragment)attendanceFragment).setRangeMsg(inCircle);




            }

        });
    }

    /**
     * 动态获取权限
     */
    private void getPermission(){
        if(Build.VERSION.SDK_INT>=23){
            XPermissionUtils.requestPermissions(ProjectAttendanceActivity.this, 100, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO}, new XPermissionUtils.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    getDingwei();
                }

                @Override
                public void onPermissionDenied(final String[] deniedPermissions, boolean alwaysDenied) {
                    if (alwaysDenied) { // 拒绝后不再询问 -> 提示跳转到设置
                        // DialogUtil.showPermissionManagerDialog(MainActivity.this, "相机");
                    } else {    // 拒绝 -> 提示此公告的意义，并可再次尝试获取权限
                        new AlertDialog.Builder(ProjectAttendanceActivity.this).setTitle("温馨提示")
                                .setMessage("我们需要定位权限才能正常使用该功能")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("验证权限", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        XPermissionUtils.requestPermissionsAgain(ProjectAttendanceActivity.this, deniedPermissions, 100);
                                    }
                                }).show();
                    }

                }
            });
        }else {
            getDingwei();
        }
    }

    public void initDialog() {
        loadingDialog = new MaterialDialog.Builder(ProjectAttendanceActivity.this)
                .title("正在定位...")
                .content("如果无法获取定位请打开gps定位")
                .progress(true, 100)
                .canceledOnTouchOutside(false)
                .build();
    }

    public void showLoading() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    public void dismissLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
