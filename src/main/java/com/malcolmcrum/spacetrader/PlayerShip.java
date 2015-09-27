package com.malcolmcrum.spacetrader;

import java.util.List;

/**
 * Created by Malcolm on 9/4/2015.
 */
public class PlayerShip extends Ship {
    private static final int UPGRADED_HULL = 50;

    private int tribbles;
    private boolean artifactOnBoard;

    public PlayerShip(ShipType type, Game game) {
        super(type, game);
        tribbles = 0;
        artifactOnBoard = false;
    }

    // I added includeUniqueEquipment because I transfer over unique equipment to the new
    // ship when the player buys a new one, and I don't want the equipment's value being
    // included in the cost of selling their old ship.
    public int getPriceWithoutCargo(boolean forInsurance, boolean includeUniqueEquipment) {
        // Trade-in value is three-quarters the original price
        // OR one-quarter if tribbles are involved and it's not for insurance purposes.
        int tradeinPrice = (type.getPrice() * (tribbles > 0 && !forInsurance? 1 : 3)) / 4;

        int repairCosts = (getFullHullStrength() - type.getHullStrength()) * type.getRepairCost();

        int refillFuel = (type.getFuelTanks() - getFuel()) * type.getCostToFillFuelTank();

        int weaponsPrice = 0;
        for (Weapon weapon : weapons) {
            if (weapon != Weapon.MorgansLaser || includeUniqueEquipment) {
                weaponsPrice += weapon.getSellPrice();
            }
        }

        int shieldsPrice = 0;
        for (Shield shield : shields) {
            if (shield.shieldType != ShieldType.LightningShield || includeUniqueEquipment) {
                shieldsPrice += shield.shieldType.getSellPrice();
            }
        }

        int gadgetsPrice = 0;
        for (Gadget gadget : gadgets) {
            if (gadget != Gadget.FuelCompactor || includeUniqueEquipment) {
                gadgetsPrice += gadget.getSellPrice();
            }
        }

        return tradeinPrice - repairCosts - refillFuel + weaponsPrice + shieldsPrice + gadgetsPrice;
    }

    public int getPrice(boolean forInsurance, boolean includeUniqueEquipment) {
        int curPrice = getPriceWithoutCargo(forInsurance, includeUniqueEquipment);
        for (Cargo c : cargo) {
            curPrice += c.buyingPrice;
        }
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

    public int getTribbles() {
        return tribbles;
    }

    public int getMercenaryDailyCost() {
        int cost = 0;
        for (Crew member : crew) {
            cost += member.getDailyCost();
        }
        return cost;
    }

    public void setTribbles(int tribbles) {
        this.tribbles = tribbles;
    }

    @Override
    int minDamage() {
        return Math.max(1, Difficulty.Impossible.getValue() - game.getDifficulty().getValue());
    }

    /**
     * @return Maximum hull strength
     */
    @Override
    public int getHullStrength() {
        if (game.getScarabStatus() == Scarab.DestroyedUpgradePerformed) {
            return type.getHullStrength() + UPGRADED_HULL;
        } else {
            return type.getHullStrength();
        }
    }

    public int getMercenaryCount() {
        return crew.size() - 1;
    }

    public void removeAllMercenaries() {
        crew.clear();
        crew.add(game.getCaptain());
    }

    public int getRepairCost() {
        return type.getRepairCost();
    }

    public boolean hasArtifactOnBoard() {
        return artifactOnBoard;
    }

    public boolean hasFreeCargoBay() {
        return cargo.size() < type.getCargoBays();
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public List<Shield> getShields() {
        return shields;
    }

    public List<Gadget> getGadgets() {
        return gadgets;
    }

    public List<Crew> getCrew() {
        return crew;
    }

    public int getFreeCargoBays() {
        return getCargoBays() - cargo.size();
    }
}
