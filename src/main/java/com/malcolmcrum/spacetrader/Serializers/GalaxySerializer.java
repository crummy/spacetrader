package com.malcolmcrum.spacetrader.Serializers;

import com.google.gson.*;
import com.malcolmcrum.spacetrader.Galaxy;
import com.malcolmcrum.spacetrader.SolarSystem;

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
        json.add("width", new JsonPrimitive(Galaxy.GALAXY_WIDTH));
        json.add("height", new JsonPrimitive(Galaxy.GALAXY_HEIGHT));

        return json;
    }

    private JsonElement GetSolarSystems(Galaxy galaxy) {
        JsonArray systems = new JsonArray();
        for (SolarSystem s : galaxy.getSystems()) {
            JsonObject system = new JsonObject();
            system.addProperty("name", s.getName());
            system.addProperty("x", s.getLocationX());
            system.addProperty("y", s.getLocationY());
            system.addProperty("hasWormhole", s.hasWormhole());
            if (s.hasWormhole()) {
                system.addProperty("wormholeDestination", s.getWormholeDestination().getName());
            }
            system.addProperty("name", s.getName());
            system.addProperty("size", s.getSize().name);
            system.addProperty("techLevel", s.getTechLevel().name);
            system.addProperty("government", s.getPolitics().name);
            if (s.isVisited()) {
                system.addProperty("resources", s.getSpecialResource().name);
            } else {
                system.addProperty("resources", "Unknown");
            }
            system.addProperty("police", s.getPoliceStrength().name);
            system.addProperty("pirates", s.getPirateStrength().name);
            system.addProperty("visited", s.isVisited());
            systems.add(system);
        }
        return systems;
    }
}
