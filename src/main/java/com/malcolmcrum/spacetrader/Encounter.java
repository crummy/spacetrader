package com.malcolmcrum.spacetrader;

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

    Encounter(Game game, Transit transit) {
        super(game);
        this.transit = transit;
        this.isPlayerFleeing = false;
        this.playerWasHit = false;
    }

    public String getDescription() {
        int clicksRemaining = transit.getClicksRemaining();
        String destinationName = transit.getDestination().getName().getTitle();
        String encounterName = getString();
        return "At " + clicksRemaining + " clicks from " + destinationName + ", you encounter " + encounterName + ".";
    }

    int getTribbles() {
        return (int)Math.sqrt(game.getShip().getTribbles()/250);
    }

    public GameState fleeAction() {
        if (game.getDifficulty() == Difficulty.Beginner) {
            game.addAlert(Alert.YouEscaped);
            return transit;
        }
        int difficulty = game.getDifficulty().getValue();
        int playerFleeChance = (GetRandom(7) + (game.getShip().getPilotSkill() / 3)) * 2;
        int opponentChaseChance = GetRandom(opponent.getPilotSkill()) * (2 * difficulty);
        if (playerFleeChance >= opponentChaseChance) {
            if (playerWasHit) {
                // TODO: add tribbles?
                game.addAlert(Alert.YouEscapedWithDamage);
            } else {
                game.addAlert(Alert.YouEscaped);
            }
            return transit;
        }

        opponentAction();
        return actionResult();
    }

    public GameState attackAction() {
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
        if (game.getShip().weapons.size() == 0) {
            game.addAlert(Alert.AttackingWithoutWeapons);
        }
    }

    protected void opponentAction() {
        switch (opponentStatus) {
            case Ignoring:
                logger.error("Player took an action, but opponent is still in Ignoring state!");
                break;
            case Awake:
                logger.error("Player took an action, but opponent is still in Awake state!");
                break;
            case Attacking: // Fire shots at player
                executeAttack(opponent, game.getShip(), isPlayerFleeing, true);
                break;
            case Fleeing:
                fleePlayer();
                break;
            case Fled:
                logger.error("Opponent has already fled but is taking an action!");
                break;
            case Surrendered:
                surrenderToPlayer();
                break;
            case Destroyed:
                logger.error("Opponent is taking a turn but is destroyed!!");
        }
    }

    protected abstract void surrenderToPlayer();

    private void fleePlayer() {
        int playerChaseChance = GetRandom(game.getShip().getPilotSkill()) * 4;
        int opponentFleeChance = (GetRandom(7 + (opponent.getPilotSkill() / 3))) * 2;
        if (playerChaseChance <= opponentFleeChance) {
            game.addAlert(Alert.OpponentEscaped);
            opponentStatus = Status.Fled;
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
        int hitChance = GetRandom(attacker.getFighterSkill() + defender.type.getSize().getValue());
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
