package net.coahr.three3.three.Util.ALiYunOSUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by 李浩 on 2018/17/7 .
 * 完成显示图片，上传下载对话框显示，进度条更新等操作。
 */
public class UIDisplayer {


    private ProgressBar bar;
    private View view;
    private TextView infoView;
    private Activity activity;

    private Handler handler;
    private MaterialDialog loadingDialog;

    private static final int DOWNLOAD_OK = 1;
    private static final int DOWNLOAD_FAIL = 2;
    private static final int UPLOAD_OK = 3;
    private static final int UPLOAD_FAIL = 4;
    private static final int UPDATE_PROGRESS = 5;
    private static final int DISPLAY_IMAGE = 6;
    private static final int DISPLAY_INFO = 7;
    private static final int SETTING_OK = 88;
    private static final int DISPLAY_UPLOADING=91;
    private static final int DISPLAY_UPLOADED=92;
    private static final int DISPLAY_CLOSEDUPLOAD=93;
     private static final int VISIBLE=94;
    private static final int GONE=95;
    private  AlertDialog.Builder builder;

    /* 必须在UI线程中初始化handler */
    public UIDisplayer( ProgressBar bar, TextView infoView,View view, Activity activity) {

        this.bar = bar;
        this.infoView = infoView;
        this.activity = activity;
        this.view=view;
        initDialog();
        builder = new AlertDialog.Builder(UIDisplayer.this.activity);
        handler = new Handler() {
            @Override
            public void handleMessage(Message inputMessage) {

                String info;
                switch (inputMessage.what) {

                    case UPLOAD_OK:
                        info= (String) inputMessage.obj;
                       builder.setTitle("上传成功").setMessage(info).show();
                        break;
                    case UPLOAD_FAIL:
                        info = (String) inputMessage.obj;
                       builder.setTitle("上传失败").setMessage(info).show();
                        break;
                    case DOWNLOAD_OK:
                        new AlertDialog.Builder(UIDisplayer.this.activity).setTitle("下载成功").setMessage("download from OSS OK!").show();
                        break;
                    case SETTING_OK:
                        builder.setTitle("设置成功").setMessage("设置域名信息成功,现在<选择图片>, 然后上传图片").show();
                        break;
                    case DOWNLOAD_FAIL:
                        info = (String) inputMessage.obj;
                        new AlertDialog.Builder(UIDisplayer.this.activity).setTitle("下载失败").setMessage(info).show();
                        break;
                    case UPDATE_PROGRESS:
                        UIDisplayer.this.bar.setProgress(inputMessage.arg1);
                        //Log.d("UpdateProgress", String.valueOf(inputMessage.arg1));
                        break;
                    case DISPLAY_IMAGE:
                       // Bitmap bm = (Bitmap) inputMessage.obj;
                       // UIDisplayer.this.imageView.setImageBitmap(bm);
                        break;
                    case DISPLAY_INFO:
                        info = (String) inputMessage.obj;
                        UIDisplayer.this.infoView.setText(info);
                        break;
                    case DISPLAY_UPLOADING:
                        showLoading();
                        break;
                    case DISPLAY_UPLOADED:
                        dismissLoading();
                        break;
                    case VISIBLE:
                        if (view.getVisibility()==View.INVISIBLE || view.getVisibility()==View.GONE){
                            view.setVisibility(View.VISIBLE);
                        }
                        break;
                    case GONE:
                        if (view.getVisibility()==View.VISIBLE){
                            view.setVisibility(View.GONE);
                        }
                    default:
                        break;
                }

            }
        };

    }

    /**
     * 进度
     */
    public void initDialog() {
         loadingDialog = new MaterialDialog.Builder(UIDisplayer.this.activity)
                .content("上传中...")
                .progress(true, 100)
                .canceledOnTouchOutside(false)
                 .cancelable(false)
                .build();
    }

    public void showLoading() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    public void dismissLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

//    //下载成功，显示对应的图片
//    public void downloadComplete(Bitmap bm) {
//        if (null != bm) {
//            displayImage(bm);
//        }
//
//        Message mes = handler.obtainMessage(DOWNLOAD_OK);
//        mes.sendToTarget();
//    }

    public void settingOK() {
        Message mes = handler.obtainMessage(SETTING_OK);
        mes.sendToTarget();
    }

    //下载失败，显示对应的失败信息
    public void downloadFail(String info) {
        Message mes = handler.obtainMessage(DOWNLOAD_FAIL, info);
        mes.sendToTarget();
    }

    //上传成功
    public void uploadComplete(String info) {
        Message mes = handler.obtainMessage(UPLOAD_OK,info);
        mes.sendToTarget();
    }

    //上传失败，显示对应的失败信息
    public void uploadFail(String info) {
        Message mes = handler.obtainMessage(UPLOAD_FAIL, info);
        mes.sendToTarget();
    }
    public void setVisible(){
        Message mes = handler.obtainMessage(VISIBLE);
        mes.sendToTarget();
    }

    public void setGone(){
        Message mes = handler.obtainMessage(GONE);
        mes.sendToTarget();
    }

    //更新进度，取值范围为[0,100]
    public void updateProgress(int progress) {
        //Log.d("UpdateProgress", String.valueOf(progress));
        if (progress > 100) {
            progress = 100;
        } else if (progress < 0) {
            progress = 0;
        }

        Message mes = handler.obtainMessage(UPDATE_PROGRESS, progress);
        mes.arg1 = progress;
        mes.sendToTarget();
    }

    //显示图像
    public void displayImage(Bitmap bm) {
        Message mes = handler.obtainMessage(DISPLAY_IMAGE, bm);
        mes.sendToTarget();
    }

    //在主界面输出文字信息
    public void displayInfo(String info) {
        Message mes = handler.obtainMessage(DISPLAY_INFO, info);
        mes.sendToTarget();
    }
    //显示上传弹窗
    public void display_uploading(){
        Message mes = handler.obtainMessage(DISPLAY_UPLOADING);
        mes.sendToTarget();
    }
    //关闭上传弹窗
    public void display_uploaded(){
        Message mes = handler.obtainMessage(DISPLAY_UPLOADED);
        mes.sendToTarget();
    }

    private void setAlterDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UIDisplayer.this.activity);
    }

}
