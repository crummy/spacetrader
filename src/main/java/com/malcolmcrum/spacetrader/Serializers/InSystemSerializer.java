package com.malcolmcrum.spacetrader.Serializers;

import com.google.gson.*;
import com.malcolmcrum.spacetrader.*;
import com.malcolmcrum.spacetrader.GameStates.InSystem;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

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
        json.add("system", GetProperties(system));

        JsonObject shipObj = new JsonObject();
        PlayerShip ship = inSystem.getPlayerShip();
        shipObj.addProperty("type", ship.getType().getName());
        shipObj.add("status", GetShipStatus(ship));
        shipObj.add("equipment", GetEquipment(ship));
        shipObj.add("crew", GetCrew(ship));
        json.add("ship", shipObj);

        json.add("market", GetMarket(system.getMarket()));
        json.add("shipsForSale", GetShipsForSale(system.getShipsForSale()));
        json.add("equipmentForSale", GetEquipmentForSale(inSystem.getEquipmentForSale()));

        if (inSystem.alreadyBoughtNewspaper()) {
            json.add("news", GetNews(inSystem.getNews()));
        }

        if (system.hasMercenary()) {
            json.add("mercenaryForHire", GetMercenary(system.getMercenary()));
        }

        return json;
    }

    private JsonArray GetEquipmentForSale(Map<String, Integer> equipmentForSale) {
        JsonArray equipment = new JsonArray();
        for (String name : equipmentForSale.keySet()) {
            JsonObject item = new JsonObject();
            item.addProperty("name", name);
            item.addProperty("price", equipmentForSale.get(name));
            equipment.add(item);
        }
        return equipment;
    }

    private JsonObject GetMercenary(Crew mercenary) {
        JsonObject merc = new JsonObject();
        merc.addProperty("name", mercenary.getName());
        merc.addProperty("fighter", mercenary.getFighterSkill());
        merc.addProperty("engineer", mercenary.getEngineerSkill());
        merc.addProperty("pilot", mercenary.getPilotSkill());
        merc.addProperty("trader", mercenary.getTraderSkill());
        merc.addProperty("dailyCost", mercenary.getDailyCost());
        return merc;
    }

    private JsonArray GetNews(List<String> headlines) {
        JsonArray news = new JsonArray();
        for (String headline : headlines) {
            news.add(new JsonPrimitive(headline));
        }
        return news;
    }

    private JsonArray GetShipsForSale(Map<ShipType, Integer> shipsForSale) {
        JsonArray ships = new JsonArray();
        for (ShipType type : shipsForSale.keySet()) {
            JsonObject shipForSale = new JsonObject();
            shipForSale.addProperty("type", type.getName());
            shipForSale.addProperty("price", shipsForSale.get(type));
            ships.add(shipForSale);
        }
        return ships;
    }

    private JsonObject GetProperties(SolarSystem system) {
        JsonObject properties = new JsonObject();
        properties.addProperty("name", system.getName());
        properties.addProperty("size", system.getSize().getName());
        properties.addProperty("techLevel", system.getTechLevel().getName());
        properties.addProperty("government", system.getPolitics().getName());
        properties.addProperty("resources", system.getSpecialResource().getName());
        properties.addProperty("police", system.getPoliceStrength().getName());
        properties.addProperty("pirates", system.getPirateStrength().getName());
        properties.addProperty("status", system.getStatus().getTitle());
        return properties;
    }

    private JsonElement GetCrew(PlayerShip ship) {
        JsonObject crew = new JsonObject();
        for (Crew c : ship.getCrew()) {
            JsonObject crewMember = new JsonObject();
            crewMember.addProperty("fighter", c.getFighterSkill());
            crewMember.addProperty("engineer", c.getEngineerSkill());
            crewMember.addProperty("trader", c.getTraderSkill());
            crewMember.addProperty("pilot", c.getPilotSkill());
            crew.add(c.getName(), crewMember);
        }
        return crew;
    }

    private static JsonElement GetMarket(Market market) {
        JsonObject items = new JsonObject();
        for (TradeItem item : TradeItem.values()) {
            JsonObject details = new JsonObject();
            details.addProperty("buyPrice", market.getBuyPrice(item).get());
            details.addProperty("sellPrice", market.getSellPrice(item).get());
            details.addProperty("quantity", market.getQuantity(item));
            items.add(item.getName(), details);
        }
        return items;
    }

    private static JsonElement GetShipStatus(PlayerShip ship) {
        JsonObject shipStatus = new JsonObject();
        shipStatus.addProperty("hull", ship.getHullStrength());
        shipStatus.addProperty("fullHull", ship.getFullHullStrength());
        shipStatus.addProperty("fuel", ship.getFuel());
        shipStatus.addProperty("fuelCapacity", ship.getFuelCapacity());
        return shipStatus;
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
