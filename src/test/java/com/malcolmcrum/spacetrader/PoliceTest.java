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
}
