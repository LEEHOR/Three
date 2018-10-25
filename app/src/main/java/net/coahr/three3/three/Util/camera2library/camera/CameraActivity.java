/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.coahr.three3.three.Util.camera2library.camera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import net.coahr.three3.three.DBbase.ImagesDB;
import net.coahr.three3.three.DBbase.ProjectsDB;
import net.coahr.three3.three.DBbase.SubjectsDB;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.ImageFactory.LubanZip;
import net.coahr.three3.three.Util.JDBC.DataBaseWork;
import net.coahr.three3.three.Util.OtherUtils.ActivityCollector;
import net.coahr.three3.three.Util.OtherUtils.FileUtils;
import net.coahr.three3.three.Util.OtherUtils.ToastUtils;
import net.coahr.three3.three.Util.Preferences.PreferencesTool;
import net.coahr.three3.three.Util.camera2library.Camera2RecordFinishActivity;
import net.coahr.three3.three.Util.imageselector.utils.ImageSelectorUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends Activity implements View.OnClickListener {

    private CameraTextureView mCameraTextureView;

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    public static final int REQUEST_CODE = 0x00000011; //图片选择代码
    private String TAG="CameraActivity";
    private PreferencesTool mpreferencesTool;
    private  File mFile;
    private  String page;          //题目的Position
    private  String pid;        //项目的Pid;
    private List<SubjectsDB> subjectsDBList; //题目数据库
    private List<ProjectsDB> projectsDBS;      //项目库
    private  List<ImagesDB> imagesDBList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2_basic);
        mpreferencesTool=new PreferencesTool(this);
        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.iv_switchCamera).setOnClickListener(this);
        findViewById(R.id.iv_images).setOnClickListener(this);
        findViewById(R.id.iv_takePhoto).setOnClickListener(this);
        mCameraTextureView =  findViewById(R.id.texture);
        pid = mpreferencesTool.getProjectId("Pid");
        page = mpreferencesTool.getProjectStartPhotoPage("subject_id");
        projectsDBS = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "Pid=?", pid);
        mFile = new File(FileUtils.createFileDir(CameraActivity.this,"takePhotos"+"/"+projectsDBS.get(0).getPname()).getPath());
      //  Log.e("RR",""+mFile.toString());
        //配置CameraTexture
        mCameraTextureView.setActivity(this);
        mCameraTextureView.setPicSaveFile(mFile);
      //  mCameraTextureView.setPicSaveName(projectsDBS.get(0).getCode()+projectsDBS.get(0).getcName());


        //ActivityCollector.addActivity(this);
    }

    @Override
    public void onClick(View view) {


     //   pid = mpreferencesTool.getProjectId("Pid");
        subjectsDBList =  DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class,"ht_id=?",page);
        mCameraTextureView.setPicSaveName(projectsDBS.get(0).getCode()+"-"+projectsDBS.get(0).getdName()+"-"+subjectsDBList.get(0).getNumber());
       // projectsDBS = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "Pid=?", pid);
      //  mCameraTextureView.setPicSaveFile(mFile);
        switch (view.getId()) {
            case R.id.iv_takePhoto:

                if (subjectsDBList!=null && subjectsDBList.size()>0){
                   imagesDBList = subjectsDBList.get(0).getImagesDBList();
                    if (imagesDBList !=null && imagesDBList.size()<10){
                        mCameraTextureView.takePicture(new CameraTextureView.TackPhotoCallback() {
                            @Override
                            public void tackPhotoSuccess(String photoPath) {
                                showToast(photoPath);
                                Intent intent = new Intent(CameraActivity.this, Camera2RecordFinishActivity.class);
                                intent.putExtra("photoPath", photoPath);
                                startActivity(intent);
                            }

                            @Override
                            public void tackPhotoError(Exception e) {
                                showToast(e.getMessage());
                            }
                        });
                    }else if (imagesDBList==null){
                        mCameraTextureView.takePicture(new CameraTextureView.TackPhotoCallback() {
                            @Override
                            public void tackPhotoSuccess(String photoPath) {
                                showToast(photoPath);
                                if (photoPath !=null){
                                    Intent intent = new Intent(CameraActivity.this, Camera2RecordFinishActivity.class);
                                    intent.putExtra("photoPath", photoPath);
                                    startActivity(intent);
                                }

                            }

                            @Override
                            public void tackPhotoError(Exception e) {
                                showToast(e.getMessage());
                            }
                        });

                    } else {
                        ToastUtils.showShort(CameraActivity.this,"照片已完成");
                    }

                }

                break;

            case R.id.iv_switchCamera:

               mCameraTextureView.switchCamera();

                break;
            case R.id.iv_images:
                if (subjectsDBList!=null && subjectsDBList.size()>0){
                    Log.e(TAG, "onClick:subjectsDBList "+subjectsDBList.size() );
                    List<ImagesDB> imagesDBList = subjectsDBList.get(0).getImagesDBList();
                    if (imagesDBList !=null && imagesDBList.size()>0){
                        Log.e(TAG, "onClick: imagesDBList"+imagesDBList.size() );
                        if (imagesDBList.size()<10){
                            int i = 10 - imagesDBList.size();
                            ImageSelectorUtils.openPhoto(CameraActivity.this, REQUEST_CODE, false, i);
                        }else {
                            ToastUtils.showLong(CameraActivity.this,"图片数量已足够");
                        }
                    }else {

                        ImageSelectorUtils.openPhoto(CameraActivity.this, REQUEST_CODE, false, 10);
                    }

                }
            break;
            case R.id.iv_close:

                finish();

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }
        mCameraTextureView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraTextureView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ConfirmationDialog();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), R.string.request_permission, Toast.LENGTH_SHORT).show();
            } else {
                //执行相机初始化操作
                mCameraTextureView.onResume();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void ConfirmationDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.request_permission)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(CameraActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                REQUEST_CAMERA_PERMISSION);
                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), R.string.camera_permission, Toast.LENGTH_SHORT).show();
                            }
                        })
                .create();
        alertDialog.show();
    }


    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    private void showToast(final String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            page = mpreferencesTool.getProjectStartPhotoPage("subject_id");
            pid = mpreferencesTool.getProjectId("Pid");
            subjectsDBList =  DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class,"ht_id=?",page);
            projectsDBS = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "Pid=?", pid);
            ArrayList<String> images = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
            File bitPhotos = FileUtils.createFileDir(CameraActivity.this, "BitPhotos");
            if (images != null && images.size() > 0) {
              //  LubanZip lubanZip = new LubanZip(CameraActivity.this);
                LubanZip.getInstance().getZip( CameraActivity.this,images, bitPhotos, subjectsDBList.get(0), projectsDBS.get(0), new LubanZip.LuBanZip() {
                    @Override
                    public void ZipSuccess() {
                       ToastUtils.showShort(CameraActivity.this,"选择成功");
                    }
                });
            }
        }
    }

}
