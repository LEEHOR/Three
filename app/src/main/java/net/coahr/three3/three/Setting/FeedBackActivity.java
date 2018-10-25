package net.coahr.three3.three.Setting;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import net.coahr.three3.three.Base.BaseActivity;
import net.coahr.three3.three.Base.BaseModel;
import net.coahr.three3.three.Base.BasePara;
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
 * Created by yuwei on 2018/4/27.
 */

public class FeedBackActivity extends BaseActivity {
    private  String feedBackContent;
    private EditText mEditText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        findUI();
    }


    @Override
    public void findUI() {
        super.findUI();
        mEditText = findViewById(R.id.editText);
        configureNaviBar(naviBar.findViewById(R.id.left) , naviBar.findViewById(R.id.right));
        setTitle((TextView) naviBar.findViewById(R.id.title), "帮助与反馈");
        getRightBtn().setText("提交");
        getRightBtn().setTextSize(10);
        getRightBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(feedBackContent);

                if (feedBackContent!=null && feedBackContent.length()>0)
                {
                    feedBack();
                }


            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                feedBackContent = s.toString();
            }
        });


    }


    public void feedBack()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("sessionId", mPreferencesTool.getSessionId("sessionId"));
        map.put("content",feedBackContent);
        BasePara para = new BasePara();
        para.setData(map);
        Subscription subscription= RetrofitManager.getInstance()
                .createService(para)
                .feedBack(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        closeInputManage();
                    }

                    @Override
                    public void onNext(BaseModel model) {

                        if (model.getResult().equals("1"))
                        {
                            ToastUtils.showShort(FeedBackActivity.this , "发送成功");
                        }
                        else
                        {
                            ToastUtils.showShort(FeedBackActivity.this ,model.getMsg());
                        }
                        closeInputManage();
                        finish();

                    }
                });
    }

    /**
     * 关闭软键盘
     */
    private void closeInputManage(){
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            if (this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

}
