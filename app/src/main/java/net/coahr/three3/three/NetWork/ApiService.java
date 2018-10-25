package net.coahr.three3.three.NetWork;


import net.coahr.three3.three.Base.BaseModel;
import net.coahr.three3.three.Model.AttendanceHistoryModel;
import net.coahr.three3.three.Model.AttendanceInfoModel;
import net.coahr.three3.three.Model.AttendanceModel;
import net.coahr.three3.three.Model.HomeDataListModle;
import net.coahr.three3.three.Model.HomeSearchModel;
import net.coahr.three3.three.Model.LoginModel;
import net.coahr.three3.three.Model.SubjectListModel;
import net.coahr.three3.three.Model.VerifyDataModel;
import net.coahr.three3.three.Model.VerifyInfoDetailModel;
import net.coahr.three3.three.Model.VerifyInfoModel;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by yuwei on 2018/3/13.
 */

public interface ApiService {



    /**
     * 登陆接口
     * @param para
     * @return
     */
    @FormUrlEncoded
    @POST(ApiContact.login)
    Observable<BaseModel<LoginModel>>getLogin(@FieldMap Map<String, Object> para);

    /**
     * 首页数据请求接口
     * @param para
     * @return
     */
    @FormUrlEncoded
    @POST(ApiContact.getHomeData)
    Observable<BaseModel<HomeDataListModle>>getHomeData(@FieldMap Map<String, Object> para);

    /**
     * 题目列表请求体
     * @param para
     * @return
     */
    @FormUrlEncoded
    @POST(ApiContact.getSubjects)
    Observable<BaseModel<SubjectListModel>> getSubjects(@FieldMap Map<String , Object> para);

    @FormUrlEncoded
    @POST(ApiContact.attendanceInfo)
    Observable<BaseModel<AttendanceInfoModel>>getAttendanceInfo(@FieldMap Map<String , Object>para);

    @FormUrlEncoded
    @POST(ApiContact.attendance)
    Observable<BaseModel<AttendanceModel>>attendance(@FieldMap Map<String , Object> para);

    @FormUrlEncoded
    @POST(ApiContact.attendanceHistory)
    Observable<BaseModel<AttendanceHistoryModel>>getAttendanceHistory(@FieldMap Map<String , Object> para);

    @FormUrlEncoded
    @POST(ApiContact.motifyPassword)
    Observable<BaseModel>motifyPassword(@FieldMap Map<String , Object>para);

    @FormUrlEncoded
    @POST(ApiContact.feedBack)
    Observable<BaseModel>feedBack(@FieldMap Map<String , Object>para);

    @FormUrlEncoded
    @POST(ApiContact.remark)
    Observable<BaseModel>remark(@FieldMap Map<String , Object>para);

    @FormUrlEncoded
    @POST(ApiContact.getVerifyData)
    Observable<BaseModel<VerifyDataModel>>getVerifyData(@FieldMap Map<String , Object>para);

    @FormUrlEncoded
    @POST(ApiContact.getHomeSearch)
    Observable<BaseModel<HomeSearchModel>>getHomeSearchData(@FieldMap Map<String , Object>para);

    @FormUrlEncoded
    @POST(ApiContact.getVerifyInfo)
    Observable<BaseModel<VerifyInfoModel>>getVerifyInfoData(@FieldMap Map<String , Object>para);

    @FormUrlEncoded
    @POST(ApiContact.getVerifyDetail)
    Observable<BaseModel<VerifyInfoDetailModel>>getVerifyDetailData(@FieldMap Map<String , Object>para);

    @POST(ApiContact.uploadImageFiles)
    Observable<BaseModel>uploadFiles(@Body MultipartBody bodys);

/*
* 下载
* */

    @FormUrlEncoded
    @POST(ApiContact.downloadTime)
    Observable<BaseModel<HomeSearchModel>>download(@FieldMap Map<String , Object>para);


    /**
     * 上传post
     */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded charset=utf-8")
    @POST(ApiContact.uploadDate)
    Observable<BaseModel>uploadDate (@FieldMap Map<String,Object> para);

}
