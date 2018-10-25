package net.coahr.three3.three.RecyclerViewAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.CommonRecycleAdapter;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.CommonViewHolder;
import net.coahr.three3.three.DBbase.SubjectsDB;
import net.coahr.three3.three.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 李浩
 * 2018/4/27
 */
public class BrowseSubjectAdapter extends CommonRecycleAdapter<SubjectsDB>  {
    protected clickSubjectListener mcommonClickListener;
    private Map<String,Boolean> map=new HashMap<>();//控件是否被点击,默认为false，如果被点击，改变值，控件根据值改变自身颜色
    // 存储勾选框状态的map集合
    private final Context mcontext ;
    private LinearLayout reLinea;
    private RelativeLayout lineaAll;
    private TextView tv,more;
    private ImageView im;
    private CardView cardView;
    private boolean is_first=true;
    private int isOpened=-1;
    private Map<Integer,Integer> masp;
    private int[] iconImage={R.drawable.subjectlist_com,R.drawable.subjectlist_uncom,R.drawable.subjectlist_up};

    //构造方法

    public BrowseSubjectAdapter(Context context, List<SubjectsDB> dataList,int layoutId, clickSubjectListener mcommonClickListener) {
        super(context, dataList,layoutId);
        this.mcommonClickListener = mcommonClickListener;
        this.mcontext=context;
        masp=new HashMap<>();
        SharedPreferences preferences=mcontext.getSharedPreferences("subject",0);
        for (int i = 0; i <dataList.size() ; i++) {
            boolean aBoolean = preferences.getBoolean(String.valueOf(i), false);
            map.put(String.valueOf(i),aBoolean);
        }
        if (dataList !=null && dataList.size()>0){
            for (int i = 0; i < dataList.size(); i++) {
               if (dataList.get(i).getsUploadStatus()==1 && dataList.get(i).getCensor()!=0 ){ //已上传
                   masp.put(dataList.get(i).getId(),1);
               } else if(dataList.get(i).getIsComplete()==1 ){ //已完成
                   masp.put(dataList.get(i).getId(),2);
               } else if (dataList.get(i).getIsComplete()==0){  //未完成
                   masp.put(dataList.get(i).getId(),3);
               } else {
                   masp.put(dataList.get(i).getId(),3);
               }
            }
        }


    }
    @Override
    protected void bindData(final CommonViewHolder holder, final SubjectsDB data, final int position)  {
        lineaAll=  holder.getView(R.id.browseSubject_Line);
        reLinea = holder.getView(R.id.rel_L_reLine);
        tv=  holder.getView(R.id.browseSubject_tv_Target);
        more= holder.getView(R.id.browseSubject_tv_more);
        im= holder.getView(R.id.broswe_image);
        cardView= holder.getView(R.id.browse_card);

        if (masp!=null){
            if (masp.get(data.getId())==1){   //
                Drawable drawable_st = mcontext.getDrawable(iconImage[2]);
                im.setImageDrawable(drawable_st);
            } else if (masp.get(data.getId())==2){
                Drawable drawable_st = mcontext.getDrawable(iconImage[0]);
                im.setImageDrawable(drawable_st);
            } else if (masp.get(data.getId())==3){
                Drawable drawable_st = mcontext.getDrawable(iconImage[1]);
                im.setImageDrawable(drawable_st);
            } else {
                Drawable drawable_st = mcontext.getDrawable(iconImage[1]);
                im.setImageDrawable(drawable_st);
            }

        } else {
            Drawable drawable_st = mcontext.getDrawable(iconImage[2]);
            im.setImageDrawable(drawable_st);
        }

        String  Target=" . ";
        String quota1 = data.getQuota1();
        if (quota1!=null){
            Target=Target+"["+quota1+"]";
            String quota2 = data.getQuota2();
            if (quota2!=null){
                Target= Target+"["+quota2+"]";
                String quota3 = data.getQuota3();
                if (quota3!=null){
                    Target= Target+"["+quota3+"]";
                }

            }
        }
        reLinea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                map.put(String.valueOf(adapterPosition),true);
                notifyDataSetChanged();
              mcommonClickListener.SubjectListener(position,map,data.getQuota3(),data.getId());
            }
        });
        if (map.get(String.valueOf(position))){
            if (masp!=null && masp.get(data.getId())==1){
                cardView.setBackgroundColor(mcontext.getResources().getColor(R.color.proInfo));
            }else {
                cardView.setBackgroundColor(mcontext.getResources().getColor(R.color.unlocation));
            }
        }else {
            cardView.setBackgroundColor(mcontext.getResources().getColor(R.color.colorWhite));
        }
        tv.setText((position+1)+" "+Target+"\n"+data.getTitle());
        if (isOpened==position){
            tv.setEllipsize(null); // 展开
            tv.setSingleLine(false);
            more.setText("点击收起");
        }else {
            more.setText("点击展开");
            tv.setLines(3);
            tv.setEllipsize(TextUtils.TruncateAt.END);
        }
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpened==holder.getAdapterPosition()){
                    //展开就闭和
                        isOpened=-1;
                        notifyItemChanged(holder.getAdapterPosition());
                }else {
                        int oldOpened=isOpened;
                        isOpened=holder.getAdapterPosition();
                        notifyItemChanged(oldOpened);
                        notifyItemChanged(isOpened);
                }
            }
        });

    }

    public  interface clickSubjectListener{
                void SubjectListener(int position,Map map,String qu3,int id);
    }

}

