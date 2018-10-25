package net.coahr.three3.three.Model;

import java.util.List;

/**
 * Created by yuwei on 2018/5/29.
 */

public class VerifyDataModel {

    private List<verifyListBean>list;


    public static class verifyListBean
    {

        private String id;
        private String Pname;
        private Integer inspect;
        private Integer record;
        private String  progress;
        private Long    modifyTime;
        private String  Dname;
        private String  code;
        private int number;
        private Long startTime;
        private Long endTime;
        private String location;
        private String areaAddress;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPname() {
            return Pname;
        }

        public void setPname(String pname) {
            Pname = pname;
        }

        public Integer getInspect() {
            return inspect;
        }

        public void setInspect(Integer inspect) {
            this.inspect = inspect;
        }

        public Integer getRecord() {
            return record;
        }

        public void setRecord(Integer record) {
            this.record = record;
        }

        public String getProgress() {
            return progress;
        }

        public void setProgress(String progress) {
            this.progress = progress;
        }

        public Long getModifyTime() {
            return modifyTime;
        }

        public void setModifyTime(Long modifyTime) {
            this.modifyTime = modifyTime;
        }

        public String getDname() {
            return Dname;
        }

        public void setDname(String dname) {
            Dname = dname;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Long getStartTime() {
            return startTime;
        }

        public void setStartTime(Long startTime) {
            this.startTime = startTime;
        }

        public Long getEndTime() {
            return endTime;
        }

        public void setEndTime(Long endTime) {
            this.endTime = endTime;
        }

        public String getAreaAddress() {
            return areaAddress;
        }

        public void setAreaAddress(String areaAddress) {
            this.areaAddress = areaAddress;
        }
    }

    public List<verifyListBean> getList() {
        return list;
    }

    public void setList(List<verifyListBean> list) {
        this.list = list;
    }

}
