package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 8/28/2015.
 */
public enum Gadget {
    CargoBays("5 extra cargo bays", 2500, TechLevel.EarlyIndustrial, 35),
    Repairs("Auto-repair system", 7500, TechLevel.Industrial, 20),
    Navigation("Navigating system", 15000, TechLevel.PostIndustrial, 20),
    Targeting("Targeting system", 25000, TechLevel.PostIndustrial, 20),
    Cloaking("Cloaking device", 100000, TechLevel.HiTech, 5),
    FuelCompactor("Fuel compactor", 30000, TechLevel.Unattainable, 0);


    private final String name;
    private final int price;
    private final TechLevel techLevel;
    private final int chance;

    Gadget(String name, int price, TechLevel techLevel, int chance) {
        this.name = name;
        this.price = price;
        this.techLevel = techLevel;
        this.chance = chance;
    }
}
