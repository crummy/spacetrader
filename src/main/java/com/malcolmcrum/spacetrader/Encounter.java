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
    Status status;

    Encounter(Game game, Transit transit) {
        super(game);
        this.transit = transit;
    }

    public String getDescription() {
        int clicksRemaining = transit.getClicksRemaining();
        String destinationName = transit.getDestination().getName().getTitle();
        String encounterName = getTitle();
        return "At " + clicksRemaining + " clicks from " + destinationName + ", you encounter " + encounterName + ".";
    }

    int getTribbles() {
        return (int)Math.sqrt(game.getShip().getTribbles()/250);
    }

    public GameState playerAttacks() {
        if (game.getShip().weapons.size() == 0) {
            game.addAlert(Alert.AttackingWithoutWeapons);
            return this;
        }
        status = Status.Attacking;

        attack();

        if (game.getShip().isDestroyed()) {
            return new ShipDestroyed(game, transit.getDestination());
        }

        if (opponent.isDestroyed()) {
            return new LootShipState(game, transit, opponent);
        }

        return this;
    }

    private boolean executeAttack(Ship attacker, Ship defender, Boolean defenderFleeing, boolean defenderIsPlayer) {
        Difficulty difficulty = game.getDifficulty();

        // On beginner level, if you flee, you will escape unharmed.
        if (difficulty == Difficulty.Beginner && defenderIsPlayer) {
            return false;
        }

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

    abstract String getTitle();

    abstract GameState executeAction();

    enum Status {
        Ignoring,
        Awake, // if police, this is "POLICEINSPECTION" equivalent
        Attacking,
        Fleeing,
        Destroyed
    }
}
