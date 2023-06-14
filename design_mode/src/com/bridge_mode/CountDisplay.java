package com.bridge_mode;

public class CountDisplay extends Display{
    public CountDisplay(DisplayImpl impl) {
        super(impl);
    }

    public void multiDisplay(int time) {
        open();
        for (int i = 0; i < time; i++) {
            print();
        }
        close();
    }
}
