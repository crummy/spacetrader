package com.malcolmcrum.spacetrader.Serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.malcolmcrum.spacetrader.Game;

import java.lang.reflect.Type;

/**
 * Not used for serializing an entire game, just outputting general data
 * Created by Crummy on 11/14/2015.
 */
public class GameSerializer implements JsonSerializer {

    @Override
    public JsonElement serialize(Object o, Type type, JsonSerializationContext jsonSerializationContext) {
        assert(type == Game.class);
        Game game = (Game)o;

        JsonObject json = new JsonObject();
        json.addProperty("name", game.getCaptain().getName());
        json.addProperty("days", game.getDays());

        return json;
    }
}
