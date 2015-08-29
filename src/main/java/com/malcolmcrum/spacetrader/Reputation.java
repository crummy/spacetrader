package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 8/29/2015.
 */
public enum Reputation {
    Harmless("Harmless", 0),
    MostlyHarmless("Mostly harmless", 10),
    Poor("Poor", 20),
    Average("Average", 40),
    AboveAverage("Above average", 80),
    Competent("Competent", 150),
    Dangerous("Dangerous", 300),
    Deadly("Deadly", 600),
    Elite("Elite", 1500);

    private final String name;
    private final int value;

    Reputation(String name, int value) {

        this.name = name;
        this.value = value;
    }
}
