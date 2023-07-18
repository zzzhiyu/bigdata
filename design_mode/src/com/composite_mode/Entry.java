package com.composite_mode;

public abstract class Entry {
    public abstract String getName();
    public abstract int getSize();
    public abstract String getPath();
    public abstract void setPath(String rootPath);
    public Entry add(Entry entry) throws FileTreatmentException {
        throw new FileTreatmentException();
    }
    public abstract void printList();

    public String toString() {
        return getPath() + "(" + getSize() + ")";
    }
}
