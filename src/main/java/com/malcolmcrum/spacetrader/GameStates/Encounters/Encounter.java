package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.*;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.LootShipState;
import com.malcolmcrum.spacetrader.GameStates.ShipDestroyed;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * Created by Malcolm on 9/2/2015.
 */
public abstract class Encounter extends GameState {

    private static final Logger logger = LoggerFactory.getLogger(Encounter.class);


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
        this.tribblesOnScreen = 0;
    }

    @Override
    public GameState init() {
        tribblesOnScreen = getTribbles();
        return this;
    }

    public String getIntroDescription() {
        int clicksRemaining = transit.getClicksRemaining();
        String destinationName = transit.getDestination().getName().getTitle();
        String encounterName = getString();
        return "At " + clicksRemaining + " clicks from " + destinationName + ", you encounter " + encounterName + ".";
    }

    int getTribbles() {
        return (int)Math.sqrt(game.getShip().getTribbles()/250);
    }

    public GameState ignoreAction() throws InvalidPlayerAction {
        if (opponentStatus != Status.Ignoring && opponentStatus != Status.Fleeing) {
            throw new InvalidPlayerAction();
        }
        return transit;
    }

    public GameState fleeAction() throws InvalidOpponentAction, InvalidPlayerAction {
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

        opponentAction();
        return actionResult();
    }

    public GameState attackAction() throws InvalidOpponentAction {
        if (opponentStatus == Status.Ignoring || opponentStatus == Status.Awake) {
            initialAttack();
        }

        boolean isOpponentFleeing = opponentStatus == Status.Fleeing;
        executeAttack(game.getShip(), opponent, isOpponentFleeing, false);

        opponentAction();
        return actionResult();
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
            game.addAlert(Alert.OpponentEscaped);
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
            return new LootShipState(game, transit, opponent);
        }
        if (opponentStatus == Status.Fled) {
            return transit;
        }

        tribblesOnScreen = getTribbles();

        return this;
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

    abstract String getString();

    protected enum Status {
        Ignoring,
        Awake, // if police, this is "POLICEINSPECTION" equivalent. if trader, this is TRADERBUY/TRADERSELL.
        Attacking,
        Fleeing,
        Fled,
        Surrendered,
        Destroyed
    }
}
