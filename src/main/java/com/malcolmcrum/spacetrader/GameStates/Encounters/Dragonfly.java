package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.*;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.malcolmcrum.spacetrader.Utils.Pluralize;

/**
 * Created by Malcolm on 9/6/2015.
 */
public class Dragonfly extends Encounter {
    private static final Logger logger = LoggerFactory.getLogger(Dragonfly.class);


    @Override
    public List<Method> getActions() {
        List<Method> actions = new ArrayList<>();
        try {
            switch(opponentStatus) {
                case Ignoring:
                    actions.add(Dragonfly.class.getMethod("actionAttack"));
                    actions.add(Dragonfly.class.getMethod("actionIgnore"));
                    break;
                case Awake:
                    break;
                case Attacking:
                    actions.add(Dragonfly.class.getMethod("actionAttack"));
                    actions.add(Dragonfly.class.getMethod("actionFlee"));
                    break;
                case Fleeing:
                    break;
                case Fled:
                    break;
                case Surrendered:
                    break;
                case Destroyed:
                    break;
            }
        } catch (NoSuchMethodException e) {
            logger.error("Method does not exist: " + e.getMessage());
        }
        return actions;
    }

    public Dragonfly(Game game, Transit transit) {
        super(game, transit);
        opponent = new Ship(ShipType.Dragonfly, game);

        int difficulty = game.getDifficulty().getValue();
        opponent.addCrew(new Crew(4 + difficulty, 6 + difficulty, 1, 6 + difficulty));

        if (game.getShip().isInvisibleTo(opponent)) {
            opponentStatus = Status.Ignoring;
        } else {
            opponentStatus = Status.Attacking;
        }
    }

    @Override
    public String getEncounterDescription() {
        String clicks = Pluralize(transit.getClicksRemaining(), "click");
        String destination = transit.getDestination().getName();
        String ship = opponent.getName();
        return "At " + clicks + " from " + destination + ", you encounter a stolen " + ship + ".";
    }

    @Override
    public String getTitle() {
        return "Dragonfly";
    }

    @Override
    protected String descriptionAwake() {
        return null;
    }

    @Override
    protected GameState destroyedOpponent() {
        game.getCaptain().killedAPirate();
        game.setDragonflyStatus(com.malcolmcrum.spacetrader.Dragonfly.Destroyed);
        return transit;
    }
}
