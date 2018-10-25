package net.coahr.three3.three.Setting;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import net.coahr.three3.three.Base.BaseActivity;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.Preferences.PreferencesTool;

/**
 * Created by yuwei on 2018/4/26.
 */

public class UploadSettingActivity extends BaseActivity {

    private RadioGroup mRadioGroup;
    private CheckBox autoUploadBox , uploadBox , qualityMbox , qualityHbox;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadsetting);
        findUI();
    }

    @Override
    public void findUI() {
        super.findUI();

        configureNaviBar(naviBar.findViewById(R.id.left) , null);
        setTitle((TextView) naviBar.findViewById(R.id.title), "数据上传设置");

       autoUploadBox = findViewById(R.id.autoUpload);
       uploadBox = findViewById(R.id.upload);
       qualityMbox = findViewById(R.id.qualityM);
       qualityHbox = findViewById(R.id.qualityH);


        autoUploadBox.setOnClickListener(new BoxCheckListenner());
        uploadBox.setOnClickListener(new BoxCheckListenner());
        qualityMbox.setOnClickListener(new BoxCheckListenner());
        qualityHbox.setOnClickListener(new BoxCheckListenner());
    }


    class BoxCheckListenner implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            PreferencesTool preferencesTool = new PreferencesTool(UploadSettingActivity.this);
            switch (v.getId())
            {
                case R.id.autoUpload:

                    autoUploadBox.setChecked(true);
                    uploadBox.setChecked(false);
                    preferencesTool.setUploadMethod("uploadMethod" , true);
                    break;

                case R.id.upload:
                    autoUploadBox.setChecked(false);
                    uploadBox.setChecked(true);
                    preferencesTool.setUploadMethod("uploadMethod" , false);
                    break;

                case R.id.qualityM:
                    qualityMbox.setChecked(true);
                    qualityHbox.setChecked(false);
                    preferencesTool.setUploadMethod("uploadQuality" , false);
                    break;

                case R.id.qualityH:
                    qualityMbox.setChecked(false);
                    qualityHbox.setChecked(true);
                    preferencesTool.setUploadMethod("uploadQuality" , true);
                    break;

            }


        }
    }

}
