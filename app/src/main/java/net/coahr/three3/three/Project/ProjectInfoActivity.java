package net.coahr.three3.three.Project;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.coahr.three3.three.Base.BaseCheckPermissionActivity;
import net.coahr.three3.three.DBbase.ImagesDB;
import net.coahr.three3.three.DBbase.ProjectsDB;
import net.coahr.three3.three.DBbase.RecorderFilesDB;
import net.coahr.three3.three.DBbase.SubjectsDB;
import net.coahr.three3.three.DBbase.UsersDB;
import net.coahr.three3.three.Model.HomeSearchModel;
import net.coahr.three3.three.Popupwindow.AlertDialogs.CommomDialog;
import net.coahr.three3.three.ProjectStartActivity;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.JDBC.DataBaseWork;
import net.coahr.three3.three.Util.OtherUtils.ToastUtils;
import net.coahr.three3.three.Util.Preferences.PreferencesTool;
import net.coahr.three3.three.Verify.BrowseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by yuwei on 2018/4/17.
 */

public class ProjectInfoActivity extends BaseCheckPermissionActivity {
    private ImageView kaoqingBtn ,fangwenBtn , attachmentBtn;
    private LinearLayout    mAdressView;
    private  ProjectsDB homeList; //首页传值
    private PreferencesTool mPreferencesTool;
    private String sessionId;
    private  List<UsersDB> usersDBS;
    private HomeSearchModel.SearchListBean searchActivity;
    private String Pid,userName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projectinfo);

        mPreferencesTool=new PreferencesTool(this);

        sessionId = mPreferencesTool.getSessionId("sessionId");
       userName= mPreferencesTool.getUserName("name");
        //首页传值在这边接收
        Intent in =getIntent();

        /**
         * 首页homeFragment
         */
        homeList = (ProjectsDB) in.getSerializableExtra("homeFragment");
        if (homeList!=null){
            Pid = homeList.getPid();

           String areaAddress=homeList.getAddress();
            String dname = homeList.getdName();
            double longitude = homeList.getLongitude();

        }

        /**
         * 搜索页面SearchActivity
         */
        searchActivity = (HomeSearchModel.SearchListBean) in.getSerializableExtra("searchActivity");
        if (searchActivity!=null){
            Pid=searchActivity.getId();
        }
        findUI();
    }

    @Override
    public void findUI() {
        super.findUI();
        setTitle((TextView) naviBar.findViewById(R.id.title), "项目信息");
        configureNaviBar(naviBar.findViewById(R.id.left), naviBar.findViewById(R.id.right));

        getRightBtn().setText("浏览");
        getRightBtn().setTextSize(10);
        getRightBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ProjectInfoActivity.this , BrowseActivity.class);
                intent.putExtra("projectId" , Pid);
                intent.putExtra("status" , -1);
                startActivity(intent);


            }
        });

        kaoqingBtn = findViewById(R.id.kaoqing);
        fangwenBtn = findViewById(R.id.fangwen);
        attachmentBtn = findViewById(R.id.attachment);
        mAdressView = findViewById(R.id.addressView);
        kaoqingBtn.setOnClickListener(new ClickListenner());
        fangwenBtn.setOnClickListener(new ClickListenner());
        attachmentBtn.setOnClickListener(new ClickListenner());

        /*数据填充*/
            TextView projectName=  findViewById(R.id.info_project_Name); //项目名称
            TextView projectManage=  findViewById(R.id.info_project_Manage); //项目经理
            TextView projectEmployee=  findViewById(R.id.info_project_Employee); //访问员
            TextView projectStyle=  findViewById(R.id.info_project_Style); //访问方式
            TextView projectDelivery= findViewById(R.id.info_project_Delivery); //发布时间
            TextView projectProjectCycle= findViewById(R.id.info_project_projectCycle); //周期
            TextView projectAttendance= findViewById(R.id.info_project_Attendance); //考勤班次
            TextView projectDealer= findViewById(R.id.info_project_Dealer);  //经销商名称
            TextView projectDealerNumber= findViewById(R.id.info_project_dealerNumber);           //经销商代码
            TextView projectDeliveryGrade= findViewById(R.id.info_project_deliveryGrade);   //经销商等级
            TextView projectAddress= findViewById(R.id.info_project_Address); //地址
            TextView projectAccessStatues= findViewById(R.id.info_project_accessStatues); //访问状态
            TextView projectUploadStatus= findViewById(R.id.info_project_uploadStatus); //上传状态
            TextView projectRemakes= findViewById(R.id.info_project_remakes); //项目说明
        List<ProjectsDB> projectsDBS = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "pid=?", Pid);
        if (projectsDBS!=null && projectsDBS.size()>0){
            projectName.setText(projectsDBS.get(0).getPname());
            projectManage.setText(projectsDBS.get(0).getManager()==null ? "暂无":projectsDBS.get(0).getManager());
            projectEmployee.setText(userName);
            int inspect = projectsDBS.get(0).getInspect();
            int record = projectsDBS.get(0).getRecord();
            String project_inspect="";
            String project_record="";
            if (inspect==1){
                project_inspect="飞检" ;
            }else if (inspect==2){
                project_inspect="神秘顾客";
            }else if(inspect==3){
                project_inspect="新店验收";
            }

            if (record==1){
                project_record="不录音";
            }else if (record==2){
                project_record="单题录音";
            }else if(record==3){
                project_record="全程录音";
            }
            projectStyle.setText(project_inspect+"·"+project_record);
            long startTime = projectsDBS.get(0).getStartTime();
            long endTime = projectsDBS.get(0).getEndTime();
            Date startTimes=new Date(startTime);
            SimpleDateFormat sdf=new SimpleDateFormat("yy-MM-dd HH:mm");
            String St = sdf.format(startTimes);
            String Et="";
            if (endTime==1){
                Et="结束公开";
            }else {
                Date endTimes=new Date(endTime);
                 Et = sdf.format(endTimes);
            }
            projectDelivery.setText(St);

            projectProjectCycle.setText(St+"～"+Et);

            projectAttendance.setText(projectsDBS.get(0).getcName());

            projectDealer.setText(projectsDBS.get(0).getdName());

            projectDealerNumber.setText(projectsDBS.get(0).getCode());

            projectDeliveryGrade.setText(projectsDBS.get(0).getGrade()==null?"暂无":projectsDBS.get(0).getGrade());

            projectAddress.setText(projectsDBS.get(0).getAddress()+projectsDBS.get(0).getLocation());

            List<SubjectsDB> subjectsDBList = projectsDBS.get(0).getSubjectsDBList();
            int subSize=0;
            int subToa=0;
            int imageSize=0;
            int recorSize=0;
            int imageSizeUp=0;
            int recorSizeUp=0;
            int upload=0;
            if (subjectsDBList !=null && subjectsDBList.size()>0){
                for (int i = 0; i <subjectsDBList.size() ; i++) {
                    if (subjectsDBList.get(i).getCensor()==0 && subjectsDBList.get(i).getIsComplete()==0 && subjectsDBList.get(i).getsUploadStatus()==0){
                        subSize++;
                        List<ImagesDB> imagesDBList = subjectsDBList.get(i).getImagesDBList();
                        if (imagesDBList !=null && imagesDBList.size()>0){
                            imageSize++;
                        }
                        List<RecorderFilesDB> recorderFiles = subjectsDBList.get(i).getRecorderFiles();
                        if (recorderFiles !=null && recorderFiles.size()>0){
                            recorSize++;
                        }
                    } else {
                        upload++;
                        List<ImagesDB> imagesDBList = subjectsDBList.get(i).getImagesDBList();
                        if (imagesDBList !=null && imagesDBList.size()>0){
                            imageSizeUp++;
                        }
                        List<RecorderFilesDB> recorderFiles = subjectsDBList.get(i).getRecorderFiles();
                        if (recorderFiles !=null && recorderFiles.size()>0){
                            recorSizeUp++;
                        }
                    }

                    subToa++;

                }
            }
               String isComplete="未完成";
            String isCompleteU="未上传";
            if (upload==subToa){
                isComplete="已完成";
                isCompleteU="已上传";
                projectAccessStatues.setText(isComplete+"("+upload+"/"+subToa+")");
                projectUploadStatus.setText("数据"+upload+isCompleteU+"，"+"附件"+(recorSizeUp+imageSizeUp)+isCompleteU);
            }else {
                isComplete="未完成";
                isCompleteU="未上传";
                projectAccessStatues.setText(isComplete+"("+subSize+"/"+subToa+")");
                projectUploadStatus.setText("数据"+subSize+isCompleteU+"，"+"附件"+(recorSize+imageSize)+isCompleteU);
            }

            projectRemakes.setText(projectsDBS.get(0).getNotice());
        }

        mAdressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(getLocation());
                if (homeList!=null){
                    Intent intent = new Intent(ProjectInfoActivity.this , ProjectMapActivity.class);
                    intent.putExtra("lat" , homeList.getLatitude());
                    intent.putExtra("lng" , homeList.getLongitude());
                    startActivity(intent);
                }
                if (searchActivity !=null){
                    ToastUtils.showLong(ProjectInfoActivity.this,"暂时无法使用");
                }



            }
        });
    }

    class ClickListenner implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId())
            {
                case R.id.kaoqing:  //考勤打卡
                    mPreferencesTool.setProjectId("Pid",Pid); //保存当前访问的项目Id

                    intent = new Intent(ProjectInfoActivity.this , ProjectAttendanceActivity.class);
                    if (homeList!=null){
                        intent.putExtra("projectId" , homeList.getPid());
                    }
                    if (searchActivity!=null){
                        intent.putExtra("projectId" , searchActivity.getId());
                    }
                    startActivity(intent);
                    break;

                case R.id.fangwen:  //开始访问
                    mPreferencesTool.setProjectId("Pid",Pid); //保存当前访问的项目Id
                    new CommomDialog(ProjectInfoActivity.this, R.style.dialog, "是否去打卡",true, new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            Intent intent = null;
                            if (confirm){
                                intent = new Intent(ProjectInfoActivity.this , ProjectAttendanceActivity.class);
                                if (homeList!=null){
                                    intent.putExtra("projectId" ,  homeList.getPid());
                                }
                                if (searchActivity!=null){
                                    intent.putExtra("projectId" , searchActivity.getId());
                                }

                                startActivity(intent);

                            }else {

                                if (homeList !=null){
                                    if (System.currentTimeMillis()>homeList.getStartTime()){
                                        intent = new Intent(ProjectInfoActivity.this , ProjectStartActivity.class);
                                        Bundle bundle=new Bundle();
                                        bundle.putSerializable("InfoHome",homeList);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                }
                                if (searchActivity !=null){
                                    if (System.currentTimeMillis()>searchActivity.getStartTime()){
                                        intent = new Intent(ProjectInfoActivity.this , ProjectStartActivity.class);
                                        Bundle bundle=new Bundle();
                                        bundle.putSerializable("InfoHome",searchActivity);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                }
                            }
                            dialog.dismiss();
                        }
                    }).setPositiveButton("去打卡").setNegativeButton("我已经打卡了").setTitle("提醒").show();


                    break;

                case R.id.attachment:  //附件
                    intent = new Intent(ProjectInfoActivity.this , ProjectAttachmentActivity.class);
                    mPreferencesTool.setProjectId("Pid",Pid); //保存当前访问的项目Id
                    Bundle bundles=new Bundle();
                    if (homeList!=null){
                        bundles.putSerializable("InfoHome",homeList);

                    }
                    if (searchActivity!=null){
                        bundles.putSerializable("InfoSearch",searchActivity);
                    }

                    intent.putExtras(bundles);
                    startActivity(intent);
                    break;
            }
        }
    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA};
    }

    @Override
    protected void permissionGrantedSuccess() {
            mPreferencesTool.setActivityPermission("Activity",true);
    }

    @Override
    protected void permissionGrantedFail() {
        mPreferencesTool.setActivityPermission("Activity",false);
    }

}
