package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.Game;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;

import static com.malcolmcrum.spacetrader.Utils.Pluralize;

/**
 * Created by Malcolm on 9/6/2015.
 */
public class Dragonfly extends Encounter {

    Dragonfly(Game game, Transit transit) {
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
