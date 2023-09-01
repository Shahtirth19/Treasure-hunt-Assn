package com.example.mdaassn;

import java.io.Serializable;

public class Objective implements Serializable {

    public String title,obj;

    public Objective() {

    }

    public Objective(String title, String obj) {
        this.title = title;
        this.obj = obj;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getObj() {
        return obj;
    }

    public void setObj(String obj) {
        this.obj = obj;
    }
}
