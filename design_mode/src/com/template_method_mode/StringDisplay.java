package com.template_method_mode;

import java.nio.charset.Charset;

public class StringDisplay extends AbstractDisplay {
    private final String str;
    private final int width;

    public StringDisplay(String str) {
        this.str = str;
        this.width = str.getBytes(Charset.defaultCharset()).length;
    }

    private void printLine() {
        System.out.print("+");
        for (int i = 0; i < width; i++) {
            System.out.print("-");
        }
        System.out.println("+");
    }


    @Override
    public void open() {
        printLine();
    }

    @Override
    public void print() {
        System.out.println("|" + str + "|");
    }

    @Override
    public void close() {
        printLine();
    }
}
