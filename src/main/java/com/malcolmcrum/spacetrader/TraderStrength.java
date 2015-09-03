package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 8/28/2015.
 */
public enum TraderStrength {
    Absent(0),
    Minimal(1),
    Few(2),
    Some(3),
    Moderate(4),
    Many(5),
    Abundant(6),
    Swarms(7);

    private int strength;

    TraderStrength(int strength) {
        this.strength = strength;
    }

    public int getStrength() {
        return strength;
    }
}
