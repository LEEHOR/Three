package net.coahr.three3.three.Project;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.coahr.three3.three.Base.BaseFragment;
import net.coahr.three3.three.Base.BaseModel;
import net.coahr.three3.three.Model.AttendanceHistoryModel;
import net.coahr.three3.three.Model.AttendanceInfoModel;
import net.coahr.three3.three.R;
import net.coahr.three3.three.RecyclerViewAdapter.ProjectAttendanceAdapter;
import net.coahr.three3.three.RecyclerViewAdapter.VerifyAdater;

import java.text.SimpleDateFormat;

/**
 * Created by yuwei on 2018/4/18.
 */

public class ProjectAttendanceHistoryFragment extends BaseFragment {

    private RecyclerView  mRecyclerView;
    private BaseModel projectInfo;
    private TextView projectNameTextView,dateTextView,codeTextView,companyNameTextView,companyAddressTextView;

    public void setProjectInfo(BaseModel projectInfo) {
        this.projectInfo = projectInfo;

        AttendanceInfoModel model = (AttendanceInfoModel) projectInfo.getData();
        projectNameTextView.setText(model.getPname());
        codeTextView.setText(model.getCode());
        companyNameTextView.setText(model.getDname());
        companyAddressTextView.setText(model.getAreaAddress()+model.getLocation());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String ymd = simpleDateFormat.format(model.getStartTime());
        dateTextView.setText(ymd +"--结束公开");

        ((ProjectAttendanceAdapter)adapter).setAddress(model.getAreaAddress()+model.getLocation());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_project_attendancehistory, container , false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findUI(view);

        ((ProjectAttendanceActivity)getActivity()).setmAttendanceInterface(new ProjectAttendanceActivity.AttendanceInterface() {
            @Override
            public void attendaceHistory(BaseModel model) {

                System.out.println(model);
                if (model.getResult().equals("1"))
                {
                    AttendanceHistoryModel attendanceHistoryModel = (AttendanceHistoryModel) model.getData();
                    ((ProjectAttendanceAdapter)adapter).Update(attendanceHistoryModel.getAttendanceList());
                }


            }
        });
    }


    public void findUI(View view)
    {
        mRecyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        setAdapter(new ProjectAttendanceAdapter(getActivity() , null , R.layout.layout_project_attendance_history , null));
        mRecyclerView.setAdapter(adapter);

        projectNameTextView = view.findViewById(R.id.shopName);
        dateTextView = view.findViewById(R.id.riqi);
        codeTextView = view.findViewById(R.id.code);
        companyNameTextView = view.findViewById(R.id.companyTextView);
        companyAddressTextView = view.findViewById(R.id.adressTextView);



    }

}
