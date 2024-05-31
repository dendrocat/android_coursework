package com.example.app.Models.Checks;

public class BaseFilter {

    private String UID;

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    private String name;

    public BaseFilter() {}

    public BaseFilter(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
