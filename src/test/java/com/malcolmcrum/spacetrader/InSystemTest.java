package com.malcolmcrum.spacetrader;

import com.malcolmcrum.spacetrader.GameStates.InSystem;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Malcolm on 9/7/2015.
 */
public class InSystemTest {
    static InSystem inSystem;
    static List<Method> methods;
    static Game game;

    @BeforeClass
    public static void setUp() {
        game = new Game();
        game.startNewGame("Bob", 5, 5, 5, 5, Difficulty.Normal);
        SolarSystem system = game.getGalaxy().getStartSystem(ShipType.Beetle.getFuelTanks());
        inSystem = new InSystem(game, system);
        methods = InSystem.getActions();
    }

    @Test
    public void testActions() throws Exception {
        Method buyFuel = findMethodNamed("buyFuel", new Class[]{int.class});
        assertNotNull(buyFuel);
        Method buyRepairs = findMethodNamed("buyRepairs", new Class[]{int.class});
        assertNotNull(buyRepairs);
        Method buyShip = findMethodNamed("buyShip", new Class[]{String.class});
        assertNotNull(buyShip);
        Method buyEscapePod = findMethodNamed("buyEscapePod");
        assertNotNull(buyEscapePod);
        Method buyInsurance = findMethodNamed("buyInsurance");
        assertNotNull(buyInsurance);
    }

    private Method findMethodNamed(String name) throws NoSuchMethodException {
        return findMethodNamed(name, null);
    }

    private Method findMethodNamed(String name, Class<?>[] args) throws NoSuchMethodException {
        for (Method m : methods) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        System.out.println("Could not find method named " + name);
        throw new NoSuchMethodException();
    }

    @Test
    public void testBuyingFuel() throws Exception {
        int fullFuel = game.getShip().getFuel();
        Method buyFuel = findMethodNamed("buyFuel", new Class[]{int.class});
        buyFuel.invoke(inSystem, 1);
        assertEquals("tank shouldn't overflow", fullFuel, game.getShip().getFuel());
    }
}