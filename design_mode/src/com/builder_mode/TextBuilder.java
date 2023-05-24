package com.builder_mode;

public class TextBuilder implements Builder{
    private final StringBuffer buffer = new StringBuffer();

    @Override
    public void makeTitle(String title) {
        buffer.append("==================================\n");
        buffer.append("[").append(title).append("]\n");
        buffer.append("\n");
    }

    @Override
    public void makeString(String str) {
        buffer.append(" ").append(str).append("\n");
        buffer.append("\n");
    }

    @Override
    public void makeItems(String[] items) {
        for (int i = 0; i < items.length; i++) {
            buffer.append("   >").append(items[i]).append("\n");
        }
        buffer.append("\n");
    }

    @Override
    public void close() {
        buffer.append("==================================\n");
    }

    public String getResult() {
        return buffer.toString();
    }
}
