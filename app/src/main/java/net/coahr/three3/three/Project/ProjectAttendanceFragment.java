package net.coahr.three3.three.Project;

import android.Manifest;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import net.coahr.three3.three.Base.BaseCheckPermissionFragment;
import net.coahr.three3.three.Base.BaseModel;
import net.coahr.three3.three.Base.BasePara;
import net.coahr.three3.three.Base.BaseTools;
import net.coahr.three3.three.Model.AttendanceInfoModel;
import net.coahr.three3.three.Model.AttendanceModel;
import net.coahr.three3.three.NetWork.RetrofitManager;
import net.coahr.three3.three.Popupwindow.AlertDialogs.CommomDialog;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.OtherUtils.ToastUtils;
import net.coahr.three3.three.Util.Preferences.PreferencesTool;
import net.coahr.three3.three.customView.AttendanceView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yuwei on 2018/4/18.
 */

public class ProjectAttendanceFragment extends BaseCheckPermissionFragment {

    private AttendanceView mAttendance;
    private TextView mAddressTextView;
    private TextView mRangeTextView;
    private TextView mTimeLabel;
    private TextView projectNameTextView, riqiTextView, codeTextView, companyNameTextView, addressTextView, startTimeTextView;
    private TextView endTimeTextView, locationTextView, startAttendanceTimeTextView, startAttendanceAddressTextView, endAttendanceTimeTextView, endAttendanceAddressTextView;
    private AttendanceView mAttendanceView;
    private RelativeLayout startBlock, endBlock, endInfoBlock;
    private ImageView startStatusBtn, endStatusBtn ;
    private Button remarkBtn,cancelBtn,terminateBtn;
    private EditText contentText;
   private TextView  attendance_re;
    private Boolean flag;
    private PopupWindow mWindow;
    private boolean startFlag, endFlag;
    private LatLng mLatLng;
    private BDLocation bdLocation;
    private AttendanceInfoModel mInfoModel;
    private PreferencesTool mpreferencesTool;
    private String msg;
    private GeoCoder mGeoCoder = GeoCoder.newInstance();
    final Handler timeHandler = new Handler();

    public void setmLatLng(LatLng mLatLng, BDLocation location) {
        this.mLatLng = mLatLng;
        this.bdLocation=location;
    }

    private Runnable updateThread = new Runnable() {
        @Override
        public void run() {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            String date = df.format(new Date());
            mTimeLabel.setText(date);
            timeHandler.postDelayed(updateThread, 1000);
        }
    };


    public void setLocationMsg(String msg) {
        this.msg=msg;
        mAddressTextView.setText(msg);
    }

    public void setRangeMsg(Boolean flags) {
        this.flag=flags;
        if (flag !=null && bdLocation !=null){
        if (flag) {
            mRangeTextView.setText("已进入考勤打卡范围");
            mAttendance.setInCircle(true);
        } else {
            mRangeTextView.setText("非考勤打卡范围");
            mAttendance.setInCircle(false);
        }
        }else {
            mRangeTextView.setText("定位中...");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_project_attendance, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mpreferencesTool=new PreferencesTool(getActivity());
        findUI(view);

        ((ProjectAttendanceActivity) getActivity()).setmRemoteDataInterface(new ProjectAttendanceActivity.RemoteDataInterface() {
            @Override
            public void remoteData(BaseModel model) {

                System.out.println(model);
                AttendanceInfoModel infoModel = (AttendanceInfoModel) model.getData();

                if (infoModel != null) {
                    mInfoModel = infoModel;
                    projectNameTextView.setText(infoModel.getPname());
                    codeTextView.setText(infoModel.getCode());
                    companyNameTextView.setText(infoModel.getDname());
                    addressTextView.setText(infoModel.getAreaAddress() + infoModel.getLocation());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String ymd = simpleDateFormat.format(infoModel.getStartTime());
                    riqiTextView.setText(ymd + "--结束公开");
                    System.out.println(ymd);

                    String[] str = infoModel.getCname().split("-");
                    if (str.length > 1) {
                        startTimeTextView.setText(str[0]);
                        endTimeTextView.setText(str[1]);
                    }
                    else if(str.length == 1){
                        startTimeTextView.setText(str[0]);
                        endTimeTextView.setText(str[0]);
                    }
                    locationTextView.setText(infoModel.getAreaAddress() + infoModel.getLocation());

                    System.out.println(str[0]);


//                    if (infoModel.getAttendance() != null)//如果有早班打卡记录就显示
//                    {
                    //进店打卡

                    /**
                     * 时间判断
                     */
                    if (infoModel.getAttendance().getStartTimeStatus() != 0) {

                            startBlock.setVisibility(View.VISIBLE);
                            endBlock.setVisibility(View.VISIBLE);
                            simpleDateFormat = new SimpleDateFormat("HH:mm");
                            String hm = simpleDateFormat.format(infoModel.getAttendance().getInTime());
                            startAttendanceTimeTextView.setText(hm);
                            if (infoModel.getAttendance().getStartTimeStatus() == 1)//上班卡正常
                            {
                                /**
                                 * 地址判断
                                 */
                                if (infoModel.getAttendance().getStartLocationStatus() !=0){

                                            if (infoModel.getAttendance().getStartLocationStatus()==1){
                                                startAttendanceAddressTextView.setText(infoModel.getAreaAddress() + infoModel.getLocation());
                                                startStatusBtn.setImageResource(R.drawable.kaoqinz);
                                            }else {
                                                getAddress(infoModel.getAttendance().getInLat(), infoModel.getAttendance().getInLng(), startAttendanceAddressTextView);
                                                startStatusBtn.setImageResource(R.drawable.kaoqinyc);
                                            }
                                }

                            } else {
                                //如果异常 判断地址是否异常
                                if (infoModel.getAttendance().getStartLocationStatus() !=0){

                                    if (infoModel.getAttendance().getStartLocationStatus()==1){
                                        startAttendanceAddressTextView.setText(infoModel.getAreaAddress() + infoModel.getLocation());
                                        startStatusBtn.setImageResource(R.drawable.kaoqinyc);
                                    }else {
                                        getAddress(infoModel.getAttendance().getInLat(), infoModel.getAttendance().getInLng(), startAttendanceAddressTextView);
                                        startStatusBtn.setImageResource(R.drawable.kaoqinyc);
                                    }
                                } else {  //地址不异常
                                    startAttendanceAddressTextView.setText(infoModel.getAreaAddress() + infoModel.getLocation());
                                    startStatusBtn.setImageResource(R.drawable.kaoqinyc);
                                }

                            }

                            mAttendanceView.setText("结束打卡");
                    }else {
                        startBlock.setVisibility(View.INVISIBLE);
                        mAttendanceView.setText("开始打卡");
                    }



                //离店打卡
                    if (infoModel.getAttendance().getEndTimeStatus() != 0) {
                        endInfoBlock.setVisibility(View.VISIBLE);
                        simpleDateFormat = new SimpleDateFormat("HH:mm");
                        String hm = simpleDateFormat.format(infoModel.getAttendance().getOutTime());
                        endAttendanceTimeTextView.setText(hm);
                        if (infoModel.getAttendance().getEndTimeStatus() == 1)//下班正常
                        {
                            if (infoModel.getAttendance().getEndLocationStatus() !=0){
                                if (infoModel.getAttendance().getEndLocationStatus()==1){
                                    endAttendanceAddressTextView.setText(infoModel.getAreaAddress() + infoModel.getLocation());
                                    //endStatusBtn.setBackgroundColor(Color.GREEN);
                                    remarkBtn.setVisibility(View.GONE);
                                }else {
                                    getAddress(infoModel.getAttendance().getInLat(), infoModel.getAttendance().getInLng(), endAttendanceAddressTextView);
                                    endStatusBtn.setImageResource(R.drawable.kaoqinyc);
                                    remarkBtn.setVisibility(View.VISIBLE);
                                }
                            }else {
                                getAddress(infoModel.getAttendance().getInLat(), infoModel.getAttendance().getInLng(), endAttendanceAddressTextView);
                                endStatusBtn.setImageResource(R.drawable.kaoqinyc);
                                remarkBtn.setVisibility(View.VISIBLE);
                            }

                        } else {
                            if (infoModel.getAttendance().getEndLocationStatus() !=0){
                                if (infoModel.getAttendance().getEndLocationStatus()==1){
                                    endAttendanceAddressTextView.setText(infoModel.getAreaAddress() + infoModel.getLocation());
                                    endStatusBtn.setImageResource(R.drawable.kaoqinyc);
                                    remarkBtn.setVisibility(View.VISIBLE);
                                }else {
                                    getAddress(infoModel.getAttendance().getInLat(), infoModel.getAttendance().getInLng(), endAttendanceAddressTextView);
                                    endStatusBtn.setImageResource(R.drawable.kaoqinyc);
                                    remarkBtn.setVisibility(View.VISIBLE);
                                }
                            }else {
                                getAddress(infoModel.getAttendance().getInLat(), infoModel.getAttendance().getInLng(), endAttendanceAddressTextView);
                                endStatusBtn.setImageResource(R.drawable.kaoqinyc);
                                remarkBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    }


                }
            }

        });
    }

    public void findUI(View view) {
        mAttendance = view.findViewById(R.id.attendance);
       // boolean attendanceInter = mpreferencesTool.getAttendanceInter("AttendanceInter");
        mAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean attendanceInter = mpreferencesTool.getAttendanceInter("AttendanceInter");
                if (attendanceInter){
                    if (flag !=null && msg !=null){
                        attendance(msg);
                    }else {
                        ToastUtils.showShort(getActivity(),"正在定位中...");
                    }
                } else {
                    ToastUtils.showShort(getActivity(),"无法连接服务器");
                }

            }
        });
        mAddressTextView = view.findViewById(R.id.adressTextView);
        mRangeTextView = view.findViewById(R.id.range);
        mTimeLabel = view.findViewById(R.id.timeLabel);
        attendance_re=view.findViewById(R.id.attendance_re);
        attendance_re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity()!=null){
                    ((ProjectAttendanceActivity)getActivity()).fragmentChangeAcitivity();
                }
            }
        });


        new Thread() {
            @Override
            public void run() {
                super.run();
                timeHandler.post(updateThread);
            }
        }.start();

        projectNameTextView = view.findViewById(R.id.projectName);
        riqiTextView = view.findViewById(R.id.riqi);
        codeTextView = view.findViewById(R.id.code);
        companyNameTextView = view.findViewById(R.id.companyName);
        addressTextView = view.findViewById(R.id.adress);
        startTimeTextView = view.findViewById(R.id.startTime);
        endTimeTextView = view.findViewById(R.id.endTime);
        locationTextView = view.findViewById(R.id.adressTextView);
        Drawable drawable = null;
        drawable = getResources().getDrawable(R.drawable.site);
        drawable.setBounds(0, 0, 30, 30);
        locationTextView.setCompoundDrawables(drawable, null, null, null);
        startBlock = view.findViewById(R.id.startBlock);
        endBlock = view.findViewById(R.id.endArea);
        endInfoBlock = view.findViewById(R.id.endInfoBlock);
        startAttendanceTimeTextView = view.findViewById(R.id.start);
        startAttendanceAddressTextView = view.findViewById(R.id.attendanceAdress);
        endAttendanceTimeTextView = view.findViewById(R.id.endTimes);
        endAttendanceAddressTextView = view.findViewById(R.id.endAttendanceAddress);
        startStatusBtn = view.findViewById(R.id.startStatus);
        endStatusBtn = view.findViewById(R.id.endStatus);
        mAttendanceView = view.findViewById(R.id.attendance);
        remarkBtn = view.findViewById(R.id.remark);
        remarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makePopWindow();


            }
        });

    }

    public void attendance(String msg) {
        if (mInfoModel == null)
            return;
        final Map<String, Object> map = new HashMap<>();
        map.put("projectId", mInfoModel.getProjectId());

        String sessionId = mPreferencesTool.getSessionId("sessionId");
        map.put("sessionId", sessionId);
        if (mInfoModel != null) {

            map.put("classId", mInfoModel.getClassId());
            map.put("dealerId", mInfoModel.getDealerId());
            if (mInfoModel.getAttendance().getStartTimeStatus() == 0) {
                //是否在打卡区域内
                map.put("startLocationStatus", mAttendance.isInCircle()? 1 : -1);
                startFlag = true;
            } else {
                map.put("endLocationStatus",  mAttendance.isInCircle()? 1 : -1);
            }
            final BasePara para = new BasePara();
            if (flag !=null && bdLocation !=null){
                if (flag){
                    map.put("longitude",mInfoModel.getLongitude());
                    map.put("latitude", mInfoModel.getLatitude());
                    para.setData(map);
                    pushClock(map,para,msg);

                }else {   //不再考情范围内
                    map.put("longitude", bdLocation.getLongitude());
                    map.put("latitude", bdLocation.getLatitude());
                    para.setData(map);
                    new CommomDialog(getActivity(), R.style.dialog,"是否继续打卡" ,false,false,null, new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm){
                                pushClock(map,para,msg);
                                dialog.dismiss();
                            }
                        }
                    }).setTitle("当前不再考勤范围内").setPositiveButton("确定").show();
                }
            }else {
                    ToastUtils.showShort(getActivity(),"正在定位中...（如果多次无法获取定位请查看是否授予定位权限）");
            }


        }


    }


    public void getAddress(double lat, double lng, final TextView textView) {
        LatLng mLatLng = new LatLng(lat, lng);

        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {


                System.out.println(reverseGeoCodeResult);
                textView.setText(reverseGeoCodeResult.getAddress());
            }
        };
        mGeoCoder.setOnGetGeoCodeResultListener(listener);
        mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(mLatLng));
    }

    public void makePopWindow() {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_remark_window, null, false);
        PopupWindow window = new PopupWindow(contentView, BaseTools.getWindowWidth(getActivity()),BaseTools.getWindowHeigh(getActivity()));

        mWindow = window;
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
        mWindow.setFocusable(true);
        mWindow.setOutsideTouchable(true);
        mWindow.setTouchable(true);
        mWindow.setClippingEnabled(false);//允许弹出窗口超出屏幕范围
        cancelBtn = contentView.findViewById(R.id.cancle);
        terminateBtn = contentView.findViewById(R.id.terminate);
        cancelBtn.setOnClickListener(new PopWindowBtnListenner());
        terminateBtn.setOnClickListener(new PopWindowBtnListenner());
        contentText = contentView.findViewById(R.id.content);
        View decorView = getActivity().getWindow().getDecorView();
        mWindow.showAtLocation(decorView, Gravity.CENTER, 0, 0);

    }

    /**
     * 备注请求
     */
    public void remarkRequest(PopupWindow popupWindow)
    {

        Map<String, Object> map = new HashMap<>();
        map.put("id", mInfoModel.getAttendance().getId());
        map.put("remark",contentText.getText());
        BasePara para = new BasePara();
        para.setData(map);
        Subscription subscription= RetrofitManager.getInstance()
                .createService(para)
                .remark(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        closeInputManage(popupWindow);
                    }

                    @Override
                    public void onNext(BaseModel model) {

                        if (model.getResult().equals("1"))
                        {

                            ToastUtils.showShort(getActivity() , "提交成功");


                        }
                        else
                        {
                            ToastUtils.showShort(getActivity() ,model.getMsg());
                        }


                        closeInputManage(popupWindow);
                        popupWindow.dismiss();
                    }
                });
            addSubscription(subscription);

    }



    class PopWindowBtnListenner implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.cancle)
            {
                mWindow.dismiss();
            }
            else
            {

                remarkRequest(mWindow);
            }


        }
    }
    private void pushClock(final Map map, BasePara para,String msg){
         Subscription subscription = RetrofitManager.getInstance()
                .createService(para)
                .attendance(map)
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

                        if (baseModel != null && baseModel.getResult().equals("1")) {
                            AttendanceModel attendanceModel = (AttendanceModel) baseModel.getData();
                            if (map.get("startLocationStatus") != null) {
                                startBlock.setVisibility(View.VISIBLE);
                                endBlock.setVisibility(View.VISIBLE);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                                String hm = dateFormat.format(attendanceModel.getSignInTime());
                                startAttendanceTimeTextView.setText(hm);
                                if (attendanceModel.getTimeStatus() == 1) {
                                   // endAttendanceAddressTextView.setText(msg);
                                   // getAddress(mLatLng.latitude, mLatLng.longitude, startAttendanceAddressTextView);

                                      mInfoModel.getAttendance().setStartTimeStatus(1);
                                      if ((int)map.get("startLocationStatus")==1){
                                         // startStatusBtn.setBackgroundColor(Color.GREEN);
                                          startAttendanceAddressTextView.setText(mInfoModel.getAreaAddress() + mInfoModel.getLocation());
                                          startStatusBtn.setImageResource(R.drawable.kaoqinz);
                                      }
                                      if ((int)map.get("startLocationStatus")==-1){
                                          startAttendanceAddressTextView.setText(msg);
                                         // getAddress(mLatLng.latitude, mLatLng.longitude, startAttendanceAddressTextView);
                                          startStatusBtn.setImageResource(R.drawable.kaoqinyc);
                                      }


                                } else if (attendanceModel.getTimeStatus() == -1) {
                                    startAttendanceAddressTextView.setText(msg);
                                   // getAddress(mLatLng.latitude, mLatLng.longitude, startAttendanceAddressTextView);
                                    mInfoModel.getAttendance().setStartTimeStatus(-1);
                                    startStatusBtn.setImageResource(R.drawable.kaoqinyc);
                                }

                                mAttendanceView.setText("结束打卡");
                            }
                            if (map.get("endLocationStatus") != null) {
                                endInfoBlock.setVisibility(View.VISIBLE);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                                String hm = dateFormat.format(attendanceModel.getSignInTime());
                                endAttendanceTimeTextView.setText(hm);
                                if (attendanceModel.getTimeStatus() == 1) {
                                    endAttendanceAddressTextView.setText(msg);
                                    if ((int)map.get("endLocationStatus")==1){
                                        startAttendanceAddressTextView.setText(mInfoModel.getAreaAddress() + mInfoModel.getLocation());
                                        remarkBtn.setVisibility(View.GONE);
                                        endStatusBtn.setImageResource(R.drawable.kaoqinz);
                                    }else {
                                        endAttendanceAddressTextView.setText(msg);
                                        //getAddress(mLatLng.latitude, mLatLng.longitude, endAttendanceAddressTextView);
                                        remarkBtn.setVisibility(View.VISIBLE);
                                        endStatusBtn.setImageResource(R.drawable.kaoqinyc);
                                    }

                                } else if (attendanceModel.getTimeStatus() == -1) {
                                    endAttendanceAddressTextView.setText(msg);
                                   // getAddress(mLatLng.latitude, mLatLng.longitude, endAttendanceAddressTextView);
                                    remarkBtn.setVisibility(View.VISIBLE);
                                    endStatusBtn.setImageResource(R.drawable.kaoqinyc);

                                }

                            }
                        }

                        System.out.println(baseModel);


                    }

                });
        addSubscription(subscription);
    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    @Override
    protected void permissionGrantedSuccess() {
        mPreferencesTool.setHomeFragmentPermission("HomeFragment",true);
    }

    @Override
    protected void permissionGrantedFail() {
        mPreferencesTool.setHomeFragmentPermission("HomeFragment",false);
                showTipsDialog();
    }
    /**
     * 关闭软键盘
     */
    private void closeInputManage(PopupWindow popupWindow){


        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }
}

