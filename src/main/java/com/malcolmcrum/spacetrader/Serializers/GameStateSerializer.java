package com.malcolmcrum.spacetrader.Serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.malcolmcrum.spacetrader.GameStates.GameState;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

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
            for (Parameter parameter : method.getParameters()) {
                // In Java 8, if compiled with -parameters, we can give the user parameter names as well as types.
                if (parameter.isNamePresent()) {
                    action.addProperty(parameter.getName(), parameter.getType().getName());
                } else {
                    action.addProperty("parameter", parameter.getType().getName());
                }
            }
            actions.add(action);
        }
        return actions;
    }

}
