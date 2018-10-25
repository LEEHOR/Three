package net.coahr.three3.three.Popupwindow.AlertDialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import net.coahr.three3.three.Base.BaseTools;
import net.coahr.three3.three.Project.ProjectInfoActivity;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Verify.BrowseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProjectDialogLast extends AlertDialog implements View.OnClickListener {
    private OnProjectCloseListener closeListener;
    private TextView Ttime,Tuser,Tstatus,TupStatus;
    private Button Btn;
    private long startTime;
    private String userName,visitStatus,upStatus;
    private boolean isBrowsers;
    private Context context;
    public ProjectDialogLast(@NonNull Context context) {
        super(context);
    }

    public ProjectDialogLast(@NonNull Context context,long startTime,boolean isBrowser,String userName,String  visitStatus,String upStatues ,OnProjectCloseListener listener) {
        super(context);
        this.context=context;
        this.closeListener=listener;
        this.startTime=startTime;
        this.userName=userName;
        this.visitStatus=visitStatus;
        this.upStatus=upStatues;
        this.isBrowsers=isBrowser;
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }



    public interface OnProjectCloseListener{
        void onClick(Dialog dialog, boolean isBrowser);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.projectlast_dialog);
        int windowWidth = BaseTools.getWindowWidth(context);
        //int windowHeigh = BaseTools.getWindowHeigh(context);
        getWindow().setBackgroundDrawableResource(R.drawable.bg_projectdialog_white);
        getWindow().setLayout(windowWidth-50, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(true);
        intView();
    }
    private void intView(){
         Ttime= findViewById(R.id.project_dialogtime);
         Tuser= findViewById(R.id.project_dialoguser);
         Tstatus= findViewById(R.id.project_dialogstatus);
         TupStatus=findViewById(R.id.project_dialogupstatus);
            Date s=new Date(startTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startTime = sdf.format(s);
            String endTime = sdf.format(new Date());
        Ttime.setText("访问时间:"+startTime+"～"+endTime);
        Tuser.setText("访问人员："+userName);
        Tstatus.setText("访问状态：访问完成"+"("+visitStatus+")");
        TupStatus.setText("上传状态:"+upStatus);
         Btn=findViewById(R.id.project_dialogbtn);
         Btn.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
           if (v.getId()==R.id.project_dialogbtn){
               closeListener.onClick(this,isBrowsers);
           }
    }
}
