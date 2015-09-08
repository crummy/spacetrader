package com.malcolmcrum.spacetrader;

import org.junit.Before;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Malcolm on 9/7/2015.
 */
public class GameStateTest {
    Game game;

    @Before
    public void setUp() {
        game = new Game();
        game.startNewGame("Bob", 5, 5, 5, 5, Difficulty.Normal);
    }

    protected Method findMethodNamed(String name, List<Method> methods) {
        for (Method m : methods) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }
}
