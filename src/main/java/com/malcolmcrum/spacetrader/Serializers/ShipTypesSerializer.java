package com.malcolmcrum.spacetrader.Serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.malcolmcrum.spacetrader.GameManager;
import com.malcolmcrum.spacetrader.ShipType;

import java.lang.reflect.Type;

/**
 * Created by Malcolm on 9/11/2015.
 */
public class ShipTypesSerializer implements JsonSerializer {
    @Override
    public JsonElement serialize(Object o, Type type, JsonSerializationContext jsonSerializationContext) {
        assert(type == GameManager.ShipTypes.class);
        GameManager.ShipTypes ships = (GameManager.ShipTypes)o;

        JsonObject json = new JsonObject();
        for (ShipType shipType : ships.types) {
            JsonObject ship = new JsonObject();
            ship.addProperty("size", shipType.getSize().getName());
            ship.addProperty("cargoBays", shipType.getCargoBays());
            ship.addProperty("maxRange", shipType.getFuelTanks());
            ship.addProperty("hullStrength", shipType.getHullStrength());
            ship.addProperty("weaponSlots", shipType.getWeaponSlots());
            ship.addProperty("shieldSlots", shipType.getShieldSlots());
            ship.addProperty("gadgetSlots", shipType.getGadgetSlots());
            ship.addProperty("crewQuarters", shipType.getCrewQuarters());
            json.add(shipType.getName(), ship);
        }

        return json;
    }
}
