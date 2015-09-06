package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.GameStates.GameState;

/**
 * Created by Malcolm on 9/6/2015.
 */
public class Dragonfly extends Encounter {

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
