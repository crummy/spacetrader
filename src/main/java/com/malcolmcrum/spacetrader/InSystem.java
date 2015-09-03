package com.malcolmcrum.spacetrader;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * Created by Malcolm on 9/3/2015.
 */
public class InSystem extends GameState {
    SolarSystem system;
    boolean alreadyPaidForNewspaper;

    public InSystem(Game game, SolarSystem system) {
        super(game);
        this.system = system;
    }

    @Override
    GameState init() {
        if (game.getCaptain().getDebt() > 0 && game.getRemindLoans() && game.getDays() % 5 == 0) {
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

        game.setCurrentSystem(system);
        game.getGalaxy().shuffleStatuses();
        game.getGalaxy().changeTradeItemQuantities();
        system.getMarket().determinePrices();
        alreadyPaidForNewspaper = false;
        addNewsEvents();
        return this;
    }

    private void autoRepair() {
        if (game.getAutoRepair()) {
            boolean repairsCompleted = game.getCurrentShip().repairFull(); // TODO: don't do this in ship. ship should not be modifying player money
            if (!repairsCompleted) {
                game.addAlert(Alert.RepairsIncomplete);
            }
        }
    }

    private void autoFuel() {
        game.getCurrentShip().repair(game.getCurrentShip().getEngineerSkill());
        if (game.getAutoFuel()) {
            boolean fullTanks = game.getCurrentShip().fillErUp(); // TODO: same as above
            if (!fullTanks) {
                game.addAlert(Alert.NoFullTanks);
            }
        }
    }

    private void multiplyTribbles() {
        int tribbles = game.getCurrentShip().getTribbles();
        int previousTribbles = tribbles;
        if (tribbles > 0 && game.getReactorStatus() != Reactor.Unavailable && game.getReactorStatus() != Reactor.Delivered) {
            tribbles /= 2;
            if (tribbles < 10) {
                game.addAlert(Alert.TribblesAllIrradiated);
                game.getCurrentShip().setTribbles(0);
            } else {
                game.addAlert(Alert.TribblesIrradiated);
                game.getCurrentShip().setTribbles(tribbles);
            }
        } else if (tribbles > 0 && game.getCurrentShip().getCargoCount(TradeItem.Narcotics) > 0) {
            tribbles = 1 + GetRandom(3);
            int deadTribbles = Math.min(1 + GetRandom(3), game.getCurrentShip().getCargoCount(TradeItem.Narcotics));
            game.getCurrentShip().removeCargo(TradeItem.Narcotics, deadTribbles);
            game.getCurrentShip().addCargo(TradeItem.Furs, deadTribbles, 0);
            game.addAlert(Alert.TribblesAteNarcotics);
            game.getCurrentShip().setTribbles(tribbles);
        } else if (tribbles > 0 && game.getCurrentShip().getCargoCount(TradeItem.Food) > 0) {
            tribbles += 100 + GetRandom(game.getCurrentShip().getCargoCount(TradeItem.Food) * 100);
            int foodLeft = GetRandom(game.getCurrentShip().getCargoCount(TradeItem.Food));
            int foodEaten = -(foodLeft - game.getCurrentShip().getCargoCount(TradeItem.Food));
            game.getCurrentShip().removeCargo(TradeItem.Food, foodEaten);
            game.addAlert(Alert.TribblesAteFood);
            foodOnBoard = true;
            game.getCurrentShip().setTribbles(tribbles);
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
        if (game.getCaptain().getDebt() > Game.DEBT_TOO_LARGE) {
            game.addAlert(Alert.DebtTooLargeForTravel);
            return this;
        }

        // Check for enough money to pay mercenaries
        int mercenaryCost = game.getCurrentShip().getMercenaryDailyCost();
        if (mercenaryCost > game.getCaptain().getCredits()) {
            game.addAlert(Alert.MustPayMercenaries);
            return this;
        }

        // Check for enough money to pay for insurance
        int insuranceCost = game.getCurrentShip().getInsuranceCost();
        if (game.getCurrentShip().isInsured()
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
            return game.getCurrentShip().type.getCostToFillFuelTank() * 25;
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
}
