package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 8/28/2015.
 */
public class Captain extends Crew {
    private static final int PSYCHOPATH_SCORE = -70;
    private static final int VILLAIN_SCORE = -30;
    private static final int CRIMINAL_SCORE = -10;
    private static final int HERO_SCORE = 75;
    private static final int CAUGHT_WITH_WILD_SCORE = -4;
    private static final int DUBIOUS_SCORE = -5;

    private final Game game;
    private final String name;

    // TODO: gotta move some of these variables elsewhere
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
    private boolean tribbleMessage;
    private int jarekStatus;
    private int invasionStatus;
    private int experimentStatus;
    private boolean possibleToGoThroughRip;
    private boolean arrivedViaWormhole;
    private int veryRareEncounter;
    private int wildStatus;
    private int reactorStatus;
    private int trackedSystem;
    private boolean showTrackedRange;
    private boolean justLootedMarie;
    private int chanceOfAVeryRareEncounter;
    private boolean canSuperWarp;
    private boolean gameLoaded;
    private boolean reserveMoney;

    private boolean isInsured;

    public Captain(Game game, String name) {
        super(0);
        this.game = game;

        this.name = name;
        credits = 1000;
        debt = 0;
        policeKills = 0;
        traderKills = 0;
        pirateKills = 0;
        policeRecordScore = 0;
        reputationScore = 0;
        japoriDiseaseStatus = 0;
        monsterHull = ShipType.SpaceMonster.getHullStrength();
        moonBought = false;
        remindLoans = true;
        noClaim = 0;
        artifactOnBoard = false;
        tribbleMessage = false;
        jarekStatus = 0;
        invasionStatus = 0;
        arrivedViaWormhole = false;
        veryRareEncounter = 0;
        wildStatus = 0;
        reactorStatus = 0;
        trackedSystem = -1;
        showTrackedRange = false;
        justLootedMarie = false;
        canSuperWarp = false;
        gameLoaded = false;

        isInsured = false;
    }

    public String getCaptainName() {
        return name;
    }

    public boolean isVillainous() {
        return policeRecordScore <= VILLAIN_SCORE;
    }

    public boolean isPsychopathic() {
        return policeRecordScore <= PSYCHOPATH_SCORE;
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

    public boolean isCriminal() {
        return policeRecordScore <= CRIMINAL_SCORE;
    }

    public void caughtWithWild() {
        policeRecordScore += CAUGHT_WITH_WILD_SCORE;
    }

    public void addCredits(int additionalCredits) {
        credits += additionalCredits;
    }

    public void subtractCredits(int creditsLost) {
        if (credits > creditsLost) {
            credits -= creditsLost;
        } else {
            debt += (creditsLost - credits);
            credits = 0;
        }
    }

    public void setInsurance(boolean insurance) {
        this.insurance = insurance;
    }

    public void setNoClaim(int noClaim) {
        this.noClaim = noClaim;
    }

    public boolean isDubious() {
        return policeRecordScore < DUBIOUS_SCORE;
    }

    public int getAvailableCash() {
        if (!reserveMoney) {
            return credits;
        } else {
            return Math.max(0, credits - game.getCurrentShip().getMercenaryDailyCost() - game.getCurrentShip().getInsuranceCost());
        }
    }
}
