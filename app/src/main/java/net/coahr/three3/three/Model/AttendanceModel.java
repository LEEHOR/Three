package net.coahr.three3.three.Model;

/**
 * Created by yuwei on 2018/5/15.
 */

public class AttendanceModel {

    private String id;
    private long signInTime;
    private int timeStatus;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setSignInTime(long signInTime) {
        this.signInTime = signInTime;
    }

    public long getSignInTime() {
        return signInTime;
    }

    public void setTimeStatus(int timeStatus) {
        this.timeStatus = timeStatus;
    }

    public int getTimeStatus() {
        return timeStatus;
    }
}
