package net.coahr.three3.three.DBbase;

import net.coahr.three3.three.Util.JDBC.DataBaseWork;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李浩
 * 2018/5/17
 */
public class UsersDB extends DataSupport {
    /**
     * id UserDB 主键
     * userName 用户名
     * sessionId 用户sessionId
     */

    private int id;
    @Column(nullable = true)
    private String userName;
    @Column(unique =true)
    private String sessionId;
    private List<ProjectsDB> projectsDBSList=new ArrayList<>();
    private List<SearchDataDB> searchDataDBList=new ArrayList<>();

    public UsersDB() {
    }

    public UsersDB(int id, String userName, String sessionId, List<ProjectsDB> projectsDBSList, List<SearchDataDB> searchDataDBList) {
        this.id = id;
        this.userName = userName;
        this.sessionId = sessionId;
        this.projectsDBSList = projectsDBSList;
        this.searchDataDBList = searchDataDBList;
    }

    public List<SearchDataDB> getSearchDataDBList() {
        return DataBaseWork.DBSelectByTogether_Where(SearchDataDB.class,"usersdb_id=?",String.valueOf(id));
    }

    public void setSearchDataDBList(List<SearchDataDB> searchDataDBList) {
        this.searchDataDBList = searchDataDBList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<ProjectsDB> getProjectsDBSList() {
        return DataBaseWork.DBSelectByTogether_Where(ProjectsDB.class,"usersdb_id=?",String.valueOf(id));
    }

    public void setProjectsDBSList(List<ProjectsDB> projectsDBSList) {
        this.projectsDBSList = projectsDBSList;
    }
}
