package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 9/4/2015.
 */
public class PlayerShip extends Ship {
    private static final int UPGRADED_HULL = 50;

    private int tribbles;
    private boolean hasEscapePod;
    private boolean hasInsurance;
    private int daysWithoutClaim;

    public PlayerShip(ShipType type, Game game) {
        super(type, game);
        tribbles = 0;
        hasEscapePod = false;
        hasInsurance = false;
        daysWithoutClaim = 0;
    }

    public int getPriceWithoutCargo(boolean forInsurance) {
        // Trade-in value is three-quarters the original price
        // OR one-quarter if tribbles are involved and it's not for insurance purposes.
        int tradeinPrice = (type.getPrice() * (tribbles > 0 && !forInsurance? 1 : 3)) / 4;

        int repairCosts = (getFullHullStrength() - type.getHullStrength()) * type.getRepairCost();

        int refillFuel = (type.getFuelTanks() - getFuel()) * type.getCostToFillFuelTank();

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

    public void setEscapePod(boolean escapePod) {
        this.hasEscapePod = escapePod;
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
}
