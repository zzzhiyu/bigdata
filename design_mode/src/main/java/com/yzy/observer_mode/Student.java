package com.yzy.observer_mode;

public class Student extends People{
    public Student(School school) {
        this.school = school;
        this.school.attach(this);
    }

    @Override
    public void update() {
        System.out.println("student get notify info:" + school.getNotifyInfo());
    }
}
