package net.coahr.three3.three.Model;

import java.io.Serializable;
import java.util.List;

public class HomeSearchModel {

    private List<SearchListBean> searchList;

    public List<SearchListBean> getSearchList() {
        return searchList;
    }

    public void setSearchList(List<SearchListBean> searchList) {
        this.searchList = searchList;
    }

    public static class SearchListBean implements Serializable {
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

        private String Dname;
        private String Pname;
        private String areaAddress;
        private String code;
        private int endTime;
        private String id;
        private int inspect;
        private String location;
        private long modifyTime;
        private int record;
        private long startTime;

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

        public int getEndTime() {
            return endTime;
        }

        public void setEndTime(int endTime) {
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

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public long getModifyTime() {
            return modifyTime;
        }

        public void setModifyTime(long modifyTime) {
            this.modifyTime = modifyTime;
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
