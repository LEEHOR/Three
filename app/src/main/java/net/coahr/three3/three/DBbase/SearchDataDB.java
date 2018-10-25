package net.coahr.three3.three.DBbase;

import org.litepal.crud.DataSupport;

public class SearchDataDB extends DataSupport {
    /**
     * id
     * message 搜索记录
     */
    private int id;
    private String message;
    private UsersDB usersDB;

    public SearchDataDB(int id, String message, UsersDB usersDB) {
        this.id = id;
        this.message = message;
        this.usersDB = usersDB;
    }

    public UsersDB getUsersDB() {
        return usersDB;
    }

    public void setUsersDB(UsersDB usersDB) {
        this.usersDB = usersDB;
    }

    public SearchDataDB() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
