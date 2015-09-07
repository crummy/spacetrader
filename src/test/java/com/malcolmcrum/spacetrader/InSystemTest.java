package com.malcolmcrum.spacetrader;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Created by Malcolm on 9/7/2015.
 */
public class InSystemTest extends GameStateTest {

    @Test
    public void testActions() throws Exception {
        Method buyFuel = findMethodNamed("buyFuel");
        assertNotNull(buyFuel);
        Method buyRepairs = findMethodNamed("buyRepairs");
        assertNotNull(buyRepairs);
        Method buyShip = findMethodNamed("buyShip");
        assertNotNull(buyShip);
        Method buyEscapePod = findMethodNamed("buyEscapePod");
        assertNotNull(buyEscapePod);
        Method buyInsurance = findMethodNamed("buyInsurance");
        assertNotNull(buyInsurance);
    }

    @Test
    public void testBuyingFuel() throws Exception {
        int fullFuel = game.getShip().getFuel();
        Method buyFuel = findMethodNamed("buyFuel");
        buyFuel.invoke(inSystem, 1);
        assertEquals("tank shouldn't overflow", fullFuel, game.getShip().getFuel());
    }
}