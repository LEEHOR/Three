package net.coahr.three3.three.Module;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.coahr.three3.three.Base.BaseFragment;
import net.coahr.three3.three.ProjectStartActivity;
import net.coahr.three3.three.R;

/**
 * Created by yuwei on 2018/4/3.
 */

public class ProjectFragment extends BaseFragment {
private Button btn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project , container ,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
               btn= view.findViewById(R.id.project_go);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), ProjectStartActivity.class);
                startActivity(intent);
            }
        });

    }
}
