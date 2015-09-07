package com.malcolmcrum.spacetrader.GameStates;

import com.malcolmcrum.spacetrader.Game;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Malcolm on 9/2/2015.
 */
public abstract class GameState {
    protected final Game game;

    public GameState(Game game) {
        this.game = game;
    }

    public abstract List<Method> getActions();

    public abstract GameState init();
}
