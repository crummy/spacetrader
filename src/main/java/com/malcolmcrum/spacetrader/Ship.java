package com.malcolmcrum.spacetrader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Malcolm on 8/29/2015.
 */
public class Ship {

    ShipType type;
    List<Cargo> cargo;
    List<Gadget> gadgets;
    List<Weapon> weapons;
    List<Shield> shields;
    List<Integer> shieldStrength;
    List<Crew> crew;
    private int fuel;
    private int hullStrength;
    private int tribbles;
    private final Game game;

    public Ship(ShipType type, Game game) {
        this.game = game;
        this.type = type;
        cargo = new ArrayList<>(type.getCargoBays());
        weapons = new ArrayList<>(type.getWeaponSlots());
        gadgets = new ArrayList<>(type.getGadgetSlots());
        shields = new ArrayList<>(type.getShieldSlots());
        shieldStrength = new ArrayList<>(type.getShieldSlots());
        crew = new ArrayList<>(type.getCrewQuarters());
        fuel = type.getFuelTanks();
        hullStrength = type.getHullStrength();
        tribbles = 0;
    }

    public void addCrew(Crew member) {
        crew.add(member);
    }

    public void addWeapon(Weapon weapon) {
        weapons.add(weapon);
    }

    public int getFuel() {
        return fuel;
    }

    public int getPriceWithoutCargo(boolean forInsurance) {
        // Trade-in value is three-quarters the original price
        // OR one-quarter if tribbles are involved and it's not for insurance purposes.
        int tradeinPrice = (type.getPrice() * (tribbles > 0 && !forInsurance? 1 : 3)) / 4;

        int repairCosts = (hullStrength - type.getHullStrength()) * type.getRepairCost();

        int refillFuel = (type.getFuelTanks() - fuel) * type.getCostToFillFuelTank();

        int weaponsPrice = 0;
        for (Weapon weapon : weapons) {
            weaponsPrice += weapon.getSellPrice();
        }

        int shieldsPrice = 0;
        for (Shield shield : shields) {
            shieldsPrice += shield.getSellPrice();
        }

        int gadgetsPrice = 0;
        for (Gadget gadget : gadgets) {
            gadgetsPrice += gadget.getSellPrice();
        }

        return tradeinPrice - repairCosts - refillFuel + weaponsPrice + shieldsPrice + gadgetsPrice;
    }

    public int getPrice(boolean forInsurance) {
        int curPrice = getPriceWithoutCargo(forInsurance);
        for (Cargo c : cargo) {
            curPrice += c.buyingPrice;
        }
        return curPrice;
    }

    public int getEnemyPrice() {
        int curPrice = type.getPrice();
        for (Weapon weapon : weapons) {
            curPrice += weapon.getPrice();
        }
        for (Shield shield : shields) {
            curPrice += shield.getPrice();
        }
        // Gadgets aren't counted in the price, because they are already taken into account in
        // the skill adjustment of the price.

        int pilotSkill = Skills.GetPilotSkill(crew, gadgets, game.getDifficulty());
        int engineerSkill = Skills.GetEngineerSkill(crew, gadgets, game.getDifficulty());
        int fighterSkill = Skills.GetFighterSkill(crew, gadgets, game.getDifficulty());

        curPrice = curPrice * (2 * pilotSkill + engineerSkill + 3 * fighterSkill) / 60;

        return curPrice;
    }

    private class Cargo {
        TradeItem item;
        int buyingPrice;
    }
}
