package net.coahr.three3.three.Model;

/**
 * Created by 李浩
 * 2018/5/9
 */
public class LoginModel {

    /**
     * msg : 请求成功！
     * name : 小明大
     * result : 1
     * sessionid : 91522da755e741ac8fa3d790fc653d81
     */

    private String msg;
    private String name;
    private int result;
    private String sessionId;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
