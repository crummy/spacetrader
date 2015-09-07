package com.malcolmcrum.spacetrader.GameStates;

import com.malcolmcrum.spacetrader.Game;

/**
 * Created by Malcolm on 9/3/2015.
 */
public class GameOver extends GameState {
    public GameOver(Game game) {
        super(game);
    }

    @Override
    public GameState init() {
        return this;
    }
}
