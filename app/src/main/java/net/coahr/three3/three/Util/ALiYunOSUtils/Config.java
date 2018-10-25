package net.coahr.three3.three.Util.ALiYunOSUtils;

public class Config {
    // 访问的endpoint地址
    public static final String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";

    public static final String STSSERVER = "http://survey.three3.cn:8081/three_research/app/answer/oss/token.htm";//STS 地址

    //callback 测试地址  survey.three3.cn:8081
    public static final String callbackAddress = "http://192.168.191.1:8080/three_research/app/answer/oss/callback.htm";
    public static final String uploadFilePath = ""; //本地文件上传地址

    public static final String bucket = "three-research";
    public static final String uploadObject = "上传object名称";
    public static final String downloadObject = "下载object名称";

    public static final int DOWNLOAD_SUC = 1;
    public static final int DOWNLOAD_Fail = 2;
    public static final int UPLOAD_SUC = 3;
    public static final int UPLOAD_Fail = 4;
    public static final int UPLOAD_PROGRESS = 5;
    public static final int LIST_SUC = 6;
    public static final int HEAD_SUC = 7;
    public static final int RESUMABLE_SUC = 8;
    public static final int SIGN_SUC = 9;
    public static final int BUCKET_SUC = 10;
    public static final int GET_STS_SUC = 11;
    public static final int MULTIPART_SUC = 12;
    public static final int STS_TOKEN_SUC = 13;
    public static final int FAIL = 9999;
    public static final int REQUESTCODE_AUTH = 10111;
    public static final int REQUESTCODE_LOCALPHOTOS = 10112;


    public static final int MESSAGE_UPLOAD_2_OSS = 10002;

}
