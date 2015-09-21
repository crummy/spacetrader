package com.malcolmcrum.spacetrader;

import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.InSystem;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

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

        game.getShip().addCrew(new Crew(1));
        game.getShip().addCrew(new Crew(2));
        inSystem.buyShip(ShipType.Flea);
        assertEquals("cannot buy ship without room for crew", game.getShip().getType(), previousShip);

        inSystem.buyShip(ShipType.Termite);
        assertTrue("crew were transferred", game.getShip().getCrew().size() == 3);
        assertTrue("new ship has captain", game.getShip().getCrew().stream().anyMatch(c -> c == game.getCaptain()));
    }

    @Test
    public void testBuyTradeItem() {
        game.getCaptain().setCredits(999999);
        SolarSystem systemWithFood = game.getGalaxy().getSystems()
                .stream()
                .filter(s -> s.getMarket().getQuantity(TradeItem.Food) > 0)
                .findAny()
                .get();
        InSystem inSystem = new InSystem(game, systemWithFood);
        int foodAvailable = inSystem.getSystem().getMarket().getQuantity(TradeItem.Food);
        int foodOnBoard = game.getShip().getCargoCount(TradeItem.Food);
        inSystem.buyTradeItem(TradeItem.Food, 1);
        assertEquals("can buy food", game.getShip().getCargoCount(TradeItem.Food), foodOnBoard + 1);
        assertTrue("food costs money", game.getCaptain().getCredits()< 999999);
        assertTrue("food removed from market", inSystem.getSystem().getMarket().getQuantity(TradeItem.Food) == foodAvailable - 1);
    }

    @Test
    public void testBuyInsurance() throws Exception {
        InSystem inSystem = new InSystem(game, game.getGalaxy().getRandomSystem());
        game.getCaptain().setEscapePod(true);
        inSystem.buyInsurance();
        assertTrue("buy insurance", game.getBank().hasInsurance());
        game.getBank().cancelInsurance();
        game.getCaptain().setEscapePod(false);
        inSystem.buyInsurance();
        assertFalse("can't buy insurance without escape pod", game.getBank().hasInsurance());
    }

    @Test
    public void testCancelInsurance() throws Exception {
        InSystem inSystem = new InSystem(game, game.getGalaxy().getRandomSystem());
        inSystem.cancelInsurance();
        assertFalse(game.getBank().hasInsurance());
    }

    @Test
    public void testBuyEscapePod() throws Exception {
        InSystem inSystem = new InSystem(game, game.getGalaxy().getRandomSystem());
        game.getCaptain().setEscapePod(false);
        game.getCaptain().setCredits(0);
        inSystem.buyEscapePod();
        assertFalse("can't afford escape pod", game.getCaptain().hasEscapePod());

        game.getCaptain().setCredits(99999999);
        inSystem.buyEscapePod();
        assertTrue("buy escape pod", game.getCaptain().hasEscapePod());
    }

    @Test
    public void testBuyRepairs() throws Exception {
        InSystem inSystem = new InSystem(game, game.getGalaxy().getRandomSystem());
        game.getShip().takeDamage(10);
        inSystem.buyRepairs(10);
        assertEquals(game.getShip().getHullStrength(), game.getShip().getFullHullStrength());

        inSystem.buyRepairs(9999);
        assertEquals(game.getShip().getHullStrength(), game.getShip().getFullHullStrength());
    }

    @Test
    public void testFireCrew() {
        SolarSystem hiTechSystem = game.getGalaxy().getSystems()
                .stream()
                .filter(s -> s.getTechLevel() == TechLevel.HiTech)
                .findAny()
                .get();
        InSystem inSystem = new InSystem(game, hiTechSystem);
        game.getCaptain().setCredits(999999);
        inSystem.buyShip(ShipType.Beetle);
        Crew crew = new Crew(2);
        game.getShip().addCrew(crew);
        inSystem.fireCrewmember(crew.getName());
        assertEquals("fired crewmember", game.getShip().getCrew().size(), 1);
        inSystem.fireCrewmember("You");
        assertEquals("can't fire captain", game.getShip().getCrew().size(), 1);
        inSystem.fireCrewmember(Crew.Name.Dane.getTitle());
        assertTrue("can't fire someone that doesn't exist", game.getShip().getCrew().size() == 1);
    }


    @Test
    public void testWarpTo() throws Exception {
        InSystem inSystem = new InSystem(game, game.getGalaxy().getRandomSystem());
        game.getShip().addFuel(999);
        SolarSystem destination = game.getGalaxy().getSystems()
                .stream()
                .filter(s -> game.getGalaxy().distanceBetween(s, inSystem.getSystem()) < game.getShip().getFuel())
                .findAny()
                .get();
        GameState newState = inSystem.warpTo(destination, false);
        assertNotEquals("successful warp", inSystem, newState);

        SolarSystem farDestination = game.getGalaxy().getSystems()
                .stream()
                .filter(s -> game.getGalaxy().distanceBetween(s, inSystem.getSystem()) > game.getShip().getFuel())
                .findAny()
                .get();
        newState = inSystem.warpTo(farDestination, false);
        assertEquals("can't warp too far", inSystem, newState);
    }

    @Test
    public void testBuyNewspaper() throws Exception {

    }
}