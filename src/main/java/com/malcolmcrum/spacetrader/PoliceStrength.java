package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 8/28/2015.
 */
public enum PoliceStrength {
    Absent(0, "Absent"),
    Minimal(1, "Minimal"),
    Few(2, "Few"),
    Some(3, "Some"),
    Moderate(4, "Moderate"),
    Many(5, "Many"),
    Abundant(6, "Abundant"),
    Swarms(7, "Swarms");

    private final int strength;
    private final String name;

    PoliceStrength(int strength, String name) {
        this.strength = strength;
        this.name = name;
    }

    public int getStrength() {
        return strength;
    }

    public String getName() {
        return name;
    }
}