package com.malcolmcrum.spacetrader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Malcolm on 8/28/2015.
 */
public class Game {
    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    private static final int MAX_POINTS_PER_SKILL = 10;
    private static final int MAX_SKILL_POINTS_TOTAL = 20;
    private static final int MIN_POINTS_PER_SKILL = 1;

    Galaxy galaxy;
    Captain captain;
    Ship ship;
    SolarSystem currentSystem;
    Difficulty difficulty;
    News news;

    public Game() {
        news = new News();
    }

    public boolean startNewGame(String commanderName, int pilotSkill, int fighterSkill, int traderSkill, int engineerSkill, Difficulty difficulty) {

        if (skillPointsDontAddUp(pilotSkill, fighterSkill, traderSkill, engineerSkill)) {
            logger.warn("Invalid skill points");
            return false;
        }

        this.difficulty = difficulty;
        galaxy = new Galaxy(difficulty);
        ship = new Ship(ShipType.Gnat);
        ship.addWeapon(Weapon.PulseLaser);
        ship.addCrew(Crew.Captain);
        currentSystem = galaxy.getStartSystem(ship.type);
        captain = new Captain(commanderName);

        if (difficulty == Difficulty.Beginner || difficulty == Difficulty.Easy) {
            currentSystem.setSpecialEvent(SolarSystem.SpecialEvent.LotteryWinner);
        }
        return true;
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
        return news.getNewspaper(currentSystem, captain, difficulty, ship);
    }
}
