package net.coahr.three3.three.RecyclerViewAdapter;

import android.content.Context;
import android.util.Log;

import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.CommonRecycleAdapter;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.CommonViewHolder;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.MultiTypeSupport;
import net.coahr.three3.three.DBbase.ImagesDB;
import net.coahr.three3.three.DBbase.ProjectsDB;
import net.coahr.three3.three.DBbase.RecorderFilesDB;
import net.coahr.three3.three.DBbase.SubjectsDB;
import net.coahr.three3.three.Module.HomeFragment;
import net.coahr.three3.three.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeAdapter extends CommonRecycleAdapter<ProjectsDB> implements MultiTypeSupport<ProjectsDB> {
private HomeFragment homeFragment;
public boolean adapterMap=false;
private CommonViewHolder.onItemCommonClickListener itemCommonClickListener;
private int shuju;
private int shangchuanI;
private int shangchuanR;
private Map<Integer,Integer> mapS;
    private Map<Integer,Integer> mapI;
    private Map<Integer,Integer> mapR;

    public HomeAdapter(Context context, List<ProjectsDB> dataList) {
        super(context, dataList, R.layout.item_root_unlocation_recycleview);
    }

    public HomeAdapter(Context context, List<ProjectsDB> dataList, CommonViewHolder.onItemCommonClickListener itemCommonClickListener) {
        super(context, dataList,  R.layout.item_root_unlocation_recycleview);
        mapS=new HashMap<>();
        mapI=new HashMap<>();
        mapR=new HashMap<>();
        shuju=0;
        shangchuanI=0;
        shangchuanR=0;
        this.itemCommonClickListener = itemCommonClickListener;
        this.multiTypeSupport=this;

        /*if (dataList !=null && dataList.size()>0){
            for (int i = 0; i <dataList.size() ; i++) {  //Pro
                if (dataList.get(i).getSubjectsDBList()!=null && dataList.get(i).getSubjectsDBList().size()>0) { //Sub
                    for (int j = 0; j < dataList.get(i).getSubjectsDBList().size(); j++) {
                        if (dataList.get(i).getSubjectsDBList().get(j).getsUploadStatus() == 0) {
                        shuju += 1;
                        mapS.put(dataList.get(i).getId(), shuju);
                        List<ImagesDB> imagesDBList = dataList.get(i).getSubjectsDBList().get(j).getImagesDBList();
                        if (imagesDBList != null && imagesDBList.size() > 0) {
                            for (int k = 0; k < imagesDBList.size(); k++) {
                                shangchuanI += 1;
                            }
                            mapI.put(dataList.get(i).getId(), shangchuanI);
                        } else {
                            mapI.put(dataList.get(i).getId(), 0);
                        }
                        List<RecorderFilesDB> recorderFiles = dataList.get(i).getSubjectsDBList().get(j).getRecorderFiles();
                        if (recorderFiles != null && recorderFiles.size() > 0) {
                            shangchuanR += 1;
                            mapR.put(dataList.get(i).getId(), shangchuanR);
                        } else {
                            mapR.put(dataList.get(i).getId(), 0);
                        }

                    }else {
                            mapS.put(dataList.get(i).getId(),0);
                            mapI.put(dataList.get(i).getId(),0);
                            mapR.put(dataList.get(i).getId(),0);
                        }
                }
                }else {
                    mapS.put(dataList.get(i).getId(),0);
                   mapI.put(dataList.get(i).getId(),0);
                   mapR.put(dataList.get(i).getId(),0);
                }
            }
        }*/
        adapterMap=true;

    }
    public boolean getAdapterMap(){
        return adapterMap;
    }
    @Override
    protected void bindData(CommonViewHolder holder, ProjectsDB data, int position) {
        shuju=0;
        shangchuanI=0;
        shangchuanR=0;
        List<SubjectsDB> subjectsDBList = data.getSubjectsDBList();
        if (subjectsDBList !=null && subjectsDBList.size()>0) {
            for (int i = 0; i < subjectsDBList.size(); i++) {
                if (subjectsDBList.get(i).getsUploadStatus() == 0) {
                shuju += 1;
                mapS.put(data.getId(), shuju);
                List<ImagesDB> imagesDBList = subjectsDBList.get(i).getImagesDBList();
                if (imagesDBList != null && imagesDBList.size() < 0) {
                    for (int j = 0; j < imagesDBList.size(); j++) {
                        shangchuanI += 1;
                    }
                    mapI.put(data.getId(), shangchuanI);
                } else {
                    mapI.put(data.getId(), 0);
                }

                List<RecorderFilesDB> recorderFiles = subjectsDBList.get(i).getRecorderFiles();
                if (recorderFiles != null && recorderFiles.size() > 0) {
                    shangchuanR += 1;
                    mapR.put(data.getId(), shangchuanR);
                } else {
                    mapR.put(data.getId(), 0);
                }
            }else {
                    mapS.put(data.getId(),0);
                    mapR.put(data.getId(),0);
                    mapI.put(data.getId(),0);
                }
            }
        }else {
           mapS.put(data.getId(),0);
           mapR.put(data.getId(),0);
           mapI.put(data.getId(),0);
        }
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
        /*开始时间*/
        Date d = new Date(data.getStartTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startTime = sdf.format(d);
        /*结束时间*/
        String endTime="";
        if (data.getEndTime()==1){
                endTime="结束公开时间";
        }else {
            Date de=new Date(data.getEndTime());
             endTime = sdf.format(de);
        }
        /*更新时间*/
        String modiyTime="";
        Log.e("rr","adtepter:结束时间"+data.getModifyTime());
            Date db=new Date(data.getModifyTime());
            SimpleDateFormat sdf2=new SimpleDateFormat("yy-MM-dd HH:mm");
            modiyTime = sdf2.format(db);


        holder.setText(R.id.rootComplete_schedule,data.getProgress())
                .setText(R.id.rootComplete_explain,"["+str1+","+str2+"]")
                .setText(R.id.rootComplete_startDate,startTime)
                .setText(R.id.rootComplete_endDate,endTime)
                .setText(R.id.rootComplete_projectNumber,data.getCode())
                .setText(R.id.rootComplete_projectTitle,data.getPname())
                .setText(R.id.rootComplete_storeName,data.getdName())
                .setText(R.id.rootComplete_local,data.getAddress()+" "+data.getLocation())
                .setText(R.id.rootComplete_bottomDate,modiyTime)
                .setText(R.id.rootComplete_countData,String.valueOf(mapS.get(data.getId())))
                .setText(R.id.rootComplete_countAttchments,String.valueOf((mapI.get(data.getId())+mapR.get(data.getId()))))
                .setCommonClickListener(itemCommonClickListener);
    }

    @Override
    public int getLayoutId(ProjectsDB item, int position) {

        if (item.getCompleteStatus()==1 && item.getDownloadTime()!=-1) {
            return R.layout.item_root_unstart_recycleview;
        } else if(item.getCompleteStatus()==2) {
            return R.layout.item_root_uncomplete_recycleview;
        } else if(item.getCompleteStatus()==3) {
            return  R.layout.item_root_complete_recycleview;
        } else if (item.getCompleteStatus()==1 && item.getDownloadTime()==-1){
            return R.layout.item_root_unlocation_recycleview;
        }
        return R.layout.item_root_unstart_recycleview;
    }
}
