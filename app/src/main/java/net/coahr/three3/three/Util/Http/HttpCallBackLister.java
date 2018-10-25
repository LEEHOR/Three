package net.coahr.three3.three.Util.Http;

/**
 * Created by 李浩 on 2018/2/22.
 */

public interface HttpCallBackLister {
    /**
     * 成功时的回调
     * @param response
     */
    void onFinish(String response);

    /**
     * 失败时的回调
     * @param e
     */
    void onError(Exception e);
}
