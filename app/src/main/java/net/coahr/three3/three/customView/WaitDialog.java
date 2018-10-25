package net.coahr.three3.three.customView;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.coahr.three3.three.R;
import net.coahr.three3.three.Verify.VerifyReviseActivity;

public class WaitDialog {

    public static Dialog createLoadingDialog(Context context, String msg) {
        View view =  LayoutInflater.from(context).inflate(R.layout.layout_wait_dialog, null);

       RelativeLayout re= view.findViewById(R.id.wait_dialog_view);
       //得到Tv
       TextView tv= view.findViewById(R.id.wait_dialog_tv);
       tv.setText(msg);
        // 创建自定义样式的Dialog
        Dialog  loadingDialog = new Dialog(context, R.style.dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(view,new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        return loadingDialog;
    }

}
