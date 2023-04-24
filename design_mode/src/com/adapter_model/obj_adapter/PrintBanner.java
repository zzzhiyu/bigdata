package com.adapter_model.obj_adapter;

public class PrintBanner extends Print{
    private final Banner banner;

    public PrintBanner(String str) {
        this.banner = new Banner(str);
    }

    @Override
    public void printStrong() {
        banner.showWithParen();
    }

    @Override
    public void printWeak() {
        banner.showWithAster();
    }
}
