package com.composite_mode;

public class File extends Entry{
    private String path;
    private String name;
    private int size;

    public File(String name, int size) {
        this.name = name;
        this.size = size;
        this.path = "/" + name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSize() {
        return size;
    }

    public void setPath(String rootPath) {
        path = rootPath + path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void printList() {
        System.out.println(this);
    }
}
