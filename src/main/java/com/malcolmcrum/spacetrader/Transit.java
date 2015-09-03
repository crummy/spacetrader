package com.malcolmcrum.spacetrader;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * Created by Malcolm on 9/2/2015.
 */
public class Transit extends GameState {

    private static final int CHANCE_OF_A_VERY_RARE_ENCOUNTER = 5; // out of a thousand

    private final SolarSystem origin;
    private final SolarSystem destination;

    private boolean possibleToGoThroughRip = false;
    private boolean beenRaided = false;
    private int clicksRemaining;
    private int totalClicks;
    private boolean arrivedViaWormhole;

    public Transit(Game game, SolarSystem destination) {
        super(game);
        origin = game.getCurrentSystem();
        this.destination = destination;

        int fabricRipProbability = game.getFabricRipProbability();
        if (!possibleToGoThroughRip
                && game.getExperimentStatus() == Experiment.Performed
                && fabricRipProbability > 0) {
            if (GetRandom(100) < fabricRipProbability || fabricRipProbability == 25) {
                // TODO: Set status
                destination = game.galaxy.getRandomSystem();
            }
        }
        possibleToGoThroughRip = true;

        clicksRemaining = game.galaxy.distanceBetween(origin, destination);
        totalClicks = clicksRemaining;
    }

    public GameState Travel() {
        --clicksRemaining;
        while (clicksRemaining > 0) {
            int engineerSkill = game.getCaptain().getEngineerSkill(); // TODO: Check entire ship, not captain?
            int repairsAmount = GetRandom(engineerSkill) >> 1;
            game.getCurrentShip().repair(repairsAmount);

            boolean spaceMonsterEncounter = clicksRemaining == 1
                    && destination.getName() == SolarSystem.Name.Acamar
                    && game.getMonsterStatus() == Monster.InAcamar;
            if (spaceMonsterEncounter) {
                return new MonsterEncounter();
            }

            boolean scarabEncounter = clicksRemaining == 20
                    && destination.getSpecialEvent() == SolarSystem.SpecialEvent.ScarabDestroyed
                    && game.getScarabStatus() == Scarab.Alive
                    && arrivedViaWormhole;
            if (scarabEncounter) {
                return new ScarabEncounter();
            }

            boolean dragonflyEncounter = clicksRemaining == 1
                    && destination.getName() == SolarSystem.Name.Zalkon
                    && game.getDragonflyStatus() == Dragonfly.GoToZalkon;
            if (dragonflyEncounter) {
                return new DragonflyEncounter();
            }

            boolean encounterMantis = false;
            if (destination.getName() == SolarSystem.Name.Gemulon
                    && game.getInvasionStatus() != Invasion.TooLate
                    && GetRandom(10) > 4) {
                encounterMantis = true;
            }

            int encounterTest = GetRandom(44 - (2 * game.getDifficulty().getValue()));

            // encounters are half as likely if you're in a flea.
            if (game.getCurrentShip().type == ShipType.Flea) {
                encounterTest *= 2;
            }

            boolean encounterPirate = false;
            boolean encounterPolice = false;
            boolean encounterTrader = false;
            if (encounterTest < destination.getPirateStrength() && !beenRaided) {
                encounterPirate = true;
            } else if (encounterTest < destination.getPirateStrength() + policeStrength()) {
                encounterPolice = true;
            } else if (encounterTest < destination.getPirateStrength() + policeStrength() + destination.getTraderStrength()) {
                encounterTrader = true;
            } else if (game.getWildStatus() == Wild.OnBoard
                    && destination.getName() == SolarSystem.Name.Kravat) {
                // if you're coming in to Kravat & you have Wild onboard, there'll be swarms o' cops.
                int rareEncounter = GetRandom(100);
                if (game.getDifficulty() == Difficulty.Beginner || game.getDifficulty() == Difficulty.Easy
                        && rareEncounter < 25) {
                    encounterPolice = true;
                } else if (game.getDifficulty == Difficulty.Normal
                        && rareEncounter < 33) {
                    encounterPolice = true;
                } else if (rareEncounter < 50) {
                    encounterPolice = true;
                }
            }

            if (!(encounterTrader || encounterPolice || encounterPirate)) {
                if (game.getCurrentShip().hasArtifactOnBoard() && GetRandom(20) <= 3) {
                    encounterMantis = true;
                }
            }

            if (encounterPolice) {
                return new PoliceEncounter();
            } else if (encounterMantis) {
                return new MantisEncounter();
            } else if (encounterPirate) {
                return new PirateEncounter();
            } else if (encounterTrader) {
                return new TraderEncounter();
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
                        if (!game.rareEncounters.marieCelest()) {
                            game.rareEncounters.encounteredMarieCelest();
                            return new MarieCelesteEncounter();
                        }
                        break;
                    case 1:
                        if (game.getCurrentShip().hasReflectiveShield()
                                && game.getCaptain().getPilotSkill() < 10
                                && !game.getCaptain().isCriminal()
                                && !game.rareEncounters.ahab()) {
                            game.rareEncounters.encounteredAhab();
                            return new AhabEncounter();
                        }
                        break;
                    case 2:
                        if (game.getCurrentShip().hasMilitaryLaser()
                                && game.getCaptain().getTraderSkill() < 10
                                && !game.getCaptain().isCriminal()
                                && !game.rareEncounters.conrad()) {
                            game.rareEncounters.encounteredConrad();
                            return new ConradEncounter();
                        }
                        break;
                    case 3:
                        if (game.getCurrentShip().hasMilitaryLaser()
                                && game.getCaptain().getTraderSkill() < 10
                                && !game.getCaptain().isCriminal()
                                && !game.rareEncounters.huie()) {
                            game.rareEncounters.encounteredHuie();
                            return new HuieEncounter();
                        }
                        break;
                    case 4:
                        if (!game.rareEncounters.oldBottle()) {
                            game.rareEncounters.encounteredOldBottle();
                            return new OldBottleEncounter();
                        }
                        break;
                    case 5:
                        if (!game.rareEncounters.goodBottle()) {
                            game.rareEncounters.encounteredGoodBottle();
                            return new GoodBottleEncounter();
                        }
                        break;
                }
            }
            --clicksRemaining;
        }
        return new ArrivalState();
    }

    /**
     * PoliceStrength adapts itself to your criminal record: you'll
     * encounter more police if you are a hardened criminal.
     * @return PoliceStrength for a system, modified by criminal score
     */
    private int policeStrength() {
        if (game.getCaptain().isPsychopathic()) {
            return 3 * destination.getPoliceStrength();
        } else if (game.getCaptain().isVillainous()) {
            return 2 * destination.getPoliceStrength();
        } else {
            return destination.getPoliceStrength();
        }

    }

    public int getClicksRemaining() {
        return clicksRemaining;
    }

    public SolarSystem getDestination() {
        return destination;
    }
}
