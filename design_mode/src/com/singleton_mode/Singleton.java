package com.singleton_mode;

public class Singleton {
    private static Singleton singleton = new Singleton();

    private Singleton() {
        System.out.println("生成了一个实列");
        System.out.println("勤快生成实列");
    }

    public static Singleton getInstance() {
        return singleton;
    }
}
