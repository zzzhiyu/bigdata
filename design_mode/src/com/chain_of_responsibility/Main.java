package com.chain_of_responsibility;

public class Main {
    public static void main(String[] args) {
        Support alice = new NoSupport("Alics");
        Support bob = new LimitSupport("Bob", 100);

    }
}
