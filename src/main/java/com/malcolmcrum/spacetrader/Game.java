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

    private static boolean reserveMoney;

    Galaxy galaxy;
    Captain captain;
    Ship ship;
    SolarSystem currentSystem;
    Difficulty difficulty = Difficulty.Normal;
    News news;
    State state;

    public boolean startNewGame(String commanderName, int pilotSkill, int fighterSkill, int traderSkill, int engineerSkill, Difficulty difficulty) {

        if (skillPointsDontAddUp(pilotSkill, fighterSkill, traderSkill, engineerSkill)) {
            logger.warn("Invalid skill points");
            return false;
        }

        galaxy = new Galaxy(this);
        news = new News(this);
        ship = new Ship(ShipType.Gnat, this);
        ship.addWeapon(Weapon.PulseLaser);
        ship.addCrew(new Crew(0));
        currentSystem = galaxy.getStartSystem(ship.type);
        captain = new Captain(commanderName);

        if (difficulty == Difficulty.Beginner || difficulty == Difficulty.Easy) {
            currentSystem.setSpecialEvent(SolarSystem.SpecialEvent.LotteryWinner);
        }

        return true;
    }

    public List<SolarSystem> getSystems() {
        return galaxy.systems;
    }

    public boolean travelToPlanet(SolarSystem destination) {
        if (state != State.OnPlanet) {
            logger.warn("Tried to travel, but we aren't on a planet");
            return false;
        }
        if (currentSystem == destination) {
            logger.warn("Tried to travel to planet we're already on");
        }
        int distance = (int)Vector2i.Distance(currentSystem.getLocation(), destination.getLocation());
        if (distance > ship.getFuel()) {
            logger.warn("Tried to travel to planet out of range of fuel tanks");
            return false;
        }
        currentSystem = destination;
        state = State.InTransit;
        return true;
    }

    private void arrivedInSystem() {
        SolarSystem.SpecialEvent systemEvent = currentSystem.getSpecialEvent();
        if (systemEvent == SolarSystem.SpecialEvent.MonsterKilled && captain.getMonsterStatus() == 2) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.MonsterKilled);
        } else if (systemEvent == SolarSystem.SpecialEvent.Dragonfly) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.Dragonfly);
        } else if (systemEvent == SolarSystem.SpecialEvent.ScarabStolen) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.ScarabStolen);
        } else if (systemEvent == SolarSystem.SpecialEvent.ScarabDestroyed && captain.getScarabStatus() == 2) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.ScarabDestroyed);
        } else if (systemEvent == SolarSystem.SpecialEvent.FlyBaratas && captain.getDragonflyStatus() == 1) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.FlyBaratas);
        } else if (systemEvent == SolarSystem.SpecialEvent.FlyMelina && captain.getDragonflyStatus() == 2) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.FlyMelina);
        } else if (systemEvent == SolarSystem.SpecialEvent.FlyRegulas && captain.getDragonflyStatus() == 3) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.FlyRegulas);
        } else if (systemEvent == SolarSystem.SpecialEvent.DragonflyDestroyed && captain.getDragonflyStatus() == 5) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.DragonflyDestroyed);
        } else if (systemEvent == SolarSystem.SpecialEvent.MedicineDelivery && captain.getJaporiDiseaseStatus() == 1) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.MedicineDelivery);
        } else if (systemEvent == SolarSystem.SpecialEvent.ArtifactDelivery && captain.getArtifactOnBoard()) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.ArtifactDelivery);
        } else if (systemEvent == SolarSystem.SpecialEvent.JaporiDisease && captain.getJaporiDiseaseStatus() == 0) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.JaporiDisease);
        } else if (systemEvent == SolarSystem.SpecialEvent.JarekGetsOut && captain.getJarekStatus() == 1) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.JarekGetsOut);
        } else if (systemEvent == SolarSystem.SpecialEvent.WildGetsOut) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.WildGetsOut);
        } else if (systemEvent == SolarSystem.SpecialEvent.GemulonRescued && captain.getInvasionStatus() > 0 && captain.getInvasionStatus() < 8) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.GemulonRescued);
        } else if (systemEvent == SolarSystem.SpecialEvent.AlienInvasion) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.AlienInvasion);
        } else if (systemEvent == SolarSystem.SpecialEvent.DisasterAverted && captain.getExperimentStatus() > 0 && captain.getExperimentStatus() < 12) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.DisasterAverted);
        } else if (systemEvent == SolarSystem.SpecialEvent.ExperimentFailed) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.ExperimentFailed);
        }
        currentSystem.setVisited();
    }

    enum State {
        NewGame,
        OnPlanet,
        InTransit,
        Dead;
    }

    private int currentWorth() {
        return ship.getPrice(false) + captain.getCredits() - captain.getDebt() + (captain.hasBoughtMoon() ? SolarSystem.COST_MOON : 0);
    }

    private boolean canAffordNewspaper() {
        if (captain.isAlreadyPaidForNewspaper()) {
            return true;
        } else return cashAvailable() >= news.getPrice();
    }

    private int cashAvailable() {
        if (!reserveMoney) {
            return captain.getCredits();
        } else {
            return Math.max(0, captain.getCredits() - mercenaryDailyCost() - insuranceCost());
        }
    }

    private int insuranceCost() {
        if (!captain.hasInsurance()) {
            return 0;
        } else {
            return Math.max(1, (((ship.getPriceWithoutCargo(true) * 5) / 2000) *
                    (100-Math.min(captain.getNoClaim(), 90)) / 100));
        }
    }

    private int mercenaryDailyCost() {
        int cost = 0;
        for (Crew crew : ship.crew) {
            cost += crew.getDailyCost();
        }
        return cost;
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

    public Ship getCurrentShip() {
        return ship;
    }

    public SolarSystem getCurrentSystem() {
        return currentSystem;
    }

    public Captain getCaptain() {
        return captain;
    }
}
