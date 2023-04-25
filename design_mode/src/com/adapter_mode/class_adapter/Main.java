package com.adapter_mode.class_adapter;

public class Main {
    public static void main(String[] args) {
        Print p = new PrintBanner("hello");
        p.printWeak();
        p.printStrong();
    }
}
