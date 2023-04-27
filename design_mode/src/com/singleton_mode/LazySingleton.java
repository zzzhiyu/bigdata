package com.singleton_mode;

public class LazySingleton {
    private static LazySingleton lazySingleton;

    private LazySingleton() {
        System.out.println("生成一个实例");
    }

    public static LazySingleton getInstance() {
        if (lazySingleton == null) {
            synchronized (LazySingleton.class) {
                if (lazySingleton == null) {
                    System.out.println("懒堕生成实例");
                    lazySingleton = new LazySingleton();
                }
            }
        }
        return lazySingleton;
    }
}
