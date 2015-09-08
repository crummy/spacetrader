package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.Alert;
import com.malcolmcrum.spacetrader.Difficulty;
import com.malcolmcrum.spacetrader.Game;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import com.malcolmcrum.spacetrader.ShipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;
import static com.malcolmcrum.spacetrader.Utils.Pluralize;

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
