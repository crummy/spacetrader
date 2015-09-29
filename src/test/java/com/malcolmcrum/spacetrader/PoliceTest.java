package com.malcolmcrum.spacetrader;

import com.malcolmcrum.spacetrader.GameStates.Encounters.Police;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Malcolm on 9/28/2015.
 */
public class PoliceTest extends GameStateTest {
    @Test
    public void testImpossibleBribe() {
        SolarSystem unbribeableSystem = game.getGalaxy().getSystems()
                .stream()
                .filter(system -> system.getPolitics().getBribeLevel() == BribeLevel.Impossible)
                .filter(system -> system.getPolitics().getPoliceStrength() != PoliceStrength.Absent)
                .findAny()
                .get();
        Police encounter = new Police(game, new Transit(game, unbribeableSystem, false));

        int credits = game.getCaptain().getCredits();
        GameState nextState = encounter.actionBribe();
        assertTrue("can't bribe unbribeable cops", nextState == encounter);
        assertTrue("don't lose money on failed bribe", credits == game.getCaptain().getCredits());
    }

    @Test
    public void testCantAffordBribe() {
        SolarSystem bribeableSystem = game.getGalaxy().getSystems()
                .stream()
                .filter(system -> system.getPolitics().getBribeLevel() != BribeLevel.Impossible)
                .filter(system -> system.getPolitics().getPoliceStrength() != PoliceStrength.Absent)
                .findAny()
                .get();
        Police encounter = new Police(game, new Transit(game, bribeableSystem, false));
        game.getCaptain().setCredits(0);

        GameState nextState = encounter.actionBribe();
        assertTrue("can't bribe if you can't afford", nextState == encounter);
        assertTrue("lose no money if you can't afford bribe", game.getCaptain().getCredits() == 0);
    }

    @Test
    public void testSuccessfulBribe() {
        SolarSystem bribeableSystem = game.getGalaxy().getSystems()
                .stream()
                .filter(system -> system.getPolitics().getBribeLevel() != BribeLevel.Impossible)
                .filter(system -> system.getPolitics().getPoliceStrength() != PoliceStrength.Absent)
                .findAny()
                .get();
        Police encounter = new Police(game, new Transit(game, bribeableSystem, false));
        int initialCredits = 999999999;
        game.getCaptain().setCredits(initialCredits);
        int bribeCost = encounter.getBribeCost();

        GameState nextState = encounter.actionBribe();
        assertTrue("successful bribe", nextState != encounter);
        assertTrue("handed over cash", game.getCaptain().getCredits() == initialCredits - bribeCost);
    }

    @Test
    public void testUneventfulSubmit() {
        SolarSystem systemWithCops = game.getGalaxy().getSystems()
                .stream()
                .filter(system -> system.getPolitics().getPoliceStrength() != PoliceStrength.Absent)
                .findAny()
                .get();
        Transit transit = new Transit(game, systemWithCops, false);
        Police encounter = new Police(game, transit);

        int policeRecord = game.getCaptain().getPoliceRecordScore();
        int cargoSpace = game.getShip().getFreeCargoBays();
        GameState nextState = encounter.actionSubmit();
        assertTrue("no illegal goods", nextState == transit);
        assertTrue("police score boost", game.getCaptain().getPoliceRecordScore() > policeRecord);
        assertTrue("no cargo taken", game.getShip().getFreeCargoBays() == cargoSpace);
    }

    @Test
    public void testSubmitWithIllegalGoods() {
        SolarSystem systemWithCops = game.getGalaxy().getSystems()
                .stream()
                .filter(system -> system.getPolitics().getPoliceStrength() != PoliceStrength.Absent)
                .findAny()
                .get();
        Transit transit = new Transit(game, systemWithCops, false);
        Police encounter = new Police(game, transit);

        game.getShip().addCargo(TradeItem.Firearms, 1, 0);
        game.getShip().addCargo(TradeItem.Narcotics, 2, 0);
        int credits = game.getCaptain().getCredits();
        int policeScore = game.getCaptain().getPoliceRecordScore();
        GameState nextState = encounter.actionSubmit();
        assertTrue(nextState == transit);
        assertTrue("lose money", game.getCaptain().getCredits() == credits - encounter.getIllegalGoodsFine());
        assertTrue("lose firearms", game.getShip().getCargoCount(TradeItem.Firearms) == 0);
        assertTrue("lose narcotics", game.getShip().getCargoCount(TradeItem.Narcotics) == 0);
        assertTrue("lose police score", game.getCaptain().getPoliceRecordScore() < policeScore);
    }
}
