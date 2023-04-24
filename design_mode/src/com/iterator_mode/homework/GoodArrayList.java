package com.iterator_mode.homework;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;

public class GoodArrayList<T> implements Aggregate<T>{
    private final ArrayList<T> goods;
    private int last;

    public GoodArrayList( int initSize) {
        this.goods = new ArrayList<>(initSize);
        this.last = 0;
    }

    @Override
    public T get(int index) {
        return this.goods.get(index);
    }

    public void add(T good) {
        this.goods.add(good);
        last++;
    }

    @Override
    public int Length() {
        return last;
    }

    @Override
    @NotNull
    public AggIterator<T> iterator() {
        return new AggIterator<>(this);
    }
}
