package com.malcolmcrum.spacetrader;

import com.malcolmcrum.spacetrader.SolarSystem.*;

/**
 * Created by Malcolm on 8/28/2015.
 */
public enum TradeItem {
    Water("Water",
            TechLevel.Preagricultural,
            TechLevel.Preagricultural,
            TechLevel.Medieval,
            30,
            3,
            4,
            Status.Drought,
            SpecialResource.SweetwaterOceans,
            SpecialResource.Desert,
            30,
            50,
            1),
    Furs("Furs",
            TechLevel.Preagricultural,
            TechLevel.Preagricultural,
            TechLevel.Preagricultural,
            250,
            10,
            10,
            Status.Cold,
            SpecialResource.RichFauna,
            SpecialResource.Lifeless,
            230,
            280,
            5),
    Food("Food",
            TechLevel.Agricultural,
            TechLevel.Preagricultural,
            TechLevel.Agricultural,
            100,
            5,
            5,
            Status.Crops,
            SpecialResource.RichSoil,
            SpecialResource.PoorSoil,
            90,
            160,
            5),
    Ore("Ore",
            TechLevel.Medieval,
            TechLevel.Medieval,
            TechLevel.Renaissance,
            350,
            20,
            10,
            Status.War,
            SpecialResource.WarlikePopulace,
            SpecialResource.MineralPoor,
            350,
            420,
            10),
    Games("Games",
            TechLevel.Renaissance,
            TechLevel.Agricultural,
            TechLevel.PostIndustrial,
            250,
            -10,
            5,
            Status.Boredom,
            SpecialResource.ArtisticPopulace,
            null,
            160,
            270,
            5),
    Firearms("Firearms",
            TechLevel.Renaissance,
            TechLevel.Preagricultural,
            TechLevel.Industrial,
            1250,
            -75,
            100,
            Status.War,
            SpecialResource.WarlikePopulace,
            null,
            600,
            1100,
            25),
    Medicine("Medicine",
            TechLevel.EarlyIndustrial,
            TechLevel.Agricultural,
            TechLevel.PostIndustrial,
            650,
            -20,
            10,
            Status.Plague,
            SpecialResource.SpecialHerbs,
            null,
            400,
            700,
            25),
    Machines("Machines",
            TechLevel.EarlyIndustrial,
            TechLevel.Renaissance,
            TechLevel.Industrial,
            900,
            -30,
            5,
            Status.Workers,
            null,
            null,
            600,
            800,
            25),
    Narcotics("Narcotics",
            TechLevel.Industrial,
            TechLevel.Preagricultural,
            TechLevel.Industrial,
            3500,
            -125,
            150,
            Status.Boredom,
            SpecialResource.WeirdMushrooms,
            null,
            2000,
            3000,
            50),
    Robots("Robots",
            TechLevel.PostIndustrial,
            TechLevel.EarlyIndustrial,
            TechLevel.HiTech,
            5000,
            -150,
            100,
            Status.Workers,
            null,
            null,
            3500,
            5000,
            100);

    public String name;
    public TechLevel techLevelRequiredForProduction;
    public TechLevel techLevelRequiredForUsage;
    public TechLevel techLevelForTopProduction;
    public int lowestTechLevelPrice;
    public int priceIncreasePerLevel;
    public int priceVariance;
    public Status doublePriceTrigger;
    public SpecialResource cheapResourceTrigger;
    public SpecialResource expensiveResourceTrigger;
    public int minTradePrice;
    public int maxTradePrice;
    public int roundOff;

    TradeItem(String name,
              TechLevel techLevelRequiredForProduction,
              TechLevel techLevelRequiredForUsage,
              TechLevel techLevelForTopProduction,
              int lowestTechLevelPrice,
              int priceIncreasePerLevel,
              int priceVariance,
              Status doublePriceTrigger,
              SpecialResource cheapResourceTrigger,
              SpecialResource expensiveResourceTrigger,
              int minTradePrice,
              int maxTradePrice,
              int roundOff) {

        this.name = name;
        this.techLevelForTopProduction = techLevelForTopProduction;
        this.techLevelRequiredForProduction = techLevelRequiredForProduction;
        this.techLevelRequiredForUsage = techLevelRequiredForUsage;
        this.lowestTechLevelPrice = lowestTechLevelPrice;
        this.priceIncreasePerLevel = priceIncreasePerLevel;
        this.priceVariance = priceVariance;
        this.doublePriceTrigger = doublePriceTrigger;
        this.cheapResourceTrigger = cheapResourceTrigger;
        this.expensiveResourceTrigger = expensiveResourceTrigger;
        this.minTradePrice = minTradePrice; // Minimum price to buy/sell in orbit
        this.maxTradePrice = maxTradePrice; // Maximum price to buy/sell in orbit
        this.roundOff = roundOff;
    }
}
