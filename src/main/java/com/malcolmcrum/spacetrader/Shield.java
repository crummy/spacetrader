package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 8/28/2015.
 */
public enum Shield {
    EnergyShield("Energy shield", 0, 5000, TechLevel.Industrial, 70),
    ReflectiveShield("Reflective shield", 0, 20000, TechLevel.PostIndustrial, 30),
    LightningShield("Lightning shield", 0, 45000, TechLevel.Unattainable, 0);

    private final String name;
    private final int power;
    private final int price;
    private final TechLevel techLevel;
    private final int chance;

    Shield(String name, int power, int price, TechLevel techLevel, int chance) {
        this.name = name;
        this.power = power;
        this.price = price;
        this.techLevel = techLevel;
        this.chance = chance;
    }
}
