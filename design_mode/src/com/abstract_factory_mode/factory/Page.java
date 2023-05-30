package com.abstract_factory_mode.factory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public abstract class Page {
    protected String title;
    protected String author;
    protected ArrayList<Item> content = new ArrayList<>();

    public void add(Item item) {
        content.add(item);
    }

    public void output() {
        try {
            String filename = title + ".html";
            FileWriter writer = new FileWriter(filename);
            writer.write(makeHTML());
            writer.close();
            System.out.println(filename + ":编写完成。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract String makeHTML();
}
