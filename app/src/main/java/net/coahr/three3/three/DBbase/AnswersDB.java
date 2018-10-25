package net.coahr.three3.three.DBbase;

import org.litepal.crud.DataSupport;

public class AnswersDB extends DataSupport {
    private  int id;
    private  String answer;
    private  String remakes;
    private SubjectsDB subjectsDB;

    public AnswersDB() {
    }

    public AnswersDB(int id, String answer, String remakes, SubjectsDB subjectsDB) {
        this.id = id;
        this.answer = answer;
        this.remakes = remakes;
        this.subjectsDB = subjectsDB;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRemakes() {
        return remakes;
    }

    public void setRemakes(String remakes) {
        this.remakes = remakes;
    }

    public SubjectsDB getSubjectsDB() {
        return subjectsDB;
    }

    public void setSubjectsDB(SubjectsDB subjectsDB) {
        this.subjectsDB = subjectsDB;
    }
}
