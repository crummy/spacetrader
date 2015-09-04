package com.malcolmcrum.spacetrader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Malcolm on 8/29/2015.
 */
public class Ship {
    private static final Logger logger = LoggerFactory.getLogger(Ship.class);


    private static final int SKILL_BONUS = 3;
    private static final int CLOAK_BONUS = 2;

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
    private boolean hasEscapePod;
    private boolean hasInsurance;
    private int daysWithoutClaim;
    private int hull;

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
        hasEscapePod = false;
        hasInsurance = false;
        daysWithoutClaim = 0;
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

        int pilotSkill = getPilotSkill();
        int engineerSkill = getEngineerSkill();
        int fighterSkill = getFighterSkill();

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

    public void removeCargo(TradeItem item, int amount) {
        if (amount > getCargoCount(item)) {
            logger.error("Trying to remove more cargo than we have!");
        }

        for (Iterator<Cargo> iterator = cargo.iterator(); iterator.hasNext() && amount > 0;) {
            Cargo c = iterator.next();
            if (c.item == item) {
                iterator.remove();
                --amount;
            }
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

    public void setEscapePod(boolean escapePod) {
        this.hasEscapePod = escapePod;
    }

    public int getTraderSkill() {
        int maxSkill = crew.stream()
                .mapToInt(Crew::getTraderSkill)
                .max()
                .getAsInt();

        if (game.getJarekStatus() == Jarek.Delivered) {
            ++maxSkill;
        }
        return applyDifficultyModifierToSkill(maxSkill);
    }

    public int getFighterSkill() {
        int maxSkill = crew.stream()
                .mapToInt(Crew::getFighterSkill)
                .max()
                .getAsInt();

        if (gadgets.contains(Gadget.Targeting)) {
            maxSkill += SKILL_BONUS;
        }

        return applyDifficultyModifierToSkill(maxSkill);
    }

    public int getEngineerSkill() {
        int maxSkill = crew.stream()
                .mapToInt(Crew::getEngineerSkill)
                .max()
                .getAsInt();

        if (gadgets.contains(Gadget.Repairs)) {
            maxSkill += SKILL_BONUS;
        }

        return applyDifficultyModifierToSkill(maxSkill);
    }

    public int getPilotSkill() {
        int maxSkill = crew.stream()
                .mapToInt(Crew::getPilotSkill)
                .max()
                .getAsInt();

        if (gadgets.contains(Gadget.Navigation)) {
            maxSkill += SKILL_BONUS;
        }
        if (gadgets.contains(Gadget.Cloaking)) {
            maxSkill += CLOAK_BONUS;
        }

        return applyDifficultyModifierToSkill(maxSkill);
    }

    private int applyDifficultyModifierToSkill(int level) {
        Difficulty d = game.getDifficulty();
        if (d == Difficulty.Beginner || d == Difficulty.Easy) {
            return level + 1;
        } else if (d == Difficulty.Impossible) {
            return Math.max(1, level - 1);
        } else {
            return level;
        }
    }

    int getMercenaryDailyCost() {
        int cost = 0;
        for (Crew member : crew) {
            cost += member.getDailyCost();
        }
        return cost;
    }

    int getInsuranceCost() {
        if (hasInsurance) {
            return 0;
        } else {
            return Math.max(1, (((getPriceWithoutCargo(true) * 5) / 2000) *
                    (100-Math.min(daysWithoutClaim, 90)) / 100));
        }
    }

    public boolean isInsured() {
        return hasInsurance;
    }

    public boolean hasEscapePod() {
        return hasEscapePod;
    }

    public void setTribbles(int tribbles) {
        this.tribbles = tribbles;
    }

    public int getCargoCount(TradeItem item) {
        int count = 0;
        for (Cargo c : cargo) {
            if (c.item == item) {
                ++count;
            }
        }
        return count;
    }

    public void addFuel(int unitsOfFuelBought) {
        fuel += unitsOfFuelBought;
        if (fuel > type.getFuelTanks()) {
            logger.error("Ship is overflowing with fuel");
            fuel = type.getFuelTanks();
        }
    }

    public int getHull() {
        return hull;
    }

    /**
     * Checks ship for at least one item of the requested type. If shield parameter
     * is null, checks ship for at least one empty slot.
     * @param shieldType Shield type to check for, or null to check for empty slot
     * @return True if there is a shield of the requested type on the ship
     */
    public boolean hasShield(ShieldType shieldType) {
        if (shieldType == null && shields.size() < type.getShieldSlots()) {
            return true;
        }
        for (Shield shield : shields) {
            if (shield.shieldType == shieldType) {
                return true;
            }
        }
        return false;
    }

    public void addShield(ShieldType shieldType) {
        if (shields.size() >= type.getShieldSlots()) {
            logger.error("Trying to add shields to a full ship!");
        } else {
            shields.add(new Shield(shieldType));
        }
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
        Shield(ShieldType shieldType) {
            this.shieldType = shieldType;
            this.power = shieldType.getPower();
        }
    }
}
