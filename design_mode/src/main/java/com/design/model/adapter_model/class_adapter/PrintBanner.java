package main.java.com.design.model.adapter_model.class_adapter;

public class PrintBanner extends Banner implements Print{
    public PrintBanner(String str) {
        super(str);
    }

    @Override
    public void printWeak() {
        showWithParen();
    }

    @Override
    public void printStrong() {
        showWithAster();
    }
}
