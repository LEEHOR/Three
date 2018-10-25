package net.coahr.three3.three.Base;

/**
 * Created by yuwei on 2018/3/19.
 */

//响应model
public class BaseModel<T> {

    private String msg;
    private String result;
    private T data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

