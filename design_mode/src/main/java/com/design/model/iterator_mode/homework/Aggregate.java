package main.java.com.design.model.iterator_mode.homework;

public interface Aggregate<T> {
    T get(int index);
    int Length();
    Iterator<T> iterator();
}
