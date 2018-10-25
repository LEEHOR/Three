package net.coahr.three3.three.Util.AudioRecorder;

/**
 * Created by 李浩 on 18/3/11.
 * 获取录音的音频流,用于拓展的处理
 */
public interface RecordStreamListener {
    void recordOfByte(byte[] data, int begin, int end);

    void OnSuccess(String fileName);

    void OnFail();
}
