package net.coahr.three3.three.Base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andsync.xpermission.XPermissionUtils;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MyLocationConfiguration;

import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseRecyclerViewAdapter;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.Preferences.PreferencesTool;

import org.litepal.tablemanager.Connector;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BaseActivity extends AppCompatActivity {
    private Button rightBtn;
    private TextView        titleView;//naviBar title
    private ImageView          leftBtn  ;
    public  Bundle          parameter; //activity透传参数
    private Intent          mIntent;//跳转activity
    public  CompositeSubscription mCompositeSubscription; //解除订阅, RX
    public  BaseRecyclerViewAdapter adapter;
    public  View            naviBar;
    private BDLocation  mLocation;
    private MyLocationConfiguration.LocationMode mLocationMode;
    public PreferencesTool mPreferencesTool;

    public void setmPreferencesTool(PreferencesTool mPreferencesTool) {
        this.mPreferencesTool = mPreferencesTool;
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();


    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       Connector.getDatabase();
        mPreferencesTool=new PreferencesTool(this);
    }

    public void findUI()
    {
        naviBar     = findViewById(R.id.naviBar);
        mLocationMode = MyLocationConfiguration.LocationMode.COMPASS;

    }
    //配置naviBar按钮
    public void configureNaviBar(View leftBtn , View rightBtn)
    {
        if (leftBtn != null)
            setLeftBtn((ImageView) leftBtn);
        if (rightBtn != null)
            setRightBtn((Button) rightBtn);
    }

    public void setLeftBtn(ImageView leftBtn) {
        this.leftBtn = leftBtn;
        this.leftBtn.setOnClickListener(new NaviBarItemOnClick());
    }

    public ImageView getLeftBtn() {
        return leftBtn;
    }

    public void setRightBtn(Button rightBtn) {
        this.rightBtn = rightBtn;
        this.rightBtn.setOnClickListener(new NaviBarItemOnClick());
        this.rightBtn.setVisibility(View.VISIBLE);
    }

    public Button getRightBtn() {
        return rightBtn;
    }

    public void setTitle(TextView title , String text) {
        this.titleView = title;
        this.titleView.setText(text);
    }

    public TextView getTitleView() {
        return titleView;
    }

    //recyclerView Item点击事件
    public void setAdapter(BaseRecyclerViewAdapter adapter)
    {
        this.adapter = adapter;
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListner() {
            @Override
            public void onItemClickListener(View v, int position , Intent intent ,Context context) {
                Toast.makeText(context , "点击了"+position , Toast.LENGTH_LONG).show();
                if (intent != null || mIntent != null)
                    startActivity(intent);
            }
        });
    }

    public int getWindowWidth() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;
        int height = size.y;
        return width;
    }

    public int getWindowHeight() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;
        int height = size.y;
        return height;
    }

    public BaseRecyclerViewAdapter getAdapter() {
        return adapter;
    }

    public Intent getmIntent() {
        return mIntent;
    }

    public void setmIntent(Intent mIntent) {
        this.mIntent = mIntent;
    }

    public BDLocation getLocation(){
        return mLocation;
    }

    public void setmLocation(BDLocation mLocation) {
        this.mLocation = mLocation;
    }
    public void pushActivity(Intent intent)
    {
        if (parameter != null && intent != null)
        {
            intent.putExtras(parameter);
            startActivity(intent);
            return;
        }
        else if(intent != null)
            startActivity(intent);
    }

    protected void requestRemote() {

    }

//导航条item left默认返回, right默认跳转下一个activity
    public void naviItemClickEven(View v)
    {
        switch (v.getId())
        {
            case R.id.left:

                finish();
                break;
            case R.id.right:

                pushActivity(mIntent);
                break;
        }
    }

    class NaviBarItemOnClick implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {

            naviItemClickEven(v);
        }
    }

    protected void addSubscription(Subscription s)
    {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
    }

    /*权限回调*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        XPermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

  /*  *//**
     * 动态获取权限
     *//*
    private void getPermission(){
        if(Build.VERSION.SDK_INT>=23){
            XPermissionUtils.requestPermissions(BaseActivity.this, 100, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE}, new XPermissionUtils.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                   // getDingwei();
                }

                @Override
                public void onPermissionDenied(final String[] deniedPermissions, boolean alwaysDenied) {
                    if (alwaysDenied) { // 拒绝后不再询问 -> 提示跳转到设置
                        // DialogUtil.showPermissionManagerDialog(MainActivity.this, "相机");
                    } else {    // 拒绝 -> 提示此公告的意义，并可再次尝试获取权限
                        new AlertDialog.Builder(BaseActivity.this).setTitle("温馨提示")
                                .setMessage("我们需要定位权限才能正常使用该功能")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("验证权限", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        XPermissionUtils.requestPermissionsAgain(BaseActivity.this, deniedPermissions, 100);
                                    }
                                }).show();
                    }

                }
            });
        }else {

        }
    }*/

}
