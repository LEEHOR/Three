package net.coahr.three3.three.Popupwindow.AlertDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.coahr.three3.three.R;

/**
 * Created by 李浩
 * 2018/4/11
 */
public class CommomDialog extends Dialog implements View.OnClickListener {
    private TextView titleTxt,contentTxt,UnWarningTxt,WarningTxt,deleteTxt,cancelTxt;
    private Context mContext;
    private String content;
    private String message;
    private OnCloseListener listener;
    private String positiveName;
    private String negativeName;
    private String title;
    private boolean showUnWarning;
    private boolean showWarning;
    private boolean outSide=false;
    public CommomDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public CommomDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
    }

    public CommomDialog( Context context, int themeResId, String content,boolean showUnWarning,boolean showWarning,String mesage, OnCloseListener listener) {
        super(context, themeResId);
        this.content = content;
        this.listener = listener;
        this.showUnWarning = showUnWarning;
        this.showWarning=showWarning;
        this.message=mesage;
    }

    public CommomDialog(Context context, int themeResId, String content,boolean outside, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
        this.listener = listener;
        this.outSide=outside;
    }


    protected CommomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public CommomDialog setTitle(String title){
        this.title = title;
        return this;
    }

    public CommomDialog setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }

    public CommomDialog setNegativeButton(String name){
        this.negativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_commom);
        setCanceledOnTouchOutside(outSide);
        initView();
    }

    private void initView(){
        titleTxt= findViewById(R.id.dialog_title);
        contentTxt=findViewById(R.id.dialog_content_shang);
        UnWarningTxt=findViewById(R.id.dialog_unWarningTxt);
        WarningTxt=findViewById(R.id.dialog_WarningTxt);
        deleteTxt=findViewById(R.id.dialog_delete);
        cancelTxt= findViewById(R.id.dialog_cancel);
        contentTxt.setText(content);
        deleteTxt.setOnClickListener(this);
        cancelTxt.setOnClickListener(this);
        if (showUnWarning){
            UnWarningTxt.setVisibility(View.VISIBLE);
            UnWarningTxt.setText(message);
        }
        if (showWarning){
            WarningTxt.setVisibility(View.VISIBLE);
            WarningTxt.setText(message);
        }
       if(!TextUtils.isEmpty(positiveName)){
            deleteTxt.setText(positiveName);
        }

        if(!TextUtils.isEmpty(negativeName)){
            cancelTxt.setText(negativeName);
        }

        if(!TextUtils.isEmpty(title)){
            titleTxt.setText(title);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_cancel:
                if(listener != null){
                    listener.onClick(this, false);
                }
                this.dismiss();
                break;
            case R.id.dialog_delete:
                if(listener != null){
                    listener.onClick(this, true);
                }
                break;
        }
    }

    public interface OnCloseListener{
        void onClick(Dialog dialog, boolean confirm);
    }
}
