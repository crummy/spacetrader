package com.malcolmcrum.spacetrader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Malcolm on 9/10/2015.
 */
public class GameManager {
    private static final Logger logger = LoggerFactory.getLogger(GameManager.class);

    private Map<Integer, Game> games = new HashMap<>();
    private Map<Integer, GameState> states = new HashMap<>();

    public int newGame(String name, int fighter, int pilot, int trader, int engineer, int difficultyIndex) {
        Game game = new Game();
        games.put(game.id, game);
        Difficulty d = Difficulty.values()[difficultyIndex];
        states.put(game.id, game.startNewGame(name, fighter, pilot, trader, engineer, d));
        logger.info("New game created for Captain " + name + ", id: " + game.id);
        return game.id;
    }

    public Map<Integer, Game> getGames() {
        return games;
    }

    public GameState getState(int id) {
        if (states.containsKey(id)) {
            return states.get(id);
        } else {
            throw new InvalidGameIdException();
        }
    }

    private Game getGame(int id) {
        if (games.containsKey(id)) {
            return games.get(id);
        } else {
            throw new InvalidGameIdException();
        }
    }

    public Galaxy getGalaxy(int id) {
        return getGame(id).getGalaxy();
    }

    // TODO: Make this check parameters as well
    public boolean isActionValid(int id, String action, String body) {
        GameState state = getState(id);
        List<Method> actions = state.getActions();
        for (Method m : actions) {
            if (m.getName().equals(action)) {
                return true;
            }
        }
        return false;
    }

    public GameState action(int id, String action, String body) {
        GameState state = getState(id);
        List<Method> actions = state.getActions();
        for (Method m : actions) {
            if (m.getName().equals(action)) {
                Object[] parameters = getParametersForMethod(body, m);
                try {
                    GameState previousState = state;
                    state = (GameState)m.invoke(state, parameters);
                    while (state != previousState) { // Some states are just transition states.
                        previousState = state;
                        state = state.init();
                    }
                    return state;
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
        List<Object> returnedParameters = new ArrayList<>();
        for (Parameter parameter : m.getParameters()) {
            String name = parameter.getName();
            String type = parameter.getType().getName();
            if (userParameters.has(name)) {
                returnedParameters.add(paramToType(name, type));
            }
        }
        return returnedParameters.toArray();
    }

    private Object paramToType(String name, String type) {
        if (type.equals("int")) {
            return Integer.parseInt(name);
        } else if (type.equals("String")) {
            return name;
        } else if (type.equals(ShipType.class.getName())) {
            return ShipType.Get(name);
        } else {
            logger.error("Could not decode parameter " + name + " into object of type " + type);
            return "????";
        }
    }

    public ShipTypes getShipTypes() {
        return new ShipTypes();
    }

    public Captain getCaptain(int id) {
        return getGame(id).getCaptain();
    }

    public Bank getBank(int id) {
        return getGame(id).getCaptain().bank;
    }

    public class ShipTypes {
        public ShipType[] types;
        ShipTypes() {
            types = ShipType.values();
        }
    }

    private class InvalidGameIdException extends IllegalArgumentException {

    }
}
