package net.coahr.three3.three.Module;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.alexswitch.ISwitch;
import com.andsync.xpermission.XPermissionUtils;

import net.coahr.three3.three.Base.BaseCheckPermissionFragment;
import net.coahr.three3.three.Base.BaseModel;
import net.coahr.three3.three.Base.BasePara;
import net.coahr.three3.three.DBbase.ProjectsDB;
import net.coahr.three3.three.DBbase.UsersDB;
import net.coahr.three3.three.Model.LoginModel;
import net.coahr.three3.three.NetWork.RetrofitManager;
import net.coahr.three3.three.Popupwindow.AlertDialogs.CommomDialog;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Setting.FeedBackActivity;
import net.coahr.three3.three.Setting.MotifyPasswordActivity;
import net.coahr.three3.three.Setting.UploadSettingActivity;
import net.coahr.three3.three.Util.GlideCache.GlideCacheUtil;
import net.coahr.three3.three.Util.JDBC.DataBaseWork;
import net.coahr.three3.three.Util.OtherUtils.FileUtils;
import net.coahr.three3.three.Util.OtherUtils.ToastUtils;
import net.coahr.three3.three.Util.Preferences.PreferencesTool;
import net.coahr.three3.three.customView.LoginDialog;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yuwei on 2018/4/3.
 */

public class SettingFragment extends BaseCheckPermissionFragment {

    private Button mLoginBtn;
    private TextView cacheTextView, saveSpaceTextView;
    private boolean testFlag = false ;
    private PreferencesTool mPreferencesTool;
    private RelativeLayout mUpdateSetting, mSaveSpace, mCleanSpace, mMotifyPassword, mFeedBack;
    private ISwitch mISwitch;
    private TextView userNameLabel;
    private  String sessionId;
    private TextView download,start,upload,complete;
    private  File takePhotos , file_pcm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mPreferencesTool=new PreferencesTool(getActivity());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findUI(view);

    }

    public void findUI(View view) {
        mPreferencesTool = new PreferencesTool(getActivity());
        sessionId = mPreferencesTool.getSessionId("sessionId");
        cacheTextView = view.findViewById(R.id.cache);
        userNameLabel = view.findViewById(R.id.userName);
        start=  view.findViewById(R.id.start);
        download=  view.findViewById(R.id.download);
        upload=  view.findViewById(R.id.upload);
        complete=  view.findViewById(R.id.complete);
        saveSpaceTextView = view.findViewById(R.id.saveSpaceText);
        saveSpaceTextView.setText(readSDCard());

        featchProjectInfo(start,download,upload,complete);
        userNameLabel.setText(sessionId != null ? mPreferencesTool.getUserName("name"):"");
        mLoginBtn = view.findViewById(R.id.loginBtn);
        mLoginBtn.setText(sessionId != null ? "退出登录" : "请登录");
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPreferencesTool.getSessionId("sessionId")!= null) {
                    mPreferencesTool.setSessionId("sessionId" , null);
        mPreferencesTool.setUserName("name" , null);
        mLoginBtn.setText("请登录");
        userNameLabel.setText("");
        //  final View decorView = getActivity().getWindow().getDecorView();
        show();
    }/* else {
                    login();
                }*/


}
        });

                mUpdateSetting = view.findViewById(R.id.updateSetting);
                mSaveSpace = view.findViewById(R.id.saveSpace);
        mCleanSpace = view.findViewById(R.id.cleanSpace);
        mMotifyPassword = view.findViewById(R.id.motifyPassword);
        mFeedBack = view.findViewById(R.id.feedback);
        mISwitch = view.findViewById(R.id.switchBtn);

        mUpdateSetting.setOnClickListener(new SettingClickListenner());
        mSaveSpace.setOnClickListener(new SettingClickListenner());
        mCleanSpace.setOnClickListener(new SettingClickListenner());
        mMotifyPassword.setOnClickListener(new SettingClickListenner());
        mFeedBack.setOnClickListener(new SettingClickListenner());
        mISwitch.setOnISwitchOnClickListener(new ISwitch.ISwitchOnClickListeners() {


            @Override
            public void open() {
                Toast.makeText(getActivity(), "Open", Toast.LENGTH_SHORT).show();
                mPreferencesTool.setBrowseMethod("browseMethod", true);
            }

            @Override
            public void close() {
                Toast.makeText(getActivity(), "close", Toast.LENGTH_SHORT).show();

                mPreferencesTool.setBrowseMethod("browseMethod", false);
            }
        });

        //mPreferencesTool.getHomeFragmentPermission("HomeFragment");
       if ( mPreferencesTool.getHomeFragmentPermission("HomeFragment")){
           setCacheTextView();
       }else {
           getPermission();
       }
    }

    /**
     * 登陆
     */
    public void login() {
        new LoginDialog(getActivity(), R.style.loginDialog, false,true, new LoginDialog.LoginInterFace() {
            @Override
            public void loginBtn(String account, String pass, Dialog dialog) {
                if (account !=null && pass !=null){
                    LoginRequest(account,pass,dialog);
                }
            }

            @Override
            public void onKeyDown(Dialog dialog) {
                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }
            }
        }).show();

    }

    /**
     * 登陆
     */
    public void show()
    {
                String sessionId = mPreferencesTool.getSessionId("sessionId");
                if (sessionId == null)
                {
                    new LoginDialog(getActivity(),R.style.loginDialog,true,false, new LoginDialog.LoginInterFace() {
                        @Override
                        public void loginBtn(String account, String pass,Dialog dialog) {
                            if (account !=null && pass !=null){

                                LoginRequest(account,pass,dialog);
                            }
                        }

                        @Override
                        public void onKeyDown(Dialog dialog) {
                            if (dialog.isShowing()){
                                // return;
                            }else {
                                dialog.show();
                                // return;
                            }
                        }
                    }).show();
                }
    }

    class SettingClickListenner implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = null;

            switch (v.getId()) {
                case R.id.updateSetting:
                    intent = new Intent(getActivity(), UploadSettingActivity.class);
                    break;

                case R.id.saveSpace:
                    return;
//                    break;

                case R.id.cleanSpace:
                    if (mPreferencesTool.getHomeFragmentPermission("HomeFragment")){
                    new CommomDialog(getActivity(), R.style.dialog, "是否立即清除", false, false, null, new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if(confirm){
                                                GlideCacheUtil.getInstance().clearCacheDiskSelf();
                                                //File bitPhotos = FileUtils.createFileDir(getActivity(), "");
                                                //String size = GlideCacheUtil.getInstance().getCacheSize();
                                               // File takePhotos = FileUtils.createFileDir(getActivity(), "takePhotos");
                                                deleteFile(takePhotos);
                                                deleteFile(file_pcm);
                                                double dirSize = getDirSize(takePhotos);
                                                double dirSize1 = getDirSize(file_pcm);
                                                DecimalFormat df = new DecimalFormat("#.00");
                                                String format = df.format((dirSize+dirSize1));
                                                System.out.println("--------------------" + format);
                                                cacheTextView.setText("0.0MB");
                            }


                            dialog.dismiss();
                        }
                    }).setTitle("").setPositiveButton("确定").show();
                    } else {
                        ToastUtils.showShort(getActivity(),"没有授权无法操作");
                    }
                    return;
//                    break;

                case R.id.motifyPassword:
                    intent = new Intent(getActivity(), MotifyPasswordActivity.class);
                    break;

                case R.id.feedback:
                    intent = new Intent(getActivity(), FeedBackActivity.class);
                    break;

            }
            startActivity(intent);

        }
    }

    public String readSDCard() {
        String state = Environment.getExternalStorageState();
        String storageSize;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSizeLong();
            long blockCount = sf.getBlockCountLong();
            long availCount = sf.getAvailableBlocksLong();

            storageSize = availCount * blockSize / 1024 / 1024 / 1024 + "GB" +"/"+ blockSize * blockCount / 1024 / 1024 / 1024 + "GB";
            return  storageSize;
        }

        return "";

    }

    private void LoginRequest(String accountText, String  passwordText, final Dialog dialog)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("username", accountText);
        map.put("password",passwordText);
        BasePara para = new BasePara();
        para.setData(map);
        Subscription subscription= RetrofitManager.getInstance()
                .createService(para)
                .getLogin(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showLong(getActivity(),"网络连接失败"+e);
                        closeInputManage(dialog);
                    }

                    @Override
                    public void onNext(BaseModel baseModel) {
                       // System.out.print(baseModel.getData());
                        if (baseModel != null)
                        {
                            if (baseModel.getResult().equals("0")){
                                ToastUtils.showShort(getActivity(),"账户或密码错误");
                            }
                            if (baseModel.getResult().equals("1")){
                                ToastUtils.showShort(getActivity(),"登陆成功");
                                 LoginModel loginModel = (LoginModel) baseModel.getData();

                                    if (loginModel != null)
                                         {
                                        mPreferencesTool.setUserName("name" , loginModel.getName());
                                        mPreferencesTool.setSessionId("sessionId" , loginModel.getSessionId());
                                        mLoginBtn.setText("退出登录");
                                        userNameLabel.setText(mPreferencesTool.getUserName("name"));
                                             List<UsersDB> usersDBS = DataBaseWork.DBSelectByTogether_Where(UsersDB.class,  "sessionId=?", loginModel.getSessionId());
                                             if (usersDBS!=null&&usersDBS.size()>0&&!usersDBS.isEmpty()){

                                               usersDBS.get(0).setUserName(loginModel.getName());
                                               usersDBS.get(0).update(usersDBS.get(0).getId());
                                             }else {
                                                 UsersDB users=new UsersDB();
                                                 users.setUserName(loginModel.getName());
                                                 users.setSessionId(loginModel.getSessionId());
                                                 boolean save = users.save();

                                             }


                                             closeInputManage(dialog);
                                             dialog.dismiss();



                                         }

                            }

                        }



                    }
                });
        addSubscription(subscription);
    }

    public void featchProjectInfo(TextView star,TextView download,TextView upload,TextView complete)
    {

        int unLoad=0,unStart=0,unUpload=0,unComplete=0;
        List<UsersDB> usersDBS = DataBaseWork.DBSelectByTogether_Where(UsersDB.class, "sessionid=?", sessionId);
        if (usersDBS!=null && usersDBS.size()>0){
            List<ProjectsDB> projectsDBSList = usersDBS.get(0).getProjectsDBSList();
            if (projectsDBSList !=null && projectsDBSList.size()>0){
                for (int i = 0; i <projectsDBSList.size() ; i++) {
                    if (projectsDBSList.get(i).getDownloadTime()== -1){
                        unLoad++;
                        download.setText(String.valueOf(unLoad));
                    }
                    if (projectsDBSList.get(i).getpUploadStatus()==0){
                        unUpload++;
                        upload.setText(String.valueOf(unUpload));
                    }
                    if (projectsDBSList.get(i).getIsComplete()==0){
                        unComplete++;
                        complete.setText(String.valueOf(unComplete));
                    }
                    if (projectsDBSList.get(i).getSubjectsDBList()==null ){
                        unStart++;
                        star.setText(String.valueOf(unStart));
                    }
                }
            }
        }
    }
    /*获取缓存文件大小*/
    public static double getDirSize(File file) {
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {//如果是文件则直接返回其大小,以“兆”为单位
                double size = (double) file.length() / 1024 / 1024;

                return size;
            }
        } else {
            return 0.0;
        }
    }

    /*删除缓存文件*/
    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
           // file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }

    private  void  setCacheTextView(){
        String size = GlideCacheUtil.getInstance().getCacheSize();
        takePhotos = FileUtils.createFileDir(getActivity(), "takePhotos");
        String fileBasePath =FileUtils.createFileDir(getActivity(),"pauseRecordDemo").getAbsolutePath()+"/pcm/";
        file_pcm = new File(fileBasePath);
        double dirSize = getDirSize(file_pcm);
        double totalSize = getDirSize(takePhotos);
        //System.out.println("--------------------" + size);
        DecimalFormat df = new DecimalFormat("#.00");
        String format = df.format((totalSize+dirSize));
        BigDecimal value = new BigDecimal((totalSize+dirSize)).divide(new BigDecimal(1000)).setScale(2,BigDecimal.ROUND_HALF_UP);
        if(format.equals(".00")){
            cacheTextView.setText("0.0MB");
        }else {
            cacheTextView.setText(String.valueOf(value)+"MB");
        }
    }
    private void  getPermission(){
        if (Build.VERSION.SDK_INT>=23){
        XPermissionUtils.requestPermissions(getActivity(), 100, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, new XPermissionUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted() {
                mPreferencesTool.setHomeFragmentPermission("HomeFragment",true);
                setCacheTextView();
            }

            @Override
            public void onPermissionDenied(final String[] deniedPermissions, boolean alwaysDenied) {
                mPreferencesTool.setHomeFragmentPermission("HomeFragment",false);
                if (alwaysDenied){

                }else {
                    new AlertDialog.Builder(getActivity()).setTitle("温馨提示")
                            .setMessage("我们需要读写权限才能正常使用该功能")
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
            setCacheTextView();
        }
    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
    }

    @Override
    protected void permissionGrantedSuccess() {
        mPreferencesTool.setHomeFragmentPermission("HomeFragment",true);
        setCacheTextView();
    }

    @Override
    protected void permissionGrantedFail() {
        cacheTextView.setText("没有授权无法读取");
        showTipsDialog();
        mPreferencesTool.setHomeFragmentPermission("HomeFragment",false);
       // isDelete=true;
    }

    /**
     * 关闭软键盘
     */
    private void closeInputManage(Dialog dialog){
        View view = dialog.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
