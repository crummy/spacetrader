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
            system.addProperty("size", s.getSize().getName());
            system.addProperty("techLevel", s.getTechLevel().getName());
            system.addProperty("government", s.getPolitics().getName());
            if (s.isVisited()) {
                system.addProperty("resources", s.getSpecialResource().getName());
            } else {
                system.addProperty("resources", "Unknown");
            }
            system.addProperty("police", s.getPoliceStrength().getName());
            system.addProperty("pirates", s.getPirateStrength().getName());
            system.addProperty("visited", s.isVisited());
            systems.add(system);
        }
        return systems;
    }
}
