package com.malcolmcrum.spacetrader;

import com.malcolmcrum.spacetrader.GameStates.InSystem;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Malcolm on 9/7/2015.
 */
public class InSystemTest extends GameStateTest {

    @Test
    public void testBuyFuel() throws Exception {
        SolarSystem system = game.getGalaxy().getStartSystem(ShipType.Beetle.getFuelTanks());
        InSystem inSystem = new InSystem(game, system);
        List<Method> methods = inSystem.getActions();

        int fullFuel = game.getShip().getFuel();
        Method buyFuel = findMethodNamed("buyFuel", methods);
        buyFuel.invoke(inSystem, 1);
        assertEquals("tank shouldn't overflow", fullFuel, game.getShip().getFuel());
    }

    @Test
    public void testBuyShip() throws Exception {
        game.getCaptain().addCredits(999999999);
        SolarSystem preAg = game.getSystems().stream()
                .filter(system -> system.getTechLevel() == TechLevel.Preagricultural)
                .findAny()
                .get();
        //game.setCurrentSystem(preAg);
        InSystem inSystem = new InSystem(game, preAg);
        ShipType previousShip = game.getShip().getType();
        inSystem.buyShip(ShipType.Flea);
        assertEquals("cannot buy ship if not for sale", game.getShip().getType(), previousShip);

        game.getCaptain().setCredits(0);
        SolarSystem hiTech = game.getSystems().stream()
                .filter(system -> system.getTechLevel() == TechLevel.HiTech)
                .findAny()
                .get();
        inSystem = new InSystem(game, hiTech);
        inSystem.buyShip(ShipType.Beetle);
        assertEquals("cannot buy ship with no money", game.getShip().getType(), previousShip);

        game.getCaptain().setCredits(99999999);
        inSystem.buyShip(ShipType.Beetle);
        assertEquals("can buy ship with enough money", game.getShip().getType(), ShipType.Beetle);

        game.getShip().addWeapon(Weapon.MorgansLaser);
        inSystem.buyShip(ShipType.Flea);
        assertEquals("cannot buy tiny ship with unique equipment", game.getShip().getType(), ShipType.Beetle);

        inSystem.buyShip(ShipType.Wasp);
        assertEquals("can buy large ship with unique equipment", game.getShip().getType(), ShipType.Wasp);
        assertTrue("unique equipment is transferred", game.getShip().hasWeapon(Weapon.MorgansLaser));

        previousShip = game.getShip().getType();
        inSystem.buyShip(ShipType.Dragonfly);
        assertEquals("cannot buy unattainable ship", game.getShip().getType(), previousShip);
    }

    @Test
    public void testBuyInsurance() throws Exception {

    }

    @Test
    public void testBuyEscapePod() throws Exception {

    }

    @Test
    public void testBuyRepairs() throws Exception {

    }

    @Test
    public void testBuyRepairs1() throws Exception {

    }


    @Test
    public void testWarpTo() throws Exception {

    }

    @Test
    public void testBuyNewspaper() throws Exception {

    }

    @Test
    public void testGetSystem() throws Exception {

    }

    @Test
    public void testGetPlayerShip() throws Exception {

    }
}