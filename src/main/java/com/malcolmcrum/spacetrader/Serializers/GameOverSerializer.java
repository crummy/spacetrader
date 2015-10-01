package com.malcolmcrum.spacetrader.Serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.malcolmcrum.spacetrader.GameStates.GameOver;
import com.malcolmcrum.spacetrader.Serializers.GameStateSerializer;

import java.lang.reflect.Type;

/**
 * Created by Malcolm on 9/12/2015.
 */
public class GameOverSerializer extends GameStateSerializer implements JsonSerializer{
    @Override
    public JsonElement serialize(Object o, Type type, JsonSerializationContext jsonSerializationContext) {
        assert(type == GameOver.class);
        GameOver gameOver = (GameOver)o;

        JsonObject json = GetGameStateJsonObject(gameOver);
        json.addProperty("finalScore", gameOver.getScore());
        return json;
    }
}
