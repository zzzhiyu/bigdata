package com.yzy.observer_mode;

/*
观察者模式 一对多：一个人发送信息给所有观察者
 */

public class ObserverPatternDemo {
    public static void main(String[] args) {
        School school = new School();
        new Student(school);
        new Teacher(school);

        school.setNotifyInfo("明天放假");
    }
}
