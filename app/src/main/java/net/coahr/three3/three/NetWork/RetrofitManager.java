package net.coahr.three3.three.NetWork;




import android.util.Log;

import net.coahr.three3.three.Base.BasePara;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by yuwei on 2018/3/13.
 */

public class RetrofitManager {

    private static RetrofitManager mRetrofitManager;
    private Retrofit mRetrofit;

    public static synchronized RetrofitManager getInstance()
    {
        if (mRetrofitManager == null)
        {
            mRetrofitManager = new RetrofitManager();
        }
        return mRetrofitManager;
    }

    private  RetrofitManager( )
    {
       // HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
      //  loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                try {
                    String text = URLDecoder.decode(message, "utf-8");
                    Log.d("OKHttp-----", text);
                } catch (UnsupportedEncodingException e) {
                   // Log.d("OKHttp-----", message);
                    e.printStackTrace();
                }

            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .addNetworkInterceptor(new HttpLogging())
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(ApiContact.BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public ApiService createService(BasePara model)
    {
        return mRetrofit.create(ApiService.class);
    }

}
