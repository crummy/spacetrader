package com.malcolmcrum.spacetrader.GameStates;

import com.malcolmcrum.spacetrader.Game;

/**
 * Created by Malcolm on 9/2/2015.
 */
public abstract class GameState {
    protected final Game game;

    public GameState(Game game) {
        this.game = game;
    }

    public abstract GameState init();
}
