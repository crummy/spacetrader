package com.malcolmcrum.spacetrader;

import com.malcolmcrum.spacetrader.GameStates.InSystem;
import org.junit.Before;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Malcolm on 9/7/2015.
 */
public class GameStateTest {
    InSystem inSystem;
    List<Method> methods;
    Game game;

    @Before
    public void setUp() {
        game = new Game();
        game.startNewGame("Bob", 5, 5, 5, 5, Difficulty.Normal);
        SolarSystem system = game.getGalaxy().getStartSystem(ShipType.Beetle.getFuelTanks());
        inSystem = new InSystem(game, system);
    }

    protected Method findMethodNamed(String name) {
        for (Method m : methods) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }
}
