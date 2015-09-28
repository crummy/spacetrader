package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 8/28/2015.
 */
public enum Politics {
    Anarchy("Anarchy",
            Reaction.None,
            PoliceStrength.Absent,
            PirateStrength.Swarms,
            TraderStrength.Minimal,
            TechLevel.Preagricultural,
            TechLevel.Industrial,
            BribeLevel.VeryEasy,
            true,
            true,
            TradeItem.Food),
    Capitalism("Capitalist State",
            Reaction.Extreme,
            PoliceStrength.Some,
            PirateStrength.Few,
            TraderStrength.Swarms,
            TechLevel.EarlyIndustrial,
            TechLevel.HiTech,
            BribeLevel.VeryHard,
            true,
            true,
            TradeItem.Ore),
    Communism("Communist State",
            Reaction.Harsh,
            PoliceStrength.Abundant,
            PirateStrength.Moderate,
            TraderStrength.Moderate,
            TechLevel.Agricultural,
            TechLevel.Industrial,
            BribeLevel.MediumEasy,
            true,
            true,
            null),
    Confederacy("Confederacy",
            Reaction.Strong,
            PoliceStrength.Moderate,
            PirateStrength.Some,
            TraderStrength.Many,
            TechLevel.Agricultural,
            TechLevel.PostIndustrial,
            BribeLevel.MediumHard,
            true,
            true,
            TradeItem.Games),
    Corporates("Corporate State",
            Reaction.Tiny,
            PoliceStrength.Abundant,
            PirateStrength.Few,
            TraderStrength.Swarms,
            TechLevel.EarlyIndustrial,
            TechLevel.HiTech,
            BribeLevel.Hard,
            true,
            true,
            TradeItem.Robots),
    Cybernetic("Cybernetic State",
            Reaction.None,
            PoliceStrength.Swarms,
            PirateStrength.Swarms,
            TraderStrength.Many,
            TechLevel.PostIndustrial,
            TechLevel.HiTech,
            BribeLevel.Impossible,
            false,
            false,
            TradeItem.Ore),
    Democracy("Democracy",
            Reaction.Normal,
            PoliceStrength.Some,
            PirateStrength.Few,
            TraderStrength.Many,
            TechLevel.Renaissance,
            TechLevel.HiTech,
            BribeLevel.Hard,
            true,
            true,
            TradeItem.Games),
    Dictatorship("Dictatorship",
            Reaction.Small,
            PoliceStrength.Moderate,
            PirateStrength.Many,
            TraderStrength.Some,
            TechLevel.Preagricultural,
            TechLevel.HiTech,
            BribeLevel.Hard,
            true,
            true,
            null),
    Fascism("Fascist State",
            Reaction.Extreme,
            PoliceStrength.Swarms,
            PirateStrength.Swarms,
            TraderStrength.Minimal,
            TechLevel.EarlyIndustrial,
            TechLevel.HiTech,
            BribeLevel.Impossible,
            false,
            true,
            TradeItem.Machines),
    Feudal("Feudal State",
            Reaction.Minimal,
            PoliceStrength.Minimal,
            PirateStrength.Abundant,
            TraderStrength.Few,
            TechLevel.Preagricultural,
            TechLevel.Renaissance,
            BribeLevel.Easy,
            true,
            true,
            TradeItem.Firearms),
    Military("Military State",
            Reaction.Extreme,
            PoliceStrength.Swarms,
            PirateStrength.Absent,
            TraderStrength.Abundant,
            TechLevel.Medieval,
            TechLevel.HiTech,
            BribeLevel.Impossible,
            false,
            true,
            TradeItem.Robots),
    Monarchy("Monarchy",
            Reaction.Small,
            PoliceStrength.Moderate,
            PirateStrength.Some,
            TraderStrength.Moderate,
            TechLevel.Preagricultural,
            TechLevel.Industrial,
            BribeLevel.Medium,
            true,
            true,
            TradeItem.Medicine),
    Pacifism("Pacifist State",
            Reaction.Extreme,
            PoliceStrength.Few,
            PirateStrength.Minimal,
            TraderStrength.Many,
            TechLevel.Preagricultural,
            TechLevel.Renaissance,
            BribeLevel.VeryHard,
            true,
            false,
            null),
    Socialism("Socialist State",
            Reaction.Normal,
            PoliceStrength.Few,
            PirateStrength.Many,
            TraderStrength.Some,
            TechLevel.Preagricultural,
            TechLevel.Industrial,
            BribeLevel.Easy,
            true,
            true,
            null),
    Satori("State of Satori",
            Reaction.None,
            PoliceStrength.Minimal,
            PirateStrength.Minimal,
            TraderStrength.Minimal,
            TechLevel.Preagricultural,
            TechLevel.Agricultural,
            BribeLevel.Impossible,
            false,
            false,
            null),
    Technocracy("Technocracy",
            Reaction.Minimal,
            PoliceStrength.Abundant,
            PirateStrength.Some,
            TraderStrength.Abundant,
            TechLevel.EarlyIndustrial,
            TechLevel.HiTech,
            BribeLevel.Hard,
            true,
            true,
            TradeItem.Water),
    Theocracy("Theocracy",
            Reaction.Strong,
            PoliceStrength.Abundant,
            PirateStrength.Minimal,
            TraderStrength.Moderate,
            TechLevel.Preagricultural,
            TechLevel.EarlyIndustrial,
            BribeLevel.Impossible,
            true,
            true,
            TradeItem.Narcotics);


    private final String name;
    private final Reaction reactionIllegal;
    private final PoliceStrength policeStrength;
    private final PirateStrength pirateStrength;
    private final TraderStrength traderStrength;
    private final TechLevel minTechLevel;
    private final TechLevel maxTechLevel;
    private final BribeLevel bribeLevel;
    private final boolean drugsOK;
    private final boolean firearmsOK;
    private final TradeItem wantedTradeItem;

    Politics(String name,
             Reaction reactionIllegal,
             PoliceStrength policeStrength,
             PirateStrength pirateStrength,
             TraderStrength traderStrength,
             TechLevel minTechLevel,
             TechLevel maxTechLevel,
             BribeLevel bribeLevel,
             boolean drugsOK,
             boolean firearmsOK,
             TradeItem wantedTradeItem) {

        this.name = name;
        this.reactionIllegal = reactionIllegal;
        this.policeStrength = policeStrength;
        this.pirateStrength = pirateStrength;
        this.traderStrength = traderStrength;
        this.minTechLevel = minTechLevel;
        this.maxTechLevel = maxTechLevel;
        this.bribeLevel = bribeLevel;
        this.drugsOK = drugsOK;
        this.firearmsOK = firearmsOK;
        this.wantedTradeItem = wantedTradeItem;
    }

    public boolean getDrugsOK() {
        return drugsOK;
    }

    public boolean getFirearmsOK() {
        return firearmsOK;
    }

    public TechLevel getMinTechLevel() {
        return minTechLevel;
    }

    public TechLevel getMaxTechLevel() {
        return maxTechLevel;
    }

    public PirateStrength getPirateStrength() {
        return pirateStrength;
    }

    public PoliceStrength getPoliceStrength() {
        return policeStrength;
    }

    public TradeItem getWantedTradeItem() {
        return wantedTradeItem;
    }

    // TODO: Consider returning ints for these
    public TraderStrength getTraderStrength() {
        return traderStrength;
    }

    public String getName() {
        return name;
    }

    public BribeLevel getBribeLevel() {
        return bribeLevel;
    }
}
