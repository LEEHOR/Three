package net.coahr.three3.three.Popupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.Preferences.PreferencesTool;

import java.util.logging.Handler;

/**
 * Created by 李浩 on 2018/3/27.
 */

public class PopuWindows extends PopupWindow  {
    private PopuWindowsListener mPopListener;
    private  PopupWindow mPopWindow;
    private  Context context;
    private PreferencesTool mpreferencesTool;
    private TextView textView_e,textView_subj,textView_singel,textView_total,textView_st;
    private int mIndex = 0;
    private LinearLayout mpopuWidowsBar;
    private Handler handler;
    private int[] iconNormal = {R.drawable.list,R.drawable.record_one, R.drawable.record_all, R.drawable.no_record};
    private int[] iconSelected = {R.drawable.list,R.drawable.record_one_c, R.drawable.record_all_c, R.drawable.no_record_c};
    private int[] iconNormal_Q={R.drawable.quit};
    private int[] iconSelected_Q={R.drawable.quit_c};
    private int[] text_1={R.string.subjectsList,R.string.singleRcorder,R.string.allRcorder,R.string.stopRecorder};
    private int[] text_2={R.string.subjectsList_2,R.string.singleRcorder_2,R.string.allRcorder_2,R.string.stopRecorder_2};
    private int[] text_3={R.string.subjectsList_3,R.string.singleRcorder_3,R.string.allRcorder_3,R.string.stopRecorder_3};
    public void showPopupWindow(View v,final Context context) {
        this.context = context;
        mpreferencesTool = new PreferencesTool(context);
        LinearLayout contentView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_project_popuwindows, null);
        mPopWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        /*获取控件*/
        int recorderModel = mpreferencesTool.getRecorderModel("RecorderModel");
        boolean isRecorderAll = mpreferencesTool.getIsRecorderAll("IsRecorderAll");
        textView_e=  contentView.findViewById(R.id.project_exitAccess);
        textView_subj= contentView.findViewById(R.id.project_subjectItem);
        textView_singel= contentView.findViewById(R.id.project_singleRecording);
        textView_total= contentView.findViewById(R.id.project_totalRecording);
        textView_st=contentView.findViewById(R.id.project_stopRecording);
        mpopuWidowsBar=  contentView.findViewById(R.id.PopuTopLine);
        TextView allR = (TextView) mpopuWidowsBar.getChildAt(3);
        TextView stop = (TextView) mpopuWidowsBar.getChildAt(4);
        /*退出按钮*/
        Drawable drawable_e = context.getDrawable(iconNormal_Q[0]);
        drawable_e.setBounds(0, 0, 70, 75);
        textView_e.setCompoundDrawables(null, drawable_e, null, null);
        textView_e.setCompoundDrawablePadding(5);
        /*上部*/
        for (int i = 0 ; i < mpopuWidowsBar.getChildCount() ; i++) {
            TextView textView = (TextView) mpopuWidowsBar.getChildAt(i);
            textView.setVisibility(View.VISIBLE);
            Drawable drawable = context.getDrawable(iconNormal[i]);
            drawable.setBounds(0, 0, 70, 75);
            textView.setCompoundDrawables(null, drawable, null, null);
            textView.setCompoundDrawablePadding(5);
            textView.setText(text_1[i]);
            if (i==0){
                textView.setTextColor(Color.parseColor("#1075d4"));
            }
            textView.setTextColor(Color.parseColor("#cccccc"));
        }


        if (recorderModel==2){
            Drawable drawable_s = context.getDrawable(iconSelected[1]);
            drawable_s.setBounds(0, 0, 70, 75);
            textView_singel.setCompoundDrawables(null, drawable_s, null, null);
            textView_singel.setCompoundDrawablePadding(5);
            textView_singel.setText(text_1[1]);
            textView_singel.setTextColor(Color.parseColor("#1075d4"));
        }
        if (recorderModel==3){

            if (isRecorderAll){  //录音暂停
                /**
                 * 全局录音按钮
                 */
                Drawable drawable_total = context.getDrawable(iconSelected[2]);
                drawable_total.setBounds(0, 0, 70, 75);
                textView_total.setCompoundDrawables(null, drawable_total, null, null);
                textView_total.setCompoundDrawablePadding(5);
                textView_total.setText(text_3[2]);
                textView_total.setTextColor(Color.parseColor("#1075d4"));


                /**
                 * 停止按钮
                 */
                Drawable drawable_st = context.getDrawable(iconSelected[3]);
                drawable_st.setBounds(0, 0, 70, 75);
                textView_st.setCompoundDrawables(null, drawable_st, null, null);
                textView_st.setCompoundDrawablePadding(5);
                textView_st.setText(text_3[3]);
                textView_st.setTextColor(Color.parseColor("#1075d4"));

            }else {  //录音没暂停
                /**
                 * 全局录音按钮
                 */
                Drawable drawable_total = context.getDrawable(iconSelected[2]);
                drawable_total.setBounds(0, 0, 70, 75);
                textView_total.setCompoundDrawables(null, drawable_total, null, null);
                textView_total.setCompoundDrawablePadding(5);
                textView_total.setText(text_2[2]);
                textView_total.setTextColor(Color.parseColor("#1075d4"));

                /**
                 * 停止按钮
                 */
                Drawable drawable_st = context.getDrawable(iconNormal[3]);
                drawable_st.setBounds(0, 0, 70, 75);
                textView_st.setCompoundDrawables(null, drawable_st, null, null);
                textView_st.setCompoundDrawablePadding(5);
                textView_st.setText(text_2[3]);
                textView_st.setTextColor(Color.parseColor("#cccccc"));
            }
        }
        textView_e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable_e = context.getDrawable(iconSelected_Q[0]);
                drawable_e.setBounds(0, 0, 70, 75);
                textView_e.setCompoundDrawables(null, drawable_e, null, null);
                textView_e.setTextColor(Color.parseColor("#1075d4"));
                textView_e.setCompoundDrawablePadding(5);
                mPopListener.exitAccess(mPopWindow);
                //mPopWindow.dismiss();
            }
        });

            textView_singel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // getIndex(1,mpopuWidowsBar,text_1);
                    Drawable drawable_s = context.getDrawable(iconSelected[1]);
                    drawable_s.setBounds(0, 0, 70, 75);
                    textView_singel.setCompoundDrawables(null, drawable_s, null, null);
                    textView_singel.setCompoundDrawablePadding(5);
                    textView_singel.setText(text_1[1]);
                    textView_singel.setTextColor(Color.parseColor("#1075d4"));
                    mPopListener.singleRecording(mPopWindow);


                }
            });

            textView_subj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // getIndex(0,mpopuWidowsBar,text_2);
                    Drawable drawable_sub = context.getDrawable(iconSelected[0]);
                    drawable_sub.setBounds(0, 0, 70, 75);
                    textView_subj.setCompoundDrawables(null, drawable_sub, null, null);
                    textView_subj.setCompoundDrawablePadding(5);
                    textView_subj.setText(text_1[0]);
                    textView_subj.setTextColor(Color.parseColor("#1075d4"));
                    mPopListener.subjectItem(mPopWindow);

                }
            });

            textView_total.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // getIndex(2,mpopuWidowsBar,text_3);
                    Drawable drawable_t = context.getDrawable(iconSelected[2]);
                    drawable_t.setBounds(0, 0, 70, 75);
                    textView_total.setCompoundDrawables(null, drawable_t, null, null);
                    textView_total.setCompoundDrawablePadding(5);
                    textView_total.setText(text_2[2]);
                    textView_total.setTextColor(Color.parseColor("#1075d4"));
                    mPopListener.totalRecording(mPopWindow);

                }
            });

            textView_st.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // getIndex(3,mpopuWidowsBar,text_3);
                    Drawable drawable_st = context.getDrawable(iconSelected[3]);
                    drawable_st.setBounds(0, 0, 70, 75);
                    textView_st.setCompoundDrawables(null, drawable_st, null, null);
                    textView_st.setCompoundDrawablePadding(5);
                    textView_st.setText(text_3[3]);
                    textView_st.setTextColor(Color.parseColor("#1075d4"));
                    mPopListener.stopRecording(mPopWindow);

                }
            });
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                System.out.println("=====================半透明");
                setBackgroundAlpha(1.0f,context);
            }
        });
        setBackgroundAlpha(0.5f,context);
        mPopWindow.setFocusable(true);
        mPopWindow.setOutsideTouchable(true);

        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.colorWhite));
        mPopWindow.setBackgroundDrawable(dw);
       // mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        //测量view 注意这里，如果没有测量  ，下面的popupHeight高度为-2  ,因为LinearLayout.LayoutParams.WRAP_CONTENT这句自适应造成的
      /*  contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = contentView.getMeasuredWidth();    //  获取测量后的宽度
        int popupHeight = contentView.getMeasuredHeight(); */ //获取测量后的高度
        //setBackgroundAlpha(0.5f);
        /*显示在标题栏的下方*/

        mPopWindow.showAsDropDown(v);
        // 按下android回退物理键 PopipWindow消失解决
        contentView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (mPopWindow != null && mPopWindow.isShowing()) {
                        mPopWindow.dismiss();
                        return true;
                    }
                }
                return false;
            }
        });

    }






    public void showPopupWindow(View v , final Context context , boolean flag)
    {
        this.context=context;

        LinearLayout contentView =(LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_project_popuwindows, null);
        mPopWindow = new PopupWindow(contentView,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        /*获取控件*/
        mpopuWidowsBar=  contentView.findViewById(R.id.PopuTopLine);
        textView_e=  contentView.findViewById(R.id.project_exitAccess);
        textView_subj= contentView.findViewById(R.id.project_subjectItem);
        textView_singel= contentView.findViewById(R.id.project_singleRecording);
        textView_total= contentView.findViewById(R.id.project_totalRecording);
        textView_st=contentView.findViewById(R.id.project_stopRecording);


        if (flag)
        {
            for (int i = 0 ; i <mpopuWidowsBar.getChildCount() ; i++) {
                TextView textView = (TextView) mpopuWidowsBar.getChildAt(i);
                textView.setVisibility(View.VISIBLE);
                Drawable drawable = context.getDrawable(iconNormal[i]);
                drawable.setBounds(0, 0, 70, 75);
                textView.setCompoundDrawables(null, drawable, null, null);
                textView.setCompoundDrawablePadding(5);
                textView.setText(text_1[i]);
                textView.setTextColor(Color.parseColor("#cccccc"));
            }
        }else {

            for (int i = 0 ; i < 1 ; i++) {
                TextView textView = (TextView) mpopuWidowsBar.getChildAt(i);
                textView.setVisibility(View.VISIBLE);
                Drawable drawable = context.getDrawable(iconNormal[i]);
                drawable.setBounds(0, 0, 70, 75);
                textView.setCompoundDrawables(null, drawable, null, null);
                textView.setCompoundDrawablePadding(5);
                textView.setText(text_1[i]);
                textView.setTextColor(Color.parseColor("#cccccc"));
            }
        }

        /*退出按钮*/
        Drawable drawable = context.getDrawable(iconNormal_Q[0]);
        drawable.setBounds(0, 0, 70, 75);
        textView_e.setCompoundDrawables(null, drawable, null, null);
        textView_e.setCompoundDrawablePadding(5);

        textView_e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable_e = context.getDrawable(iconSelected_Q[0]);
                drawable_e.setBounds(0, 0, 70, 75);
                textView_e.setCompoundDrawables(null, drawable_e, null, null);
                textView_e.setTextColor(Color.parseColor("#1075d4"));
                textView_e.setCompoundDrawablePadding(5);
                mPopListener.exitAccess(mPopWindow);
            }
        });

        textView_singel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIndex(1,mpopuWidowsBar,text_1);
                   /* Drawable drawable_s = context.getDrawable(iconSelected[1]);
                    drawable_s.setBounds(0, 0, 60, 65);
                    textView_singel.setCompoundDrawables(null, drawable_s, null, null);
                    textView_singel.setCompoundDrawablePadding(5);
                    textView_singel.setText(text_1[1]);
                    textView_singel.setTextColor(Color.parseColor("#1075d4"));*/
                mPopListener.singleRecording(mPopWindow);
                mPopWindow.dismiss();
            }
        });

        textView_subj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIndex(0,mpopuWidowsBar,text_2);
                   /* Drawable drawable_sub = context.getDrawable(iconSelected[0]);
                    drawable_sub.setBounds(0, 0, 60, 65);
                    textView_subj.setCompoundDrawables(null, drawable_sub, null, null);
                    textView_subj.setCompoundDrawablePadding(5);
                    textView_subj.setText(text_1[0]);
                    textView_subj.setTextColor(Color.parseColor("#1075d4"));*/
                mPopListener.subjectItem(mPopWindow);
                //mPopWindow.dismiss();
            }
        });

        textView_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIndex(2,mpopuWidowsBar,text_3);

                mPopListener.totalRecording(mPopWindow);
                mPopWindow.dismiss();
            }
        });

        textView_st.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIndex(3,mpopuWidowsBar,text_3);
                 /*   Drawable drawable_st = context.getDrawable(iconSelected[3]);
                    drawable_st.setBounds(0, 0, 60, 65);
                    textView_st.setCompoundDrawables(null, drawable_st, null, null);
                    textView_st.setCompoundDrawablePadding(5);
                    textView_st.setText(text_1[3]);
                    textView_st.setTextColor(Color.parseColor("#1075d4"));*/
                mPopListener.stopRecording(mPopWindow);
                mPopWindow.dismiss();
            }
        });
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f,context);
            }
        });
        setBackgroundAlpha(0.5f,context);
        mPopWindow.setFocusable(true);
        mPopWindow.setOutsideTouchable(true);

        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.colorWhite));
        mPopWindow.setBackgroundDrawable(dw);
        // mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        //测量view 注意这里，如果没有测量  ，下面的popupHeight高度为-2  ,因为LinearLayout.LayoutParams.WRAP_CONTENT这句自适应造成的
      /*  contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = contentView.getMeasuredWidth();    //  获取测量后的宽度
        int popupHeight = contentView.getMeasuredHeight(); */ //获取测量后的高度
        //setBackgroundAlpha(0.5f);
        /*显示在标题栏的下方*/

        mPopWindow.showAsDropDown(v);
        // 按下android回退物理键 PopipWindow消失
        contentView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                   /* if (mPopWindow != null && mPopWindow.isShowing()) {
                        mPopWindow.dismiss();
                        return true;
                    }*/
                   return true;
                }
                return false;
            }
        });


    }


    public void setOnButtonClickListener(PopuWindowsListener listener) {
        this.mPopListener = listener;
    }

    public void setBackgroundAlpha(float bgAlpha,Context context) {
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
            lp.alpha = bgAlpha; //0.0-1.0
        if (bgAlpha == 1.0) {
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            ((Activity)context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        ((Activity)context).getWindow().setAttributes(lp);
    }


    private void getIndex(int indexs,ViewGroup viewGroup,int [] text){

        if (mIndex == indexs) {
            return ;
        } else {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                TextView textView = (TextView) viewGroup.getChildAt(i);
                Drawable drawable = null;

                if (i == indexs) {
                    drawable = context.getResources().getDrawable(iconSelected[i]);
                    drawable.setBounds(0, 0, 70, 75);
                    textView.setCompoundDrawables(null, drawable, null, null);
                    textView.setTextColor(Color.parseColor("#1075d4"));
                    textView.setText(text[i]);
                    textView.setCompoundDrawablePadding(5);

                } else {
                    drawable =  context.getResources().getDrawable(iconNormal[i]);
                    drawable.setBounds(0, 0, 70, 75);
                    textView.setCompoundDrawables(null, drawable, null, null);
                    textView.setTextColor(Color.parseColor("#cccccc"));
                    textView.setText(text[i]);
                    textView.setCompoundDrawablePadding(5);
                }
            }

            mIndex = indexs;

        }

    }

}
