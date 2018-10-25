package net.coahr.three3.three.NetWork;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by yuwei on 2018/3/16.
 */

public class UpLoadTask implements Runnable {

    public static final int UPLOAD_STATUS_UPLOADING = 0;
    public static final int UPLOAD_STATUS_ERROR = 1;
    public static final int UPLOAD_STATUS_PAUSE = 2;
    private static String FILE_MODE = "rwd";
    private OkHttpClient mClient;
    private UploadTaskListener mListener;
    public Builder mBuilder;
    private String id;
    private String url;
    private String fileName;
    private int uploadStatus;
    private int chunck,chuncks;
    private int position;
    private int errorCode;

    static String BOUNDARY = "----------" + System.currentTimeMillis();
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("multipart/form-data;boundary=" + BOUNDARY);

    public UpLoadTask(Builder builder)
    {
        mBuilder    = builder;
        mClient     = new OkHttpClient();
        this.id     = mBuilder.id;
        this.url    = mBuilder.url;
        this.fileName = mBuilder.fileName;
        this.uploadStatus = mBuilder.uploadStatus;
        this.chunck = mBuilder.chunck;
        setmListener(mBuilder.listener);

    }
    private void addParams(MultipartBody.Builder builder , Map<String , String> params)
    {
        if (params != null && !params.isEmpty())
        {
            for (String key : params.keySet())
            {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\"") , RequestBody.create(null , params.get(key)));
            }
        }
    }
    @Override
    public void run() {

        try {
            int blockLength = 1024 * 1024;
            File file = new File("文件路径"+fileName);
            if (file.length() % blockLength == 0)
            {
                chuncks = (int) (file.length()/blockLength);
            }
            else
            {
                chuncks = (int) (file.length()/blockLength+1);
            }
            while (chunck <= chuncks && uploadStatus != UPLOAD_STATUS_PAUSE && uploadStatus != UPLOAD_STATUS_ERROR)
            {
                uploadStatus = UPLOAD_STATUS_UPLOADING;
                Map<String , String> params = new HashMap<String, String>();
                final byte[] mBlock = FileUtils.getBlock((chunck -1)*blockLength , file , blockLength);
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                addParams(builder , params);
                RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN , mBlock);
                builder.addFormDataPart("后台字段",fileName , requestBody);
                Request request = new Request.Builder()
                                                .url("")
                                                .post(builder.build())
                                                .build();
                Response response = null;
                response = mClient.newCall(request).execute();
                if (response.isSuccessful())
                {
                    onCallBack();
                    chunck++;
                }
                else
                {
                    uploadStatus = UPLOAD_STATUS_ERROR;
                    onCallBack();
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
            uploadStatus = UPLOAD_STATUS_ERROR;
            onCallBack();
        }

    }

    public void setmClient(OkHttpClient mClient) {
        this.mClient = mClient;
    }

    public void setmListener(UploadTaskListener mListener) {
        this.mListener = mListener;
    }

    private void onCallBack()
    {
        mHandler.sendEmptyMessage(uploadStatus);
    }
    Handler mHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg) {
            int code = msg.what;
            switch (code)
            {
                case UPLOAD_STATUS_UPLOADING:
                    mListener.onUploading(UpLoadTask.this ,getDownLoadPercent() , position);
                    break;
                case UPLOAD_STATUS_ERROR:
                    mListener.onError(UpLoadTask.this, errorCode,position);
                    break;
                case UPLOAD_STATUS_PAUSE:
                    mListener.onPause(UpLoadTask.this);
                    break;
            }
        }
    };

    private String getDownLoadPercent() {
        String baifenbi = "0";// 接受百分比的值
        if (chunck >= chuncks) {
            return "100";
        }
        double baiy = chunck * 1.0;
        double baiz = chuncks * 1.0;
        // 防止分母为0出现NoN
        if (baiz > 0) {
            double fen = (baiy / baiz) * 100;
            //NumberFormat nf = NumberFormat.getPercentInstance();
            //nf.setMinimumFractionDigits(2); //保留到小数点后几位
            // 百分比格式，后面不足2位的用0补齐
            //baifenbi = nf.format(fen);
            //注释掉的也是一种方法
            DecimalFormat df1 = new DecimalFormat("0");//0.00
            baifenbi = df1.format(fen);
        }
        return baifenbi;
    }


    public static class Builder
    {
        private String id;
        private String url;
        private String fileName;
        private int uploadStatus;
        private int chunck;
        private UploadTaskListener listener;

        public Builder setId(String id)
        {
            this.id = id;
            return this;
        }

        public Builder setUrl(String url)
        {
            this.url = url;
            return this;
        }

        public Builder setUploadStatus(int uploadStatus) {
            this.uploadStatus = uploadStatus;
            return this;
        }

        public Builder setChunck(int chunck) {
            this.chunck = chunck;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setListener(UploadTaskListener listener) {
            this.listener = listener;
            return this;
        }
        public UpLoadTask build()
        {
            return new UpLoadTask(this);
        }
    }



}
