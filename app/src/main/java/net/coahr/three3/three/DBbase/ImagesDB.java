package net.coahr.three3.three.DBbase;

import org.litepal.crud.DataSupport;

/**
 * Created by 李浩
 * 2018/4/23
 * 图片保存路经
 */
public class ImagesDB extends DataSupport {
    /**Imageid 图片Id
     * imageName  图片名
     * imagePath  图片源地址
     *zibImagePath  压缩后图片保存地址
     */
    private int id;
    private String imageName;
    private String imagePath;
    private String zibImagePath;
    private SubjectsDB SubjectsDB;
    private ProjectsDB projectsDB;

    public ImagesDB(int id, String imageName, String imagePath, String zibImagePath, SubjectsDB SubjectsDB, ProjectsDB projectsDB) {
        this.id = id;
        this.imageName = imageName;
        this.imagePath = imagePath;
        this.zibImagePath = zibImagePath;
        this.SubjectsDB = SubjectsDB;
        this.projectsDB = projectsDB;
    }

    public ImagesDB() {
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

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getZibImagePath() {
        return zibImagePath;
    }

    public void setZibImagePath(String zibImagePath) {
        this.zibImagePath = zibImagePath;
    }
}
