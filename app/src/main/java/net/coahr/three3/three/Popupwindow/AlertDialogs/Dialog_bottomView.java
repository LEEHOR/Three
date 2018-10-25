package net.coahr.three3.three.Popupwindow.AlertDialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import net.coahr.three3.three.R;


/**
 * Created by 李浩 on 2018/3/27.
 */

public class Dialog_bottomView {
 private Dialog_bottomListener bottomListener;
    public void showMydialog(Context context){
        final Dialog   dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        View   inflate = LayoutInflater.from(context).inflate(R.layout.item_projectstart_bottom_dialog, null);
        //初始化控件
        TextView  choosePhoto = (TextView) inflate.findViewById(R.id.choosePhoto);
        TextView  takePhoto = (TextView) inflate.findViewById(R.id.takePhoto);
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomListener.choosePhotos(dialog);
            }
        });
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomListener.takePhotos(dialog);
            }
        });
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity( Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 60;//设置Dialog距离底部的距离
        // 将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框

    }
    public void setOnButtonClickListener(Dialog_bottomListener listener) {
        this.bottomListener = listener;
    }
}
