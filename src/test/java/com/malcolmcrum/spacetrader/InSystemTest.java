package com.malcolmcrum.spacetrader;

import com.malcolmcrum.spacetrader.GameStates.InSystem;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Malcolm on 9/7/2015.
 */
public class InSystemTest extends GameStateTest {

    @Test
    public void testActions() throws Exception {
        SolarSystem system = game.getGalaxy().getStartSystem(ShipType.Beetle.getFuelTanks());
        InSystem inSystem = new InSystem(game, system);
        List<Method> methods = inSystem.getActions();

        Method buyFuel = findMethodNamed("buyFuel", methods);
        assertNotNull(buyFuel);
        Method buyRepairs = findMethodNamed("buyRepairs", methods);
        assertNotNull(buyRepairs);
        Method buyShip = findMethodNamed("buyShip", methods);
        assertNotNull(buyShip);
        Method buyEscapePod = findMethodNamed("buyEscapePod", methods);
        assertNotNull(buyEscapePod);
        Method buyInsurance = findMethodNamed("buyInsurance", methods);
        assertNotNull(buyInsurance);
    }

    @Test
    public void testBuyingFuel() throws Exception {
        SolarSystem system = game.getGalaxy().getStartSystem(ShipType.Beetle.getFuelTanks());
        InSystem inSystem = new InSystem(game, system);
        List<Method> methods = inSystem.getActions();

        int fullFuel = game.getShip().getFuel();
        Method buyFuel = findMethodNamed("buyFuel", methods);
        buyFuel.invoke(inSystem, 1);
        assertEquals("tank shouldn't overflow", fullFuel, game.getShip().getFuel());
    }
}