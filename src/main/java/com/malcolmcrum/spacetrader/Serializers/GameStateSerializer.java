package com.malcolmcrum.spacetrader.Serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.malcolmcrum.spacetrader.GameStates.GameState;

import java.lang.reflect.Method;

/**
 * Created by Malcolm on 9/10/2015.
 */
public class GameStateSerializer {

    protected static JsonObject GetGameStateJsonObject(GameState gameState) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("state", gameState.getName());
        jsonObject.add("actions", GetActions(gameState));
        return jsonObject;
    }

    protected static JsonArray GetActions(GameState gameState) {
        JsonArray actions = new JsonArray();
        for (Method method : gameState.getActions()) {
            JsonObject action = new JsonObject();
            action.addProperty("name", method.getName());
            action.addProperty("parameters", method.getParameterCount());
            actions.add(action);
        }
        return actions;
    }

}
