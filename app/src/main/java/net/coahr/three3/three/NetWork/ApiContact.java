package net.coahr.three3.three.NetWork;

/**
 * Created by yuwei on 2018/3/14.
 */

public class ApiContact {


    //测试接口
    public static final String hot = "app/home/hot.htm";
    
    public static final String BASE_URL = "http://survey.three3.cn:8081/three_research/";
   // public static final String BASE_URL="http://4df9a1a9.ngrok.io/three_research/";

    //public static final String BASE_URL="http://192.168.191.1:8080/three_research/";
     //登陆接口
    public static final String login="app/login.htm";
    //首页请求接口
    public static final String getHomeData="app/home/list.htm";

    public static final String attendanceInfo="app/attendance/infor.htm";

    public static final String attendance="app/attendance/signIn.htm";

    public static final String attendanceHistory="app/attendance/history.htm";

    public static final String motifyPassword="app/update/password.htm";

    public static final String feedBack="app/opinion/add.htm";

    public static final String remark="app/attendance/remark.htm";
    //题目信息接口
    public static final String getSubjects="app/question/list.htm";

    public static final String getVerifyData = "app/censor/list.htm";

    public static final String getHomeSearch="app/home/search.htm";

    public static final String getVerifyInfo = "app/censor/infor.htm";

    public static final String getVerifyDetail = "app/censor/detail.htm";

    public static final String uploadImageFiles = "app/answer/uploadAnswer.htm";

    public static final String downloadTime = "app/home/download.htm";

    public static final String uploadDate="app/answer/callback.htm";
}
