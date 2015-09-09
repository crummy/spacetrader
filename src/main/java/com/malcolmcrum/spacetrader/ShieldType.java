package com.malcolmcrum.spacetrader;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * Created by Malcolm on 8/28/2015.
 */
public enum ShieldType {
    EnergyShield("Energy shield", 100, 5000, TechLevel.Industrial, 70),
    ReflectiveShield("Reflective shield", 200, 20000, TechLevel.PostIndustrial, 30),
    LightningShield("Lightning shield", 350, 45000, TechLevel.Unattainable, 0);

    private final String name;
    private final int power;
    private final int price;
    private final TechLevel techLevelRequiredForSale;
    private final int chance;

    ShieldType(String name, int power, int price, TechLevel techLevelRequiredForSale, int chance) {
        this.name = name;
        this.power = power;
        this.price = price;
        this.techLevelRequiredForSale = techLevelRequiredForSale;
        this.chance = chance;
    }

    public int getSellPrice() {
        return (2 * price) / 3;
    }

    public int getPrice() {
        return price;
    }

    public int getPower() {
        return power;
    }

    public static ShieldType GetAdjustedRandomShield() {
        int totalOccurrenceChance = 0;
        for (ShieldType type : ShieldType.values()) {
            totalOccurrenceChance += type.chance;
        }

        int chosenOccurrenceIndex = GetRandom(totalOccurrenceChance);

        int currentOccurrenceTotal = 0;
        for (ShieldType type : ShieldType.values()) {
            currentOccurrenceTotal += type.chance;
            if (currentOccurrenceTotal >= chosenOccurrenceIndex) {
                return type;
            }
        }
        return null;
    }
}
