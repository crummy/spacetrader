package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 8/28/2015.
 */
public enum BribeLevel {
    Impossible(0),
    VeryHard(1),
    Hard(2),
    MediumHard(3),
    Medium(4),
    MediumEasy(5),
    Easy(6),
    VeryEasy(7);

    private final int value;

    BribeLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
