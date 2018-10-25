package net.coahr.three3.three.Util.camera2library;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import net.coahr.three3.three.DBbase.ImagesDB;
import net.coahr.three3.three.DBbase.ProjectsDB;
import net.coahr.three3.three.DBbase.SubjectsDB;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.ImageFactory.ZipImageFactory;
import net.coahr.three3.three.Util.JDBC.DataBaseWork;
import net.coahr.three3.three.Util.OtherUtils.FileUtils;
import net.coahr.three3.three.Util.Preferences.PreferencesTool;
import net.coahr.three3.three.Util.camera2library.camera.CameraActivity;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by 李浩 on 2018/4/22.
 * 自定义的配置文件
 *
 */
public class Camera2RecordFinishActivity extends AppCompatActivity {
    private String picPath;//图片地址
    private ImageView iv;
    private ImageView reback; //返回
    private ImageView load;     //保存本地
    private ImageView retake;   //重拍
    private Button save_next;   //保存并拍下一张
    private   File newFile;     //保存到系统相册
    private  File oldFile;       //临时文件地址
    private ImagesDB imagesDB; //图片数据库
    private  List<SubjectsDB> subjectsDBList; //题目数据库
    private List<ProjectsDB> projectsDBS;      //项目库
    private   String fileName;  //图片名
    private PreferencesTool mpreferencesTool;
    private  String page;          //题目的Position
    private  String pid;        //项目的Pid;
    private boolean isCopy=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏模式
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera2_finish_detail);
        LitePal.initialize(this);
        mpreferencesTool=new PreferencesTool(this);
        page = mpreferencesTool.getProjectStartPhotoPage("subject_id");
        pid = mpreferencesTool.getProjectId("Pid");

        subjectsDBList =  DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "ht_id=?", page);
        projectsDBS = DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class, "Pid=?", pid);
        iv = findViewById(R.id.iv);
        load=findViewById(R.id.camera2_load);
        save_next=findViewById(R.id.camera2_save_next);
        reback=findViewById(R.id.camera2_reback);
        retake=findViewById(R.id.camera2_retake);
        if (getIntent() != null) {
            //获取传递过来的图片地址
            picPath = getIntent().getStringExtra("photoPath");
            Log.e("rr","图片路径"+picPath);
            if (TextUtils.isEmpty(picPath)) {
                iv.setVisibility(View.GONE);
            } else {
                Toast.makeText(Camera2RecordFinishActivity.this, "=-==-==" + picPath, Toast.LENGTH_SHORT).show();
                iv.setImageBitmap(BitmapFactory.decodeFile(picPath));
                //截取文件名
                int start=picPath.lastIndexOf("/")+1;
                fileName = picPath.substring(start);
            }

        }

        load.setOnClickListener(new MyButton());
        reback.setOnClickListener(new MyButton());
        retake.setOnClickListener(new MyButton());
        save_next.setOnClickListener(new MyButton());
    }
    class MyButton implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.camera2_load:
                    isCopy=true;
                   /* //获取文件类型
                    String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
                    //替换之前
                    Toast.makeText(Camera2RecordFinishActivity.this,""+fileName,Toast.LENGTH_LONG).show();
                    Log.e("Fi","替换之前的名字"+fileName);
                    //替换
                     final String newFileName = fileName.replace("netThree","jpg");
                    Log.e("Fi","替换之后的名字"+newFileName);
                    //替换之后*/
                   // Toast.makeText(Camera2RecordFinishActivity.this,""+newFileName,Toast.LENGTH_LONG).show();
                   // final String newFileName = fileName.replace("netThree","jpg");
                    if (!TextUtils.isEmpty(picPath)){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                copy(picPath,fileName,Camera2RecordFinishActivity.this);
                            }
                        }).start();
                        //saveToDB(fileName,picPath);
                      //  Log.e("Fi","保存相册完成");
                        Toast.makeText(Camera2RecordFinishActivity.this,""+"保存完成",Toast.LENGTH_LONG).show();
                    }

                   break;
                case R.id.camera2_reback:
                   // List<ImagesDB> imagesDBList = subjectsDBList.get(0).getImagesDBList();
                        if (isCopy) {
                            if (newFile != null && oldFile != null) {
                                Toast.makeText(Camera2RecordFinishActivity.this, "" + "保存尚未完成", Toast.LENGTH_LONG).show();
                                Log.e("Fi", "保存尚未完成1/newFile!=null&&oldFile!=null");
                                if (newFile.exists() && oldFile.exists()) {
                                    Toast.makeText(Camera2RecordFinishActivity.this, "" + "保存尚未完成", Toast.LENGTH_LONG).show();
                                    Log.e("Fi", "保存尚未完成2/newFile.exists()&&oldFile.exists()");
                                    if (newFile.length() - oldFile.length() >= 0 && newFile.length() != 0) {
                                        Toast.makeText(Camera2RecordFinishActivity.this, "" + "保存完成", Toast.LENGTH_LONG).show();
                                        Log.e("Fi", "保存完成");
                                        if (picPath != null) {
                                            saveToDB(fileName, picPath);
                                        }
                                        finish();

                                    } else {
                                        Toast.makeText(Camera2RecordFinishActivity.this, "" + "保存尚未完成", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                   // finish();
                                }
                            } else {

                               // finish();
                            }
                        } else {
                            if (picPath != null) {
                                saveToDB(fileName, picPath);
                            }
                            finish();
                        }
                    break;
                case R.id.camera2_retake:
                    File file=new File(picPath);

                    if (isCopy) {
                        if (newFile != null) {
                            if (newFile.exists()) {
                                newFile.delete();
                                Log.e("FI", "相册文件已经删除");
                                Toast.makeText(Camera2RecordFinishActivity.this, "" + "相册文件已经删除", Toast.LENGTH_LONG).show();
                            }
                        }
                        if (newFile != null) {
                            if (newFile.toString() != null) {
                                Log.e("FI", "相册库已经删除" + newFile.toString());
                                deletePic(newFile.getPath());
                            }
                        }

                        if (file.exists()&&file!=null){
                            file.delete();
                            Log.e("FI","临时文件已经删除");
                            Toast.makeText(Camera2RecordFinishActivity.this,""+"临时文件已经删除",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (file.exists()&&file!=null){
                            file.delete();
                            Log.e("FI","临时文件已经删除");
                            Toast.makeText(Camera2RecordFinishActivity.this,""+"临时文件已经删除",Toast.LENGTH_LONG).show();
                        }
                    }
                    finish();
                    break;
                case  R.id.camera2_save_next:
                    Log.e("rr","拍摄下一张"+fileName);

                    if (picPath!=null){
                        saveToDB(fileName,picPath);

                    }
                   // finish();
                    break;
            }
        }
    }
    /**
     * 复制文件
     *
     * @param oldPath 需要复制的文件路径
     * @param newName 复制后的文件新名字
     */
    public void copy(String oldPath, String newName, Context context) {
        try {
            oldFile = new File(oldPath);
            if (oldFile.exists()) {
                //保存到系统相册
                File publicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM+"/Camera");
              newFile=new File(publicDirectory,newName);
              Log.e("Fi","copy/保存到系统相册"+newFile.toString());
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, length);
                }
                inStream.close();
                fileOutputStream.close();
                //把文件插入到系统图库(系统会自己命名且另存一张图片)
               // MediaStore.Images.Media.insertImage(context.getContentResolver(), newFile.getAbsolutePath(), newName, null);
                //保存图片后发送广播通知更新数据库
                Uri uri = Uri.fromFile(newFile);
                Log.e("Fi","刷新系统相册"+uri.toString());
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deletePic(String path){
        if(!TextUtils.isEmpty(path)){
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Log.e("FI","查询系统库uri"+uri);
            ContentResolver contentResolver = Camera2RecordFinishActivity.this.getContentResolver();
            String url =  MediaStore.Images.Media.DATA + "='" + path + "'";
            Log.e("FI","查询系统库URL"+url);
            //删除图片
            contentResolver.delete(uri, url, null);
            Log.e("FI","相册文件已经删除");
        }
    }

   /* private void DeleteImage(String imgPath) {
        ContentResolver resolver = Camera2RecordFinishActivity.this.getContentResolver();
        Cursor cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=?",
                new String[] { imgPath }, null);
        boolean result = false;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri = ContentUris.withAppendedId(contentUri, id);
            int count =  Camera2RecordFinishActivity.this.getContentResolver().delete(uri, null, null);
            result = count == 1;
        } else {
            File file = new File(imgPath);
            result = file.delete();
        }
    }*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            if(newFile!=null&&oldFile!=null){
                Toast.makeText(Camera2RecordFinishActivity.this,""+"保存尚未完成完成",Toast.LENGTH_LONG).show();
                if (newFile.exists() && oldFile.exists()){
                    Toast.makeText(Camera2RecordFinishActivity.this,""+"保存尚未完成完成",Toast.LENGTH_LONG).show();
                    if (newFile.length()-oldFile.length()>=0&&newFile.length()!=0){
                        Toast.makeText(Camera2RecordFinishActivity.this,""+"保存完成",Toast.LENGTH_LONG).show();
                        finish();
                    }else {
                        Toast.makeText(Camera2RecordFinishActivity.this,""+"保存尚未完成",Toast.LENGTH_LONG).show();
                    }
                }else {
                    finish();
                }
            }else {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void saveToDB(final String imageName, final String ImagePath){
        List<ImagesDB> imagesDBS = DataSupport.where("imageName=?", imageName).find(ImagesDB.class);
        if (imagesDBS !=null&&!imagesDBS.isEmpty()){
            Toast.makeText(Camera2RecordFinishActivity.this,""+"已经保存,请勿重复保存",Toast.LENGTH_LONG).show();
        } else {
            File bitPhotos = FileUtils.createFileDir(Camera2RecordFinishActivity.this, "BitPhotos");
            Luban.with(Camera2RecordFinishActivity.this)
                    .load(ImagePath)
                    .ignoreBy(100)
                    .setTargetDir(bitPhotos.getAbsolutePath())
                    .filter(new CompressionPredicate() {
                        @Override
                        public boolean apply(String path) {
                            return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                        }
                    })
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess(File file) {
                            imagesDB =new ImagesDB();
                            imagesDB.setImagePath(ImagePath);
                            imagesDB.setImageName(imageName);
                            imagesDB.setZibImagePath(file.getAbsolutePath());
                            imagesDB.setSubjectsDB(subjectsDBList.get(0));
                            imagesDB.setProjectsDB(projectsDBS.get(0));
                            boolean save = imagesDB.save();
                            if (save){
                                finish();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("拍照", "onError: "+e );
                        }
                    }).launch();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isCopy=false;
    }
}
