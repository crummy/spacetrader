package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 8/28/2015.
 */
public enum ShipType {
    Flea("Flea",        10, 0, 0, 0, 1, 20, TechLevel.EarlyIndustrial, 1,  2000,   5,   2,  25, null, null, TraderStrength.Absent, 1, Size.Tiny),
    Gnat("Gnat",        15, 1, 0, 1, 1, 14,        TechLevel.Industrial,      2, 10000,  50,  28, 100, PoliceStrength.Absent, PirateStrength.Absent, TraderStrength.Absent, 1, Size.Small),
    Firefly("Firefly",  20, 1, 1, 1, 1, 17,        TechLevel.Industrial,      3, 25000, 75, 20, 100, PoliceStrength.Absent, PirateStrength.Absent, TraderStrength.Absent, 1, Size.Small),
    Mosquito("Mosquito", 15, 2, 1, 1, 1, 13,       TechLevel.Industrial,      5, 30000, 100, 20, 100, PoliceStrength.Absent, PirateStrength.Minimal, TraderStrength.Absent, 1, Size.Small),
    Bumblebee("Bumblebee", 25, 1, 2, 2, 2, 15,     TechLevel.Industrial,      7, 60000, 125, 15, 100, PoliceStrength.Minimal, PirateStrength.Minimal, TraderStrength.Absent, 1, Size.Medium),
    Beetle("Beetle",    50, 0, 1, 1, 3, 14,       TechLevel.Industrial,     10, 80000, 50, 3, 50, null, null, TraderStrength.Absent, 1, Size.Medium),
    Hornet("Hornet",    20, 3, 2, 1, 2, 16,       TechLevel.PostIndustrial, 15, 100000, 200, 6, 150, PoliceStrength.Few, PirateStrength.Some, TraderStrength.Minimal, 2, Size.Large),
    Grasshopper("Grasshopper", 30, 2, 2, 3, 3, 15, TechLevel.PostIndustrial, 15, 150000, 300, 2, 150, PoliceStrength.Some, PirateStrength.Moderate, TraderStrength.Few, 3, Size.Large),
    Termite("Termite",  60, 1, 3, 2, 3, 13,         TechLevel.HiTech,         20, 225000, 300, 2, 200, PoliceStrength.Moderate, PirateStrength.Many, TraderStrength.Some, 4, Size.Huge),
    Wasp("Wasp",        35, 3, 2, 2, 3, 14,         TechLevel.HiTech,         20, 300000, 500, 2, 200, PoliceStrength.Many, PirateStrength.Abundant, TraderStrength.Moderate, 5, Size.Huge),
    SpaceMonster("Space Monster", 0, 3, 0, 0, 1, 1, TechLevel.Unattainable,   1, 500000, 0, 0, 500, null, null, null, 1, Size.Huge),
    Dragonfly("Dragonfly", 0, 2, 3, 2, 1, 1,        TechLevel.Unattainable,   1, 500000, 0, 0, 10, null, null, null, 1, Size.Small),
    Mantis("Mantis",    0, 3, 1, 3, 3, 1,           TechLevel.Unattainable,   1, 500000, 0, 0, 300, null, null, null, 1, Size.Medium),
    Scarab("Scarab",    20, 2, 0, 0, 2, 1,          TechLevel.Unattainable,   1, 500000, 0, 0, 400, null, null, null, 1, Size.Large),
    Bottle("Bottle",    0, 0, 0, 0, 0, 1,           TechLevel.Unattainable,   1, 100, 0, 0, 10, null, null, null, 1, Size.Small);

    private final String name;
    private final int cargoBays;
    private final int weaponSlots;
    private final int shieldSlots;
    private final int gadgetSlots;
    private final int crewQuarters;
    private final int fuelTanks;
    private final TechLevel minTechLevel;
    private final int costToFillFuelTank;
    private final int price;
    private final int initialBounty;
    private final int encounterChance;
    private final int hullStrength;
    private final PoliceStrength minStrengthForPoliceEncounter;
    private final PirateStrength minStrengthForPirateEncounter;
    private final TraderStrength minStrengthForTraderEncounter;
    private final int repairCost;
    private final Size size;

    ShipType(String name,
             int cargoBays,
             int weaponSlots,
             int shieldSlots,
             int gadgetSlots,
             int crewQuarters,
             int fuelTanks,
             TechLevel minTechLevel,
             int costToFillFuelTank,
             int price,
             int initialBounty,
             int encounterChance,
             int hullStrength,
             PoliceStrength minStrengthForPoliceEncounter,
             PirateStrength minStrengthForPirateEncounter,
             TraderStrength minStrengthForTraderEncounter,
             int repairCost,
             Size size) {

        this.name = name;
        this.cargoBays = cargoBays;
        this.weaponSlots = weaponSlots;
        this.shieldSlots = shieldSlots;
        this.gadgetSlots = gadgetSlots;
        this.crewQuarters = crewQuarters;
        this.fuelTanks = fuelTanks;
        this.minTechLevel = minTechLevel;
        this.costToFillFuelTank = costToFillFuelTank;
        this.price = price;
        this.initialBounty = initialBounty;
        this.encounterChance = encounterChance;
        this.hullStrength = hullStrength;
        this.minStrengthForPoliceEncounter = minStrengthForPoliceEncounter;
        this.minStrengthForPirateEncounter = minStrengthForPirateEncounter;
        this.minStrengthForTraderEncounter = minStrengthForTraderEncounter;
        this.repairCost = repairCost;
        this.size = size;
    }

    // TODO: Move all of these methods into Ship?

    public int getCargoBays() {
        return cargoBays;
    }

    public int getWeaponSlots() {
        return weaponSlots;
    }

    public int getGadgetSlots() {
        return gadgetSlots;
    }

    public int getShieldSlots() {
        return shieldSlots;
    }

    public int getCrewQuarters() {
        return crewQuarters;
    }

    public int getFuelTanks() {
        return fuelTanks;
    }

    public int getHullStrength() {
        return hullStrength;
    }

    public int getPrice() {
        return price;
    }

    /**
     * @return Cost to repair a single unit of hull
     */
    public int getRepairCost() {
        return repairCost;
    }

    /**
     * @return Cost to fill a single fuel tank (of which the ship has many)
     */
    public int getCostToFillFuelTank() {
        return costToFillFuelTank;
    }

    enum Size {
        Tiny,
        Small,
        Medium,
        Large,
        Huge
    }
}