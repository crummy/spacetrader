package com.malcolmcrum.spacetrader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Malcolm on 8/28/2015.
 */
public class Commander {
    private static final int CHANCE_OF_A_VERY_RARE_ENCOUNTER = 5;

    private String name;
    private int credits;
    private int debt;
    private int days;
    private SolarSystem currentSystem;
    private int policeKills;
    private int traderKills;
    private int pirateKills;
    private int policeRecordScore;
    private int reputationScore;
    private int monsterStatus;
    private int dragonflyStatus;
    private int scarabStatus;
    private int japoriDiseaseStatus;
    private boolean moonBought;
    private int monsterHull;
    private boolean escapePod;
    private boolean insurance;
    private boolean remindLoans;
    private int noClaim = 0;
    private boolean artifactOnBoard;
    Map<TradeItem, Integer> buyingPrice;
    private boolean tribbleMessage;
    private int jarekStatus;
    private int invasionStatus;
    private int experimentStatus;
    private int fabricRipProbability = 0;
    private boolean possibleToGoThroughRip;
    private boolean arrivedViaWormhole;
    private int veryRareEncounter;
    private int wildStatus;
    private int reactorStatus;
    private int trackedSystem;
    private boolean showTrackedRange;
    private boolean justLootedMarie;
    private int chanceOfAVeryRareEncounter;
    private boolean alreadyPaidForNewspaper;
    private boolean canSuperWarp;
    private boolean gameLoaded;

    public Commander(String name, SolarSystem currentSystem) {
        this.name = name;
        credits = 1000;
        debt = 0;
        days = 0;
        this.currentSystem = currentSystem;
        policeKills = 0;
        traderKills = 0;
        policeRecordScore = 0;
        reputationScore = 0;
        monsterStatus = 0;
        dragonflyStatus = 0;
        scarabStatus = 0;
        japoriDiseaseStatus = 0;
        monsterHull = ShipType.SpaceMonster.getHullStrength();
        moonBought = false;
        escapePod = false;
        insurance = false;
        remindLoans = true;
        noClaim = 0;
        artifactOnBoard = false;
        buyingPrice = new HashMap<>();
        for (TradeItem item : TradeItem.values()) {
            buyingPrice.put(item, 0);
        }
        tribbleMessage = false;
        jarekStatus = 0;
        invasionStatus = 0;
        experimentStatus = 0;
        fabricRipProbability = 0;
        possibleToGoThroughRip = false;
        arrivedViaWormhole = false;
        veryRareEncounter = 0;
        wildStatus = 0;
        reactorStatus = 0;
        trackedSystem = -1;
        showTrackedRange = false;
        justLootedMarie = false;
        chanceOfAVeryRareEncounter = CHANCE_OF_A_VERY_RARE_ENCOUNTER;
        alreadyPaidForNewspaper = false;
        canSuperWarp = false;
        gameLoaded = false;

        //findStartingPlanet();
    }
}
