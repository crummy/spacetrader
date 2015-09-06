package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.GameStates.GameState;

/**
 * Created by Malcolm on 9/6/2015.
 */
public class Scarab extends Encounter {

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
