package net.coahr.three3.three.RecyclerViewAdapter;

import android.content.Context;

import net.coahr.three3.three.BaseAdapter.BaseListAdapter.CommonAdapter;
import net.coahr.three3.three.BaseAdapter.BaseListAdapter.ViewHolder;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseRecyclerViewAdapter;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseViewHolder;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.CommonRecycleAdapter;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.CommonViewHolder;
import net.coahr.three3.three.DBbase.ImagesDB;
import net.coahr.three3.three.DBbase.SearchDataDB;
import net.coahr.three3.three.Model.HomeSearchModel;
import net.coahr.three3.three.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by 李浩
 */

public class SearchAdapter extends CommonRecycleAdapter<HomeSearchModel.SearchListBean> {
    protected CommonViewHolder.onItemCommonClickListener mcommonClickListener;

    /**
     * 实例化具体实现
     *
     * @param context  父activity
     * @param datas    加载的数据
     * @param layoutId
     */
    public SearchAdapter(Context context, List<HomeSearchModel.SearchListBean> datas, int layoutId,CommonViewHolder.onItemCommonClickListener listener) {
        super(context, datas, layoutId);
        this.mcommonClickListener=listener;
    }

    @Override
    protected void bindData(CommonViewHolder holder, HomeSearchModel.SearchListBean data, int position) {
        int inspect = data.getInspect();
        String str1="";
        if (inspect == 1) {
            str1="飞检";
        }else if (inspect==2){
            str1="神秘顾客";
        }else {
            str1="新店验收";
        }
        int record = data.getRecord();
        String str2="";
        if (record == 1) {
            str2="不录音";
        }else if (record==2){
            str2="单体录音";
        }else {
            str2="全程录音";
        }
        Date d = new Date(data.getStartTime());
        Date de=new Date(data.getEndTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startTime = sdf.format(d);
        String endTime = sdf.format(de);

        String modiyTime="";
        if (data.getModifyTime()==1){
            modiyTime="结束公开时间";
        }else {
            Date db=new Date(data.getModifyTime());
            SimpleDateFormat sdf2=new SimpleDateFormat("yy-MM-dd HH:mm");
            modiyTime = sdf2.format(db);
        }
        holder.setText(R.id.rootSearch_inspect_record,"["+str1+","+str2+"]")
                .setText(R.id.rootSearch_startDate,startTime)
                .setText(R.id.rootSearch_endDate,endTime)
                .setText(R.id.rootSearch_projectNumber,data.getCode())
                .setText(R.id.rootSearch_projectTitle,data.getPname())
                .setText(R.id.rootSearch_storeName,data.getDname())
                .setText(R.id.rootSearch_local,data.getAreaAddress()+data.getLocation())
                .setText(R.id.rootSearch_modifyTime,modiyTime)
                .setCommonClickListener(mcommonClickListener);
    }


}
