package com.yzy.observer_mode;

public class Teacher extends People{
    public Teacher(School school) {
        this.school = school;
        this.school.attach(this);
    }

    @Override
    public void update() {
        System.out.println("people get notify info:" + school.getNotifyInfo());
    }
}
