package com.malcolmcrum.spacetrader.Serializers;

import com.google.gson.*;
import com.malcolmcrum.spacetrader.*;
import com.malcolmcrum.spacetrader.GameStates.InSystem;

import java.lang.reflect.Type;

/**
 * Created by Malcolm on 9/10/2015.
 */
public class InSystemSerializer extends GameStateSerializer implements JsonSerializer {
    @Override
    public JsonElement serialize(Object o, Type type, JsonSerializationContext context) {
        assert(type == InSystem.class);
        InSystem inSystem = (InSystem)o;
        SolarSystem system = inSystem.getSystem();

        JsonObject json = GetGameStateJsonObject(inSystem);
        json.addProperty("systemName", system.getName());

        PlayerShip ship = inSystem.getPlayerShip();

        json.add("shipStatus", GetShipStatus(ship));

        json.add("equipment", GetEquipment(ship));

        return json;
    }

    private static JsonElement GetShipStatus(PlayerShip ship) {
        JsonObject shipStatus = new JsonObject();
        shipStatus.addProperty("hull", ship.getHullStrength());
        shipStatus.addProperty("fullHull", ship.getFullHullStrength());
        shipStatus.addProperty("fuel", ship.getFuel());
        shipStatus.addProperty("fuelCapacity", ship.getFuelCapacity());
    }

    private static JsonElement GetEquipment(PlayerShip ship) {
        JsonObject equipment = new JsonObject();
        JsonArray weapons = new JsonArray();
        for (Weapon weapon : ship.getWeapons()) {
            weapons.add(new JsonPrimitive(weapon.getName()));
        }
        equipment.add("weapons", weapons);

        JsonArray shields = new JsonArray();
        for (Ship.Shield shield : ship.getShields()) {
            shields.add(new JsonPrimitive(shield.getName()));
        }
        equipment.add("shields", shields);

        JsonArray gadgets = new JsonArray();
        for (Gadget gadget : ship.getGadgets()) {
            gadgets.add(new JsonPrimitive(gadget.getName()));
        }
        equipment.add("gadgets", gadgets);

        return equipment;
    }

}
