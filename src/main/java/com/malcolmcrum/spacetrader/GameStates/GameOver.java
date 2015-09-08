package com.malcolmcrum.spacetrader.GameStates;

import com.malcolmcrum.spacetrader.Game;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Malcolm on 9/3/2015.
 */
public class GameOver extends GameState {
    public GameOver(Game game) {
        super(game);
    }

    public List<Method> getActions() {
        List<Method> actions = new ArrayList<>();
        try {
            actions.add(GameOver.class.getMethod("newGame"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return actions;
    }

    @Override
    public GameState init() {
        return this;
    }

    public GameState newGame() {
        // TODO
        return this;
    }
}
