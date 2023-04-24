package com.iterator_mode.homework;

public interface Aggregate<T> {
    T get(int index);
    int Length();
    Iterator<T> iterator();
}
