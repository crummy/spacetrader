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
    private static final int CLEAN_SCORE = 0;
    private static final int KILL_PIRATE_SCORE = 1;
    private static final int KILL_POLICE_SCORE = -6;
    private static final int ATTACK_POLICE_SCORE = -3;
    private static final int FLEE_FROM_INSPECTION = -2;
    private static final int KILL_TRADER_SCORE = -4;
    private static final int ATTACK_TRADER_SCORE = -2;
    private static final int LAWFUL_SCORE = 5;
    private static final int TRAFFICKING_SCORE = -1;

    private final Game game;
    private final String name;

    // TODO: gotta move some of these variables elsewhere
    private int credits;
    private int policeKills;
    private int traderKills;
    private int pirateKills;
    private int policeRecordScore;
    private int reputationScore;
    private int japoriDiseaseStatus;
    private boolean moonBought;
    private int jarekStatus;
    private int invasionStatus;
    private int experimentStatus;
    private boolean possibleToGoThroughRip;
    private boolean arrivedViaWormhole;
    private int veryRareEncounter;
    private int trackedSystem;
    private boolean showTrackedRange;
    private boolean canSuperWarp;
    private boolean artifactOnBoard;
    private boolean hasEscapePod;
    private boolean reserveMoney;

    public Captain(Game game, String name, int pilot, int fighter, int trader, int engineer) {
        super(pilot, fighter, trader, engineer);
        this.game = game;
        this.name = name;
        credits = 1000;
        policeKills = 0;
        traderKills = 0;
        pirateKills = 0;
        policeRecordScore = 0;
        reputationScore = 0;
        japoriDiseaseStatus = 0;
        moonBought = false;
        artifactOnBoard = false;
        jarekStatus = 0;
        invasionStatus = 0;
        arrivedViaWormhole = false;
        veryRareEncounter = 0;
        trackedSystem = -1;
        showTrackedRange = false;
        canSuperWarp = false;
        hasEscapePod = false;
        reserveMoney = false;
    }

    public String getName() {
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

    public boolean isClean() {
        return policeRecordScore > CLEAN_SCORE;
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

    public boolean hasBoughtMoon() {
        return moonBought;
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
            game.getBank().addDebt(creditsLost - credits);
            credits = 0;
        }
    }

    public boolean isDubious() {
        return policeRecordScore < DUBIOUS_SCORE;
    }

    public int getAvailableCash() {
        if (!reserveMoney) {
            return credits;
        } else {
            return Math.max(0, credits - game.getShip().getMercenaryDailyCost() - game.getBank().getInsuranceCost());
        }
    }

    public void addPoliceScore(int i) {
        policeRecordScore += i;
    }

    public void attackedPolice() {
        policeRecordScore += ATTACK_POLICE_SCORE;
    }

    public void attackedTrader() {
        policeRecordScore += ATTACK_TRADER_SCORE;
    }

    public void makeCriminal() {
        policeRecordScore = CRIMINAL_SCORE;
    }

    public void makeDubious() {
        policeRecordScore = DUBIOUS_SCORE;
    }


    public int getPoliceRecordScore() {
        return policeRecordScore;
    }

    public int getWorth() {
        return game.getShip().getPrice(false, true)
                + credits
                - game.getBank().getDebt()
                + (moonBought ? SolarSystem.COST_MOON : 0);
    }

    public void fledPolice() {
        if (policeRecordScore > DUBIOUS_SCORE) {
            boolean easierThanNormal = game.getDifficulty() == Difficulty.Beginner || game.getDifficulty() == Difficulty.Easy;
            policeRecordScore = DUBIOUS_SCORE - (easierThanNormal ? 0 : 1);
        } else {
            policeRecordScore += FLEE_FROM_INSPECTION;
        }
    }

    public boolean isDangerous() {
        return reputationScore >= Reputation.Dangerous.getScore();
    }

    public void makeDangerous() {
        reputationScore = Reputation.Dangerous.getScore();
    }

    public void addReputation(int rep) {
        reputationScore += rep;
    }

    public void killedACop() {
        ++policeKills;
        addPoliceScore(KILL_POLICE_SCORE);
    }

    public void killedAPirate() {
        ++pirateKills;
        addPoliceScore(KILL_PIRATE_SCORE);
    }

    public void killedATrader() {
        ++traderKills;
        addPoliceScore(KILL_TRADER_SCORE);
    }

    public void makeVillain() {
        policeRecordScore = VILLAIN_SCORE;
    }

    public int getEliteScore() {
        return Reputation.Elite.getScore();
    }

    public int getReputationScore() {
        return reputationScore;
    }

    public boolean isLawful() {
        return policeRecordScore >= LAWFUL_SCORE;
    }

    public boolean isAverage() {
        return reputationScore >= Reputation.Average.getScore();
    }

    public int getKills() {
        return pirateKills + policeKills + traderKills;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public boolean hasEscapePod() {
        return hasEscapePod;
    }

    public void setEscapePod(boolean escapePod) {
        this.hasEscapePod = escapePod;
    }

    public void caughtTrafficking() {
        policeRecordScore += TRAFFICKING_SCORE;
    }

    public void passedInspection() {
        policeRecordScore -= TRAFFICKING_SCORE;
    }

    public void addPilotSkills(int amount) {
        this.pilot += amount;
        if (pilot > Game.MAX_POINTS_PER_SKILL) {
            pilot = Game.MAX_POINTS_PER_SKILL;
        }
    }

    public void addEngineerSkills(int amount) {
        this.engineer += amount;
        if (engineer > Game.MAX_POINTS_PER_SKILL) {
            engineer = Game.MAX_POINTS_PER_SKILL;
        }
    }

    public void addTraderSkills(int amount) {
        trader += amount;
        if (trader > Game.MAX_POINTS_PER_SKILL) {
            trader = Game.MAX_POINTS_PER_SKILL;
        }
    }

    enum Reputation {
        Harmless("Harmless", 0),
        MostlyHarmless("Mostly harmless", 10),
        Poor("Poor", 20),
        Average("Average", 40),
        AboveAverage("Above average", 80),
        Competent("Competent", 150),
        Dangerous("Dangerous", 300),
        Deadly("Deadly", 600),
        Elite("Elite", 1500);

        private final String name;
        private final int score;

        Reputation(String name, int score) {

            this.name = name;
            this.score = score;
        }

        int getScore() {
            return score;
        }
    }
}
