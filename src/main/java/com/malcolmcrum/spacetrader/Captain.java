package com.malcolmcrum.spacetrader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Malcolm on 8/28/2015.
 */
public class Captain {
    private static final int CHANCE_OF_A_VERY_RARE_ENCOUNTER = 5;
    private static final int VILLAIN_SCORE = -30;
    private static final int HERO_SCORE = 75;

    private String name;
    private int credits;
    private int debt;
    private int days;
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
    private int fabricRipProbability;
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

    private boolean isInsured;

    public Captain(String name) {
        this.name = name;
        credits = 1000;
        debt = 0;
        days = 0;
        policeKills = 0;
        traderKills = 0;
        pirateKills = 0;
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

        isInsured = false;
    }

    public String getName() {
        return name;
    }

    public int getMonsterStatus() {
        return monsterStatus;
    }

    public int getScarabStatus() {
        return scarabStatus;
    }

    public int getDragonflyStatus() {
        return dragonflyStatus;
    }

    public int getPoliceRecordScore() {
        return policeRecordScore;
    }

    public boolean isVillainous() {
        return policeRecordScore <= VILLAIN_SCORE;
    }

    public boolean isHeroic() {
        return policeRecordScore == HERO_SCORE;
    }

    public int getJaporiDiseaseStatus() {
        return japoriDiseaseStatus;
    }

    public boolean getArtifactOnBoard() {
        return artifactOnBoard;
    }

    public int getJarekStatus() {
        return jarekStatus;
    }

    public int getInvasionStatus() {
        return invasionStatus;
    }

    public int getExperimentStatus() {
        return experimentStatus;
    }

    public boolean isAlreadyPaidForNewspaper() {
        return alreadyPaidForNewspaper;
    }

    public int getCredits() {
        return credits;
    }

    public boolean hasInsurance() {
        return isInsured;
    }

    public int getNoClaim() {
        return noClaim;
    }

    public boolean hasBoughtMoon() {
        return moonBought;
    }

    public int getDebt() {
        return debt;
    }
}
