package com.rammelbalagtas.rammel_diceroller;

public class Die {
    private int numberOfSides;
    private int result1;
    private int result2;
    private boolean willRollTwice;

    public Die(int numberOfSides, boolean willRollTwice) {
        this.numberOfSides = numberOfSides;
        this.willRollTwice = willRollTwice;
        result1 = roll();
        if (willRollTwice) {
            result2 = roll();
        }
    }

    public int roll() {
        return (int) (Math.random() * numberOfSides) + 1;
    }
    public int getNumberOfSides() {
        return numberOfSides;
    }
    public int getResult1() { return result1; }
    public int getResult2() {
        return result2;
    }
    public boolean willRollTwice() {
        return willRollTwice;
    }
}
