package net.coahr.three3.three.Base;

import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by yuwei on 2018/3/19.
 */

//请求map
public class BasePara {

    private Map<String , Object> data;

    public void setData(Map<String, Object> data) {
        this.data = data;
    }


    public Map<String, Object> getData() {
        return data;
    }



}

