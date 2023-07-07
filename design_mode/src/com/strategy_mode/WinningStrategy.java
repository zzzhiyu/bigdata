package com.strategy_mode;

import java.util.Random;

public class WinningStrategy implements Strategy{
    private final Random random;
    private boolean won = false;
    private Hand preHand;
    public WinningStrategy(int seed) {
        random = new Random(seed);
    }

    @Override
    public Hand nextHand() {
        if (!won) {
            preHand = Hand.getHand(random.nextInt(3));
        }
        return preHand;
    }

    @Override
    public void study(boolean win) {
        won = win;
    }
}
