package net.coahr.three3.three.Util.ImageFactory;

import android.content.Context;
import android.text.TextUtils;

import net.coahr.three3.three.DBbase.ImagesDB;
import net.coahr.three3.three.DBbase.ProjectsDB;
import net.coahr.three3.three.DBbase.SubjectsDB;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class LubanZip {
    private LuBanZip zipSuccess;
    private static LubanZip mLuBanZip;

    public static LubanZip  getInstance(){

        if (mLuBanZip ==null){
            mLuBanZip=new LubanZip();
        }
        return mLuBanZip;
    }

    public  void getZip(Context context,final List<String> imageFileList, File outPath, final SubjectsDB subjectsDB, final ProjectsDB projectsDB, final LuBanZip luBanZip){
        final List<Boolean> list=new ArrayList<>();
        for (int i = 0; i <imageFileList.size() ; i++) {
            final String imagePath = imageFileList.get(i);
            //截取文件名
            int start = imagePath.lastIndexOf("/") + 1;
            final String fileName = imagePath.substring(start);
            Luban.with(context)
                    .load(imageFileList.get(i))
                    .ignoreBy(100)
                    .setTargetDir(outPath.getAbsolutePath())
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
                            ImagesDB imagesDB = new ImagesDB();
                            imagesDB.setImagePath(imagePath);
                            imagesDB.setImageName(fileName);
                            imagesDB.setZibImagePath(file.getAbsolutePath());
                            imagesDB.setSubjectsDB(subjectsDB);
                            imagesDB.setProjectsDB(projectsDB);
                            boolean save = imagesDB.save();
                            list.add(save);
                            if (list.size()==imageFileList.size()){
                                luBanZip.ZipSuccess();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            list.add(false);
                            if (list.size()==imageFileList.size()){
                                luBanZip.ZipSuccess();
                            }
                        }
                    }).launch();
        }


    }

    public interface LuBanZip{
        void ZipSuccess();
    }

    public void setLubanZip(LuBanZip zip){
      this.zipSuccess=zip;
    }
}
