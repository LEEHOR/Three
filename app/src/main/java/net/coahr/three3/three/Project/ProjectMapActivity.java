package net.coahr.three3.three.Project;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.andsync.xpermission.XPermissionUtils;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import net.coahr.three3.three.Base.BaseActivity;
import net.coahr.three3.three.Base.LocationApplication;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.BaiduSDk.BDLocationUtils;
import net.coahr.three3.three.Util.BaiduSDk.BaiDuSdkListener;

import overlayutil.DrivingRouteOverlay;

/**
 * Created by yuwei on 2018/5/7.
 */

public class ProjectMapActivity extends BaseActivity {
    public double longitude,latitude;
    private   BDLocationUtils bdLocationUtils;
    private   LocationApplication application;
    private MapView mMapView;
    private BaiduMap    mBaiduMap;
    private MyLocationConfiguration.LocationMode mLocationMode;
    private BitmapDescriptor mCurrentMarker;

    private RoutePlanSearch mRoutePlanSearch;
    private Button      mRouthBtn , mBackBtn;
    private BDLocation mLocation;
    private LatLng destinationLocation , currentLocation;
    private boolean isFirstLocate = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_map);
        double lat = getIntent().getDoubleExtra("lat", 0.0);
        double lng = getIntent().getDoubleExtra("lng", 0.0);
        destinationLocation = new LatLng(lat , lng);
        System.out.println(getLocation());
        findUI();

    }
    @Override
    public void findUI() {
        super.findUI();
        mRouthBtn = findViewById(R.id.routh);
        mBackBtn = findViewById(R.id.back);
        mMapView = findViewById(R.id.baiduMap);

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mLocationMode = MyLocationConfiguration.LocationMode.COMPASS;
        getPermission();
      /*  mLocationClient  = new LocationClient(this);

        mLocationClient.registerLocationListener(new NotiftLocationListener());*/



        mRoutePlanSearch = RoutePlanSearch.newInstance();
        mRoutePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                if(drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR)
                if (drivingRouteResult.getRouteLines().size()>0)
                {
                    for ( int i = 0 ; i < drivingRouteResult.getRouteLines().size();i++)
                    {
                        DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
                        overlay.setData(drivingRouteResult.getRouteLines().get(i));
                        overlay.addToMap();
                        overlay.zoomToSpan();
                    }

                }

            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        });



        mBackBtn.setOnClickListener(new ButtonClickListenner());


    }


    @Override
    protected void onStart() {
        super.onStart();
      //  mLocationClient.start();

    }


    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        application.  bdLocationUtils.mLocationClient.stop();
        application. bdLocationUtils.unRegisterLocationListener();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        application. bdLocationUtils.mLocationClient.stop();
        application. bdLocationUtils.unRegisterLocationListener();
        mRoutePlanSearch.destroy();
        mMapView.onDestroy();

    }
    /*将当前位置显示在地图上*/
    private void navigateTo(BDLocation location){
        if(isFirstLocate){
//            /*获取经纬度*/
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.setMapStatus(update);
            update=MapStatusUpdateFactory.zoomTo(16f);
            mBaiduMap.setMapStatus(update);


        /*获取当前位置 并显示到地图上*/
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        mBaiduMap.setMyLocationData(locationData);
        isFirstLocate = false;
        }
    }

    class ButtonClickListenner implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {

            switch (v.getId())
            {
                case R.id.back:
                    finish();
                    break;

                case R.id.routh:


                    MyLocationData locationData = new MyLocationData.Builder()
                            .accuracy(mLocation.getRadius())
                            .direction(100)
                            .latitude(mLocation.getLatitude())
                            .longitude(mLocation.getLongitude()).build();

                    mBaiduMap.setMyLocationData(locationData);

                    MyLocationConfiguration configuration = new MyLocationConfiguration(mLocationMode , true , mCurrentMarker);
                    mBaiduMap.setMyLocationConfiguration(configuration);

                    currentLocation = new LatLng(mLocation.getLatitude() , mLocation.getLongitude());

                    PlanNode stNode = PlanNode.withLocation(currentLocation);
                    PlanNode enNode = PlanNode.withLocation(destinationLocation);
                    mRoutePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(enNode));
                    break;
            }
        }


    }
    private void getDingwei(){
        application = LocationApplication.getInstance();
        application. bdLocationUtils.doLocation();//开启定位
        //开始定位
        application.  bdLocationUtils.mLocationClient.start();
        application. bdLocationUtils.setSdkListener(new BaiDuSdkListener() {
            @Override
            public void getLongLatAddress(Double Longitude, Double Latitude, String address,StringBuffer stringBuffer,BDLocation location) {
                latitude=Latitude;
                longitude=Longitude;
                mLocation=location;
                System.out.println(location.getProvince()+location.getCity()+location.getDistrict()+location.getStreet());
                /*显示当前位置地图*/
                navigateTo(location);
                if(location.getLocType() ==BDLocation.TypeGpsLocation
                        ||location.getLocType() == BDLocation.TypeNetWorkException){

                }
                if (location != null)
                {
                    mLocation = location;
                    mRouthBtn.setOnClickListener(new ButtonClickListenner());
                   // bdLocationUtils.mLocationClient.stop();
                }


            }

        });
    }
    /**
     * 动态获取权限
     */
    private void getPermission(){
        if(Build.VERSION.SDK_INT>=23){
            XPermissionUtils.requestPermissions(ProjectMapActivity.this, 100, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO}, new XPermissionUtils.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    getDingwei();
                }

                @Override
                public void onPermissionDenied(final String[] deniedPermissions, boolean alwaysDenied) {
                    if (alwaysDenied) { // 拒绝后不再询问 -> 提示跳转到设置
                        // DialogUtil.showPermissionManagerDialog(MainActivity.this, "相机");
                    } else {    // 拒绝 -> 提示此公告的意义，并可再次尝试获取权限
                        new AlertDialog.Builder(ProjectMapActivity.this).setTitle("温馨提示")
                                .setMessage("我们需要定位权限才能正常使用该功能")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("验证权限", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        XPermissionUtils.requestPermissionsAgain(ProjectMapActivity.this, deniedPermissions, 100);
                                    }
                                }).show();
                    }

                }
            });
        }else {
            getDingwei();
        }
    }
}
