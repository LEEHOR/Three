package net.coahr.three3.three.Util.ALiYunOSUtils;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;

public class OssClient {
    public  static  OssClient ossClient;
    private  OSSCredentialProvider credentialProvider;
    private static   Context mcontext;
    public OssClient() {

    }
    public static   OssClient getInstance(Context context){
        mcontext=context;
        if (ossClient ==null){
            ossClient=new OssClient();
        }
        return ossClient;
    }
    public OSS getOss(){
        OSSCredentialProvider token = getToken();
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        OSS oss = new OSSClient(mcontext.getApplicationContext(),Config.endpoint, token, conf);
        return  oss;
    }

    private OSSCredentialProvider getToken(){
       // credentialProvider = new OSSAuthCredentialsProvider(Config.STSSERVER);
                credentialProvider = new Provider(Config.STSSERVER,mcontext);
            Log.e("OSSCredentialProvider", "getToken: "+"请求" );
        return  credentialProvider;
    }
}
