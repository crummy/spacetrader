package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 9/3/2015.
 */
public class GameOver extends GameState {
    GameOver(Game game) {
        super(game);
    }

    @Override
    GameState init() {
        return this;
    }
}
