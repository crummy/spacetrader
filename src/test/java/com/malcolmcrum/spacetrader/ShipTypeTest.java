package com.malcolmcrum.spacetrader;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Malcolm on 9/8/2015.
 */
public class ShipTypeTest {

    @Test
    public void testGetAdjustedRandomShip() throws Exception {
        for (int i = 0; i < 1000; ++i) {
            ShipType type = ShipType.GetAdjustedRandomShip();
            assertTrue(type != ShipType.Dragonfly);
            assertTrue(type != ShipType.Scarab);
            assertTrue(type != ShipType.Mantis);
            assertTrue(type != ShipType.SpaceMonster);
        }
    }
}