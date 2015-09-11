package com.malcolmcrum.spacetrader.GameStates;

import com.malcolmcrum.spacetrader.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * InSystem.
 * Created by Malcolm on 9/3/2015.
 */
public class InSystem extends GameState {
    private static final Logger logger = LoggerFactory.getLogger(InSystem.class);

    private SolarSystem system;
    boolean alreadyPaidForNewspaper;

    public InSystem(Game game, SolarSystem system) {
        super(game);
        this.system = system;
    }

    @Override
    public List<Method> getActions() {
        List<Method> actions = new ArrayList<>();
        try {
            actions.add(InSystem.class.getMethod("buyRepairs", int.class));
            actions.add(InSystem.class.getMethod("buyFuel", int.class));
            actions.add(InSystem.class.getMethod("buyShip", ShipType.class));
            actions.add(InSystem.class.getMethod("buyEscapePod"));
            actions.add(InSystem.class.getMethod("buyInsurance"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return actions;
    }

    @Override
    public GameState init() {
        if (game.getBank().getDebt() > 0 && game.getDays() % 5 == 0) {
            game.addAlert(Alert.DebtReminder);
        }

        // Reactor warnings as the thing melts down, and possible death
        if (game.getReactorStatus() == Reactor.NineteenDaysLeft) {
            game.addAlert(Alert.ReactorConsume);
        } else if (game.getReactorStatus() == Reactor.FiveDaysLeft) {
            game.addAlert(Alert.ReactorNoise);
        } else if (game.getReactorStatus() == Reactor.ThreeDaysLeft) {
            game.addAlert(Alert.ReactorSmoke);
        } else if (game.getReactorStatus() == Reactor.OneDayLeft) {
            game.setReactorStatus(Reactor.Unavailable);
            game.addAlert(Alert.ReactorMeltdown);
            return new ShipDestroyed(game, system);
        }

        if (game.getTrackAutoOff() && game.getTrackedSystem() == system) {
            game.setTrackedSystem(null);
        }

        multiplyTribbles();

        autoRepair();
        autoFuel();

        easterEgg();

        game.setCurrentSystem(system);
        game.getGalaxy().shuffleStatuses();
        game.getGalaxy().changeTradeItemQuantities();
        alreadyPaidForNewspaper = false;
        addNewsEvents();
        return this;
    }

    @Override
    public String getName() {
        return "InSystem";
    }

    /**
     * There's an easter egg that can give the player a lightning shield
     */
    private void easterEgg() {
        if (system.getType() == SolarSystem.Name.Og) {
            for (TradeItem item : TradeItem.values()) {
                if (game.getShip().getCargoCount(item) != 1) {
                    return;
                }
            }
            game.addAlert(Alert.Egg);
            if (game.getShip().hasShield(null)) {
                game.getShip().addShield(ShieldType.LightningShield);
                for (TradeItem item : TradeItem.values()) {
                    game.getShip().removeCargo(item, 1);
                }
            }
        }
    }

    /**
     * If autoRepair setting is enabled for player, try to repair the whole ship
     */
    private void autoRepair() {
        if (game.getAutoRepair()) {
            boolean repairsCompleted = buyRepairs();
            if (!repairsCompleted) {
                game.addAlert(Alert.RepairsIncomplete);
            }
        }
    }

    /**
     * Ensures a player is capable of purchasing a ship, and does so.
     * Note that unique equipment may be lost in this transaction
     * @param type
     * @return
     */
    public GameState buyShip(ShipType type) {
        Map<ShipType, Integer> shipsForSale = system.getShipsForSale();
        if (!shipsForSale.containsKey(type)) {
            logger.error("Tried to buy unavailable ship");
        } else if (game.getBank().getDebt() > 0) {
            game.addAlert(Alert.CannotPurchaseShipInDebt);
        } else if (game.getCaptain().getCredits() < shipsForSale.get(type)) {
            game.addAlert(Alert.CannotAffordShip);
        } else if (game.getJarekStatus() == Jarek.OnBoard && type.getCrewQuarters() < 2) {
            game.addAlert(Alert.PassengerNeedsQuarters);
        } else if (game.getWildStatus() == Wild.OnBoard && type.getCrewQuarters() < 2) {
            game.addAlert(Alert.PassengerNeedsQuarters);
        } else if (game.getReactorStatus() != Reactor.Unavailable && game.getReactorStatus() != Reactor.Delivered) {
            game.addAlert(Alert.CannotSellShipWithReactor);
        } else if (cannotTransferUniqueEquipment(game.getShip(), type)) {
            game.addAlert(Alert.CannotTransferUniqueEquipment);
        } else {
            int price = system.getShipsForSale().get(type);
            game.getCaptain().subtractCredits(price);
            PlayerShip newShip = new PlayerShip(type, game);
            transferUniqueEquipment(game.getShip(), newShip);
            game.setShip(newShip);
            if (game.getScarabStatus() == Scarab.DestroyedUpgradePerformed) {
                game.setScarabStatus(Scarab.Unavailable);
            }
            game.addAlert(Alert.ShipPurchased);
        }
        return this;
    }

    private void transferUniqueEquipment(PlayerShip oldShip, PlayerShip newShip) {
        if (oldShip.hasGadget(Gadget.FuelCompactor)) {
            newShip.addGadget(Gadget.FuelCompactor);
        }
        if (oldShip.hasWeapon(Weapon.MorgansLaser)) {
            newShip.addWeapon(Weapon.MorgansLaser);
        }
        if (game.getShip().hasShield(ShieldType.LightningShield)) {
            newShip.addShield(ShieldType.LightningShield);
        }
    }

    private boolean cannotTransferUniqueEquipment(PlayerShip ship, ShipType type) {
        if (game.getShip().hasGadget(Gadget.FuelCompactor) && type.getGadgetSlots() == 0) {
            return false;
        } else if (game.getShip().hasWeapon(Weapon.MorgansLaser) && type.getWeaponSlots() == 0) {
            return false;
        } else if (game.getShip().hasShield(ShieldType.LightningShield) && type.getShieldSlots() == 0) {
            return false;
        }
        return true;
    }

    public GameState buyInsurance() {
        // TODO
        return this;
    }

    public GameState buyEscapePod() {
        // TODO
        return this;
    }

    public boolean buyRepairs() {
        int spend = -(game.getShip().getHullStrength() - game.getShip().getFullHullStrength()) * game.getShip().getRepairCost();
        return buyRepairs(spend);
    }

    /**
     * Buy 'spend' worth of repairs
     * @param spend Amount of credits to spend on repairs
     * @return True if all repairs performed. False if player couldn't pony up enough cash.
     */
    public boolean buyRepairs(int spend) {
        boolean success = false;
        int maxPurchase = (game.getShip().getFullHullStrength() - game.getShip().getHullStrength() * game.getShip().getRepairCost());
        if (spend > maxPurchase) {
            logger.error("Requesting to repair more hull than is damaged!");
            spend = maxPurchase;
            success = false;
        }
        if (spend > game.getCaptain().getCredits()) {
            logger.info("Could not afford requested amount of repairs");
            spend = game.getCaptain().getCredits();
            success = false;
        }

        int repairsBought = spend / game.getShip().getRepairCost();
        game.getShip().repair(repairsBought);
        game.getCaptain().subtractCredits(repairsBought * game.getShip().getRepairCost());
        return success;
    }

    /**
     * If autoFuel setting is enabled for the player, try to fill up the tank
     */
    public void autoFuel() {
        game.getShip().repair(game.getShip().getEngineerSkill());
        if (game.getAutoFuel()) {
            boolean fullTanks = buyFuel();
            if (!fullTanks) {
                game.addAlert(Alert.NoFullTanks);
            }
        }
    }

    /**
     * Buys max amount of fuel
     * @return True if all fuel requested was purchased
     */
    public boolean buyFuel() {
        int spend = -(game.getShip().getFuel() - game.getShip().getFuelCapacity()) * game.getShip().getCostToFillFuelTank();
        return buyFuel(spend);
    }

    /**
     * Buy 'credits' worth of fuel
     * @param spend Amount of credits to spend on fuel
     * @return True if purchased all fuel requested. False if player couldn't pony up enough cash.
     */
    public boolean buyFuel(int spend) {
        boolean success = true;
        int maxPurchase = (game.getShip().getFuelCapacity() - game.getShip().getFuel()) * game.getShip().getCostToFillFuelTank();
        if (spend > maxPurchase) {
            logger.error("Requesting to fill up with more fuel than would fit in the tank!");
            spend = maxPurchase;
            success = false;
        }
        if (spend > game.getCaptain().getCredits()) {
            logger.info("Could not afford requested amount of fuel");
            spend = game.getCaptain().getCredits();
            success = false;
        }

        int unitsOfFuelBought = spend / game.getShip().getCostToFillFuelTank();
        game.getShip().addFuel(unitsOfFuelBought);
        game.getCaptain().subtractCredits(unitsOfFuelBought * game.getShip().getCostToFillFuelTank());
        return success;
    }

    private void multiplyTribbles() {
        int tribbles = game.getShip().getTribbles();
        int previousTribbles = tribbles;
        boolean foodOnBoard = false;
        if (tribbles > 0 && game.getReactorStatus() != Reactor.Unavailable && game.getReactorStatus() != Reactor.Delivered) {
            tribbles /= 2;
            if (tribbles < 10) {
                game.addAlert(Alert.TribblesAllIrradiated);
                game.getShip().setTribbles(0);
            } else {
                game.addAlert(Alert.TribblesIrradiated);
                game.getShip().setTribbles(tribbles);
            }
        } else if (tribbles > 0 && game.getShip().getCargoCount(TradeItem.Narcotics) > 0) {
            tribbles = 1 + GetRandom(3);
            int deadTribbles = Math.min(1 + GetRandom(3), game.getShip().getCargoCount(TradeItem.Narcotics));
            game.getShip().removeCargo(TradeItem.Narcotics, deadTribbles);
            game.getShip().addCargo(TradeItem.Furs, deadTribbles, 0);
            game.addAlert(Alert.TribblesAteNarcotics);
            game.getShip().setTribbles(tribbles);
        } else if (tribbles > 0 && game.getShip().getCargoCount(TradeItem.Food) > 0) {
            tribbles += 100 + GetRandom(game.getShip().getCargoCount(TradeItem.Food) * 100);
            int foodLeft = GetRandom(game.getShip().getCargoCount(TradeItem.Food));
            int foodEaten = -(foodLeft - game.getShip().getCargoCount(TradeItem.Food));
            game.getShip().removeCargo(TradeItem.Food, foodEaten);
            game.addAlert(Alert.TribblesAteFood);
            foodOnBoard = true;
            game.getShip().setTribbles(tribbles);
        }

        if (tribbles > 0 && tribbles < Game.MAX_TRIBBLES) {
            tribbles += 1 + GetRandom(Math.max(1, tribbles >> (foodOnBoard ? 0 : 1)));
        }

        if (tribbles > Game.MAX_TRIBBLES) {
            tribbles = Game.MAX_TRIBBLES;
        }

        if ((previousTribbles < 100 && tribbles >= 100)
                || (previousTribbles < 1000 && tribbles >= 1000)
                || (previousTribbles < 10000 && tribbles >= 10000)
                || (previousTribbles < 50000 && tribbles >= 50000)) {
            game.addAlert(Alert.TribblesOnBoard);
        }

        // TribbleMessage = False?
    }

    GameState warpTo(SolarSystem destination, boolean viaSingularity) {
        // If wild is aboard, make sure ship is armed!
        if (game.getWildStatus() == Wild.OnBoard) {
            game.addAlert(Alert.WildWontGo);
            return this;
            // TODO: Allow player to kick Wild out if they want to leave anyway
        }

        // Check for large debt
        if (game.getBank().veryLargeDebt()) {
            game.addAlert(Alert.DebtTooLargeForTravel);
            return this;
        }

        // Check for enough money to pay mercenaries
        int mercenaryCost = game.getShip().getMercenaryDailyCost();
        if (mercenaryCost > game.getCaptain().getCredits()) {
            game.addAlert(Alert.MustPayMercenaries);
            return this;
        }

        // Check for enough money to pay for insurance
        int insuranceCost = game.getShip().getInsuranceCost();
        if (game.getBank().hasInsurance()
                && (insuranceCost + mercenaryCost > game.getCaptain().getCredits())) {
            game.addAlert(Alert.CantAffordInsuranceBill);
            return this;
        }

        // Check for enough money to pay wormhole tax
        int wormholeCost = wormholeTax(destination);
        if (insuranceCost + mercenaryCost + wormholeCost > game.getCaptain().getCredits()) {
            game.addAlert(Alert.CantAffordWormholeTax);
            return this;
        }

        if (!viaSingularity) {
            game.getCaptain().subtractCredits(wormholeCost);
            game.getCaptain().subtractCredits(mercenaryCost);
            game.getCaptain().subtractCredits(insuranceCost);
        }

        return new Transit(game, destination, viaSingularity);
    }

    private int wormholeTax(SolarSystem destination) {
        if (game.getGalaxy().wormholeExistsBetween(system, destination)) {
            return game.getShip().getCostToFillFuelTank() * 25;
        } else {
            return 0;
        }
    }

    private void addNewsEvents() {
        Captain captain = game.getCaptain();
        News news = game.getNews();
        SolarSystem.SpecialEvent systemEvent = system.getSpecialEvent();
        if (systemEvent == SolarSystem.SpecialEvent.MonsterKilled && game.getMonsterStatus() == Monster.Destroyed) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.MonsterKilled);
        } else if (systemEvent == SolarSystem.SpecialEvent.Dragonfly) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.Dragonfly);
        } else if (systemEvent == SolarSystem.SpecialEvent.ScarabStolen) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.ScarabStolen);
        } else if (systemEvent == SolarSystem.SpecialEvent.ScarabDestroyed && game.getScarabStatus() == Scarab.DestroyedUpgradeAvailable) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.ScarabDestroyed);
        } else if (systemEvent == SolarSystem.SpecialEvent.FlyBaratas && game.getDragonflyStatus() == Dragonfly.GoToBaratas) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.FlyBaratas);
        } else if (systemEvent == SolarSystem.SpecialEvent.FlyMelina && game.getDragonflyStatus() == Dragonfly.GoToMelina) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.FlyMelina);
        } else if (systemEvent == SolarSystem.SpecialEvent.FlyRegulas && game.getDragonflyStatus() == Dragonfly.GoToRegulas) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.FlyRegulas);
        } else if (systemEvent == SolarSystem.SpecialEvent.DragonflyDestroyed && game.getDragonflyStatus() == Dragonfly.Destroyed) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.DragonflyDestroyed);
        } else if (systemEvent == SolarSystem.SpecialEvent.MedicineDelivery && captain.getJaporiDiseaseStatus() == 1) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.MedicineDelivery);
        } else if (systemEvent == SolarSystem.SpecialEvent.ArtifactDelivery && captain.getArtifactOnBoard()) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.ArtifactDelivery);
        } else if (systemEvent == SolarSystem.SpecialEvent.JaporiDisease && captain.getJaporiDiseaseStatus() == 0) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.JaporiDisease);
        } else if (systemEvent == SolarSystem.SpecialEvent.JarekGetsOut && captain.getJarekStatus() == 1) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.JarekGetsOut);
        } else if (systemEvent == SolarSystem.SpecialEvent.WildGetsOut) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.WildGetsOut);
        } else if (systemEvent == SolarSystem.SpecialEvent.GemulonRescued && captain.getInvasionStatus() > 0 && captain.getInvasionStatus() < 8) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.GemulonRescued);
        } else if (systemEvent == SolarSystem.SpecialEvent.AlienInvasion) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.AlienInvasion);
        } else if (systemEvent == SolarSystem.SpecialEvent.DisasterAverted && captain.getExperimentStatus() > 0 && captain.getExperimentStatus() < 12) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.DisasterAverted);
        } else if (systemEvent == SolarSystem.SpecialEvent.ExperimentFailed) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.ExperimentFailed);
        }
        system.setVisited();
    }

    private boolean canAffordNewspaper() {
        if (alreadyPaidForNewspaper) {
            return true;
        } else return game.getCaptain().getAvailableCash() >= game.getNews().getPrice();
    }

    public SolarSystem getSystem() {
        return system;
    }

    public PlayerShip getPlayerShip() {
        return game.getShip();
    }
}