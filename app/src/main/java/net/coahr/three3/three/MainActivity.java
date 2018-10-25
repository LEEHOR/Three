package net.coahr.three3.three;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import net.coahr.three3.three.Base.BaseActivity;
import net.coahr.three3.three.Base.BaseModel;
import net.coahr.three3.three.Base.BasePara;
import net.coahr.three3.three.DBbase.UsersDB;
import net.coahr.three3.three.Model.LoginModel;
import net.coahr.three3.three.Module.HomeFragment;
import net.coahr.three3.three.Module.SettingFragment;
import net.coahr.three3.three.Module.UploadFragment;
import net.coahr.three3.three.Module.VerifyFragment;
import net.coahr.three3.three.NetWork.RetrofitManager;
import net.coahr.three3.three.Util.JDBC.DataBaseWork;
import net.coahr.three3.three.Util.OtherUtils.ToastUtils;
import net.coahr.three3.three.Util.Preferences.PreferencesTool;
import net.coahr.three3.three.customView.LoginDialog;
import net.coahr.three3.three.customView.Tarbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    private Tarbar mTarbar;
    private FragmentTransaction fragmentTransaction;
    private Fragment homeFragment,uploadFragment,verifyFragment,settingFragment;
    private PreferencesTool mPreferencesTool;
   private FragmentManager manager;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findUI();
        mTarbar.setItemClickEven(mTarbar);
        mPreferencesTool = new PreferencesTool(MainActivity.this);
        manager=getSupportFragmentManager();
        if (mPreferencesTool.getSessionId("sessionId")!=null){
            homeFragment = HomeFragment.getInstance(false);
            manager.beginTransaction().add(R.id.fg_container, homeFragment, "home").commit();
        }
        setTarbarListenner();
        final View decorView = getWindow().getDecorView();
        show(decorView);

    }

    public void show(final View decorView)
    {
        decorView.post(new Runnable() {
            @Override
            public void run() {


                String sessionId = mPreferencesTool.getSessionId("sessionId");
                if (sessionId == null)
                {
                   new LoginDialog(MainActivity.this,R.style.loginDialog,true,false, new LoginDialog.LoginInterFace() {
                       @Override
                       public void loginBtn(String account, String pass,Dialog dialog) {
                           if (account !=null && pass !=null){

                               RequestIntenet(account,pass,dialog);
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
        });
    }
    @Override
    public void findUI() {
        super.findUI();
        mTarbar = findViewById(R.id.tarbar);
    }



    void setTarbarListenner()
    {
        mTarbar.setListenner(new Tarbar.changeItem() {
            @Override
            public void changeItem(int index) {
                String name = mPreferencesTool.getUserName("name");
                if (name != null) {
                    fragmentTransaction =getSupportFragmentManager().beginTransaction();
                    if (homeFragment != null)
                        fragmentTransaction.hide(homeFragment);
                    if (uploadFragment != null)
                        fragmentTransaction.hide(uploadFragment);
                    if (verifyFragment != null)
                        fragmentTransaction.hide(verifyFragment);
                    if (settingFragment != null)
                        fragmentTransaction.hide(settingFragment);
                    switch (index) {
                        case 0:
                            if (homeFragment == null) {
                                homeFragment = HomeFragment.getInstance(false);

                                fragmentTransaction.add(R.id.fg_container, homeFragment);

                            } else {
                                fragmentTransaction.show(homeFragment);
                            }
                            break;
                        case 1:
                            if (uploadFragment == null) {
                                uploadFragment = new UploadFragment();
                                fragmentTransaction.add(R.id.fg_container, uploadFragment);
                            } else {
                                fragmentTransaction.show(uploadFragment);
                            }
                            break;
                        case 2:
                            if (verifyFragment == null) {
                                verifyFragment = new VerifyFragment(MainActivity.this);
                                fragmentTransaction.add(R.id.fg_container, verifyFragment);
                            } else {
                                fragmentTransaction.show(verifyFragment);
                            }
                            break;
                        case 3:
                            if (settingFragment == null) {
                                settingFragment = new SettingFragment();
                                fragmentTransaction.add(R.id.fg_container, settingFragment);
                            } else {
                                fragmentTransaction.show(settingFragment);
                            }
                            break;
                    }
                    fragmentTransaction.commit();
                } else {
                   // ToastUtils.showLong(MainActivity.this,"请登陆");
                    ToastUtils.Toast_showImage(MainActivity.this,"请登录",0, Toast.LENGTH_LONG);
                }
            }
        });
    }

  /*登陆*/
    public void RequestIntenet(String Account, String Password, final Dialog dialog){
        Map<String, Object> map = new HashMap<>();
        map.put("username", Account);
        map.put("password",Password);
        BasePara para = new BasePara();
        para.setData(map);
        Subscription subscription= RetrofitManager.getInstance()
                .createService(para)
                .getLogin(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseModel<LoginModel>>() {
                               @Override
                               public void onCompleted() {
                                  // ToastUtils.showLong(MainActivity.this,"登陆成功");
                               }

                               @Override
                               public void onError(Throwable e) {

                                  ToastUtils.showLong(MainActivity.this,"无法连接到网络"+e);
                                   closeInputManage(dialog);
                               }

                               @Override
                               public void onNext(BaseModel baseModel) {
                                  System.out.print(baseModel.getData());
                                  if (baseModel != null) {


                                      if (baseModel.getResult().equals("0")){
                                          ToastUtils.showShort(MainActivity.this,"账户或密码错误");
                                      }
                                      if (baseModel.getResult().equals("1")){
                                          ToastUtils.showShort(MainActivity.this,baseModel.getMsg()+"1");

                                      LoginModel loginModel = (LoginModel) baseModel.getData();
                                      if (loginModel!=null){
                                      String sessionId = loginModel.getSessionId();
                                      String name = loginModel.getName();
                                      mPreferencesTool.setUserName("name", name);
                                      mPreferencesTool.setSessionId("sessionId",sessionId );
                                      UsersDB usersDB=new UsersDB();
                                      List<UsersDB> usersDBS = DataBaseWork.DBSelectBy_Where(UsersDB.class, new String[]{"sessionId","userName"}, "sessionId=?", sessionId);
                                      if (usersDBS.size()>0&&!usersDBS.isEmpty()&&usersDBS!=null){

                                          String userName = usersDBS.get(0).getUserName();
                                          if (userName.equals(name)){

                                          }else {
                                              usersDB.setUserName(name);
                                              int update = usersDB.update(usersDBS.get(0).getId());
                                              if(update>0){
                                              }
                                          }
                                      }else {
                                          usersDB.setUserName(name);
                                          usersDB.setSessionId(sessionId);
                                          boolean save = usersDB.save();
                                          if (save){
                                          }
                                      }
                                      homeFragment = HomeFragment.getInstance(true);
                                     // homeFragment=new HomeFragment();
                                      manager.beginTransaction().add(R.id.fg_container, homeFragment, "home").commit();
                                      //mWindow.dismiss();
                                          closeInputManage(dialog);
                                      dialog.dismiss();

                                  }
                                  }

                               }

                               }
                           });
                        addSubscription(subscription);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
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

    /**
     * 关闭软键盘
     */
    private void closeInputManage(Dialog dialog){

        View view =dialog.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) this. getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
