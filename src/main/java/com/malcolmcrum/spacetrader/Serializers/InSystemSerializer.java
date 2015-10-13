package com.malcolmcrum.spacetrader.Serializers;

import com.google.gson.*;
import com.malcolmcrum.spacetrader.*;
import com.malcolmcrum.spacetrader.GameStates.InSystem;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * TODO: What is the difference between insystem and system? Differentiate them
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
        shipObj.add("cargo", GetCargo(ship));
        json.add("ship", shipObj);

        json.add("escapePod", GetEscapePod(inSystem));
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

    private JsonElement GetEscapePod(InSystem inSystem) {
        JsonObject escapePod = new JsonObject();
        boolean isAvailable = inSystem.escapePodForSale();
        escapePod.add("isAvailable", new JsonPrimitive(isAvailable));
        if (isAvailable) {
            escapePod.add("price", new JsonPrimitive(inSystem.getEscapePodPrice()));
        }
        return escapePod;
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

    private JsonObject GetMercenary(Mercenary mercenary) {
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
        for (ShipType type : ShipType.values()) {
            if (type.getMinTechLevel() == TechLevel.Unattainable) {
                continue;
            }

            JsonObject ship = new JsonObject();
            ship.addProperty("type", type.getName());
            if (shipsForSale.containsKey(type)) {
                ship.addProperty("price", shipsForSale.get(type));
            }
            ships.add(ship);
        }
        return ships;
    }

    private JsonObject GetProperties(SolarSystem system) {
        JsonObject properties = new JsonObject();
        properties.addProperty("name", system.getName());
        properties.addProperty("size", system.getSize().name);
        properties.addProperty("techLevel", system.getTechLevel().name);
        properties.addProperty("government", system.getPolitics().name);
        properties.addProperty("resources", system.getSpecialResource().name);
        properties.addProperty("police", system.getPoliceStrength().name);
        properties.addProperty("pirates", system.getPirateStrength().name);
        properties.addProperty("traders", system.getTraderStrength().name);
        properties.addProperty("status", system.getStatus().description);
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
            if (market.getBuyPrice(item).isPresent()) {
                details.addProperty("buyPrice", market.getBuyPrice(item).get());
            }
            if (market.getSellPrice(item).isPresent()) {
                details.addProperty("sellPrice", market.getSellPrice(item).get());
            }
            details.addProperty("quantity", market.getQuantity(item));
            items.add(item.name, details);
        }
        return items;
    }

    private static JsonElement GetShipStatus(PlayerShip ship) {
        JsonObject shipStatus = new JsonObject();
        shipStatus.addProperty("hull", ship.getHullStrength());
        shipStatus.addProperty("fullHull", ship.getFullHullStrength());
        shipStatus.addProperty("repairCost", ship.getRepairCost());
        shipStatus.addProperty("fuel", ship.getFuel());
        shipStatus.addProperty("fuelCapacity", ship.getFuelCapacity());
        shipStatus.addProperty("fuelCost", ship.getCostToFillFuelTank());
        return shipStatus;
    }

    private static JsonElement GetEquipment(PlayerShip ship) {
        JsonObject equipment = new JsonObject();
        JsonArray weapons = new JsonArray();
        for (int i = 0; i < ship.getWeaponSlots(); ++i) {
            if (i < ship.getWeapons().size()) {
                weapons.add(new JsonPrimitive(ship.getWeapons().get(i).getName()));
            } else {
                weapons.add(new JsonPrimitive("Empty Slot"));
            }
        }
        equipment.add("weapons", weapons);

        JsonArray shields = new JsonArray();
        for (int i = 0; i < ship.getShieldSlots(); ++i) {
            if (i < ship.getShields().size()) {
                shields.add(new JsonPrimitive(ship.getShields().get(i).getName()));
            } else {
                shields.add(new JsonPrimitive("Empty Slot"));
            }
        }
        equipment.add("shields", shields);

        JsonArray gadgets = new JsonArray();
        for (int i = 0; i < ship.getGadgetSlots(); ++i) {
            if (i < ship.getGadgets().size()) {
                gadgets.add(new JsonPrimitive(ship.getGadgets().get(i).name));
            } else {
                gadgets.add(new JsonPrimitive("Empty Slot"));
            }
        }
        equipment.add("gadgets", gadgets);

        return equipment;
    }

    private static JsonElement GetCargo(PlayerShip ship) {
        JsonObject cargo = new JsonObject();
        for (TradeItem item : TradeItem.values()) {
            cargo.add(item.name, new JsonPrimitive(ship.getCargoCount(item)));
        }
        return cargo;
    }

}
