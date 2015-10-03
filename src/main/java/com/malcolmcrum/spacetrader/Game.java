package com.malcolmcrum.spacetrader;

import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.InSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Malcolm on 8/28/2015.
 */
public class Game {
    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    public static final int MAX_POINTS_PER_SKILL = 10;
    private static final int MAX_SKILL_POINTS_TOTAL = 20;
    private static final int MIN_POINTS_PER_SKILL = 1;
    public static final int MAX_TRIBBLES = 100000;

    private int days;

    private Galaxy galaxy;
    private Captain captain;
    private PlayerShip ship;
    private SolarSystem currentSystem;
    private Difficulty difficulty = Difficulty.Normal;
    private News news;
    private RareEncounters rareEncounters;
    private List<Alert> unreadAlerts;

    private SolarSystem trackedSystem;
    private int monsterHull;
    private Quests quests;


    public GameState startNewGame(String commanderName, int pilotSkill, int fighterSkill, int traderSkill, int engineerSkill, Difficulty difficulty) {

        if (skillPointsDontAddUp(pilotSkill, fighterSkill, traderSkill, engineerSkill)) {
            logger.warn("Invalid skill points");
            return null;
        }

        quests = new Quests();
        captain = new Captain(commanderName, pilotSkill, fighterSkill, traderSkill, engineerSkill, this);
        ship = new PlayerShip(ShipType.Gnat, quests, difficulty);
        ship.addWeapon(Weapon.PulseLaser);
        ship.addCrew(captain);

        galaxy = new Galaxy(captain, ship, difficulty);
        news = new News(this);
        currentSystem = galaxy.getStartSystem(ship.getFuelCapacity());

        rareEncounters = new RareEncounters();
        unreadAlerts = new ArrayList<>();

        if (difficulty == Difficulty.Beginner || difficulty == Difficulty.Easy) {
            currentSystem.setSpecialEvent(SolarSystem.SpecialEvent.LotteryWinner);
        }

        days = 0;
        monsterHull = ShipType.SpaceMonster.getHullStrength();

        return new InSystem(this, currentSystem);
    }

    public List<SolarSystem> getSystems() {
        return galaxy.systems;
    }

    public void addAlert(Alert alert) {
        unreadAlerts.add(alert);
    }

    public void dayPasses() {
        captain.bank.payInterest();
        if (captain.bank.hasInsurance()) {
            captain.bank.incrementNoClaim();
        }
        days++;
    }

    // TODO: consider moving captain, too.
    public void setShip(PlayerShip ship) {
        this.ship = ship;
    }

    public News getNews() {
        return news;
    }

    public Galaxy getGalaxy() {
        return galaxy;
    }

    public void setCurrentSystem(SolarSystem currentSystem) {
        this.currentSystem = currentSystem;
    }

    private boolean skillPointsDontAddUp(int pilotSkill, int fighterSkill, int traderSkill, int engineerSkill) {
        boolean wrongTotalSkillPoints = pilotSkill + fighterSkill + traderSkill + engineerSkill != MAX_SKILL_POINTS_TOTAL;
        boolean skillOutOfRange = pilotSkill < MIN_POINTS_PER_SKILL || pilotSkill > MAX_POINTS_PER_SKILL
                                    || fighterSkill < MIN_POINTS_PER_SKILL || fighterSkill > MAX_POINTS_PER_SKILL
                                    || traderSkill < MIN_POINTS_PER_SKILL || traderSkill > MAX_POINTS_PER_SKILL
                                    || engineerSkill < MIN_POINTS_PER_SKILL || engineerSkill > MAX_POINTS_PER_SKILL;
        return wrongTotalSkillPoints || skillOutOfRange;
    }

    public List<String> getNewspaper() {
        return news.getNewspaper();
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public PlayerShip getShip() {
        return ship;
    }

    public SolarSystem getCurrentSystem() {
        return currentSystem;
    }

    public Captain getCaptain() {
        return captain;
    }

    public RareEncounters getRareEncounters() {
        return rareEncounters;
    }

    public SolarSystem getTrackedSystem() {
        return trackedSystem;
    }

    public Quests getQuests() {
        return quests;
    }

    public int getDays() {
        return days;
    }
}
