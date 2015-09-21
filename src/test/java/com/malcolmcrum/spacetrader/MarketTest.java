package com.malcolmcrum.spacetrader;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by Malcolm on 9/21/2015.
 */
public class MarketTest {
    private static Game game;

    @BeforeClass
    public static void setUp() {
        game = new Game();
        game.startNewGame("Bob", 5, 5, 5, 5, Difficulty.Easy);
    }

    @Test
    public void testBannedItems() {
        for (SolarSystem s : game.getGalaxy().getSystems()) {
            Market m = s.getMarket();
            if (!s.getPolitics().getDrugsOK()) {
                assertTrue("no selling of drugs", !m.getSellPrice(TradeItem.Narcotics).isPresent());
                assertEquals("no drugs available", (long)m.getQuantity(TradeItem.Narcotics), 0);
            }
            if (!s.getPolitics().getFirearmsOK()) {
                assertTrue("no selling of guns", !m.getSellPrice(TradeItem.Firearms).isPresent());
                assertEquals("no guns available", (long)m.getQuantity(TradeItem.Firearms), 0);
            }
        }
    }
}