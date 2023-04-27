package com.singleton_mode;

import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        System.out.println("Start.");
        Singleton obj3 = Singleton.getInstance();
        Singleton obj4 = Singleton.getInstance();
        LazySingleton obj1 = LazySingleton.getInstance();
        LazySingleton obj2 = LazySingleton.getInstance();
        System.out.println("实例相同");
        System.out.println("End.");
    }
}
