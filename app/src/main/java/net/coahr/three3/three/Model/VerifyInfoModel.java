package net.coahr.three3.three.Model;

import android.content.Intent;

import java.util.List;

/**
 * Created by yuwei on 2018/5/29.
 */

public class VerifyInfoModel {

    private List<verifyInfoListBean> list;
    public static class verifyInfoListBean
    {
        private String id;
        private String title;
        private Integer stage;
        private String suggestion;
        private String quota1;
        private String quota2;
        private String quota3;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Integer getStage() {
            return stage;
        }

        public void setStage(Integer stage) {
            this.stage = stage;
        }

        public String getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(String suggestion) {
            this.suggestion = suggestion;
        }

        public String getQuota1() {
            return quota1;
        }

        public void setQuota1(String quota1) {
            this.quota1 = quota1;
        }

        public String getQuota2() {
            return quota2;
        }

        public void setQuota2(String quota2) {
            this.quota2 = quota2;
        }

        public String getQuota3() {
            return quota3;
        }

        public void setQuota3(String quota3) {
            this.quota3 = quota3;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public List<verifyInfoListBean> getList() {
        return list;
    }

    public void setList(List<verifyInfoListBean> list) {
        this.list = list;
    }
}
