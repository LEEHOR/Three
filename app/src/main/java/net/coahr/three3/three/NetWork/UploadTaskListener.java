package net.coahr.three3.three.NetWork;

import java.io.File;

/**
 * Created by yuwei on 2018/3/16.
 */

public interface UploadTaskListener {

    void onUploading(UpLoadTask uploadTask, String percent, int position);
    void onUploadSuccess(UpLoadTask uploadTask, File file);
    void onError(UpLoadTask uploadTask, int errorCode, int position);
    void onPause(UpLoadTask uploadTask);
}
