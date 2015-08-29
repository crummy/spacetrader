package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 8/29/2015.
 */
public enum PoliceRecord {
    Psycho("Psycho", -100),
    Villain("Villain", -70),
    Criminal("Criminal", -30),
    Crook("Crook", -10),
    Dubious("Dubious", -5),
    Clean("Clean", 0),
    Lawful("Lawful", 5),
    Trusted("Trusted", 10),
    Liked("Liked", 25),
    Hero("Hero", 75);

    private final String name;
    private final int value;

    PoliceRecord(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
