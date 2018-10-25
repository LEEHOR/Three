package net.coahr.three3.three.Model;

import java.util.List;

/**
 * Created by yuwei on 2018/5/17.
 */

public class AttendanceHistoryModel {
    private List<AttendanceBean>attendanceList;

    public List<AttendanceBean> getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(List<AttendanceBean> attendanceList) {
        this.attendanceList = attendanceList;
    }

    public static class AttendanceBean{

        private String dateTime;
        private int endLocationStatus;
        private int startLocationStatus;
        private int startTimeStatus;
        private int endTimeStatus;
        private long inTime;
        private long outTime;
        private double inLng;
        private double inLat;
        private double outLng;
        private double outLat;
        private int    status;
        private String progress;
        private String remark;


        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public int getEndLocationStatus() {
            return endLocationStatus;
        }

        public void setEndLocationStatus(int endLocationStatus) {
            this.endLocationStatus = endLocationStatus;
        }

        public int getStartLocationStatus() {
            return startLocationStatus;
        }

        public void setStartLocationStatus(int startLocationStatus) {
            this.startLocationStatus = startLocationStatus;
        }

        public int getEndTimeStatus() {
            return endTimeStatus;
        }

        public void setEndTimeStatus(int endTimeStatus) {
            this.endTimeStatus = endTimeStatus;
        }

        public int getStartTimeStatus() {
            return startTimeStatus;
        }

        public void setStartTimeStatus(int startTimeStatus) {
            this.startTimeStatus = startTimeStatus;
        }

        public long getInTime() {
            return inTime;
        }

        public void setInTime(long inTime) {
            this.inTime = inTime;
        }

        public long getOutTime() {
            return outTime;
        }

        public void setOutTime(long outTime) {
            this.outTime = outTime;
        }

        public double getInLng() {
            return inLng;
        }

        public void setInLng(double inLng) {
            this.inLng = inLng;
        }

        public double getInLat() {
            return inLat;
        }

        public void setInLat(double inLat) {
            this.inLat = inLat;
        }

        public double getOutLat() {
            return outLat;
        }

        public void setOutLat(double outLat) {
            this.outLat = outLat;
        }

        public double getOutLng() {
            return outLng;
        }

        public void setOutLng(double outLng) {
            this.outLng = outLng;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getProgress() {
            return progress;
        }

        public void setProgress(String progress) {
            this.progress = progress;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }


}

