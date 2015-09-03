package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 9/2/2015.
 */
public abstract class GameState {
    protected final Game game;

    GameState(Game game) {
        this.game = game;
    }

    abstract GameState init();
}
