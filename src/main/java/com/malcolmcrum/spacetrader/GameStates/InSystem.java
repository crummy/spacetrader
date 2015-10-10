package com.malcolmcrum.spacetrader.GameStates;

import com.google.gson.JsonElement;
import com.malcolmcrum.spacetrader.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * InSystem.
 * Created by Malcolm on 9/3/2015.
 */
public class InSystem extends GameState {
    private static final Logger logger = LoggerFactory.getLogger(InSystem.class);

    private SolarSystem system;
    private Quests quests;
    private Captain captain;
    private boolean alreadyPaidForNewspaper;
    private final int ESCAPE_POD_PRICE = 2000;

    public InSystem(Game game, SolarSystem system) {
        super(game);
        this.quests = game.getQuests();
        this.captain = game.getCaptain();
        this.system = system;

        system.setVisited();
    }

    public Map<String, Integer> getEquipmentForSale() {
        Map<String, Integer> equipment = new HashMap<>();
        for (Weapon weapon : Weapon.values()) {
            if (!weapon.getTechLevelRequired().isBeyond(system.getTechLevel())) {
                equipment.put(weapon.getName(), equipmentPrice(weapon.getTechLevelRequired(), weapon.getPrice()));
            }
        }
        for (Gadget gadget : Gadget.values()) {
            if (!gadget.techLevel.isBeyond(system.getTechLevel())) {
                equipment.put(gadget.name, equipmentPrice(gadget.techLevel, gadget.getPrice()));
            }
        }
        for (ShieldType shield : ShieldType.values()) {
            if (!shield.getTechLevelRequired().isBeyond(system.getTechLevel())) {
                equipment.put(shield.getName(), equipmentPrice(shield.getTechLevelRequired(), shield.getPrice()));
            }
        }
        return equipment;
    }

    private int equipmentPrice(TechLevel techLevel, int price) {
        if (techLevel.isBeyond(system.getTechLevel())) {
            return 0;
        } else {
            return (price * (100 - game.getCaptain().getTraderSkill())) / 100;
        }
    }

    public List<String> getNews() {
        return game.getNews().getNewspaper();
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
            actions.add(InSystem.class.getMethod("cancelInsurance"));
            actions.add(InSystem.class.getMethod("buyNewspaper"));
            actions.add(InSystem.class.getMethod("hireCrewmember"));
            actions.add(InSystem.class.getMethod("fireCrewmember", String.class));
            actions.add(InSystem.class.getMethod("buyTradeItem", TradeItem.class, int.class));
            actions.add(InSystem.class.getMethod("sellTradeItem", TradeItem.class, int.class));
            actions.add(InSystem.class.getMethod("warpTo", SolarSystem.class, boolean.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return actions;
    }

    @Override
    public GameState init() {
        if (captain.bank.getDebt() > 0 && game.getDays() % 5 == 0) {
            game.addAlert(Alert.DebtReminder);
        }

        // Reactor warnings as the thing melts down, and possible death
        if (quests.reactorDaysLeft() == 19) {
            game.addAlert(Alert.ReactorConsume);
        } else if (quests.reactorDaysLeft() == 5) {
            game.addAlert(Alert.ReactorNoise);
        } else if (quests.reactorDaysLeft() == 3) {
            game.addAlert(Alert.ReactorSmoke);
        } else if (quests.reactorDaysLeft() == 1) {
            quests.reactorDestroyed();
            game.addAlert(Alert.ReactorMeltdown);
            return new ShipDestroyed(game, system);
        }

        multiplyTribbles();

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

    public GameState buyTradeItem(TradeItem item, int quantity) {
        if (system.getMarket().getBuyPrice(item) == null) {
            logger.error("Trying to buy an item with no buy price!");
            return this;
        } else if (system.getMarket().getQuantity(item) < quantity) {
            logger.error("Trying to buy more items than are for sale!");
            return this;
        } else {
            system.getMarket().buyItem(item, quantity);
            return this;
        }
    }

    public GameState sellTradeItem(TradeItem item, int quantity) {
        if (system.getMarket().getSellPrice(item) == null) {
            logger.error("Trying to sell an item with no sell price!");
            return this;
        } else if (game.getShip().getCargoCount(item) < quantity) {
            logger.error("Trying to sell more items than ship has!");
            return this;
        } else {
            system.getMarket().sellItem(item, quantity);
            return this;
        }
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
     * Ensures a player is capable of purchasing a ship, and does so.
     * Note that unique equipment may be lost in this transaction
     * @param type
     * @return Updated GameState, with alerts if necessary
     */
    public GameState buyShip(ShipType type) {
        Map<ShipType, Integer> shipsForSale = system.getShipsForSale();
        if (!shipsForSale.containsKey(type)) {
            logger.error("Tried to buy unavailable ship");
        } else if (captain.bank.getDebt() > 0) {
            game.addAlert(Alert.CannotPurchaseShipInDebt);
        } else if (captain.getCredits() < shipsForSale.get(type)) {
            game.addAlert(Alert.CannotAffordShip);
        } else if (quests.isJarekOnBoard() && type.getCrewQuarters() < 2) {
            game.addAlert(Alert.PassengerNeedsQuarters);
        } else if (quests.isWildOnBoard() && type.getCrewQuarters() < 2) {
            game.addAlert(Alert.PassengerNeedsQuarters);
        } else if (quests.isReactorOnBoard()) {
            game.addAlert(Alert.CannotSellShipWithReactor);
        } else if (!canTransferUniqueEquipment(game.getShip(), type)) {
            game.addAlert(Alert.CannotTransferUniqueEquipment);
        } else if (game.getShip().getCrew().size() > type.getCrewQuarters()) {
            game.addAlert(Alert.TooManyCrewMembers);
        } else {
            int price = system.getShipsForSale().get(type);
            game.getCaptain().subtractCredits(price);
            PlayerShip newShip = new PlayerShip(type, quests, game.getDifficulty());
            transferUniqueEquipment(game.getShip(), newShip);
            transferCrew(game.getShip(), newShip);
            game.setShip(newShip);
            if (quests.scarabUpgradePerformed()) {
                quests.setScarabUnavailable();
            }
            game.addAlert(Alert.ShipPurchased);
        }
        return this;
    }

    private void transferCrew(PlayerShip ship, PlayerShip newShip) {
        for (int i = 0; i < ship.getCrew().size(); ++i) {
            newShip.addCrew(ship.getCrew().get(i));
        }
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

    private boolean canTransferUniqueEquipment(PlayerShip ship, ShipType type) {
        if (ship.hasGadget(Gadget.FuelCompactor) && type.getGadgetSlots() == 0) {
            return false;
        } else if (ship.hasWeapon(Weapon.MorgansLaser) && type.getWeaponSlots() == 0) {
            return false;
        } else if (ship.hasShield(ShieldType.LightningShield) && type.getShieldSlots() == 0) {
            return false;
        }
        return true;
    }

    public GameState buyInsurance() {
        if (!game.getCaptain().hasEscapePod()) {
            game.addAlert(Alert.NoEscapePod);
        } else {
            captain.bank.setInsurance(true);
        }
        return this;
    }

    public GameState cancelInsurance() {
        captain.bank.cancelInsurance();
        return this;
    }

    public GameState buyEscapePod() {
        if (game.getCaptain().getCredits() >= ESCAPE_POD_PRICE) {
            game.getCaptain().subtractCredits(ESCAPE_POD_PRICE);
            game.getCaptain().setEscapePod(true);
        } else {
            game.addAlert(Alert.CannotAffordEscapePod);
        }
        return this;
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
        if (tribbles > 0 && quests.isReactorOnBoard()) {
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

    public GameState warpTo(SolarSystem destination, boolean viaSingularity) {
        if (game.getGalaxy().distanceBetween(system, destination) > game.getShip().getFuel()) {
            game.addAlert(Alert.WarpDestinationOutOfRange);
            return this;
        }

        // If wild is aboard, make sure ship is armed!
        if (quests.isWildOnBoard()) {
            game.addAlert(Alert.WildWontGo);
            return this;
        }

        // Check for large debt
        if (captain.bank.veryLargeDebt()) {
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
        int insuranceCost = captain.bank.getInsuranceCost();
        if (captain.bank.hasInsurance()
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

        game.getNews().resetNewsEvents();
        if (!viaSingularity) {
            game.getCaptain().subtractCredits(wormholeCost);
            game.getCaptain().subtractCredits(mercenaryCost);
            game.getCaptain().subtractCredits(insuranceCost);
            game.dayPasses();
        }

        system.getMarket().resetTradeCountdown();

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
        News news = game.getNews();
        SolarSystem.SpecialEvent systemEvent = system.getSpecialEvent();
        if (systemEvent == SolarSystem.SpecialEvent.MonsterKilled && quests.isMonsterKilled()) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.MonsterKilled);
        } else if (systemEvent == SolarSystem.SpecialEvent.Dragonfly) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.Dragonfly);
        } else if (systemEvent == SolarSystem.SpecialEvent.ScarabStolen) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.ScarabStolen);
        } else if (systemEvent == SolarSystem.SpecialEvent.ScarabDestroyed && quests.scarabUpgradeAvailable()) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.ScarabDestroyed);
        } else if (systemEvent == SolarSystem.SpecialEvent.FlyBaratas && quests.isDragonflyAt(SolarSystem.Name.Baratas)) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.FlyBaratas);
        } else if (systemEvent == SolarSystem.SpecialEvent.FlyMelina && quests.isDragonflyAt(SolarSystem.Name.Melina)) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.FlyMelina);
        } else if (systemEvent == SolarSystem.SpecialEvent.FlyRegulas && quests.isDragonflyAt(SolarSystem.Name.Regulas)) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.FlyRegulas);
        } else if (systemEvent == SolarSystem.SpecialEvent.DragonflyDestroyed && quests.isDragonflyDestroyed()) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.DragonflyDestroyed);
        } else if (systemEvent == SolarSystem.SpecialEvent.MedicineDelivery && quests.isAntidoteOnBoard()) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.MedicineDelivery);
        } else if (systemEvent == SolarSystem.SpecialEvent.ArtifactDelivery && quests.isArtifactOnBoard()) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.ArtifactDelivery);
        } else if (systemEvent == SolarSystem.SpecialEvent.JaporiDisease && !quests.isAntidoteOnBoard()) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.JaporiDisease);
        } else if (systemEvent == SolarSystem.SpecialEvent.JarekGetsOut && quests.isJarekOnBoard()) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.JarekGetsOut);
        } else if (systemEvent == SolarSystem.SpecialEvent.WildGetsOut) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.WildGetsOut);
        } else if (systemEvent == SolarSystem.SpecialEvent.GemulonRescued && quests.isInvasionComing()) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.GemulonRescued);
        } else if (systemEvent == SolarSystem.SpecialEvent.AlienInvasion) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.AlienInvasion);
        } else if (systemEvent == SolarSystem.SpecialEvent.DisasterAverted && quests.isExperimentComing()) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.DisasterAverted);
        } else if (systemEvent == SolarSystem.SpecialEvent.ExperimentFailed) {
            news.addSpecialEvent(SolarSystem.SpecialEvent.ExperimentFailed);
        }
        system.setVisited();
    }

    // When the player buys a newspaper and the gamestate is serialized, the news is included.
    public GameState buyNewspaper() {
        if (canAffordNewspaper()) {
            alreadyPaidForNewspaper = true;
        }
        return this;
    }

    private boolean canAffordNewspaper() {
        if (alreadyPaidForNewspaper) {
            return true;
        } else return captain.bank.getAvailableCash() >= game.getNews().getPrice();
    }

    public SolarSystem getSystem() {
        return system;
    }

    public PlayerShip getPlayerShip() {
        return game.getShip();
    }

    public boolean alreadyBoughtNewspaper() {
        return alreadyPaidForNewspaper;
    }

    public GameState fireCrewmember(String name) {
        Optional<Crew> firedCrew = game.getShip().getCrew()
                .stream()
                .filter(s -> s.getName().equals(name))
                .findFirst();
        if (!firedCrew.isPresent()) {
            logger.error("Tried to fire someone that isn't on board");
        } else if (firedCrew.get() == game.getCaptain()) {
            game.addAlert(Alert.CannotFireYourself);
        } else {
            game.getShip().getCrew().remove(firedCrew.get());
        }
        return this;
    }

    public GameState hireCrewmember() {
        if (!system.hasMercenary()) {
            game.addAlert(Alert.NoMercenaryForHire);
        } else if (game.getShip().getCrew().size() == game.getShip().getCrewQuarters()) {
            game.addAlert(Alert.NoRoomForMercenary);
        } else {
            game.getShip().addCrew(system.getMercenary());
        }
        return this;
    }

    public boolean escapePodForSale() {
        // If a system can build a Flea, they can build an escape pod.
        return !system.getTechLevel().isBefore(ShipType.Flea.getMinTechLevel());
    }

    public int getEscapePodPrice() {
        return ESCAPE_POD_PRICE;
    }
}
