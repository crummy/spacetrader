package com.malcolmcrum.spacetrader.GameStates;

import com.malcolmcrum.spacetrader.*;
import com.malcolmcrum.spacetrader.GameStates.Encounters.*;
import com.malcolmcrum.spacetrader.GameStates.Encounters.Dragonfly;
import com.malcolmcrum.spacetrader.GameStates.Encounters.Monster;
import com.malcolmcrum.spacetrader.GameStates.Encounters.Scarab;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * Created by Malcolm on 9/2/2015.
 */
public class Transit extends GameState {

    private static final int CHANCE_OF_A_VERY_RARE_ENCOUNTER = 5; // out of a thousand

    private final SolarSystem origin;
    private SolarSystem destination;

    private boolean possibleToGoThroughRip;
    private boolean beenRaided;
    private int clicksRemaining;
    private int totalClicks;
    private boolean arrivedViaWormhole;
    private boolean beenInspected;
    private boolean litterWarning;
    private boolean hadEncounter;
    private boolean justLootedMarie;

    public Transit(Game game, SolarSystem destination, boolean viaSingularity) {
        super(game);
        origin = game.getCurrentSystem();
        this.destination = destination;
        this.destination.getMarket().determinePrices();
        this.possibleToGoThroughRip = true;
        this.beenRaided = false;

        if (viaSingularity) {
            game.getNews().addNotableEvent(News.NotableEvent.ArrivalViaSingularity);
        }
        arrivedViaWormhole = viaSingularity || origin.getWormholeDestination() == destination;

        clicksRemaining = 21;
        totalClicks = clicksRemaining;
        beenRaided = false;
        beenInspected = false;
        litterWarning = false;

        game.setMonsterHullStrength((game.getMonsterHullStrength() * 105)/100);
        if (game.getMonsterHullStrength() > ShipType.SpaceMonster.getHullStrength()) {
            game.setMonsterHullStrength(ShipType.SpaceMonster.getHullStrength());
        }

        if (game.getDays() % 3 == 0 && game.getCaptain().isClean()) {
            game.getCaptain().addPoliceScore(-1);
        } else if (game.getCaptain().isDubious()){
            Difficulty d = game.getDifficulty();
            if (d == Difficulty.Beginner || d == Difficulty.Easy || d == Difficulty.Normal) {
                game.getCaptain().addPoliceScore(1);
            } else if (game.getDays() % d.getValue() == 0) {
                game.getCaptain().addPoliceScore(1);
            }
        }

        int fabricRipProbability = game.getFabricRipProbability();
        if (game.getExperimentStatus() == Experiment.Performed
                && fabricRipProbability > 0) {
            if (GetRandom(100) < fabricRipProbability || fabricRipProbability == 25) {
                game.addAlert(Alert.FlyInFabricRip);
                this.destination = game.getGalaxy().getRandomSystem();
            }
        }
    }

    @Override
    public String getName() {
        return "Transit";
    }

    @Override
    public List<Method> getActions() {
        // There are no actions possible in Transit. It just serves to generate Encounters.
        return new ArrayList<>();
    }

    @Override
    public GameState init() {
        return travel();
    }

    public GameState travel() {
        --clicksRemaining;
        while (clicksRemaining > 0) {
            int engineerSkill = game.getShip().getEngineerSkill();
            int repairsAmount = GetRandom(engineerSkill) >> 1;
            game.getShip().repair(repairsAmount);

            boolean spaceMonsterEncounter = clicksRemaining == 1
                    && destination.getType() == SolarSystem.Name.Acamar
                    && game.getMonsterStatus() == com.malcolmcrum.spacetrader.Monster.InAcamar;
            if (spaceMonsterEncounter) {
                return new Monster(game, this);
            }

            boolean scarabEncounter = clicksRemaining == 20
                    && destination.getSpecialEvent() == SolarSystem.SpecialEvent.ScarabDestroyed
                    && game.getScarabStatus() == com.malcolmcrum.spacetrader.Scarab.Alive
                    && arrivedViaWormhole;
            if (scarabEncounter) {
                return new Scarab(game, this);
            }

            boolean dragonflyEncounter = clicksRemaining == 1
                    && destination.getType() == SolarSystem.Name.Zalkon
                    && game.getDragonflyStatus() == com.malcolmcrum.spacetrader.Dragonfly.GoToZalkon;
            if (dragonflyEncounter) {
                return new Dragonfly(game, this);
            }

            boolean encounterMantis = false;
            if (destination.getType() == SolarSystem.Name.Gemulon
                    && game.getInvasionStatus() != Invasion.TooLate
                    && GetRandom(10) > 4) {
                encounterMantis = true;
            }

            int encounterTest = GetRandom(44 - (2 * game.getDifficulty().getValue()));

            // encounters are half as likely if you're in a flea.
            if (game.getShip().getType() == ShipType.Flea) {
                encounterTest *= 2;
            }

            boolean encounterPirate = false;
            boolean encounterPolice = false;
            boolean encounterTrader = false;
            if (encounterTest < destination.getPirateStrength().getStrength() && !beenRaided) {
                encounterPirate = true;
            } else if (encounterTest < destination.getPirateStrength().getStrength() + policeStrength()) {
                encounterPolice = true;
            } else if (encounterTest < destination.getPirateStrength().getStrength() + policeStrength() + destination.getTraderStrength().getStrength()) {
                encounterTrader = true;
            } else if (game.getWildStatus() == Wild.OnBoard
                    && destination.getType() == SolarSystem.Name.Kravat) {
                // if you're coming in to Kravat & you have Wild onboard, there'll be swarms o' cops.
                int rareEncounter = GetRandom(100);
                if (game.getDifficulty() == Difficulty.Beginner || game.getDifficulty() == Difficulty.Easy
                        && rareEncounter < 25) {
                    encounterPolice = true;
                } else if (game.getDifficulty() == Difficulty.Normal
                        && rareEncounter < 33) {
                    encounterPolice = true;
                } else if (rareEncounter < 50) {
                    encounterPolice = true;
                }
            }

            if (!(encounterTrader || encounterPolice || encounterPirate)) {
                if (game.getShip().hasArtifactOnBoard() && GetRandom(20) <= 3) {
                    encounterMantis = true;
                }
            }

            if (encounterPolice) {
                return new Police(game, this);
            } else if (encounterMantis) {
                return new Mantis(game, this);
            } else if (encounterPirate) {
                return new Pirate(game, this);
            } else if (encounterTrader) {
                return new Trader(game, this);
            }

            // Very Rare Random Events:
            // 1. Encounter the abandoned Marie Celeste, which you may loot.
            // 2. Captain Ahab will trade your Reflective Shield for skill points in Piloting.
            // 3. Captain Conrad will trade your Military Laser for skill points in Engineering.
            // 4. Captain Huie will trade your Military Laser for points in Trading.
            // 5. Encounter an out-of-date bottle of Captain Marmoset's Skill Tonic. This
            //    will affect skills depending on game difficulty level.
            // 6. Encounter a good bottle of Captain Marmoset's Skill Tonic, which will invoke
            //    IncreaseRandomSkill one or two times, depending on game difficulty.
            else if (game.getDays() > 10 && GetRandom(1000) < CHANCE_OF_A_VERY_RARE_ENCOUNTER) {
                int rareEncounter = GetRandom(6);
                switch (rareEncounter) {
                    case 0:
                        if (!game.getRareEncounters().marie()) {
                            game.getRareEncounters().encounteredMarie();
                            return new MarieCeleste(game, this);
                        }
                        break;
                    case 1:
                        if (game.getShip().hasShield(ShieldType.ReflectiveShield)
                                && game.getCaptain().getPilotSkill() < 10
                                && !game.getCaptain().isCriminal()
                                && !game.getRareEncounters().ahab()) {
                            game.getRareEncounters().encounteredAhab();
                            return new Ahab(game, this);
                        }
                        break;
                    case 2:
                        if (game.getShip().hasWeapon(Weapon.MilitaryLaser)
                                && game.getCaptain().getTraderSkill() < 10
                                && !game.getCaptain().isCriminal()
                                && !game.getRareEncounters().conrad()) {
                            game.getRareEncounters().encounteredConrad();
                            return new Conrad(game, this);
                        }
                        break;
                    case 3:
                        if (game.getShip().hasWeapon(Weapon.MilitaryLaser)
                                && game.getCaptain().getTraderSkill() < 10
                                && !game.getCaptain().isCriminal()
                                && !game.getRareEncounters().huie()) {
                            game.getRareEncounters().encounteredHuie();
                            return new Huie(game, this);
                        }
                        break;
                    case 4:
                        if (!game.getRareEncounters().oldBottle()) {
                            game.getRareEncounters().encounteredOldBottle();
                            return new OldBottle(game, this);
                        }
                        break;
                    case 5:
                        if (!game.getRareEncounters().goodBottle()) {
                            game.getRareEncounters().encounteredGoodBottle();
                            return new GoodBottle(game, this);
                        }
                        break;
                }
            }
            --clicksRemaining;
        }

        // Ah, just when you thought you were gonna get away with it...
        if (justLootedMarie) {
            ++clicksRemaining;
            return new PostMariePolice(game, this);
        }

        if (hadEncounter) {
            game.addAlert(Alert.Arrival);
        } else {
            game.addAlert(Alert.ArrivalUneventfulTrip);
        }
        return new InSystem(game, destination);
    }

    /**
     * PoliceStrength adapts itself to your criminal record: you'll
     * encounter more police if you are a hardened criminal.
     * @return PoliceStrength for a system, modified by criminal score
     */
    private int policeStrength() {
        if (game.getCaptain().isPsychopathic()) {
            return 3 * destination.getPoliceStrength().getStrength();
        } else if (game.getCaptain().isVillainous()) {
            return 2 * destination.getPoliceStrength().getStrength();
        } else {
            return destination.getPoliceStrength().getStrength();
        }

    }

    public int getClicksRemaining() {
        return clicksRemaining;
    }

    public SolarSystem getDestination() {
        return destination;
    }

    public void setHadEncounter(boolean hadEncounter) {
        this.hadEncounter = hadEncounter;
    }

    public boolean hasBeenInspected() {
        return beenInspected;
    }

    public void policeInspectedPlayer() {
        beenInspected = true;
    }

    public void hasBeenRaided() {
        this.beenRaided = true;
    }
}
