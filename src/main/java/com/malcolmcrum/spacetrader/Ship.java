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

    protected ShipType type;
    protected List<Cargo> cargo;
    protected List<Gadget> gadgets;
    protected List<Weapon> weapons;
    protected List<Shield> shields;
    protected List<Crew> crew;

    protected int hullStrength;
    protected final Difficulty difficulty;

    public Ship(ShipType type, Difficulty difficulty) {
        this.difficulty = difficulty;
        this.type = type;
        cargo = new ArrayList<>(type.getCargoBays());
        weapons = new ArrayList<>(type.getWeaponSlots());
        gadgets = new ArrayList<>(type.getGadgetSlots());
        shields = new ArrayList<>(type.getShieldSlots());
        crew = new ArrayList<>(type.getCrewQuarters());
        hullStrength = type.getHullStrength();
    }

    public void addCrew(Crew member) {
        if (crew.contains(member)) {
            logger.warn("Tried to add a crewmember that already exists, named " + member.getName());
        } else {
            crew.add(member);
        }
    }

    public void addWeapon(Weapon weapon) {
        weapons.add(weapon);
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
        if (amount == 0) {
            logger.info("Trying to remove 0 cargo??");
        } else if (amount > getCargoCount(item)) {
            logger.error("Trying to remove more cargo than we have!");
            amount = getCargoCount(item);
        }

        for (Iterator<Cargo> iterator = cargo.iterator(); iterator.hasNext() && amount > 0; ) {
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

    protected int totalCargoBays() {
        int bays = type.getCargoBays();
        for (Gadget gadget : gadgets) {
            if (gadget == Gadget.CargoBays) {
                bays += 5;
            }
        }
        return bays;
    }

    public int getTraderSkill() {
        int maxSkill = crew.stream()
                .mapToInt(Crew::getTraderSkill)
                .max()
                .getAsInt();

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

    protected int applyDifficultyModifierToSkill(int level) {
        if (difficulty == Difficulty.Beginner || difficulty == Difficulty.Easy) {
            return level + 1;
        } else if (difficulty == Difficulty.Impossible) {
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

    public void addShield(ShieldType shieldType, int power) {
        if (shields.size() >= type.getShieldSlots()) {
            logger.error("Trying to add shields to a full ship!");
        } else {
            shields.add(new Shield(shieldType, power));
        }
    }

    public void addShield(ShieldType shieldType) {
        addShield(shieldType, shieldType.getPower());
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
            if (shield.power >= damage) {
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
            damage = Math.min(damage, (getFullHullStrength()/minDamage()));

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

    public boolean hasGadget(Gadget gadget) {
        return gadgets.contains(gadget);
    }

    public int reputationGainForKilling() {
        return 1 + type.ordinal();
    }

    public void setHullStrength(int hullStrength) {
        this.hullStrength = hullStrength;
    }

    public String getName() {
        return type.getName();
    }

    public ShipType getType() {
        return type;
    }

    public void addGadget(Gadget gadget) {
        if (gadgets.size() > type.getGadgetSlots()) {
            logger.error("Tried to add more gadgets than we have slots!");
            return;
        }
        gadgets.add(gadget);
    }

    public int getCargoBays() {
        int bays = type.getCargoBays();
        for (Gadget g : gadgets) {
            if (g == Gadget.CargoBays) {
                bays += 5;
            }
        }
        return bays;
    }

    public boolean hasWeapon(Weapon weapon) {
        return weapons.contains(weapon);
    }

    public boolean hasShields() {
        for (Shield shield : shields) {
            if (shield.power > 0) {
                return true;
            }
        }
        return false;
    }

    public int getCrewQuarters() {
        return type.getCrewQuarters();
    }

    public int getGadgetSlots() {
        return type.getGadgetSlots();
    }

    public int getWeaponSlots() {
        return type.getWeaponSlots();
    }

    public int getShieldSlots() {
        return type.getShieldSlots();
    }

    public int getFreeCargoBays() {
        return getCargoBays() - cargo.size();
    }

    public class Cargo {
        final TradeItem item;
        final int buyingPrice;
        Cargo(TradeItem item, int buyingPrice) {
            this.item = item;
            this.buyingPrice = buyingPrice;
        }

    }
    public class Shield {
        private final ShieldType shieldType;
        int power;

        Shield(ShieldType shieldType, int power) {
            this.shieldType = shieldType;
            this.power = power;
        }
        public String getName() {
            return shieldType.getName();
        }

        public ShieldType getType() {
            return shieldType;
        }

    }
}
