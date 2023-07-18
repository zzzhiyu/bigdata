package com.composite_mode;

import java.util.ArrayList;
import java.util.Iterator;

public class Directory extends Entry{
    private String path;
    private String name;
    private final ArrayList<Entry> directory = new ArrayList<>();

    public Directory(String name) {
        this.name = name;
        this.path = "/" + name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSize() {
        int size = 0;
        for (Entry entry : directory) {
            size += entry.getSize();
        }
        return size;
    }

    public void setPath(String rootPath) {
        path = rootPath + path;
    }

    @Override
    public String getPath() {
        return path;
    }

    public Entry add(Entry entry) {
        entry.setPath(path);
        directory.add(entry);
        return this;
    }

    @Override
    public void printList() {
        System.out.println(this);
        for (Entry entry: directory) {
            entry.printList();
        }
    }
}
