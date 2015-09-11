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

    private Experiment experimentStatus;
    private Monster monsterStatus;
    private int fabricRipProbability;
    private Scarab scarabStatus;
    private Dragonfly dragonflyStatus;
    private Invasion invasionStatus;
    private Wild wildStatus;
    private int days;
    private Japori japoriDiseaseStatus;
    private Reactor reactorStatus;

    private Galaxy galaxy;
    private Captain captain;
    private PlayerShip ship;
    private SolarSystem currentSystem;
    private Bank bank;
    private Difficulty difficulty = Difficulty.Normal;
    private News news;
    private RareEncounters rareEncounters;
    private List<Alert> unreadAlerts;

    private boolean artifactOnBoard;
    private Jarek jarekStatus;

    private boolean trackAutoOff;
    private SolarSystem trackedSystem;
    private int monsterHull;


    public GameState startNewGame(String commanderName, int pilotSkill, int fighterSkill, int traderSkill, int engineerSkill, Difficulty difficulty) {

        if (skillPointsDontAddUp(pilotSkill, fighterSkill, traderSkill, engineerSkill)) {
            logger.warn("Invalid skill points");
            return null;
        }

        bank = new Bank(this);
        captain = new Captain(this, commanderName);
        ship = new PlayerShip(ShipType.Flea, this);
        ship.addWeapon(Weapon.PulseLaser);
        ship.addCrew(new Crew(0));

        galaxy = new Galaxy(this);
        news = new News(this);
        currentSystem = galaxy.getStartSystem(ship.getFuelCapacity());

        rareEncounters = new RareEncounters();
        unreadAlerts = new ArrayList<>();

        if (difficulty == Difficulty.Beginner || difficulty == Difficulty.Easy) {
            currentSystem.setSpecialEvent(SolarSystem.SpecialEvent.LotteryWinner);
        }

        fabricRipProbability = 0;
        experimentStatus = Experiment.NotStarted;
        monsterStatus = Monster.Unavailable;
        scarabStatus = Scarab.Unavailable;
        dragonflyStatus = Dragonfly.Unavailable;
        invasionStatus = Invasion.Unavailable;
        wildStatus = Wild.Unavailable;
        days = 0;
        japoriDiseaseStatus = Japori.NoDisease;
        reactorStatus = Reactor.Unavailable;
        monsterHull = ShipType.SpaceMonster.getHullStrength();

        return new InSystem(this, currentSystem);
    }

    public List<SolarSystem> getSystems() {
        return galaxy.systems;
    }

    public int getFabricRipProbability() {
        return fabricRipProbability;
    }

    public Experiment getExperimentStatus() {
        return experimentStatus;
    }

    public Monster getMonsterStatus() {
        return monsterStatus;
    }

    public Scarab getScarabStatus() {
        return scarabStatus;
    }

    public Dragonfly getDragonflyStatus() {
        return dragonflyStatus;
    }

    public Invasion getInvasionStatus() {
        return invasionStatus;
    }

    public Wild getWildStatus() {
        return wildStatus;
    }

    public int getDays() {
        return days;
    }

    public Japori getJaporiDiseaseStatus() {
        return japoriDiseaseStatus;
    }

    public Reactor getReactorStatus() {
        return reactorStatus;
    }

    public void addAlert(Alert alert) {
        unreadAlerts.add(alert);
    }

    public void setReactorStatus(Reactor reactorStatus) {
        this.reactorStatus = reactorStatus;
    }

    public void setJaporiDiseaseStatus(Japori japoriDiseaseStatus) {
        this.japoriDiseaseStatus = japoriDiseaseStatus;
    }

    public void setArtifactOnBoard(boolean artifactOnBoard) {
        this.artifactOnBoard = artifactOnBoard;
    }

    public Object getJarekStatus() {
        return jarekStatus;
    }

    public void setJarekStatus(Jarek jarekStatus) {
        this.jarekStatus = jarekStatus;
    }

    public void setWildStatus(Wild wildStatus) {
        this.wildStatus = wildStatus;
    }

    public void dayPasses() {
        days++;
    }

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

    public boolean getRemindLoans() {
        return false; // TODO
    }

    public boolean getTrackAutoOff() {
        return trackAutoOff;
    }

    public SolarSystem getTrackedSystem() {
        return trackedSystem;
    }

    public void setTrackedSystem(SolarSystem trackedSystem) {
        this.trackedSystem = trackedSystem;
    }

    public boolean getAutoRepair() {
        return false; // TODO
    }

    public boolean getAutoFuel() {
        return false;//TODO
    }

    public void setMonsterStatus(Monster monsterStatus) {
        this.monsterStatus = monsterStatus;
    }

    public void setDragonflyStatus(Dragonfly dragonflyStatus) {
        this.dragonflyStatus = dragonflyStatus;
    }

    public void setScarabStatus(Scarab scarabStatus) {
        this.scarabStatus = scarabStatus;
    }

    public int getMonsterHullStrength() {
        return monsterHull;
    }

    public void setMonsterHullStrength(int monsterHullStrength) {
        this.monsterHull = monsterHullStrength;
    }

    public boolean getArtifactOnBoard() {
        return artifactOnBoard;
    }

    public Bank getBank() {
        return bank;
    }
}