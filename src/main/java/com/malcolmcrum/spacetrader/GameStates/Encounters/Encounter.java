package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.*;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.LootShip;
import com.malcolmcrum.spacetrader.GameStates.ShipDestroyed;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;
import static com.malcolmcrum.spacetrader.Utils.RandomEnum;

/**
 * Base class for all encounters.
 * Includes a lot of default encounter behaviour - some of which is unused in base classes
 * (e.g. you can't attack a bottle).
 * Call getAction() on an encounter to get ways you can interact with it.
 * Created by Malcolm on 9/2/2015.
 */
public abstract class Encounter extends GameState {

    private static final Logger logger = LoggerFactory.getLogger(Encounter.class);

    protected static final String INVALID_DESCRIPTION = "INVALID DESCRIPTION";
    protected static final String MISSING_TITLE = "MISSING TITLE";

    Transit transit;
    Ship opponent;
    Status opponentStatus;
    boolean isPlayerFleeing;
    boolean playerWasHit;
    int tribblesOnScreen;

    Encounter(Game game, Transit transit) {
        super(game);
        transit.setHadEncounter(true);
        this.transit = transit;
        this.isPlayerFleeing = false;
        this.playerWasHit = false;
        this.tribblesOnScreen = getTribbles();;
        this.opponentStatus = Status.Awake;

        int tries = getShipTypeTries();

        ShipType shipType = chooseShipType(tries);

        opponent = new Ship(shipType, game);

        tries = getEquipmentTries();

        addGadgets(tries);

        addCargo();

        addWeapons(tries);

        addShields(tries);

        setHullStrength();

        addCrew();
    }

    @Override
    public GameState init() {
        return this;
    }

    @Override
    public String getName() {
        return "Encounter";
    }

    /**
     * @return A sentence describing the encounter
     */
    public abstract String getEncounterDescription();

    public abstract String getTitle();

    /**
     * @return A single sentence or two describing the current action of the opponent
     */
    public String getTurnDescription() {
        switch (opponentStatus) {
            case Ignoring:
                return descriptionIgnoring();
            case Awake:
                return descriptionAwake();
            case Attacking:
                return descriptionAttacking();
            case Fleeing:
                return descriptionFleeing();
            case Fled:
                return INVALID_DESCRIPTION;
            case Surrendered:
                return descriptionSurrendered();
            case Destroyed:
                return INVALID_DESCRIPTION;
            default:
                return INVALID_DESCRIPTION;
        }
    }

    protected String descriptionSurrendered() {
        return "Your opponent hails that he surrenders to you.";
    }

    protected String descriptionFleeing() {
        return "Your opponent is fleeing.";
    }

    protected String descriptionAttacking() {
        return "Your opponent attacks.";
    }

    protected abstract String descriptionAwake();

    protected String descriptionIgnoring() {
        if (game.getShip().isInvisibleTo(opponent)) {
            return "It doesn't notice you.";
        } else {
            return "It ignores you.";
        }
    }

    int getTribbles() {
        return (int)Math.sqrt(game.getShip().getTribbles()/250);
    }

    public GameState actionIgnore() throws InvalidPlayerAction {
        if (opponentStatus != Status.Ignoring && opponentStatus != Status.Fleeing) {
            throw new InvalidPlayerAction();
        }
        return transit;
    }

    public GameState actionFlee() throws InvalidOpponentAction, InvalidPlayerAction {
        isPlayerFleeing = true;

        opponentAction();

        if (game.getDifficulty() == Difficulty.Beginner) {
            game.addAlert(Alert.YouEscaped);
            return transit;
        }
        int difficulty = game.getDifficulty().getValue();
        int playerFleeChance = (GetRandom(7) + (game.getShip().getPilotSkill() / 3)) * 2;
        int opponentChaseChance = GetRandom(opponent.getPilotSkill()) * (2 * difficulty);
        if (playerFleeChance >= opponentChaseChance) {
            if (playerWasHit) {
                tribblesOnScreen = getTribbles();
                game.addAlert(Alert.YouEscapedWithDamage);
            } else {
                game.addAlert(Alert.YouEscaped);
            }
            return transit;
        }

        return actionResult();
    }

    public GameState actionAttack() throws InvalidOpponentAction {
        if (opponentStatus == Status.Ignoring || opponentStatus == Status.Awake) {
            initialAttack();
        }

        boolean isOpponentFleeing = opponentStatus == Status.Fleeing;
        executeAttack(game.getShip(), opponent, isOpponentFleeing, false);

        opponentAction();
        return actionResult();
    }

    public Ship getOpponent() {
        return opponent;
    }

    /**
     * Sends out warnings if attacking would be foolish, and sets opponent status
     * to attacking.
     * Maybe the warnings should be handled in the UI level though.
     * Called if this is the first attack round of an encounter
     */
    void initialAttack() {
        opponentStatus = Status.Attacking;
        if (game.getShip().weaponStrength() == 0) {
            game.addAlert(Alert.AttackingWithoutWeapons);
        }
    }

    protected void opponentAction() throws InvalidOpponentAction {
        switch (opponentStatus) {
            case Ignoring:
                logger.error("Player took an action, but opponent is still in Ignoring state!");
                throw new InvalidOpponentAction();
            case Awake:
                logger.error("Player took an action, but opponent is still in Awake state!");
                throw new InvalidOpponentAction();
            case Attacking: // Fire shots at player
                executeAttack(opponent, game.getShip(), isPlayerFleeing, true);
                break;
            case Fleeing:
                fleePlayer();
                break;
            case Fled:
                logger.error("Opponent has already fled but is taking an action!");
                throw new InvalidOpponentAction();
            case Surrendered:
                surrenderToPlayer();
                break;
            case Destroyed:
                logger.error("Opponent is taking a turn but is destroyed!!");
                throw new InvalidOpponentAction();
        }
        if (opponent.getHullStrength() < opponent.getFullHullStrength()) {
            opponentHasBeenDamaged();
        }
    }

    /**
     * Decide whether to change tactics (e.g. flee)
     */
    protected void opponentHasBeenDamaged() {

    }

    protected void surrenderToPlayer() throws InvalidOpponentAction {

    }

    private void fleePlayer() {
        int playerChaseChance = GetRandom(game.getShip().getPilotSkill()) * 4;
        int opponentFleeChance = (GetRandom(7 + (opponent.getPilotSkill() / 3))) * 2;
        if (playerChaseChance <= opponentFleeChance) {
            opponentStatus = Status.Fled;
        } else {
            game.addAlert(Alert.OpponentDidntEscape);
        }
    }

    /**
     * Wrap up action phase, by checking for end states.
     * If one is found, return the new state to return to, otherwise
     * return 'this', as we're not done with the encounter yet.
     * @return The next GameState to transition to
     */
    protected GameState actionResult() {
        if (game.getShip().isDestroyed() && opponent.isDestroyed()) {
            game.addAlert(Alert.BothDestroyed);
            opponentStatus = Status.Destroyed;
        }
        if (game.getShip().isDestroyed()) {
            return new ShipDestroyed(game, transit.getDestination());
        } else if (opponent.isDestroyed()) {
            game.addAlert(Alert.OpponentDestroyed);
            opponentStatus = Status.Destroyed;
            game.getCaptain().addReputation(opponent.reputationGainForKilling());
            return destroyedOpponent();
        }
        if (opponentStatus == Status.Fled) {
            game.addAlert(Alert.OpponentEscaped);
            return transit;
        }

        tribblesOnScreen = getTribbles();

        return this;
    }

    /**
     * Perform any special tasks after an opponent is destroyed
     * (setting news events, increasing rep)
     * @return Next state to transition to
     */
    protected GameState destroyedOpponent() {
        return new LootShip(game, transit, opponent);
    }

    /**
     * An attacker tries to do damage, while a defender attempts to dodge or flee.
     * @param attacker Ship attacking the defender
     * @param defender Ship trying not to get hit
     * @param defenderFleeing If true, attacker gets a second shot, but chance to hit is smaller
     * @param defenderIsPlayer If true, free flees on Beginner for defenders, and possibility for reactor damage boost for attackers
     * @return True if the defender took damage
     */
    protected boolean executeAttack(Ship attacker, Ship defender, Boolean defenderFleeing, boolean defenderIsPlayer) {
        Difficulty difficulty = game.getDifficulty();

        // FighterSkill attacker is pitted against PilotSkill defender; if defender
        // is fleeing the attacker has a free shot, but the chance to hit is smaller
        int hitChance = GetRandom(attacker.getFighterSkill() + defender.getSizeValue());
        int dodgeChance = (defenderFleeing ? 2 : 1) * GetRandom(5 + (defender.getPilotSkill() >> 1));
        if (hitChance < dodgeChance) {
            // Missed.
            return false;
        }

        if (attacker.weaponStrength() == 0) {
            return false;
        }

        int damage = GetRandom(attacker.weaponStrength() * (100 + 2 * attacker.getEngineerSkill()) / 100);

        // If reactor on board -- damage is boosted!
        if (defenderIsPlayer && game.getReactorStatus() != Reactor.Unavailable && game.getReactorStatus() != Reactor.Delivered) {
            if (difficulty == Difficulty.Beginner || difficulty == Difficulty.Easy) {
                damage *= 1 + (difficulty.getValue() + 1) * 0.25;
            } else {
                damage *= 1 + (difficulty.getValue() + 1) * 0.33;
            }
        }

        defender.takeDamage(damage);
        return true;
    }

    public void setStatus(Status status) {
        this.opponentStatus = status;
    }

    /**
     * The simplest ship that this encounter can use.
     * Actually, it's a flea for anyone but a trader.
     * @return Poorest ship type possible for this encounter
     */
    protected ShipType baseShipType() {
        return ShipType.Flea;
    }

    protected ShipType chooseShipType(int tries) {

        ShipType shipType = baseShipType();
        for (int i = 0; i < tries; ++i) {
            ShipType betterShip;
            do {
                betterShip = ShipType.GetAdjustedRandomShip();
            } while (!shipTypeAcceptable(betterShip));

            if (betterShip != null && betterShip.ordinal() > shipType.ordinal()) {
                shipType = betterShip;
            }
        }
        return shipType;
    }

    protected boolean shipTypeAcceptable(ShipType betterShip) {
        return true;
    }

    protected void addCrew() {
        int pilotSkill = 1 + GetRandom(Game.MAX_POINTS_PER_SKILL);
        int fighterSkill = 1 + GetRandom(Game.MAX_POINTS_PER_SKILL);
        int traderSkill = 1 + GetRandom(Game.MAX_POINTS_PER_SKILL);
        int engineerSkill = 1 + GetRandom(Game.MAX_POINTS_PER_SKILL);
        int difficulty = game.getDifficulty().getValue();
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

    protected void setHullStrength() {
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
    }

    protected void addShields(int tries) {
        int shields;
        if (opponent.getShieldSlots() == 0) {
            shields = 0;
        } else if (game.getDifficulty() != Difficulty.Impossible) {
            shields = GetRandom(opponent.getShieldSlots() + 1);
            if (shields < opponent.getShieldSlots()) {
                if (tries > 3) {
                    ++shields;
                } else {
                    shields += GetRandom(2);
                }
            }
        } else {
            shields = opponent.getShieldSlots();
        }

        ShieldType bestShieldSoFar = ShieldType.EnergyShield;
        for (int i = 0; i < shields; ++i) {
            for (int j = 0; j < tries; ++j) {
                ShieldType randomShield = ShieldType.GetAdjustedRandomShield();
                if (randomShield != null && randomShield.ordinal() > bestShieldSoFar.ordinal()) {
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
    }

    protected void addWeapons(int tries) {
        int weapons;
        if (opponent.getWeaponSlots() == 0) {
            weapons = 0;
        }  else if (opponent.getWeaponSlots() == 1) {
            weapons = 1;
        } else if (game.getDifficulty() != Difficulty.Impossible) {
            weapons = 1 + GetRandom(opponent.getWeaponSlots());
            if (weapons < opponent.getWeaponSlots()) {
                if (tries > 4 && game.getDifficulty() == Difficulty.Hard) {
                    ++weapons;
                } else if (tries > 3 || game.getDifficulty() == Difficulty.Hard) {
                    weapons += GetRandom(2);
                }
            }
        } else {
            weapons = opponent.getWeaponSlots();
        }

        Weapon bestWeaponSoFar = Weapon.PulseLaser;
        for (int i = 0; i < weapons; ++i) {
            for (int j = 0; j < tries; ++j) {
                Weapon randomWeapon = Weapon.GetAdjustedRandomWeapon();
                if (randomWeapon != null && randomWeapon.ordinal() > bestWeaponSoFar.ordinal()) {
                    bestWeaponSoFar = randomWeapon;
                }
            }
            opponent.addWeapon(bestWeaponSoFar);
        }
    }

    protected void addCargo() {
        if (opponent.getCargoBays() > 5) {
            int cargoToGenerate = getCargoToGenerate();

            if (cargoToGenerate < 1) {
                cargoToGenerate = 1;
            }

            for (int i = 0; i < cargoToGenerate; ++i) {
                TradeItem item = RandomEnum(TradeItem.class);
                opponent.addCargo(item, 1, 0);
            }
        }
    }

    protected int getCargoToGenerate() {
        int cargoToGenerate;
        int cargoBays = opponent.getCargoBays();
        if (game.getDifficulty() == Difficulty.Hard || game.getDifficulty() == Difficulty.Impossible) {
            int m = 3 + GetRandom(cargoBays - 5);
            cargoToGenerate = Math.min(m, 15);
        } else {
            cargoToGenerate = cargoBays;
        }
        return cargoToGenerate;
    }

    protected void addGadgets(int tries) {
        int gadgetCount;
        if (opponent.getGadgetSlots() == 0) {
            gadgetCount = 0;
        } else if (game.getDifficulty() != Difficulty.Impossible) {
            gadgetCount = GetRandom(opponent.getGadgetSlots() + 1);
            if (gadgetCount < opponent.getGadgetSlots()) {
                if (tries > 4) {
                    ++gadgetCount;
                } else if (tries > 2) {
                    gadgetCount += GetRandom(2);
                }
            }
        } else {
            gadgetCount = opponent.getGadgetSlots();
        }

        Gadget bestGadgetSoFar = Gadget.CargoBays;
        for (int i = 0; i < gadgetCount; ++i) {
            for (int j = 0; j < tries; ++j) {
                Gadget randomGadget = Gadget.GetAdjustedRandomGadget();
                if (!opponent.hasGadget(randomGadget)) {
                    if (randomGadget != null && randomGadget.ordinal() > bestGadgetSoFar.ordinal()) {
                        bestGadgetSoFar = randomGadget;
                    }
                }
            }
            opponent.addGadget(bestGadgetSoFar);
        }
    }

    protected int getShipTypeTries() {
        return 1;
    }

    protected int getEquipmentTries() {
        int difficulty = game.getDifficulty().getValue();
        int normal = Difficulty.Normal.getValue();
        return Math.max(1, (game.getCaptain().getWorth() / 150000) + difficulty - normal);
    }

    public enum Status {
        Ignoring,
        Awake, // if police, this is "POLICEINSPECTION" equivalent. if trader, this is TRADERBUY/TRADERSELL.
        Attacking,
        Fleeing,
        Fled,
        Surrendered,
        Destroyed
    }
}
