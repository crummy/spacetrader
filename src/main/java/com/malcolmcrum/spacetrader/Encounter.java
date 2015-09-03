package com.malcolmcrum.spacetrader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        return (int)Math.sqrt(game.getCurrentShip().getTribbles()/250);
    }

    public GameState playerAttacks() {
        if (opponent.weapons.size() == 0) {
            logger.error("Attacking with no weapons!");
        }
        status = Status.Attacking;

        attack();

        if (game.getCurrentShip().isDestroyed()) {
            return new ShipDestroyed();
        }

        if (opponent.isDestroyed()) {
            return new LootShipState(this);
        }

        return this;
    }

    abstract String getTitle();

    enum Status {
        Ignoring,
        Awake, // if police, this is "POLICEINSPECTION" equivalent
        Attacking,
        Fleeing,
        Destroyed
    }
}
