package net.coahr.three3.three.DBbase;

import org.litepal.crud.DataSupport;


/**
 * Created by 李浩
 * 2018/5/15
 * 录音保存库
 */
public class RecorderFilesDB extends DataSupport {
    /**
     * RecorderId id
     *RecorderPath 录音地址
     * RecorderName 录音名
     * SubjectsDB   题目
     * projectsDB   项目
     */
    private int id;
    private String RecorderPath;
    private String RecorderName;
    private SubjectsDB SubjectsDB; //题目列表
    private ProjectsDB projectsDB;  //所属项目
    private int isReUpload;


    public RecorderFilesDB() {
    }

    public RecorderFilesDB(int id, String recorderPath, String recorderName, net.coahr.three3.three.DBbase.SubjectsDB subjectsDB, ProjectsDB projectsDB, int isReUpload) {
        this.id = id;
        RecorderPath = recorderPath;
        RecorderName = recorderName;
        SubjectsDB = subjectsDB;
        this.projectsDB = projectsDB;
        this.isReUpload = isReUpload;
    }

    public ProjectsDB getProjectsDB() {
        return projectsDB;
    }

    public void setProjectsDB(ProjectsDB projectsDB) {
        this.projectsDB = projectsDB;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SubjectsDB getSubjectsDB() {
        return SubjectsDB;
    }

    public void setSubjectsDB(SubjectsDB SubjectsDB) {
        this.SubjectsDB = SubjectsDB;
    }

    public String getRecorderPath() {
        return RecorderPath;
    }

    public void setRecorderPath(String recorderPath) {
        RecorderPath = recorderPath;
    }

    public String getRecorderName() {
        return RecorderName;
    }

    public void setRecorderName(String recorderName) {
        RecorderName = recorderName;
    }

    public int getIsReUpload() {
        return isReUpload;
    }

    public void setIsReUpload(int isReUpload) {
        this.isReUpload = isReUpload;
    }
}
