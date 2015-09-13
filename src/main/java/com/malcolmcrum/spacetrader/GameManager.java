package com.malcolmcrum.spacetrader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

    // TODO: Make this check parameters as well
    public boolean isActionValid(String action, String body) {
        List<Method> actions = state.getActions();
        for (Method m : actions) {
            if (m.getName().equals(action)) {
                return true;
            }
        }
        return false;
    }

    public GameState action(String action, String body) {
        List<Method> actions = state.getActions();
        for (Method m : actions) {
            if (m.getName().equals(action)) {
                Class<?>[] parameters = getParametersForMethod(body, m);
                try {
                    GameState previousState = state;
                    state = (GameState)m.invoke(state);
                    while (state != previousState) { // Some states are just transition states.
                        previousState = state;
                        state = state.init();
                    }
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

    private Object[] getParametersForMethod(String body, Method m) {
        if (body.length() == 0) {
            return new Object[0];
        }
        JsonObject json = new JsonParser().parse(body).getAsJsonObject();
        if (!json.has("parameters")) {
            logger.warn("Passed in JSON for action with content but no parameters");
            return new Object[0];
        }

        JsonObject userParameters = json.getAsJsonObject("parameters");
        Object[] returnedParameters
        for (Parameter parameter : m.getParameters()) {
            String name = parameter.getName();
            String type = parameter.getType().getName();
            if (userParameters.get(name).getAsString().equals(type)) {

            }
        }

    }

    public ShipTypes getShipTypes() {
        return new ShipTypes();
    }

    public Captain getCaptain() {
        return game.getCaptain();
    }

    public Bank getBank() {
        return game.getBank();
    }

    public class ShipTypes {
        public ShipType[] types;
        ShipTypes() {
            types = ShipType.values();
        }
    }
}
