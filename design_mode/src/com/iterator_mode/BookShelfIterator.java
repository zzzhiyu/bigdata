package com.iterator_mode;

public class BookShelfIterator implements Iterator {
    private final BookShelf bookShelf;
    private int index;

    public BookShelfIterator(BookShelf bookShelf) {
        this.bookShelf = bookShelf;
        this.index = 0;
    }

    @Override
    public boolean hashNext() {
        return this.index < this.bookShelf.getLength();
     }

    @Override
    public Object next() {
        Book book = this.bookShelf.getBookAt(this.index);
        this.index ++;
        return book;
    }
}
