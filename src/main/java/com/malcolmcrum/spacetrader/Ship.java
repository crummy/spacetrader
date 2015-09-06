package com.malcolmcrum.spacetrader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * Created by Malcolm on 8/29/2015.
 */
public class Ship {
    private static final Logger logger = LoggerFactory.getLogger(Ship.class);

    private static final int SKILL_BONUS = 3;
    private static final int CLOAK_BONUS = 2;

    // TODO: make below stuff private
    ShipType type;
    List<Cargo> cargo;
    List<Gadget> gadgets;
    List<Weapon> weapons;
    List<Shield> shields;
    List<Crew> crew;

    private int fuel;
    protected int hullStrength;
    final Game game;

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

    public int getFuelCapacity() {
        return type.getFuelTanks();
    }

    public int getCostToFillFuelTank() {
        return type.getCostToFillFuelTank();
    }

    public int getPrice() {
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

    /**
     * @return Current hull strength
     */
    public int getHullStrength() {
        return hullStrength;
    }

    /**
     * @return Maximum hull strength
     */
    public int getFullHullStrength() {
        return type.getHullStrength();
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

    public int weaponStrength() {
        int strength = 0;
        for (Weapon weapon : weapons) {
            strength += weapon.getPower();
        }
        return strength;
    }

    public void takeDamage(int damage) {
        // First, shields get depleted
        for (Shield shield : shields) {
            if (shield.power > damage) {
                shield.power -= damage;
                damage = 0;
                break;
            } else {
                damage -= shield.power;
                shield.power = 0;
            }
        }

        // Remaining damage is taken out of the hull
        if (damage > 0) {
            // Reduce damage by part of engineer skill, but ensure damage is at least 1.
            damage -= GetRandom(getEngineerSkill());
            if (damage <= 0) {
                damage = 1;
            }

            // At least 2 shots on Normal level are needed to destroy the hull
            // (3 on Easy, 4 on Beginner, 1 on Hard or Impossible). For opponents,
            // it is always 2.
            damage = Math.min(damage, (getHullStrength()/minDamage()));

            hullStrength = hullStrength - damage;
            if (hullStrength < 0) {
                hullStrength = 0;
            }
        }
    }

    // Overridden in PlayerShip.
    int minDamage() {
        return 2;
    }

    public int getSizeValue() {
        return type.getSize().getValue();
    }

    public boolean isInvisibleTo(Ship opponent) {
        return hasGadget(Gadget.Cloaking) && getEngineerSkill() > opponent.getEngineerSkill();
    }

    private boolean hasGadget(Gadget gadget) {
        return gadgets.contains(gadget);
    }

    public int reputationForKilling() {
        return 1 + type.ordinal();
    }

    public void setHullStrength(int hullStrength) {
        this.hullStrength = hullStrength;
    }

    public String getName() {
        return type.getName();
    }

    class Cargo {
        TradeItem item;
        int buyingPrice;
        Cargo(TradeItem item, int buyingPrice) {
            this.item = item;
            this.buyingPrice = buyingPrice;
        }
    }

    class Shield {
        ShieldType shieldType;
        int power;
        Shield(ShieldType shieldType) {
            this.shieldType = shieldType;
            this.power = shieldType.getPower();
        }
    }
}
