package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.*;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;
import static com.malcolmcrum.spacetrader.Utils.Pluralize;
import static com.malcolmcrum.spacetrader.Utils.RandomEnum;

/**
 * Created by Malcolm on 9/4/2015.
 */
public class Pirate extends Encounter {
    private static final Logger logger = LoggerFactory.getLogger(Pirate.class);


    public Pirate(Game game, Transit transit) {
        super(game, transit);
        int tries = 1 + (game.getCaptain().getWorth() / 100000);
        int difficulty = game.getDifficulty().getValue();
        int normal = Difficulty.Normal.getValue();
        tries = Math.max(1, tries + difficulty - normal);

        int difficultyModifier = (game.getDifficulty() == Difficulty.Hard || game.getDifficulty() == Difficulty.Impossible) ? difficulty - normal : 0;

        ShipType shipType = ShipType.Gnat;
        for (int i = 0; i < tries; ++i) {
            ShipType betterShip;
            int shipLevel = 0;
            int destinationRequirement = transit.getDestination().getPirateStrength() + difficultyModifier;
            do {
                // TODO: Change here for other encounters
                betterShip = ShipType.GetAdjustedRandomShip();
                if (betterShip.getMinStrengthForPirateEncounter() == null) continue;
                shipLevel = betterShip.getMinStrengthForPirateEncounter().getStrength();
            } while (destinationRequirement < shipLevel);
            if (betterShip.ordinal() > shipType.ordinal()) {
                shipType = betterShip;
            }
        }

        opponent = new Ship(shipType, game);

        tries = Math.max(1, (game.getCaptain().getWorth() / 150000) + difficulty - normal);

        int gadgetCount;
        if (shipType.getGadgetSlots() == 0) {
            gadgetCount = 0;
        } else if (game.getDifficulty() != Difficulty.Impossible) {
            gadgetCount = GetRandom(shipType.getGadgetSlots() + 1);
            if (gadgetCount < shipType.getGadgetSlots()) {
                if (tries > 4) {
                    ++gadgetCount;
                } else if (tries > 2) {
                    gadgetCount += GetRandom(2);
                }
            }
        } else {
            gadgetCount = shipType.getGadgetSlots();
        }

        Gadget bestGadgetSoFar = Gadget.CargoBays;
        for (int i = 0; i < gadgetCount; ++i) {
            for (int j = 0; j < tries; ++j) {
                Gadget randomGadget = Gadget.GetAdjustedRandomGadget();
                if (!opponent.hasGadget(randomGadget)) {
                    if (randomGadget.ordinal() > bestGadgetSoFar.ordinal()) {
                        bestGadgetSoFar = randomGadget;
                    }
                }
            }
            opponent.addGadget(bestGadgetSoFar);
        }

        int cargoBays = opponent.getCargoBays();
        if (cargoBays > 5) {
            int cargoToGenerate;
            // TODO: Change here for other encounters
            if (game.getDifficulty() == Difficulty.Hard || game.getDifficulty() == Difficulty.Impossible) {
                int m = 3 + GetRandom(cargoBays - 5);
                cargoToGenerate = Math.min(m, 15);
                cargoToGenerate = cargoToGenerate / difficulty;
            } else {
                cargoToGenerate = cargoBays;
                cargoToGenerate = (cargoToGenerate * 4) / 5;
            }
            if (cargoToGenerate < 1) {
                cargoToGenerate = 1;
            }

            for (int i = 0; i < cargoToGenerate; ++i) {
                TradeItem item = RandomEnum(TradeItem.class);
                opponent.addCargo(item, 1, 0);
            }
        }

        int weapons;
        if (shipType.getWeaponSlots() == 0) {
            weapons = 0;
        }  else if (shipType.getWeaponSlots() == 1) {
            weapons = 1;
        } else if (game.getDifficulty() != Difficulty.Impossible) {
            weapons = 1 + GetRandom(shipType.getWeaponSlots());
            if (weapons < shipType.getWeaponSlots()) {
                if (tries > 4 && game.getDifficulty() == Difficulty.Hard) {
                    ++weapons;
                } else if (tries > 3 || game.getDifficulty() == Difficulty.Hard) {
                    weapons += GetRandom(2);
                }
            }
        } else {
            weapons = shipType.getWeaponSlots();
        }

        Weapon bestWeaponSoFar = Weapon.PulseLaser;
        for (int i = 0; i < weapons; ++i) {
            for (int j = 0; j < tries; ++j) {
                Weapon randomWeapon = Weapon.GetAdjustedRandomWeapon();
                if (randomWeapon.ordinal() > bestWeaponSoFar.ordinal()) {
                    bestWeaponSoFar = randomWeapon;
                }
            }
            opponent.addWeapon(bestWeaponSoFar);
        }

        int shields;
        if (shipType.getShieldSlots() == 0) {
            shields = 0;
        } else if (game.getDifficulty() != Difficulty.Impossible) {
            shields = GetRandom(shipType.getShieldSlots() + 1);
            if (shields < shipType.getShieldSlots()) {
                if (tries > 3) {
                    ++shields;
                } else {
                    shields += GetRandom(2);
                }
            }
        } else {
            shields = shipType.getShieldSlots();
        }

        ShieldType bestShieldSoFar = ShieldType.EnergyShield;
        for (int i = 0; i < shields; ++i) {
            for (int j = 0; j < tries; ++j) {
                ShieldType randomShield = ShieldType.GetAdjustedRandomShield();
                if (randomShield.ordinal() > bestShieldSoFar.ordinal()) {
                    bestShieldSoFar = randomShield;
                }
            }
            int shieldPower = 0;
            for (int j = 0; j < 5; ++j) {
                int randomPower = GetRandom(bestShieldSoFar.getPower());
                if (randomPower > shieldPower) {
                    shieldPower = randomPower;
                }
            }
            opponent.addShield(bestShieldSoFar, shieldPower);
        }

        if (!opponent.hasShields() || GetRandom(10) <= 7) {
            int strength = 0;
            for (int i = 0; i < 5; ++i) {
                int randomStrength = 1 + GetRandom(1 + opponent.getFullHullStrength());
                if (randomStrength > strength) {
                    strength = randomStrength;
                }
            }
            opponent.setHullStrength(strength);
        }

        int pilotSkill = 1 + GetRandom(Game.MAX_POINTS_PER_SKILL);
        int fighterSkill = 1 + GetRandom(Game.MAX_POINTS_PER_SKILL);
        int traderSkill = 1 + GetRandom(Game.MAX_POINTS_PER_SKILL);
        int engineerSkill = 1 + GetRandom(Game.MAX_POINTS_PER_SKILL);
        if (transit.getDestination().getType() == SolarSystem.Name.Kravat && game.getWildStatus() == Wild.OnBoard && GetRandom(10) < difficulty + 1) {
            engineerSkill = Game.MAX_POINTS_PER_SKILL;
        }
        opponent.addCrew(new Crew(pilotSkill, fighterSkill, traderSkill, engineerSkill));

        int crew;
        if (game.getDifficulty() != Difficulty.Impossible) {
            crew = 1 + GetRandom(opponent.getCrewQuarters());
            if (game.getDifficulty() == Difficulty.Hard && crew < opponent.getCrewQuarters()) {
                ++crew;
            }
        } else {
            crew = opponent.getCrewQuarters();
        }

        for (int i = 0; i < crew; ++i) {
            opponent.addCrew(new Crew());
        }
    }

    @Override
    public List<Method> getActions() {
        List<Method> actions = new ArrayList<>();
        try {
            switch(opponentStatus) {
                case Ignoring:
                    actions.add(Pirate.class.getMethod("actionAttack"));
                    actions.add(Pirate.class.getMethod("actionIgnore"));
                    break;
                case Awake:
                    break;
                case Attacking:
                    actions.add(Pirate.class.getMethod("actionAttack"));
                    actions.add(Pirate.class.getMethod("actionFlee"));
                    actions.add(Pirate.class.getMethod("actionSurrender"));
                    break;
                case Fleeing:
                    actions.add(Pirate.class.getMethod("actionAttack"));
                    actions.add(Pirate.class.getMethod("actionIgnore"));
                    break;
                case Fled:
                    break;
                case Surrendered:
                    actions.add(Pirate.class.getMethod("actionAttack"));
                    actions.add(Pirate.class.getMethod("actionPlunder"));
                    break;
                case Destroyed:
                    break;
            }
        } catch (NoSuchMethodException e) {
            logger.error("Method does not exist: " + e.getMessage());
        }
        return actions;
    }

    public GameState actionSurrender() {
        // TODO
        return this;
    }

    public GameState actionPlunder() {
        // TODO
        return this;
    }

    @Override
    protected void surrenderToPlayer() {

    }

    @Override
    public String getTitle() {
        return "pirate ship";
    }

    @Override
    public String getEncounterDescription() {
        String clicks = Pluralize(transit.getClicksRemaining(), "click");
        String destination = transit.getDestination().getName();
        String ship = opponent.getName();
        return "At " + clicks + " from " + destination + ", you encounter a pirate " + ship + ".";
    }

    @Override
    protected String descriptionAwake() {
        return "?????";
    }

    @Override
    protected GameState destroyedOpponent() {
        if (!game.getCaptain().isDubious()) {
            game.addAlert(Alert.BountyEarned);
            // NOTE: In the original, it seems the bounty is added whether or not the player is dubious.
            // I suspect the bounty should only be added if the BountyEarned message is sent, so that
            // is how I have implemented it.
            game.getCaptain().addCredits(getBounty());
        }
        game.getCaptain().killedAPirate();
        return super.destroyedOpponent();
    }

    /**
     * Decide whether to change tactics (e.g. flee)
     */
    protected void opponentHasBeenDamaged() {
        if (opponent.getHullStrength() < (opponent.getFullHullStrength() * 2) / 3) {
            if (game.getShip().getHullStrength() < (game.getShip().getFullHullStrength() * 2) / 3) {
                if (GetRandom(10) > 3) {
                    opponentStatus = Status.Fleeing;
                }
            } else {
                opponentStatus = Status.Fleeing;
                if (GetRandom(10) > 8) {
                    opponentStatus = Status.Surrendered;
                }
            }
        }
    }

    private int getBounty() {
        int bounty = opponent.getPrice();
        bounty /= 200;
        bounty /= 25;
        bounty *= 25;
        if (bounty <= 0) {
            bounty = 25;
        }
        if (bounty > 2500) {
            bounty = 2500;
        }
        return bounty;
    }
}
