package com.malcolmcrum.spacetrader;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * Created by Malcolm on 9/2/2015.
 */
public class Transit extends GameState {

    private static final int CHANCE_OF_A_VERY_RARE_ENCOUNTER = 5; // out of a thousand

    private final SolarSystem origin;
    private SolarSystem destination;

    private boolean possibleToGoThroughRip = false;
    private boolean beenRaided = false;
    private int clicksRemaining;
    private int totalClicks;
    private boolean arrivedViaWormhole;
    private boolean beenInspected;
    private boolean litterWarning;

    public Transit(Game game, SolarSystem destination, boolean viaSingularity) {
        super(game);
        origin = game.getCurrentSystem();
        this.destination = destination;
    }

    @Override
    GameState init() {
        clicksRemaining = 21;
        totalClicks = clicksRemaining;
        beenRaided = false;
        beenInspected = false;
        litterWarning = false;
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
        return this;
    }

    public GameState Travel() {
        --clicksRemaining;
        while (clicksRemaining > 0) {
            int engineerSkill = game.getCaptain().getEngineerSkill(); // TODO: Check entire ship, not captain?
            int repairsAmount = GetRandom(engineerSkill) >> 1;
            game.getShip().repair(repairsAmount);

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
            if (game.getShip().type == ShipType.Flea) {
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
                if (game.getShip().hasArtifactOnBoard() && GetRandom(20) <= 3) {
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
                        if (!game.getRareEncounters().marie()) {
                            game.getRareEncounters().encounteredMarie();
                            return new MarieCelesteEncounter();
                        }
                        break;
                    case 1:
                        if (game.getShip().hasReflectiveShield()
                                && game.getCaptain().getPilotSkill() < 10
                                && !game.getCaptain().isCriminal()
                                && !game.getRareEncounters().ahab()) {
                            game.getRareEncounters().encounteredAhab();
                            return new AhabEncounter();
                        }
                        break;
                    case 2:
                        if (game.getShip().hasMilitaryLaser()
                                && game.getCaptain().getTraderSkill() < 10
                                && !game.getCaptain().isCriminal()
                                && !game.getRareEncounters().conrad()) {
                            game.getRareEncounters().encounteredConrad();
                            return new ConradEncounter();
                        }
                        break;
                    case 3:
                        if (game.getShip().hasMilitaryLaser()
                                && game.getCaptain().getTraderSkill() < 10
                                && !game.getCaptain().isCriminal()
                                && !game.getRareEncounters().huie()) {
                            game.getRareEncounters().encounteredHuie();
                            return new HuieEncounter();
                        }
                        break;
                    case 4:
                        if (!game.getRareEncounters().oldBottle()) {
                            game.getRareEncounters().encounteredOldBottle();
                            return new OldBottleEncounter();
                        }
                        break;
                    case 5:
                        if (!game.getRareEncounters().goodBottle()) {
                            game.getRareEncounters().encounteredGoodBottle();
                            return new GoodBottleEncounter();
                        }
                        break;
                }
            }
            --clicksRemaining;
        }

        // Ah, just when you thought you were gonna get away with it...
        if (justLootedMarie) {
            ++clicksRemaining;
            return new PostMariePoliceEncounter();
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
