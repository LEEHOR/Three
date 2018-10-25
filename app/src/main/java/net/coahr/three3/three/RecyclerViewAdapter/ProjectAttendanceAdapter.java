package net.coahr.three3.three.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseRecyclerViewAdapter;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseViewHolder;
import net.coahr.three3.three.Model.AttendanceHistoryModel;
import net.coahr.three3.three.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static net.coahr.three3.three.Model.AttendanceHistoryModel.*;

/**
 * Created by yuwei on 2018/4/18.
 */

public class ProjectAttendanceAdapter extends BaseRecyclerViewAdapter {

    private TextView dateTextView,startTimeTextView,endTimeTextView,startAddressTextView ,endAddressTextView,startInfoMsgTextView , endInfoMsgTextView , processTextView;
    private ImageView startStatusBtn , endStatusBtn;
    private GeoCoder mGeoCoder = GeoCoder.newInstance();
    private String address;


    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 实例化具体实现
     *
     * @param context  父activity
     * @param datas    加载的数据
     * @param layoutId
     */
    public ProjectAttendanceAdapter(Context context, List datas, int layoutId , Intent intent) {
        super(context, datas, layoutId , intent);
    }

    @Override
    public void bindData(BaseViewHolder holder, Object data, int position) {
        AttendanceBean bean = (AttendanceBean) data;

        Date d = new Date(Long.valueOf(bean.getDateTime()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateTextView.setText(sdf.format(d));
        String dateTime = bean.getDateTime();
        if (bean.getInTime() == 0)//却卡
        {
            startStatusBtn.setImageResource(R.drawable.kaoqinq);
            endStatusBtn.setImageResource(R.drawable.kaoqinq);
            startTimeTextView.setText("");
            endTimeTextView.setText("");
            startAddressTextView.setText("");
            endAddressTextView.setText("");

        }
        else//不缺卡
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
            String hm = simpleDateFormat.format(bean.getInTime());
            startTimeTextView.setText(hm);
            endTimeTextView.setText( simpleDateFormat.format(bean.getOutTime()));



            if (bean.getStartLocationStatus() == 1 && bean.getStartTimeStatus() == 1)
            {
                startAddressTextView.setText(address);
                startStatusBtn.setImageResource(R.drawable.kaoqinz);

            }
            else
            {
                getAddress(bean.getInLat() , bean.getInLng() , startAddressTextView);
                startStatusBtn.setImageResource(R.drawable.kaoqinyc);

            }


            //离店打卡
            if (bean.getOutTime()==0){ //缺卡
                endStatusBtn.setImageResource(R.drawable.kaoqinq);
                endTimeTextView.setText("");
                endAddressTextView.setText("");
            }else {
                if (bean.getEndLocationStatus() == 1 && bean.getEndTimeStatus() == 1)
                {
                    endAddressTextView.setText(address);
                    endStatusBtn.setImageResource(R.drawable.kaoqinz);
                }
                else
                {
                    getAddress(bean.getOutLat() , bean.getOutLng() , endAddressTextView);
                    endStatusBtn.setImageResource(R.drawable.kaoqinyc);
                }
            }



        }
            endInfoMsgTextView.setText(bean.getRemark());
            processTextView.setText("访问进度"+bean.getProgress());





    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        System.out.println(datas.size());
        findUI(holder);
        bindData((BaseViewHolder) holder, datas.get(position) , position);

    }

    @Override
    public int getItemCount() {
        if (datas!= null)
            return datas.size();
        else
            return 0;
    }

    public void findUI(RecyclerView.ViewHolder holder)
    {
        dateTextView = holder.itemView.findViewById(R.id.date);
        startTimeTextView = holder.itemView.findViewById(R.id.comeon);
        endTimeTextView = holder.itemView.findViewById(R.id.back);
        startAddressTextView = holder.itemView.findViewById(R.id.startAdress);
        endAddressTextView = holder.itemView.findViewById(R.id.adressBack);
        endInfoMsgTextView = holder.itemView.findViewById(R.id.endMsg);
        startStatusBtn = holder.itemView.findViewById(R.id.startStatus);
        endStatusBtn = holder.itemView.findViewById(R.id.endStatus);
        processTextView = holder.itemView.findViewById(R.id.process);

    }


    public void getAddress(double lat , double lng , final TextView textView ) {
        LatLng mLatLng = new LatLng( lat, lng);
        ReverseGeoCodeOption mReverseGeoCodeOption = new ReverseGeoCodeOption();
        mReverseGeoCodeOption.location(mLatLng);


        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {


                System.out.println(reverseGeoCodeResult);
                textView.setText(reverseGeoCodeResult.getAddress());
            }
        };
        mGeoCoder.setOnGetGeoCodeResultListener(listener);
        mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(mLatLng));
    }



}
