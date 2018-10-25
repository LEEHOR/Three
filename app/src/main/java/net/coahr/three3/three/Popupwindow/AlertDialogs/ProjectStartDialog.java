package net.coahr.three3.three.Popupwindow.AlertDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.TextView;

import net.coahr.three3.three.R;

public class ProjectStartDialog extends AlertDialog implements View.OnClickListener{
    private RadioButton Rq,Rd;
    private TextView Tq,Tc;
    private OnStartCloseListener closeListener;
    private int status=0;
    public ProjectStartDialog(Context context, OnStartCloseListener closeListener) {
        super(context);
        this.closeListener=closeListener;
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public ProjectStartDialog(Context context) {
        super(context);
    }

    public ProjectStartDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_projectstart_recorderselect_dialog);
        initView();
    }
    private void initView(){
       Rq = findViewById(R.id.project_start_Rq);
       Rd=findViewById(R.id.project_start_Rd);
       Tq= findViewById(R.id.project_start_q);
       Tc=findViewById(R.id.project_start_c);
       Rq.setOnClickListener(this);
       Rd.setOnClickListener(this);
       Tq.setOnClickListener(this);
       Tc.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
           case R.id.project_start_Rq:
                status=3;
                break;
            case R.id.project_start_Rd:
                status=2;
                break;
            case R.id.project_start_q:
                if (closeListener!=null){
                    closeListener.onClick(this,true,status);
                }
                break;
            case R.id.project_start_c:
                if (closeListener!=null){
                    closeListener.onClick(this,false,status);
                }
                break;
        }
    }
    public interface OnStartCloseListener{
        void onClick(Dialog dialog, boolean confirm,int status);
    }
}
