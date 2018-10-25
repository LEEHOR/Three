package net.coahr.three3.three.Util.OtherUtils;

import java.io.File;
import java.util.List;

/**
 * Created by 李浩
 * 2018/5/16
 */
public class FileDelets {
    public static boolean deleteFiles(List<String> StringPath){
        if (StringPath!=null&&!StringPath.isEmpty()){
            for (int i = 0; i < StringPath.size(); i++) {
                if (StringPath.get(i)!=null){
                    File file=new File(StringPath.get(i));
                    if (file.exists()) {
                        boolean delete = file.delete();
                        if (delete){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
