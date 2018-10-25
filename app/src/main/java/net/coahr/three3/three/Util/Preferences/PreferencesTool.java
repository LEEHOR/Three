package net.coahr.three3.three.Util.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArrayMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.coahr.three3.three.Model.HomeDataListModle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yuwei on 2018/4/25.
 */

public class PreferencesTool {

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    public PreferencesTool(Context context)
    {
        this.mContext = context;
        mSharedPreferences = context.getSharedPreferences("userPreferences",Context.MODE_PRIVATE );
        mEditor = mSharedPreferences.edit();
    }

    //key:"logins"
    public void setLoginState(String key , boolean value)
    {
        mEditor.putBoolean(key , value);
        mEditor.commit();
    }
    public boolean getLoginState(String key)
    {
        boolean state = mSharedPreferences.getBoolean(key , false);

        return  state;
    }
    //key:"uploadMethod"
    public void setUploadMethod(String key , boolean value)
    {
        mEditor.putBoolean(key , value);
        mEditor.commit();
    }

    public boolean getUploadMethod(String key)
    {
        boolean state = mSharedPreferences.getBoolean(key , false);

        return  state;
    }
    //key:"uploadQuality"
    public void setUploadQuality(String key , boolean value)
    {
        mEditor.putBoolean(key , value);
        mEditor.commit();
    }

    public boolean getUploadQuality(String key)
    {
        boolean state = mSharedPreferences.getBoolean(key , false);

        return  state;
    }

    //key:"browseMethod"
    public void setBrowseMethod(String key , boolean value)
    {
        mEditor.putBoolean(key , value);
        mEditor.commit();
    }

    public boolean getBrowseMethod(String key)
    {
        boolean state = mSharedPreferences.getBoolean(key , false);

        return  state;
    }
    public void  setProjectStartPhotoPage(String key,String page){
        mEditor.putString(key,page);
        mEditor.commit();
    }

    public String getProjectStartPhotoPage(String key){
        String anInt = mSharedPreferences.getString(key, null);
        return anInt;
    }

    public void setHomeLocation(String key1,String key2,double latitude,double longitude){
        mEditor.putString(key1,String.valueOf(latitude) );
        mEditor.commit();
        mEditor.putString(key2, String.valueOf(longitude));
        mEditor.commit();
    }
    public Map<String,String> getHomeLocation(String key1,String key2){
        String string = mSharedPreferences.getString(key1, null);
        String string1 = mSharedPreferences.getString(key2, null);
        Map<String,String> map=new ArrayMap<>();
        map.put(key1,string);
        map.put(key2,string1);
        return  map;
    }

    public void setUserInfo(String key1 ,String key2,String name , String sessionId)
    {
        mEditor.putString(key1 , name);
        mEditor.commit();
        mEditor.putString(key2 , sessionId);
        mEditor.commit();
    }

    public Map<String , String>getUserInfo(String key1 , String key2)
    {
        String name = mSharedPreferences.getString(key1 , null);
        String sessionId = mSharedPreferences.getString(key2 , null);
        Map<String , String> map = new ArrayMap<>();
        map.put(key1 , name);
        map.put(key2 , sessionId);
        return map;

    }

    public void  setUserName(String key , String name)
    {
        mEditor.putString(key , name);
        mEditor.commit();
    }

    public void  setSessionId(String key , String sessionId)
    {
        mEditor.putString(key , sessionId);
        mEditor.commit();

    }



    public String getUserName(String key)
    {
        return mSharedPreferences.getString(key , null);
    }

    public String getSessionId(String key)
    {
        return mSharedPreferences.getString(key , null);
    }

    public void setTop_rb1(String key,boolean b){
       mEditor.putBoolean(key,b);
       mEditor.commit();
    }
    public void setTop_rb2(String key,boolean b){
        mEditor.putBoolean(key,b);
        mEditor.commit();
    }
    public void setTop_rb3(String key,boolean b){
        mEditor.putBoolean(key,b);
        mEditor.commit();
    }
    public boolean getTob_rb1(String key){
        boolean aBoolean = mSharedPreferences.getBoolean(key, false);
        return  aBoolean;
    }
    public boolean getTob_rb2(String key){
        boolean aBoolean = mSharedPreferences.getBoolean(key, false);
        return  aBoolean;
    }
    public boolean getTob_rb3(String key){
        boolean aBoolean = mSharedPreferences.getBoolean(key, false);
        return  aBoolean;
    }
    public void setIsNetWorked(String key,boolean b){
        mEditor.putBoolean(key,b);
        mEditor.commit();
    }
    public boolean getIsNetWork(String key){
        return  mSharedPreferences.getBoolean(key,true);
    }

    //缓存List数据到本地
    public void setHomeFragmentDataList(String key, List<HomeDataListModle.AllListBean> datalist){
        if (null == datalist || datalist.size() <= 0)
            return;

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        mEditor.putString(key, strJson);
        mEditor.commit();
    }
    //取出本地缓存List数据
    public  List<HomeDataListModle.AllListBean> getHomeFragmentDataList(String key){
        List<HomeDataListModle.AllListBean> dataList=new ArrayList<HomeDataListModle.AllListBean>();
        String strJson = mSharedPreferences.getString(key, null);
        if (null == strJson) {
            return dataList;
        }
        Gson gson = new Gson();
        dataList = gson.fromJson(strJson, new TypeToken<List<HomeDataListModle.AllListBean>>() {
        }.getType());
        return dataList;
    }

    //设置单题录音
    public void setSingleRecorder(String key,boolean b){
        mEditor.putBoolean(key,b);
        mEditor.commit();

    }
    //设置全局录音
    public void setAllRecorder(String key,boolean b){
        mEditor.putBoolean(key,b);
        mEditor.commit();

    }
    //获取单体录音
    public boolean getSingleRecorder(String key){
       return mSharedPreferences.getBoolean(key,false);
    }

    //获取全局录音
    public boolean getAllRecorder(String key){
        return mSharedPreferences.getBoolean(key,false);
    }

    //保存当前访问项目Id
    public void setProjectId(String key,String b){
        mEditor.putString(key,b);
        mEditor.commit();

    }
    public String getProjectId(String key){
        return  mSharedPreferences.getString(key,null);
    }

    public void setIsRecorderAll(String key,boolean b){
        mEditor.putBoolean(key,b);
        mEditor.commit();
    }

    public boolean getIsRecorderAll(String key){
        return mSharedPreferences.getBoolean(key,false);
    }

    //录音模式
    public void setRecorderModel(String key,int b){
        mEditor.putInt(key,b);
        mEditor.commit();
    }

    public int getRecorderModel(String key){
        return mSharedPreferences.getInt(key,1);
    }

    public void setProjectTarget(String key,String b){
        mEditor.putString(key,b);
        mEditor.commit();
    }

    public String getProjectTarget(String key){
        return mSharedPreferences.getString(key,null);
    }


    /* public void  setImagelist(String key,List<String> list){
         if (null == list || list.size() <= 0)
             return;

         Gson gson = new Gson();
         //转换成json数据，再保存
         String strJson = gson.toJson(list);
         mEditor.clear();
         mEditor.putString(key, strJson);
         mEditor.commit();
     }

    public  List  getImagelist(String key) {
        List<String> datalist=new ArrayList<>();
        String strJson = mSharedPreferences.getString(key, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<String>>() {
        }.getType());
        return datalist;

    }*/

    public void setHomeFragmentPermission(String key,boolean permission){
        mEditor.putBoolean(key,permission);
        mEditor.commit();
    }
    public Boolean getHomeFragmentPermission(String key){
       return  mSharedPreferences.getBoolean(key,false);
    }

    public void setProjectStartProgress(String key,int progress){
        mEditor.putInt(key,progress);
        mEditor.commit();
    }

    public int getProjectStartProgress(String key) {
        return mSharedPreferences.getInt(key,0);
    }

    public void setActivityPermission(String key, boolean b) {
        mEditor.putBoolean(key,b);
        mEditor.commit();
    }

    public boolean getmActivityPermission(String key) {
        return mSharedPreferences.getBoolean(key,false);
    }

    public void setExpiration(String key, String b){
        mEditor.putString(key,b);
        mEditor.commit();
    }
    public String getExpiration(String key){
        return mSharedPreferences.getString(key,null);
    }

    public void setSecurityToken(String key, String b){
        mEditor.putString(key, b);
        mEditor.commit();
    }
    public String getSecurityToken(String key){
        return  mSharedPreferences.getString(key,null);
    }

    public void setAK(String key, String b){
        mEditor.putString(key, b);
        mEditor.commit();
    }
    public String getAK(String key){
        return  mSharedPreferences.getString(key,null);
    }

    public void setSK(String key, String b){
        mEditor.putString(key, b);
        mEditor.commit();
    }
    public String getSK(String key){
        return  mSharedPreferences.getString(key,null);
    }

    public void setAttendanceInter(String key,boolean b){
        mEditor.putBoolean(key,b);
        mEditor.commit();
    }

    public boolean getAttendanceInter(String key){
        return mSharedPreferences.getBoolean(key,false);
    }
}
