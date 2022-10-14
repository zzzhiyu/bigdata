package com.yzy.observer_mode;

import java.util.ArrayList;
import java.util.List;

public class School{
    private String notifyInfo;
    private List<People> peoples = new ArrayList<>();

    public String getNotifyInfo() {
        return notifyInfo;
    }

    public void setNotifyInfo(String notifyInfo) {
        this.notifyInfo = notifyInfo;
        notifyAllPeoples();
    }
    
    public void attach(People people) {
        peoples.add(people);
    }
    
    public void notifyAllPeoples() {
        for (People people : peoples) {
            people.update();
        }
    }
}
