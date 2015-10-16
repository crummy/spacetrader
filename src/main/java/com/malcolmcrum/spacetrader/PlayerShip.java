package com.malcolmcrum.spacetrader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Malcolm on 9/4/2015.
 */
public class PlayerShip extends Ship {
    private static final Logger logger = LoggerFactory.getLogger(PlayerShip.class);

    private static final int UPGRADED_HULL = 50;

    private int fuel;
    private int tribbles;
    private boolean artifactOnBoard;
    private final Quests quests;

    // TODO: this should take Captain as an argument. no playerships without captains.
    public PlayerShip(ShipType type, Captain captain, Quests quests, Difficulty difficulty) {
        super(type, difficulty);
        crew.add(captain);
        this.quests = quests;
        tribbles = 0;
        artifactOnBoard = false;
        fuel = type.getFuelTanks();
    }

    // I added includeUniqueEquipment because I transfer over unique equipment to the new
    // ship when the player buys a new one, and I don't want the equipment's value being
    // included in the cost of selling their old ship.
    public int getPriceWithoutCargo(boolean forInsurance, boolean includeUniqueEquipment) {
        // Trade-in value is three-quarters the original price
        // OR one-quarter if tribbles are involved and it's not for insurance purposes.
        final int tradeInPrice = (type.getPrice() * (tribbles > 0 && !forInsurance? 1 : 3)) / 4;

        final int repairCosts = (getFullHullStrength() - type.getHullStrength()) * type.getRepairCost();

        final int refillFuel = (type.getFuelTanks() - getFuel()) * type.getCostToFillFuelTank();

        int weaponsPrice = 0;
        for (Weapon weapon : weapons) {
            if (weapon != Weapon.MorgansLaser || includeUniqueEquipment) {
                weaponsPrice += weapon.getSellPrice();
            }
        }

        int shieldsPrice = 0;
        for (Shield shield : shields) {
            if (shield.getType() != ShieldType.LightningShield || includeUniqueEquipment) {
                shieldsPrice += shield.getType().getSellPrice();
            }
        }

        int gadgetsPrice = 0;
        for (Gadget gadget : gadgets) {
            if (gadget != Gadget.FuelCompactor || includeUniqueEquipment) {
                gadgetsPrice += gadget.getSellPrice();
            }
        }

        return tradeInPrice - repairCosts - refillFuel + weaponsPrice + shieldsPrice + gadgetsPrice;
    }

    public int getPrice(boolean forInsurance, boolean includeUniqueEquipment) {
        int curPrice = getPriceWithoutCargo(forInsurance, includeUniqueEquipment);
        for (Cargo c : cargo) {
            curPrice += c.buyingPrice;
        }
        return curPrice;
    }

    public int getCostToFillFuelTank() {
        return type.getCostToFillFuelTank();
    }

    public int getFuelCapacity() {
        return type.getFuelTanks();
    }

    public int getFuel() {
        return fuel;
    }

    public void addFuel(int unitsOfFuelBought) {
        fuel += unitsOfFuelBought;
        if (fuel > type.getFuelTanks()) {
            logger.error("Ship is overflowing with fuel");
            fuel = type.getFuelTanks();
        }
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
            if (shield.power > shield.getType().getPower()) {
                repairs = shield.power - shield.getType().getPower();
                shield.power = shield.getType().getPower();
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
        return Math.max(1, Difficulty.Impossible.value - difficulty.value);
    }

    /**
     * @return Maximum hull strength
     */
    @Override
    public int getHullStrength() {
        if (quests.scarabUpgradePerformed()) {
            return type.getHullStrength() + UPGRADED_HULL;
        } else {
            return type.getHullStrength();
        }
    }

    public int getMercenaryCount() {
        return crew.size() - 1;
    }

    public void removeAllMercenaries() {
        for (int i = 1; i < crew.size(); ++i) {
            crew.remove(i);
        }
    }

    @Override
    public int getTraderSkill() {
        int maxSkill = crew.stream()
                .mapToInt(Crew::getTraderSkill)
                .max()
                .getAsInt();

        if (quests.isJarekDelivered()) {
            ++maxSkill;
        }
        return applyDifficultyModifierToSkill(maxSkill);
    }

    @Override
    protected int totalCargoBays() {
        int bays = super.totalCargoBays();
        if (quests.isAntidoteOnBoard()) { // carrying the cure
            bays -= 10;
        }

        if (quests.isReactorOnBoard()) { // carrying the melting-down reactor
            bays -= (5 + 10 - (quests.getReactorDays() - 1)/2);
        }
        return bays;
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
}
