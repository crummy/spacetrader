package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 8/28/2015.
 */
enum Difficulty {
    Beginner(0, "Beginner"),
    Easy(1, "Easy"),
    Normal(2, "Normal"),
    Hard(3, "Hard"),
    Impossible(4, "Impossible");

    private final int value;
    private final String name;

    Difficulty(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }
}
