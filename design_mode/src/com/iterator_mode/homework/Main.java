package com.iterator_mode.homework;
import com.iterator_mode.Book;

public class Main {
    public static void main(String[] args) {
        GoodArrayList<Book> books = new GoodArrayList<>(4);
        books.add(new Book("Around the World in 80 Days"));
        books.add(new Book("Bible"));
        books.add(new Book("Cinderella"));
        books.add(new Book("Daddy-Long-Legs"));
        AggIterator<Book> it = books.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().getName());
        }
    }
}
