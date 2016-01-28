package com.digdream.androidrubiksolver.entity;

/**
 * Created by dubuqingfeng on 2/25/15.
 */
public class HistoryMessage {
    private String msg;
    public String upset;
    public String recover;
    public String history_recover_time;
    public String author;
    public String id;
    public String test_time;
    public String classify;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getTest_time() {
        return test_time;
    }

    public void setTest_time(String test_time) {
        this.test_time = test_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getHistory_recover_time() {
        return history_recover_time;
    }

    public void setHistory_recover_time(String history_recover_time) {
        this.history_recover_time = history_recover_time;
    }

    public String getUpset() {
        return upset;
    }

    public void setUpset(String upset) {
        this.upset = upset;
    }

    public String getRecover() {
        return recover;
    }

    public void setRecover(String recover) {
        this.recover = recover;
    }

}
