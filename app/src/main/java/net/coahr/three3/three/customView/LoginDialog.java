package net.coahr.three3.three.customView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import net.coahr.three3.three.Base.BaseTools;
import net.coahr.three3.three.MainActivity;
import net.coahr.three3.three.R;

import java.util.Objects;

public class LoginDialog extends Dialog implements View.OnClickListener {
    private EditText accountText , passwordText;
    private Button loginBtn;
    private LoginInterFace interFace;
    private Context mContext;
    private boolean isFull,OnTouchOutside;
    public LoginDialog(Context context,LoginInterFace loginInterFace) {
        super(context);
        this.mContext=context;
        this.interFace=loginInterFace;
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public LoginDialog(@NonNull Context context, int themeResId,boolean isFull,boolean OnTouchOutside,LoginInterFace loginInterFace) {
        super(context, themeResId);
        this.mContext=context;
        this.interFace=loginInterFace;
        this.isFull=isFull;
        this.OnTouchOutside=OnTouchOutside;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        int windowWidth = BaseTools.getWindowWidth(mContext);
        //int windowHeigh = BaseTools.getWindowHeigh(mContext);

        if (isFull){
            Drawable drawable=getContext().getResources().getDrawable(R.drawable.login_background);
            getWindow().setBackgroundDrawable(drawable);
            getWindow().setLayout(windowWidth, ViewGroup.LayoutParams.MATCH_PARENT);// ViewGroup.LayoutParams.WRAP_CONTENT
        }else {
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);// ViewGroup.LayoutParams.WRAP_CONTENT
        }

        getWindow().setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(OnTouchOutside);
        accountText = findViewById(R.id.user);
        passwordText = findViewById(R.id.password);
       // accountText.setOnClickListener(this);
        //passwordText.setOnClickListener(this);
        accountText.addTextChangedListener(new TextChangeListenner());
        passwordText.addTextChangedListener(new TextChangeListenner());
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
    }



    class TextChangeListenner implements TextWatcher
    {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (!accountText.getText().toString().isEmpty()&&!passwordText.getText().toString().isEmpty())
            {
                loginBtn.setBackgroundResource(R.color.colorAccent);
                loginBtn.setEnabled(true);
            }
            else
            {
                loginBtn.setBackgroundResource(R.color.colorGary);
                loginBtn.setEnabled(false);
            }
        }
    }
    //登录
   /* class LoginBtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {


        }
    }*/
        public interface LoginInterFace{
           void loginBtn(String account,String pass,Dialog dialog);
           void onKeyDown(Dialog dialog);
      }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            interFace.onKeyDown(this);
                return true;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
            if (v.getId()==R.id.loginBtn){
                String account = accountText.getText().toString();
                String pass = passwordText.getText().toString();
                interFace.loginBtn(account,pass,this);
            }
    }
}
