package net.coahr.three3.three.RecyclerViewAdapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.CommonRecycleAdapter;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.CommonViewHolder;
import net.coahr.three3.three.DBbase.ImagesDB;
import net.coahr.three3.three.DBbase.ProjectsDB;
import net.coahr.three3.three.DBbase.RecorderFilesDB;
import net.coahr.three3.three.DBbase.SubjectsDB;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.JDBC.DataBaseWork;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public  class UploadAdapter extends CommonRecycleAdapter<ProjectsDB>  {
    private CheckBox cb;
    private boolean medo=false;
    protected CommonViewHolder.onItemCommonClickListener mcommonClickListener;
    private CbInteface cbInteface;
    private int subjectSize;
    private  int ImageSize,reSize;
    private Map<Integer, Boolean> map ;
    // 初始化map集合
    public void initCheck(boolean flag) {
        // map集合的数量和list的数量是一致的
        map = new HashMap<>();
        for (int i = 0; i < dataList.size(); i++) {
            // 设置默认的显示
            map.put(i, flag);
        }
    }
    public void setMap(Map map,int position){
        this.map=map;
        notifyItemChanged(position);
    }
    public UploadAdapter(Context context, List<ProjectsDB> dataList, int layoutId) {
        super(context, dataList, layoutId);

    }

    @Override
    protected void bindData(CommonViewHolder holder, ProjectsDB data, final int position) {

        subjectSize=0;
        ImageSize=0;
        reSize=0;
        List<SubjectsDB> subjectsDBLists = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "projectsdb_id=? and iscomplete=? and censor =?", String.valueOf(data.getId()), String.valueOf(1), String.valueOf(0));
            if (subjectsDBLists!=null && subjectsDBLists.size()>0){
                Log.e("uploadAdapter", "bindData: ");
                subjectSize = subjectsDBLists.size();
                    for (int i = 0; i <subjectsDBLists.size() ; i++) {
                    List<ImagesDB> imagesDBList = subjectsDBLists.get(i).getImagesDBList();
                    if (imagesDBList !=null && imagesDBList.size()>0){
                        ImageSize +=imagesDBList.size();
                    }
                        List<RecorderFilesDB> recorderFiles = subjectsDBLists.get(i).getRecorderFiles();
                        if (recorderFiles!=null){
                        reSize+=1;
                    }
                }
            }
        Log.e("上传", "bindData: 数据大小 "+ (ImageSize+reSize));

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
        Log.e("上传","adtepter:结束时间"+data.getModifyTime());
        if (data.getModifyTime()==1){
            modiyTime="结束公开时间";
        }else {
            Date db=new Date(data.getModifyTime());
            SimpleDateFormat sdf2=new SimpleDateFormat("yy-MM-dd HH:mm");
            modiyTime = sdf2.format(db);
        }

        holder.setText(R.id.rootupload_schedule,data.getProgress())
                .setText(R.id.rootupload_explain,"["+str1+","+str2+"]")
                .setText(R.id.rootupload_startDate,startTime)
                .setText(R.id.rootupload_endDate,endTime)
                .setText(R.id.rootupload_projectNumber,data.getCode())
                .setText(R.id.rootupload_projectTitle,data.getPname())
                .setText(R.id.rootupload_storeName,data.getdName())
                .setText(R.id.rootupload_local,data.getAddress()+" "+data.getLocation())
                .setText(R.id.rootupload_bottomDate,modiyTime)
                .setText(R.id.rootupload_countData,""+subjectSize)
                .setText(R.id.rootupload_countAttchments,""+(ImageSize+reSize))
                .setCommonClickListener(mcommonClickListener);

        //复选框监听
        cb= holder.getView(R.id.rootupload_Rb);

        if(medo){
            holder.setViewVisibility(R.id.rootupload_Left, VISIBLE);
        }else {
            holder.setViewVisibility(R.id.rootupload_Left,GONE);
        }
        cb. setChecked(map.get(position));
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbInteface!=null){
                    cbInteface.CbClick(position,map);
                }
                /*map.put(position, !map.get(position));
                //刷新适配器
                notifyDataSetChanged();*/
            }
        });
        // 设置状态
        if (map.get(position) == null) {
            map.put(position, false);
        }
    }

    // 构造方法
    public UploadAdapter(Context context, List<ProjectsDB> dataList, int layoutId, CommonViewHolder.onItemCommonClickListener commonClickListener) {
        super(context, dataList, layoutId);
        this.mcommonClickListener = commonClickListener;
        // 默认为不选中
        initCheck(false);
    }


    // 全选按钮获取状态
    public Map<Integer, Boolean> getMap() {
        // 返回状态
        return this.map;
    }
    //复选框显示和隐藏
    public boolean checked_true(){
        return medo=true;
    }
    public boolean checked_false()
    {
        return  medo=false;
    }
    public interface CbInteface{
        void CbClick(int position,Map<Integer,Boolean> map);
    }
    public void setCbInteface(CbInteface inteface){
        this.cbInteface=inteface;
    }

}
