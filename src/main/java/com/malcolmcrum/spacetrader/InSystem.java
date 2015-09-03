package com.malcolmcrum.spacetrader;

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
        game.setCurrentSystem(system);
        game.getGalaxy().shuffleStatuses();
        game.getGalaxy().changeTradeItemQuantities();
        system.getMarket().determinePrices();
        alreadyPaidForNewspaper = false;
        addNewsEvents();
        return this;
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
