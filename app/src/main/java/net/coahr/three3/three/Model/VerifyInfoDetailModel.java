package net.coahr.three3.three.Model;

import java.util.List;

/**
 * Created by yuwei on 2018/5/31.
 */

public class VerifyInfoDetailModel {

    private List<VerifyDetailBean>list;

    public static class VerifyDetailBean
    {
        private String description;
        private String id;
        private String remark;
        private String title;
        private String quota1;
        private String quota2;
        private String quota3;
        private List<StageBean>stageList;
        private String options;
        private int flag;
        private List imageSource;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
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

        public List<StageBean> getStageList() {
            return stageList;
        }

        public void setStageList(List<StageBean> stageList) {
            this.stageList = stageList;
        }

        public String getOptions() {
            return options;
        }

        public void setOptions(String options) {
            this.options = options;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public List getImageSource() {
            return imageSource;
        }

        public void setImageSource(List imageSource) {
            this.imageSource = imageSource;
        }
    }

    public static class StageBean
    {
        private String name;
        private int stage;
        private String suggestion;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getStage() {
            return stage;
        }

        public void setStage(int stage) {
            this.stage = stage;
        }

        public String getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(String suggestion) {
            this.suggestion = suggestion;
        }
    }

    public List<VerifyDetailBean> getList() {
        return list;
    }

    public void setList(List<VerifyDetailBean> list) {
        this.list = list;
    }
}
