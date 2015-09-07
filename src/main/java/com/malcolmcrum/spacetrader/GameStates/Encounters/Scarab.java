package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.Game;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.malcolmcrum.spacetrader.Utils.Pluralize;

/**
 * Created by Malcolm on 9/6/2015.
 */
public class Scarab extends Encounter {

    @Override
    public List<Method> getActions() {
        List<Method> actions = new ArrayList<>();
        try {
            switch(opponentStatus) {
                case Ignoring:
                    actions.add(Scarab.class.getMethod("actionAttack"));
                    actions.add(Scarab.class.getMethod("ignoreAction"));
                    break;
                case Awake:
                    break;
                case Attacking:
                    actions.add(Scarab.class.getMethod("actionAttack"));
                    actions.add(Scarab.class.getMethod("actionFlee"));
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

    public Scarab(Game game, Transit transit) {
        super(game, transit);
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
        return "Scarab";
    }

    @Override
    protected String descriptionAwake() {
        return null;
    }

    @Override
    protected GameState destroyedOpponent() {
        game.getCaptain().killedAPirate();
        game.setScarabStatus(com.malcolmcrum.spacetrader.Scarab.DestroyedUpgradeAvailable);
        return transit;
    }
}
