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

    public final int strength;
    public final String name;

    PoliceStrength(int strength, String name) {
        this.strength = strength;
        this.name = name;
    }
}
