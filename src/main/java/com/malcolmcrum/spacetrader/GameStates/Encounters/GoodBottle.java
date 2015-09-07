package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.Game;
import com.malcolmcrum.spacetrader.GameStates.Transit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.malcolmcrum.spacetrader.Utils.Pluralize;

/**
 * Created by Malcolm on 9/6/2015.
 */
public class GoodBottle extends Encounter {

    public GoodBottle(Game game, Transit transit) {
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
                    actions.add(GoodBottle.class.getMethod("actionDrink"));
                    actions.add(GoodBottle.class.getMethod("actionIgnore"));
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
    public String getEncounterDescription() {
        String clicks = Pluralize(transit.getClicksRemaining(), "click");
        String destination = transit.getDestination().getName();
        String ship = opponent.getName();
        return "At " + clicks + " from " + destination + ", you encounter a floating bottle.";
    }

    @Override
    public String getTitle() {
        return MISSING_TITLE;
    }

    @Override
    protected String descriptionAwake() {
        return "It appears to be a rare bottle of Captain Marmoset's Skill Tonic!";
    }

    @Override
    protected String descriptionAttacking() {
        return INVALID_DESCRIPTION;
    }

    @Override
    protected String descriptionFleeing() {
        return INVALID_DESCRIPTION;
    }

    @Override
    protected String descriptionSurrendered() {
        return INVALID_DESCRIPTION;
    }
}
