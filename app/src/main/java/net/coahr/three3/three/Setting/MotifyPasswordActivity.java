package net.coahr.three3.three.Setting;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.coahr.three3.three.Base.BaseActivity;
import net.coahr.three3.three.Base.BaseModel;
import net.coahr.three3.three.Base.BasePara;
import net.coahr.three3.three.MainActivity;
import net.coahr.three3.three.NetWork.RetrofitManager;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.OtherUtils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yuwei on 2018/4/26.
 */

public class MotifyPasswordActivity extends BaseActivity {
    private EditText originPassword;
    private EditText newPassword;
    private EditText terminatePassword;

    private String originText;
    private String newText;
    private String terminateText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motifypassword);
        findUI();
    }


    @Override
    public void findUI() {
        super.findUI();
        configureNaviBar(naviBar.findViewById(R.id.left) , naviBar.findViewById(R.id.right));
        setTitle((TextView) naviBar.findViewById(R.id.title), "修改密码");
        getRightBtn().setText("保存");
        getRightBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("origin:"+originText+"new:"+newText+"terminate"+terminateText);

                if (newText!=null&&terminateText!=null)
                {
                    if (!newText.equals(terminateText))
                    {
                        ToastUtils.showLong(MotifyPasswordActivity.this,"密码不一致");
                        return;
                    }
                }

                if (originText!=null&&originText.length() > 0 && newText!=null&&newText.length() > 0 &&terminateText!=null
                && terminateText.length() > 0)
                {
                    motifyPassword(originText,newText);
                }


            }
        });


        originPassword = findViewById(R.id.originText);
        newPassword = findViewById(R.id.newText);
        terminatePassword = findViewById(R.id.terminateText);


        originPassword.addTextChangedListener(new PasswordTextWatch(R.id.originText));
        newPassword.addTextChangedListener(new PasswordTextWatch(R.id.newText));
        terminatePassword.addTextChangedListener(new PasswordTextWatch(R.id.terminateText));


    }


    class PasswordTextWatch implements TextWatcher
    {
        private int resId;
        public PasswordTextWatch(@IdRes int id)
        {
            this.resId = id;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {


            switch (this.resId)
            {
                case R.id.originText:

                    originText = s.toString();

                    break;

                case R.id.newText:
                    newText = s.toString();
                    break;

                case R.id.terminateText:
                    terminateText = s.toString();
                    break;
            }




        }
    }


    public void motifyPassword(String oldPassword,String newPassword)
    {

        Map<String, Object> map = new HashMap<>();
        map.put("sessionId", mPreferencesTool.getSessionId("sessionId"));
        map.put("oldPassword",oldPassword);
        map.put("newPassword" ,newPassword);
        BasePara para = new BasePara();
        para.setData(map);
        Subscription subscription= RetrofitManager.getInstance()
                .createService(para)
                .motifyPassword(map)
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

                        if (model.getResult().equals("1"))
                        {
                            ToastUtils.showShort(MotifyPasswordActivity.this , "修改成功");

                        }
                        else
                        {
                            ToastUtils.showShort(MotifyPasswordActivity.this , "修改失败");
                        }
                        finish();

                    }
                });
        addSubscription(subscription);

    }



}
