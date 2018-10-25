package net.coahr.three3.three.NetWork;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**OkHttp3Utils
 * Created by 李浩 on 2018/2/22.
 */

public class OkHttpCallBackUtil_request {
    private static final String BASE_URL = "http://192.168.191.1:8080/three_research";//请求接口根地址
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private OkHttpClient mOkHttpClient;//okHttpClient 实例
    private static volatile OkHttpCallBackUtil_request mGetUtil;

    /**
     * 初始化
     */
    public OkHttpCallBackUtil_request(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                try {
                    String text = URLDecoder.decode(message, "utf-8");
                    Log.e("OKHttp-----", text);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e("OKHttp-----", message);
                }
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mOkHttpClient=new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(30, TimeUnit.SECONDS)//设置读取超时时间
                .addNetworkInterceptor(new HttpLogging())
                .writeTimeout(30, TimeUnit.SECONDS)//设置写入超时时间
                .build();
    }

    //单例
    public static OkHttpCallBackUtil_request getInstance(){
        OkHttpCallBackUtil_request instance=mGetUtil;
        if(instance==null){
            synchronized(OkHttpCallBackUtil_request.class){
                instance = mGetUtil;
                if(instance==null){
                    instance= new OkHttpCallBackUtil_request();
                    mGetUtil=instance;
                }
            }
        }
        return instance;
    }

    /**
     * 统一为请求添加头信息
     *              自己完善
     * @return
     */
    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder();
        return builder;
    }
    /**
     *  get有参数同步请求
     * @param Address
     *              地址
     * @param paramsMap
     *               Map参数
     */
    public  String  sendOkHttpRequest_getByT(String Address, HashMap<String, String> paramsMap) {
        String res=null;
        StringBuilder tempParams = new StringBuilder();
        try {
            //处理参数
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                //对参数进行URLEncoder
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            //补全请求地址(http://地址?数据1名字=" + URLEncoder.encode(数据1,"utf-8") + "&数据2名字=" +URLEncoder.encode(数据2,"utf-8"))
            String requestUrl = String.format("%s/%s?%s", BASE_URL, Address, tempParams.toString());

            Request request =
                    addHeaders()
                    .url(requestUrl)
                    .build();
            Call call = mOkHttpClient.newCall(request);
            Response response = call.execute();
          res=  response.body().string();

        } catch (Exception e) {
        }
        return res;
    }

    /**
     * get有参数异步请求
     * @param actionUrl
     * @param paramsMap
     * @param callback
     */
    public  void sendOkHttpRequest_getByY(String actionUrl, HashMap<String, String> paramsMap,okhttp3.Callback callback){
        try {
            //处理参数
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            //补全请求地址(http://地址?数据1名字=" + URLEncoder.encode(数据1,"utf-8") + "&数据2名字=" +URLEncoder.encode(数据2,"utf-8"))
            String requestUrl = String.format("%s/%s?%s", BASE_URL, actionUrl, tempParams.toString());
            Request request=
                     addHeaders()
                    .url(requestUrl)
                    .build();
            mOkHttpClient.newCall(request).enqueue(callback);
        }catch (Exception e){

        }
    }

    /**
     * get无参数的异步请求
     * @param actionUrl
     * @param callback
     */
    public  void sendOkHttpRequest_getByY(String actionUrl,okhttp3.Callback callback){
        String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
        Request request=addHeaders()
                .url(requestUrl)
                .build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }
    /**
     * post同步请求json
     * @param actionUrl
     * @param paramsMap
     * @return
     */
    public String sendOkHttpRequest_postByT(String actionUrl, HashMap<String, String> paramsMap){
        String res=null;
        try {
            //处理参数
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            //补全请求地址(http://根地址/请求地址)
            String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
            //生成参数
            String params = tempParams.toString();
            //创建一个请求实体对象 RequestBody()
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
            Request request=addHeaders()
                    .url(requestUrl)
                    .post(body)
                    .build();
            Call call = mOkHttpClient.newCall(request);
            Response response = call.execute();
            res=response.body().string();
        }catch (Exception e){

        }
        return res;
    }

    /**
     * post异步请求json
     * @param actionUrl
     * @param paramsMap
     * @param callback
     */
    public  void sendOkHttpRequest_postByY(String actionUrl, HashMap<String, String> paramsMap,okhttp3.Callback callback){
        try {
            //处理参数
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            //获得请求地址
            String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
            //生成参数
            String params = tempParams.toString();
            //创建一个请求实体对象 RequestBody
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
            Request request=addHeaders()
                    .url(requestUrl)
                    .post(body)
                    .build();
            mOkHttpClient.newCall(request).enqueue(callback);
    }catch (Exception e){

        }
    }

    /**
     * post表单同步请求
     * @param actionUrl
     * @param paramsMap
     * @return
     */
    public String sendOkHttpRequest_postByT_From(String actionUrl, HashMap<String, String> paramsMap){
        String res=null;
        try {
            //创建一个FormBody.Builder
            FormBody.Builder builder = new FormBody.Builder();
            for (String key : paramsMap.keySet()) {
                //追加表单信息
                builder.add(key, paramsMap.get(key));
            }
            //生成表单实体对象
            RequestBody formBody = builder.build();
            //补全请求地址
            String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
            Request request = addHeaders()
                    .url(requestUrl)
                    .post(formBody)
                    .build();
            Call call = mOkHttpClient.newCall(request);
            Response response = call.execute();
            res = response.body().string();
        }catch (Exception e){

        }
      return res;
    }

    /**
     * post异步请求表单请求
     * @param actionUrl
     * @param paramsMap
     * @param callback
     */
    public  void sendOkHttpRequest_postByY_From(String actionUrl, Map<String, Object> paramsMap, okhttp3.Callback callback){
        try {
            //创建一个FormBody.Builder
            FormBody.Builder builder = new FormBody.Builder();
            for (String key : paramsMap.keySet()) {
                //追加表单信息
                builder.add(key,  (String)paramsMap.get(key));
            }
            //生成表单实体对象
            RequestBody formBody = builder.build();
            //补全请求地址
            String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
            Log.e("rr","地址"+requestUrl);
            Request request =new Request.Builder()
                    .url(requestUrl)
                    .post(formBody)
                    .build();
            mOkHttpClient.newCall(request).enqueue(callback);
    }catch (Exception e){
        }
    }

}
