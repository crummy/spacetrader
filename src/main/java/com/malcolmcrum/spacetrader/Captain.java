package com.malcolmcrum.spacetrader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Malcolm on 8/28/2015.
 */
public class Captain extends Crew {
    private static final Logger logger = LoggerFactory.getLogger(Captain.class);

    private final String name;
    private final Game game;
    public final Bank bank;
    public final Reputation reputation;
    public final PoliceRecord policeRecord;

    // TODO: gotta move some of these variables elsewhere
    private int policeKills;
    private int traderKills;
    private int pirateKills;
    private int policeRecordScore;
    private int reputationScore;
    private boolean moonBought;
    private boolean possibleToGoThroughRip;
    private boolean arrivedViaWormhole;
    private int trackedSystem;
    private boolean showTrackedRange;
    private boolean canSuperWarp;
    private boolean artifactOnBoard;
    private boolean hasEscapePod;
    private boolean reserveMoney;
    private int credits;

    public Captain(String name, int pilot, int fighter, int trader, int engineer, Game game) {
        super(pilot, fighter, trader, engineer);
        this.game = game;
        this.name = name;
        this.bank = new Bank(this, game);
        this.reputation = new Reputation();
        this.policeRecord = new PoliceRecord(game.getDifficulty());
        policeKills = 0;
        traderKills = 0;
        pirateKills = 0;
        credits = 1000;
        policeRecordScore = 0;
        reputationScore = 0;
        moonBought = false;
        arrivedViaWormhole = false;
        trackedSystem = -1;
        showTrackedRange = false;
        canSuperWarp = false;
        hasEscapePod = false;
        reserveMoney = false;
    }

    public String getName() {
        return name;
    }

    public void addCredits(int additionalCredits) {
        credits += additionalCredits;
    }

    public void subtractCredits(int creditsLost) {
        if (credits > creditsLost) {
            credits -= creditsLost;
        } else {
            logger.info("Lost more credits than we have - increasing debt.");
            bank.addDebt(creditsLost - credits);
            credits = 0;
        }
    }

    public int getCredits() {
        return credits;
    }

    public boolean hasBoughtMoon() {
        return moonBought;
    }

    public int getWorth() {
        return game.getShip().getPrice(false, true)
                + credits
                - bank.getDebt()
                + (moonBought ? SolarSystem.COST_MOON : 0);
    }

    public void killedACop() {
        ++policeKills;
        policeRecord.add(PoliceRecord.Actions.KillPolice.modifier);
    }

    public void killedAPirate() {
        ++pirateKills;
        policeRecord.add(PoliceRecord.Actions.KillPirate.modifier);
    }

    public void killedATrader() {
        ++traderKills;
        policeRecord.add(PoliceRecord.Actions.KillTrader.modifier);
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

    public boolean addPilotSkills(int amount) {
        this.pilot += amount;
        if (pilot > Game.MAX_POINTS_PER_SKILL) {
            pilot = Game.MAX_POINTS_PER_SKILL;
            return false;
        } else {
            return true;
        }
    }

    public boolean addEngineerSkills(int amount) {
        this.engineer += amount;
        if (engineer > Game.MAX_POINTS_PER_SKILL) {
            engineer = Game.MAX_POINTS_PER_SKILL;
            return false;
        } else {
            return true;
        }
    }

    public boolean addTraderSkills(int amount) {
        trader += amount;
        if (trader > Game.MAX_POINTS_PER_SKILL) {
            trader = Game.MAX_POINTS_PER_SKILL;
            return false;
        } else {
            return true;
        }
    }

    public boolean addFighterSkills(int amount) {
        fighter += amount;
        if (fighter > Game.MAX_POINTS_PER_SKILL) {
            fighter = Game.MAX_POINTS_PER_SKILL;
            return false;
        } else {
            return true;
        }
    }
}
