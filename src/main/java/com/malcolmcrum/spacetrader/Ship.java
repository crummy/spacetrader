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
            shieldsPrice += shield.shieldType.getSellPrice();
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
            curPrice += shield.shieldType.getPrice();
        }
        // Gadgets aren't counted in the price, because they are already taken into account in
        // the skill adjustment of the price.

        int pilotSkill = Skills.GetPilotSkill(crew, gadgets, game.getDifficulty());
        int engineerSkill = Skills.GetEngineerSkill(crew, gadgets, game.getDifficulty());
        int fighterSkill = Skills.GetFighterSkill(crew, gadgets, game.getDifficulty());

        curPrice = curPrice * (2 * pilotSkill + engineerSkill + 3 * fighterSkill) / 60;

        return curPrice;
    }

    public void repair(int repairs) {
        hullStrength += repairs;
        if (hullStrength > type.getHullStrength()) {
            repairs = hullStrength - type.getHullStrength();
            hullStrength = type.getHullStrength();
        } else {
            repairs = 0;
        }

        // Shields are easier to repair
        repairs = 2 * repairs;
        for (Shield shield : shields) {
            shield.power += repairs;
            if (shield.power > shield.shieldType.getPower()) {
                repairs = shield.power - shield.shieldType.getPower();
                shield.power = shield.shieldType.getPower();
            } else {
                repairs = 0;
            }
        }
    }

    public boolean hasReflectiveShield() {
        return shields.stream()
                .anyMatch(shield -> shield.shieldType == ShieldType.ReflectiveShield);
    }

    public boolean hasMilitaryLaser() {
        return weapons.contains(Weapon.MilitaryLaser);
    }

    public int getTribbles() {
        return tribbles;
    }

    public boolean isDestroyed() {
        return hullStrength <= 0;
    }

    public boolean addCargo(TradeItem item, int amount, int buyingPrice) {
        if (filledCargoBays() + amount <= totalCargoBays()) {
            for (int i = 0; i < amount; ++i) {
                cargo.add(new Cargo(item, buyingPrice));
            }
            return true;
        } else {
            return false;
        }
    }

    private int filledCargoBays() {
        return cargo.size();
    }

    private int totalCargoBays() {
        int bays = type.getCargoBays();
        for (Gadget gadget : gadgets) {
            if (gadget == Gadget.CargoBays) {
                bays += 5;
            }
        }
        if (game.getJaporiDiseaseStatus() == Japori.GoToJapori) {
            bays -= 10;
        }
        // since the quest ends when the reactor [?]
        if (game.getReactorStatus() != Reactor.Unavailable
                && game.getReactorStatus() != Reactor.Delivered) {
            bays -= (5 + 10 - (game.getReactorStatus().getValue() - 1)/2);
        }
        return bays;
    }

    private class Cargo {
        TradeItem item;
        int buyingPrice;
        Cargo(TradeItem item, int buyingPrice) {
            this.item = item;
            this.buyingPrice = buyingPrice;
        }
    }

    private class Shield {
        ShieldType shieldType;
        int power;
    }
}
