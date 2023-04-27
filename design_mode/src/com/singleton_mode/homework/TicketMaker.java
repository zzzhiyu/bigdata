package com.singleton_mode.homework;

public class TicketMaker {
    private int ticket = 1000;
    private static TicketMaker ticketMaker;

    private TicketMaker() {}

    public int getNextTicketNumber() {
        return ticket++;
    }

    public static TicketMaker getInstance() {
        if (ticketMaker == null) {
            synchronized (TicketMaker.class) {
                if (ticketMaker == null) {
                    ticketMaker = new TicketMaker();
                }
            }
        }
        return ticketMaker;
    }
}
