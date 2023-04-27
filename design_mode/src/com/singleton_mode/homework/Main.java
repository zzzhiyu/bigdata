package com.singleton_mode.homework;

public class Main {
    public static void main(String[] args) {
        TicketMaker ticketMaker = TicketMaker.getInstance();
        System.out.println(ticketMaker.getNextTicketNumber());
        System.out.println(ticketMaker.getNextTicketNumber());
        Triple triple0 = Triple.getInstance(0);
        Triple triple1 = Triple.getInstance(1);
        Triple triple2 = Triple.getInstance(2);
        System.out.println(triple0.getId());
        System.out.println(triple1.getId());
        System.out.println(triple2.getId());
    }
}
