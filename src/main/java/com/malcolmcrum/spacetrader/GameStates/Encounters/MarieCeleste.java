package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.Game;
import com.malcolmcrum.spacetrader.GameStates.Transit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.malcolmcrum.spacetrader.Utils.Pluralize;

/**
 * Somewhere in here, if they loot, don't forget to transit.setJustLootedMarie(true);
 * Created by Malcolm on 9/6/2015.
 */
public class MarieCeleste extends Encounter {

    public MarieCeleste(Game game, Transit transit) {
        super(game, transit);
    }

    @Override
    public List<Method> getActions() {
        List<Method> actions = new ArrayList<>();
        try {
            switch(opponentStatus) {
                case Ignoring:
                    break;
                case Awake:
                    actions.add(MarieCeleste.class.getMethod("actionBoard"));
                    actions.add(MarieCeleste.class.getMethod("actionIgnore"));
                    break;
                case Attacking:

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
            e.printStackTrace();
        }
        return actions;
    }

    @Override
    public String getTitle() {
        return MISSING_TITLE;
    }

    @Override
    protected String descriptionAwake() {
        return "The Marie Celeste appears to be completed abandoned.";
    }

    @Override
    public String getEncounterDescription() {
        String clicks = Pluralize(transit.getClicksRemaining(), "click");
        String destination = transit.getDestination().getName();
        String ship = opponent.getName();
        return "At " + clicks + " from " + destination + ", you encounter a a drifting ship.";
    }
}
