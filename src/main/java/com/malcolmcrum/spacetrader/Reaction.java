package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 8/28/2015.
 */
public enum Reaction {
    None(0),
    Minimal(1),
    Tiny(2),
    Small(3),
    Normal(4),
    Strong(5),
    Harsh(6),
    Extreme(7);


    private final int severity;

    Reaction(int severity) {
        this.severity = severity;
    }

}
