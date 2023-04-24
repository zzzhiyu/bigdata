package com.iterator_mode.homework;

public class AggIterator<T> implements Iterator<T> {
    private final Aggregate<T> goods;
    private int index;

    public AggIterator(Aggregate<T> goods) {
        this.goods = goods;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < goods.Length();
    }

    @Override
    public T next() {
        T good = goods.get(this.index);
        index++;
        return good;
    }
}
