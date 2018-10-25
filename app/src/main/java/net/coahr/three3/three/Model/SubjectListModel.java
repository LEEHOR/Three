package net.coahr.three3.three.Model;

import java.util.List;

public class SubjectListModel {


    /**
     * data : {"questionList":[{"describeStatus":1,"description":"dsadas","id":"4","name":"汉口","options":"是&否","photoStatus":1,"recordStatus":1,"title":"dasd","type":0},{"describeStatus":1,"description":"43432","id":"5","name":"汉口","photoStatus":1,"recordStatus":1,"title":"3432","type":0},{"describeStatus":1,"description":"333terte","id":"6","name":"汉口","photoStatus":1,"recordStatus":1,"title":"545et","type":0},{"describeStatus":1,"description":"dasd","id":"7","name":"汉口","photoStatus":1,"recordStatus":1,"title":"dd","type":0},{"describeStatus":1,"description":"gdf","id":"8","name":"汉口","photoStatus":1,"recordStatus":1,"title":"gfdgd","type":0},{"describeStatus":1,"description":"rewr","id":"9","name":"汉口","photoStatus":1,"recordStatus":1,"title":"drrrrqe","type":0}]}
     * msg : 请求成功
     * result : 1
     */
        private List<QuestionListBean> questionList;

        public List<QuestionListBean> getQuestionList() {
            return questionList;
        }

        public void setQuestionList(List<QuestionListBean> questionList) {
            this.questionList = questionList;
        }

        public static class QuestionListBean {
            /**
             * describeStatus : 1    是否说明
             * description : dsadas  题目描述
             * id : 4
             * quota1   指标
             * name : 汉口  指标
             * options : 是&否  答案
             * photoStatus : 1  拍照
             * recordStatus : 1 录音
             * title : dasd    标题
             * type : 0    题目类型
             */

            private Integer describeStatus;
            private String description;
            private String id;
            private String quota1;
            private String quota2;
            private String quota3;
            private String options;
            private Integer photoStatus;
            private Integer recordStatus;
            private String title;
            private Integer type;
            private int flag;
            private String remark;
            private Integer censor;
            private List<VerifyInfoDetailModel.StageBean>stageList;

            public Integer getDescribeStatus() {
                return describeStatus;
            }

            public void setDescribeStatus(Integer describeStatus) {
                this.describeStatus = describeStatus;
            }

            public Integer getCensor() {
                return censor;
            }

            public void setCensor(Integer censor) {
                this.censor = censor;
            }

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

            public String getOptions() {
                return options;
            }

            public void setOptions(String options) {
                this.options = options;
            }

            public Integer getPhotoStatus() {
                return photoStatus;
            }

            public void setPhotoStatus(Integer photoStatus) {
                this.photoStatus = photoStatus;
            }

            public Integer getRecordStatus() {
                return recordStatus;
            }

            public void setRecordStatus(Integer recordStatus) {
                this.recordStatus = recordStatus;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public Integer getType() {
                return type;
            }

            public void setType(Integer type) {
                this.type = type;
            }

            public int getFlag() {
                return flag;
            }

            public void setFlag(int flag) {
                this.flag = flag;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public List<VerifyInfoDetailModel.StageBean> getStageList() {
                return stageList;
            }

            public void setStageList(List<VerifyInfoDetailModel.StageBean> stageList) {
                this.stageList = stageList;
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

        }



}
