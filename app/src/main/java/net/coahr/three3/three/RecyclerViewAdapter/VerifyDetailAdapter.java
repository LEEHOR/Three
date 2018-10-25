package net.coahr.three3.three.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseRecyclerViewAdapter;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseViewHolder;
import net.coahr.three3.three.Model.VerifyInfoModel;
import net.coahr.three3.three.R;

import java.util.List;

/**
 * Created by yuwei on 2018/4/9.
 */

public class VerifyDetailAdapter extends BaseRecyclerViewAdapter {
    private ReviseClickListenner reviseClickListenner;
    private Boolean verifyFlag;
    private Context mContext;
    private TextView titleTextView , verifyTextView, NameTextView,suggestionTextView;

    public void setReviseClickListenner(ReviseClickListenner reviseClickListenner) {
        this.reviseClickListenner = reviseClickListenner;
    }

    public void setVerifyFlag(Boolean verifyFlag) {
        this.verifyFlag = verifyFlag;
    }


    /**
     * 实例化具体实现
     *
     * @param context  父activity
     * @param datas    加载的数据
     * @param layoutId
     */
    public VerifyDetailAdapter(Context context, List datas, int layoutId , Intent intent) {
        super(context, datas, layoutId , intent);
        this.mContext=context;
    }

    @Override
    public void bindData(BaseViewHolder holder, Object data, final int position) {
        findUI(holder.itemView , position );
        VerifyInfoModel.verifyInfoListBean infoModel = (VerifyInfoModel.verifyInfoListBean) datas.get(position);

        String titleContent = infoModel.getTitle();
        titleTextView.setText("1."+isEmptyOrNull(infoModel) + titleContent);
        String stage = null;
        switch (infoModel.getStage())
        {
            case 1:
                stage = "初审";
                break;
            case 2:
                stage = "复审";
                break;
            case 3:
                stage = "终审";
                break;
        }
        verifyTextView.setText(stage);
        NameTextView.setText(infoModel.getName());
        suggestionTextView.setText(infoModel.getSuggestion());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListner.onItemClickListener(v , position , getmIntent(),mContext);
            }
        });
    }

   /* @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {





        bindData((BaseViewHolder) holder, null , position);
    }*/

//    @Override
//    public int getItemCount() {
//        if (datas!= null)
//            return datas.size();
//        else
//            return 0;
//    }


    public void findUI(View v , final int position) {
        super.findUI(v);
        titleTextView = v.findViewById(R.id.question);
        verifyTextView = v.findViewById(R.id.verify);
        NameTextView = v.findViewById(R.id.name);
        suggestionTextView = v.findViewById(R.id.suggestion);

        Button reviseBtn = v.findViewById(R.id.revise);
        reviseBtn.setText(verifyFlag ? "浏览详情" : "进入修改");
        reviseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("--------------------------click");

                reviseClickListenner.revise(position);
            }
        });
    }


    public interface ReviseClickListenner
    {
        void revise(int position);
    }

    public String isEmptyOrNull(VerifyInfoModel.verifyInfoListBean model)
    {
        if(model.getQuota1() == null || model.getQuota1().isEmpty() )
        {
            return "";
        }
        else if( model.getQuota2() == null || model.getQuota2().isEmpty() )
        {
            return "[" + model.getQuota1() + "]";
        }
        else if( model.getQuota3() == null || model.getQuota3().isEmpty() )
        {
            return "[" + model.getQuota1() + "]" + "[" + model.getQuota2() + "]";
        }
        else
        {
            return "[" + model.getQuota1() + "]" + "[" + model.getQuota2() + "]" + "[" + model.getQuota3() + "]";
        }
    }
}
