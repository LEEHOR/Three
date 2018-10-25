package net.coahr.three3.three.Verify;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import net.coahr.three3.three.Base.BaseActivity;
import net.coahr.three3.three.Base.BaseModel;
import net.coahr.three3.three.Base.BasePara;
import net.coahr.three3.three.DBbase.ProjectsDB;
import net.coahr.three3.three.DBbase.SubjectsDB;
import net.coahr.three3.three.Model.VerifyInfoModel;
import net.coahr.three3.three.NetWork.RetrofitManager;
import net.coahr.three3.three.R;
import net.coahr.three3.three.RecyclerViewAdapter.VerifyDetailAdapter;
import net.coahr.three3.three.Util.JDBC.DataBaseWork;
import net.coahr.three3.three.Util.LogUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yuwei on 2018/4/8.
 */

public class VerifyDetailActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private Boolean verifyFlag;
    private String mProjectId;
    private int    mStatus;
    private  VerifyInfoModel verifyInfoModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_detail);
        findUI();
        LogUtils.tagPrefix="审核";
        int status = getIntent().getIntExtra("status" , -1);
        if (status == -1)
        {
            verifyFlag = false;
        }
        else if(status == 1)
        {
            verifyFlag = true;
        }
        ((VerifyDetailAdapter)adapter).setVerifyFlag(verifyFlag);

        setTitle((TextView) naviBar.findViewById(R.id.title), verifyFlag ? "已通过" : "未通过");

        String projectId = getIntent().getStringExtra("projectId");

        requestRemote(projectId , status);
        mProjectId = projectId;
        mStatus = status;

    }

    @Override
    public void findUI() {
        super.findUI();
        configureNaviBar(naviBar.findViewById(R.id.left) , null);
        mRecyclerView   = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(VerifyDetailActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        setAdapter(new VerifyDetailAdapter(VerifyDetailActivity.this , null , R.layout.item_recyclerview_verifydetail_fail , null));
        mRecyclerView.setAdapter(adapter);
        ((VerifyDetailAdapter)getAdapter()).setReviseClickListenner(new VerifyDetailAdapter.ReviseClickListenner() {
            @Override
            public void revise(int position) {
                Intent intent = new Intent(VerifyDetailActivity.this , VerifyReviseActivity.class);
                intent.putExtra("projectId" , mProjectId);
                intent.putExtra("status" , mStatus);
                intent.putExtra("index" , position);
                pushActivity(intent);
            }
        });
    }

    protected void requestRemote(final String projectId , int status) {
        super.requestRemote();

        Map<String, Object> map = new HashMap<>();
        map.put("projectId" , projectId);
        map.put("status" , status);
        BasePara para = new BasePara();
        para.setData(map);
        Subscription subscription= RetrofitManager.getInstance()
                .createService(para)
                .getVerifyInfoData(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("onError",e);
                    }

                    @Override
                    public void onNext(BaseModel model) {
                        verifyInfoModel = (VerifyInfoModel) model.getData();
                        verifyInfoModel.getList().get(0).getStage();
                       LogUtils.d("onNext"+verifyInfoModel.getList().get(0).getStage());
                        if (model.getResult().equals("1"))
                        {

                            if (verifyInfoModel.getList()!= null && verifyInfoModel.getList().size() > 0)
                            {
                                adapter.Update(verifyInfoModel.getList());
                                List<ProjectsDB> projectsDBS = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "pid=?", projectId);
                                if (projectsDBS!=null && !projectsDBS.isEmpty()  &&projectsDBS.size()>0){
                                    projectsDBS.get(0).setpUploadStatus(0);
                                    projectsDBS.get(0).setToDefault("pUploadStatus");
                                    projectsDBS.get(0).setStage(String.valueOf(verifyInfoModel.getList().get(0).getStage()));
                                    projectsDBS.get(0).update(projectsDBS.get(0).getId());

                                    for (VerifyInfoModel.verifyInfoListBean bean: verifyInfoModel.getList()) {

                                        List<SubjectsDB> subjectsDBList = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "projectsdb_id=? and ht_id=? and dh=?", String.valueOf(projectsDBS.get(0).getId()), bean.getId(),"0");
                                        if (subjectsDBList !=null && !subjectsDBList.isEmpty() && subjectsDBList.size()>0){
                                            subjectsDBList.get(0).setsUploadStatus(0);
                                            subjectsDBList.get(0).setToDefault("sUploadStatus");
                                            subjectsDBList.get(0).setCensor(0);
                                            subjectsDBList.get(0).setDh("1");
                                            subjectsDBList.get(0).setIsComplete(0);
                                            subjectsDBList.get(0).setToDefault("censor");
                                            subjectsDBList.get(0).update(subjectsDBList.get(0).getId());
                                        }

                                    }
                                }

                            }

                        }

                    }
                });
        addSubscription(subscription);

    }
}
