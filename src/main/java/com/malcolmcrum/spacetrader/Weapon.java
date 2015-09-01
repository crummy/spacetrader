package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 8/28/2015.
 */
public enum Weapon {
    PulseLaser("Pulse laser", 15, 2000, TechLevel.Industrial, 50),
    BeamLaser("Beam laser", 25, 12500, TechLevel.PostIndustrial, 35),
    MilitaryLaser("Military laser", 35, 35000, TechLevel.HiTech, 15),
    MorgansLaser("Morgan's laser", 85, 50000, TechLevel.Unattainable, 0); // "fixme" in original src?

    private final String name;
    private final int power;
    private final int price;
    private final TechLevel techLevel;
    private final int chance;

    Weapon(String name, int power, int price, TechLevel techLevel, int chance) {
        this.name = name;
        this.power = power;
        this.price = price;
        this.techLevel = techLevel;
        this.chance = chance;
    }

    public int getSellPrice() {
        return (2 * price) / 3;
    }

    public int getPrice() {
        return price;
    }
}
