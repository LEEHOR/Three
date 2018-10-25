package net.coahr.three3.three.Model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 李浩
 * 2018/5/11
 */
public class HomeDataListModle {

        private List<AllListBean> allList;
        private List<AllListBean> completeList;
        private List<AllListBean> newList;
        private List<AllListBean> unCompleteList;

    public List<AllListBean> getAllList() {
        return allList;
    }

    public void setAllList(List<AllListBean> allList) {
        this.allList = allList;
    }

    public List<AllListBean> getCompleteList() {
        return completeList;
    }

    public void setCompleteList(List<AllListBean> completeList) {
        this.completeList = completeList;
    }

    public List<AllListBean> getNewList() {
        return newList;
    }

    public void setNewList(List<AllListBean> newList) {
        this.newList = newList;
    }

    public List<AllListBean> getUnCompleteList() {
        return unCompleteList;
    }

    public void setUnCompleteList(List<AllListBean> unCompleteList) {
        this.unCompleteList = unCompleteList;
    }

    public static class AllListBean extends DataSupport implements Serializable  {
            /**
             * //Cname : 自由班次
             * Dname : 东风本田东合店
             * Pname : 斯锐调查系统132456
             * areaAddress : 湖北省武汉市蔡甸区
             * code : 123
             * //completeStatus : 1
             * //distance : 29604
             * //downloadTime : -1
             * endTime : 1
             * id : 43c4065cc2494634b71bc870ebbd1766
             * inspect : 1
             * //latitude : 30.506281
             * location : 东合中心B座
             * //longitude : 114.163684
             * modifyTime : 1526029757614
             * progress : 0/100
             * record : 3
             * startTime : 1526029757500
             */

        /**
         * Dname : 东风本田东合店
         * Pname : 斯锐调查系统132456
         * areaAddress : 湖北省武汉市蔡甸区
         * code : 123
         * endTime : 1
         * id : 43c4065cc2494634b71bc870ebbd1766
         * inspect : 1
         * location : 东合中心B座
         * modifyTime : 1526029757614
         * record : 3
         * startTime : 1526029757500
         */

            private String Cname;
            private String Dname;
            private String Pname;
            private String areaAddress;
            private String code;
            private int completeStatus;
            private String distance;
            private long downloadTime;
            private Long endTime;
            private String id;
            private String grade;
            private int inspect;
            private double latitude;
            private String location;
            private double longitude;
            private long modifyTime;
            private String progress;
            private int record;
            private long startTime;
            private String manager;
            private String notice;

        public AllListBean(String cname, String dname, String pname, String areaAddress, String code, int completeStatus, String distance, long downloadTime, Long endTime, String id, String grade, int inspect, double latitude, String location, double longitude, long modifyTime, String progress, int record, long startTime, String manager, String notice) {
            Cname = cname;
            Dname = dname;
            Pname = pname;
            this.areaAddress = areaAddress;
            this.code = code;
            this.completeStatus = completeStatus;
            this.distance = distance;
            this.downloadTime = downloadTime;
            this.endTime = endTime;
            this.id = id;
            this.grade = grade;
            this.inspect = inspect;
            this.latitude = latitude;
            this.location = location;
            this.longitude = longitude;
            this.modifyTime = modifyTime;
            this.progress = progress;
            this.record = record;
            this.startTime = startTime;
            this.manager = manager;
            this.notice = notice;
        }

        public AllListBean() {
        }

        public String getManager() {
            return manager;
        }

        public void setManager(String manager) {
            this.manager = manager;
        }

        public String getNotice() {
            return notice;
        }

        public void setNotice(String notice) {
            this.notice = notice;
        }

        public String getCname() {
                return Cname;
            }

            public void setCname(String Cname) {
                this.Cname = Cname;
            }

            public String getDname() {
                return Dname;
            }

            public void setDname(String Dname) {
                this.Dname = Dname;
            }

            public String getPname() {
                return Pname;
            }

            public void setPname(String Pname) {
                this.Pname = Pname;
            }

            public String getAreaAddress() {
                return areaAddress;
            }

            public void setAreaAddress(String areaAddress) {
                this.areaAddress = areaAddress;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public int getCompleteStatus() {
                return completeStatus;
            }

            public void setCompleteStatus(int completeStatus) {
                this.completeStatus = completeStatus;
            }

            public String getDistance() {
                return distance;
            }

            public void setDistance(String distance) {
                this.distance = distance;
            }

            public long getDownloadTime() {
                return downloadTime;
            }

            public void setDownloadTime(long downloadTime) {
                this.downloadTime = downloadTime;
            }

            public long getEndTime() {
                return endTime;
            }

            public void setEndTime(long endTime) {
                this.endTime = endTime;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public int getInspect() {
                return inspect;
            }

            public void setInspect(int inspect) {
                this.inspect = inspect;
            }

        public void setEndTime(Long endTime) {
            this.endTime = endTime;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
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

            public long getModifyTime() {
                return modifyTime;
            }

            public void setModifyTime(long modifyTime) {
                this.modifyTime = modifyTime;
            }

            public String getProgress() {
                return progress;
            }

            public void setProgress(String progress) {
                this.progress = progress;
            }

            public int getRecord() {
                return record;
            }

            public void setRecord(int record) {
                this.record = record;
            }

            public long getStartTime() {
                return startTime;
            }

            public void setStartTime(long startTime) {
                this.startTime = startTime;
            }

    }


}
