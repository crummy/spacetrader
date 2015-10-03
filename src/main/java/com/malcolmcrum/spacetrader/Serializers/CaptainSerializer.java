package com.malcolmcrum.spacetrader.Serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.malcolmcrum.spacetrader.Captain;

import java.lang.reflect.Type;

/**
 * Created by Malcolm on 9/11/2015.
 */
public class CaptainSerializer implements JsonSerializer {
    @Override
    public JsonElement serialize(Object o, Type type, JsonSerializationContext jsonSerializationContext) {
        assert(type == Captain.class);
        Captain captain = (Captain)o;

        JsonObject json = new JsonObject();
        json.addProperty("name", captain.getName());
        json.addProperty("pilot", captain.getPilotSkill());
        json.addProperty("fighter", captain.getFighterSkill());
        json.addProperty("trader", captain.getTraderSkill());
        json.addProperty("engineer", captain.getEngineerSkill());
        json.addProperty("worth", captain.getWorth());
        json.addProperty("reputation", captain.reputation.getScore());
        json.addProperty("policeRecord", captain.policeRecord.getScore());
        json.addProperty("difficulty", "");
        json.addProperty("kills", captain.getKills());
        json.addProperty("cash", captain.getCredits());

        return json;
    }
}
