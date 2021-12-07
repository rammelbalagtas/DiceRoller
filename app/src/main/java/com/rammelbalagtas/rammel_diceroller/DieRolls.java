package com.rammelbalagtas.rammel_diceroller;

import java.util.ArrayList;

public class DieRolls {

    private ArrayList<Die> rollList;

    public DieRolls() {
        this.rollList = new ArrayList<>();
    }

    public ArrayList<Die> getRollList() {
        return rollList;
    }
}
