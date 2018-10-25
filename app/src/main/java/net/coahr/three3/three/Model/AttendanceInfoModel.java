package net.coahr.three3.three.Model;

/**
 * Created by yuwei on 2018/5/15.
 */

public class AttendanceInfoModel {

    private String Cname;
    private String Dname;
    private String Pname;
    private String areaAddress;
    private String classId;
    private int    closeStatus;
    private String code;
    private String dealerId;
    private long   endTime;
    private double latitude;
    private String location;
    private double longitude;
    private String notice;
    private String projectId;
    private long   startTime;
    private int    status;
    private AttendanceBean attendance;


    public String getCname() {
        return Cname;
    }

    public void setCname(String cname) {
        Cname = cname;
    }

    public String getDname() {
        return Dname;
    }

    public void setDname(String dname) {
        Dname = dname;
    }

    public String getPname() {
        return Pname;
    }

    public void setPname(String pname) {
        Pname = pname;
    }

    public String getAreaAddress() {
        return areaAddress;
    }

    public void setAreaAddress(String areaAddress) {
        this.areaAddress = areaAddress;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public int getCloseStatus() {
        return closeStatus;
    }

    public void setCloseStatus(int closeStatus) {
        this.closeStatus = closeStatus;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public AttendanceBean getAttendance() {
        return attendance;
    }

    public void setAttendance(AttendanceBean attendance) {
        this.attendance = attendance;
    }



    public static class AttendanceBean{

        private String id;
        private double inLat;
        private double inLng;
        private double outLng;
        private double outLat;
        private long   inTime;
        private long   outTime;
        private int    startTimeStatus;
        private int    endTimeStatus;
        private int    endLocationStatus;
        private int    startLocationStatus;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public double getInLat() {
            return inLat;
        }

        public void setInLat(double inLat) {
            this.inLat = inLat;
        }

        public double getInLng() {
            return inLng;
        }

        public void setInLng(double inLng) {
            this.inLng = inLng;
        }

        public double getOutLng() {
            return outLng;
        }

        public void setOutLng(double outLng) {
            this.outLng = outLng;
        }

        public double getOutLat() {
            return outLat;
        }

        public void setOutLat(double outLat) {
            this.outLat = outLat;
        }

        public long getInTime() {
            return inTime;
        }

        public void setInTime(long inTime) {
            this.inTime = inTime;
        }

        public int getStartTimeStatus() {
            return startTimeStatus;
        }

        public long getOutTime() {
            return outTime;
        }

        public void setOutTime(long outTime) {
            this.outTime = outTime;
        }

        public void setStartTimeStatus(int startTimeStatus) {
            this.startTimeStatus = startTimeStatus;
        }

        public int getEndTimeStatus() {
            return endTimeStatus;
        }

        public void setEndTimeStatus(int endTimeStatus) {
            this.endTimeStatus = endTimeStatus;
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
    }



}
