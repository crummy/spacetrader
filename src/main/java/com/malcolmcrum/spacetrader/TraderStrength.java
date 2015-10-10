package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 8/28/2015.
 */
public enum TraderStrength {
    Absent(0, "Absent"),
    Minimal(1, "Minimal"),
    Few(2, "Few"),
    Some(3, "Some"),
    Moderate(4, "Moderate"),
    Many(5, "Many"),
    Abundant(6, "Abundant"),
    Swarms(7, "Swarms");

    private final int strength;
    public final String name;

    TraderStrength(int strength, String name) {
        this.name = name;
        this.strength = strength;
    }

    public int getStrength() {
        return strength;
    }

    public String getName() {
        return name;
    }
}
