package com.malcolmcrum.spacetrader;

import com.malcolmcrum.spacetrader.GameStates.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Eventually will handle multiple games. Right now, just one.
 * Created by Malcolm on 9/10/2015.
 */
public class GameManager {
    private static final Logger logger = LoggerFactory.getLogger(GameManager.class);

    private Game game;
    private GameState state;

    public GameManager() {
        newGame();
    }

    public void newGame() {
        game = new Game();
        state = game.startNewGame("Billy Bob", 5, 5, 5, 5, Difficulty.Normal);
    }


    public GameState getState() {
        return state;
    }

    public Galaxy getGalaxy() {
        return game.getGalaxy();
    }

    public boolean isActionValid(String action) {
        List<Method> actions = state.getActions();
        for (Method m : actions) {
            if (m.getName().equals(action)) {
                return true;
            }
        }
        return false;
    }

    public GameState action(String action) {
        List<Method> actions = state.getActions();
        for (Method m : actions) {
            if (m.getName().equals(action)) {
                try {
                    state = (GameState)m.invoke(state);
                } catch (IllegalAccessException e) {
                    logger.error("IllegalAccessException? Dang.");
                } catch (InvocationTargetException e) {
                    logger.error("InvocationTargetException... I guess");
                }
            }
        }
        logger.error("Could not find action " + action);
        return state;
    }

    public ShipTypes getShipTypes() {
        return new ShipTypes();
    }

    public Captain getCaptain() {
        return game.getCaptain();
    }

    public class ShipTypes {
        public ShipType[] types;
        ShipTypes() {
            types = ShipType.values();
        }
    }
}
