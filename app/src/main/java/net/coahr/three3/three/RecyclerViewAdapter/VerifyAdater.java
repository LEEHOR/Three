package net.coahr.three3.three.RecyclerViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseRecyclerViewAdapter;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseViewHolder;
import net.coahr.three3.three.Model.VerifyDataModel;
import net.coahr.three3.three.Module.VerifyFragment;
import net.coahr.three3.three.R;

import java.text.SimpleDateFormat;
import java.util.List;


/**
 * Created by yuwei on 2018/4/8.
 */

public class VerifyAdater extends BaseRecyclerViewAdapter {

    private boolean pass;
    private TextView progressTextView,interviewsTyoeTextView,projectDateTextView,codeTextView,projectNameTextView,companyTextView;
    private TextView shopAddressTextView,motifyTimeTextView,failNumTextView;
    private Context mcontext;
   // private OnItemClickListner onItemClickListners;
    public void setPass(boolean pass) {
        this.pass = pass;
    }
    public boolean getPass()
    {
        return pass;
    }


    private TextView mRevise;

    /**
     * 实例化具体实现
     *
     * @param context  父activity
     * @param datas    加载的数据
     * @param layoutId
     */
    public VerifyAdater(Context context, List datas, int layoutId , VerifyFragment fragment , Intent intent) {
        super(context, datas, layoutId , intent);
    this.mcontext=context;
   // this.onItemClickListners=listner;
        fragment.setmChangeListInterface(new VerifyFragment.ChangeListInterface() {
            @Override
            public void change(boolean falg) {

                pass = falg;
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public void bindData(BaseViewHolder holder, Object data, final int position) {
        findUI(holder.itemView);
        VerifyDataModel.verifyListBean model = (VerifyDataModel.verifyListBean) datas.get(position);
        failNumTextView.setVisibility(pass ? View.INVISIBLE : View.VISIBLE);
        progressTextView.setText(model.getProgress());
        String inspectStr = null;
        switch (model.getInspect())
        {
            case 1:
                inspectStr = "飞检";
                break;
            case 2:
                inspectStr = "神秘顾客";
                break;
            case 3:
                inspectStr = "新店验收";
                break;
        }
        String recordStr = null;
        switch (model.getRecord())
        {
            case 1:
                recordStr = "不录音";
                break;
            case 2:
                recordStr = "单题录音";
                break;
            case 3:
                recordStr = "全程录音";
                break;
        }
        interviewsTyoeTextView.setText("["+ inspectStr + "," + recordStr + "]");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String ymd = simpleDateFormat.format(model.getStartTime());
        if (model.getEndTime() == 1)
        {
            projectDateTextView.setText(ymd +"--结束公开");
        }
        else if(model.getEndTime() != 0)
        {

            String end = simpleDateFormat.format(model.getEndTime());
            projectDateTextView.setText(ymd +"--" + end);
        }
        codeTextView.setText(model.getCode());
        projectNameTextView.setText(model.getPname());
        companyTextView.setText(model.getDname());
        shopAddressTextView.setText(model.getAreaAddress()+model.getLocation());
        SimpleDateFormat modifyDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String modiyStr = modifyDateFormat.format(model.getModifyTime());
        motifyTimeTextView.setText(modiyStr+"更新");
        failNumTextView.setText("共有"+model.getNumber()+"道题被打回");

     /*  holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyDataModel.verifyListBean model = (VerifyDataModel.verifyListBean) datas.get(position);
                getmIntent().putExtra("projectId" ,model.getId());
                onItemClickListner.onItemClickListener(v , position , getmIntent(),mcontext);
            }
        });*/

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyDataModel.verifyListBean model = (VerifyDataModel.verifyListBean) datas.get(position);
                getmIntent().putExtra("projectId" ,model.getId());
                onItemClickListner.onItemClickListener(v , position , getmIntent(),mcontext);

            }
        });
        bindData((BaseViewHolder) holder, null , position);
    }

    @Override
    public int getItemCount() {
        if (datas!= null)
            return datas.size();
        else
            return 0;
    }


    @Override
    public void findUI(View v) {
        super.findUI(v);
        progressTextView = v.findViewById(R.id.completeOfStatus);
        interviewsTyoeTextView = v.findViewById(R.id.type);
        projectDateTextView = v.findViewById(R.id.date);
        codeTextView = v.findViewById(R.id.projectId);
        projectNameTextView = v.findViewById(R.id.projectName);
        companyTextView = v.findViewById(R.id.companyName);
        shopAddressTextView = v.findViewById(R.id.address);
        motifyTimeTextView = v.findViewById(R.id.update);
        failNumTextView = v.findViewById(R.id.verifyFail);
    }
}
