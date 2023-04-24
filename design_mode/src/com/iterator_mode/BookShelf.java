package com.iterator_mode;

public class BookShelf implements Aggregate{
    private final Book[] books;
    private int last = 0;

    public BookShelf(int maxSize) {
        this.books = new Book[maxSize];
    }

    public Book getBookAt(int index) {
        return this.books[index];
    }

    public void appendBook(Book book) {
        this.books[this.last++] = book;
    }

    public int getLength() {
        return this.last;
    }

    @Override
    public Iterator iterator() {
        return new BookShelfIterator(this);
    }

}
