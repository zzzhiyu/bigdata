package com.builder_mode;

public class TextBuilder extends Builder{
    private final StringBuffer buffer = new StringBuffer();

    @Override
    public void makeTitle(String title) {
        buffer.append("==================================\n");
        buffer.append("[").append(title).append("]\n");
        buffer.append("\n");
    }

    @Override
    public void makeString(String str) {
        
    }

    @Override
    public void makeItems(String[] items) {

    }

    @Override
    public void close() {

    }
}
