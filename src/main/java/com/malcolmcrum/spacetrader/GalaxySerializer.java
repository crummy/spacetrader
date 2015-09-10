package com.malcolmcrum.spacetrader;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by Malcolm on 9/10/2015.
 */
public class GalaxySerializer implements JsonSerializer {
    @Override
    public JsonElement serialize(Object o, Type type, JsonSerializationContext jsonSerializationContext) {
        assert(type == Galaxy.class);
        Galaxy galaxy = (Galaxy)o;

        JsonObject json = new JsonObject();

        json.add("solarSystems", GetSolarSystems(galaxy));

        return json;
    }

    private JsonElement GetSolarSystems(Galaxy galaxy) {
        JsonArray systems = new JsonArray();
        for (SolarSystem s : galaxy.systems) {
            JsonObject system = new JsonObject();
            system.addProperty("name", s.getName());
            system.addProperty("x", s.getLocation().x);
            system.addProperty("y", s.getLocation().y);
            system.addProperty("hasWormhole", s.hasWormhole());
            systems.add(system);
        }
        return systems;
    }
}
