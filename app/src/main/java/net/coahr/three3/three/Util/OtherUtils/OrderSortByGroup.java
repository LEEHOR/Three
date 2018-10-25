package net.coahr.three3.three.Util.OtherUtils;

import android.util.Log;

import net.coahr.three3.three.DBbase.ProjectsDB;
import net.coahr.three3.three.Model.HomeDataListModle;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

/**项目排序
 * Created by 李浩
 * 2018/4/11
 */
public class OrderSortByGroup {
    public static void ListSortByDestance(List<ProjectsDB> list, final boolean b){
        if (list!=null&&!list.isEmpty()){
            Collections.sort(list, new Comparator<ProjectsDB>() {
                @Override
                public int compare(ProjectsDB o1, ProjectsDB o2) {
                    int dt1 = Integer.parseInt(o1.getDistance());
                    int dt2 = Integer.parseInt(o2.getDistance());
                    if (b){
                        if (dt1 > dt2) {
                            return 1;
                        } else if (dt1 < dt2) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }else {
                        if (dt1 > dt2) {
                            return -1;
                        } else if (dt1 < dt2) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }

                    }
            });
            for (int i = 0; i < list.size(); i++) {
                Log.e("rr","距离排序"+list.get(i).getStartTime()+"距离为："+list.get(i).getDistance());
            }
        }else {
            Log.e("rr","距离排序为空");
        }

    }
    public static List<ProjectsDB> ListSortByStartTime(List<ProjectsDB> list, final boolean b){
        if (list!=null&&!list.isEmpty()){
            Collections.sort(list, new Comparator<ProjectsDB>() {
                @Override
                public int compare(ProjectsDB o1, ProjectsDB o2) {
                    long startTime1 = o1.getStartTime();
                    long startTime2 = o2.getStartTime();
                    if (b){
                        if (startTime1>startTime2){
                            return 1;
                        }else if (startTime1<startTime2){
                            return -1;
                        }else {
                            return 0;
                        }
                    }else {
                        if (startTime1 > startTime2){
                            return -1;
                        }else if (startTime1 < startTime2){
                            return 1;
                        }else {
                            return 0;
                        }
                    }

                }
            });
            for (int i = 0; i <list.size() ; i++) {
                Log.e("rr","开始时间排序"+list.get(i).getStartTime());
            }
        }else {
            Log.e("rr","开始时间排序为空");
        }

        return list;

    }
    public static List<ProjectsDB> ListSortByEndTime(List<ProjectsDB> list, final boolean b){
        if (list!=null&&!list.isEmpty()){
            Collections.sort(list, new Comparator<ProjectsDB>() {
                @Override
                public int compare(ProjectsDB o1, ProjectsDB o2) {
                    long endTime1 = o1.getEndTime();
                    long endTime2 = o2.getEndTime();
                    if (b){
                        if (endTime1>endTime2){
                            return 1;
                        }else if (endTime1 < endTime2){
                            return -1;
                        }else {
                            return 0;
                        }
                    }else {
                        if (endTime1 > endTime2){
                            return -1;
                        }else if (endTime1 < endTime2){
                            return 1;
                        }else {
                            return 0;
                        }
                    }


                }
            });
            for (int i = 0; i <list.size() ; i++) {
                Log.e("rr","结束时间排序"+list.get(i).getEndTime());
            }
        }else {
            Log.e("rr","结束时间排序为空");
        }
        return list;
    }

    //通过HashSet踢除重复元素并且按照自然顺序排列
    public static<T> List<T> removeDuplicate(List<T> list) {
        TreeSet set=new TreeSet(list);
        list.clear();
        list.addAll(set);
        return list;
    }
}
