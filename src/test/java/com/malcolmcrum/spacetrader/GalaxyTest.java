package com.malcolmcrum.spacetrader;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class GalaxyTest
{
    private static Galaxy galaxy;

    @BeforeClass
    public static void setUp() {
        Difficulty difficulty = Difficulty.Easy;
        Captain captain = new Captain("Bob", 5, 5, 5, 5, new Game());
        PlayerShip ship = new PlayerShip(ShipType.Beetle, captain, new Quests(), difficulty);
        galaxy = new Galaxy(captain, ship, difficulty);
    }

    @Test
    public void testHardcodedSpecialEvents() {
        for (SolarSystem system : galaxy.systems) {
            // following are hardcoded directly
            if (system.getType() == SolarSystem.Name.Acamar) assertEquals(system.getSpecialEvent(), SolarSystem.SpecialEvent.MonsterKilled);
            if (system.getType() == SolarSystem.Name.Baratas) assertEquals(system.getSpecialEvent(), SolarSystem.SpecialEvent.FlyBaratas);
            if (system.getType() == SolarSystem.Name.Melina) assertEquals(system.getSpecialEvent(), SolarSystem.SpecialEvent.FlyMelina);
            if (system.getType() == SolarSystem.Name.Regulas) assertEquals(system.getSpecialEvent(), SolarSystem.SpecialEvent.FlyRegulas);
            if (system.getType() == SolarSystem.Name.Zalkon) assertEquals(system.getSpecialEvent(), SolarSystem.SpecialEvent.DragonflyDestroyed);
            if (system.getType() == SolarSystem.Name.Japori) assertEquals(system.getSpecialEvent(), SolarSystem.SpecialEvent.MedicineDelivery);
            if (system.getType() == SolarSystem.Name.Utopia) assertEquals(system.getSpecialEvent(), SolarSystem.SpecialEvent.Retirement);
            if (system.getType() == SolarSystem.Name.Devidia) assertEquals(system.getSpecialEvent(), SolarSystem.SpecialEvent.JarekGetsOut);
            if (system.getType() == SolarSystem.Name.Kravat) assertEquals(system.getSpecialEvent(), SolarSystem.SpecialEvent.WildGetsOut);

            // following may in extreme circumstances (like a tiny galaxy) not exist?
            if (system.getType() == SolarSystem.Name.Gemulon) assertEquals(system.getSpecialEvent(), SolarSystem.SpecialEvent.GemulonRescued);
            if (system.getType() == SolarSystem.Name.Daled) assertEquals(system.getSpecialEvent(), SolarSystem.SpecialEvent.ExperimentFailed);
            if (system.getType() == SolarSystem.Name.Nix) assertEquals(system.getSpecialEvent(), SolarSystem.SpecialEvent.ReactorDelivered);
        }
    }

    @Test
    public void verifyMercenaries() {
        for (int i = 0; i < Mercenary.getTotalMercenaries(); ++i) {
            Mercenary.Name name = Mercenary.Name.values()[i];
            int count = systemsWithMercenaryNamed(name);
            if (i == Mercenary.getTotalMercenaries() - 1) { // captain or zeethibal
                assertTrue(name + " found on " + count + " systems", count == 0);
            } else {
                assertTrue(name + " found on " + count + " systems", count == 1);
            }
        }
    }

    @Test
    public void systemsWithinGalaxyBounds() {
        for (SolarSystem system : galaxy.systems) {
            Vector2i location = system.getLocation();
            assertTrue(location.x > 0);
            assertTrue(location.x < Galaxy.GALAXY_WIDTH);
            assertTrue(location.y > 0);
            assertTrue(location.y < Galaxy.GALAXY_HEIGHT);
        }
    }

    @Test
    public void noSystemTooClose() {
        for (SolarSystem system : galaxy.systems) {
            double minDistance = Integer.MAX_VALUE;
            for (SolarSystem otherSystem : galaxy.systems) {
                if (system != otherSystem) {
                    double distance = Vector2i.Distance(system.getLocation(), otherSystem.getLocation());
                    if (distance < minDistance) {
                        minDistance = distance;
                    }
                }
            }
            assertTrue(Math.pow(minDistance, 2) > Math.pow(Galaxy.MIN_DISTANCE + 1, 2));
        }
    }

    @Test
    public void allSystemsHaveNeighbours() {
        for (SolarSystem system : galaxy.systems) {
            double closestDistance = Integer.MAX_VALUE;
            for (SolarSystem otherSystem : galaxy.systems) {
                if (system != otherSystem) {
                    double distance = Vector2i.Distance(system.getLocation(), otherSystem.getLocation());
                    if (distance < closestDistance) {
                        closestDistance = distance;
                    }
                }
            }
            assertTrue(closestDistance <= Galaxy.CLOSE_DISTANCE);
        }
    }

    @Test
    public void politicsAndTechLevelCompatible() {
        for (SolarSystem system : galaxy.systems) {
            Politics politics = system.getPolitics();
            TechLevel techLevel = system.getTechLevel();
            assertFalse(techLevel.isBeyond(politics.getMaxTechLevel()));
            assertFalse(techLevel.isBefore(politics.getMinTechLevel()));
            assertFalse(techLevel == TechLevel.Unattainable);
        }
    }

    @Test
    public void testRandomSpecialEvents() {
        for (SolarSystem.SpecialEvent event : SolarSystem.SpecialEvent.values()) {
            long occurrencesOfEvent = systemsWithEvent(event);
            if (event == SolarSystem.SpecialEvent.ScarabDestroyed
                    || event == SolarSystem.SpecialEvent.ScarabStolen
                    || event == SolarSystem.SpecialEvent.ArtifactDelivery
                    || event == SolarSystem.SpecialEvent.AlienInvasion
                    || event == SolarSystem.SpecialEvent.DangerousExperiment
                    || event == SolarSystem.SpecialEvent.MorgansReactor) {
                assertTrue("NotableEvent: " + event + "(" + occurrencesOfEvent + ")",
                        occurrencesOfEvent == 0 || occurrencesOfEvent == 1);
            } else {
                assertTrue("NotableEvent: " + event + "(" + occurrencesOfEvent + ")",
                        occurrencesOfEvent == event.occurrence || event.hasFixedLocation);
            }
        }
    }

    @Test
    public void testWormholes() {
        List<SolarSystem> wormholeSystems = galaxy.systems.stream()
                .filter(SolarSystem::hasWormhole)
                .collect(Collectors.toList());
        assertSame(wormholeSystems.size(), Galaxy.MAX_WORM_HOLES);
        for (SolarSystem system : wormholeSystems) {
            assertTrue(system.hasWormhole());
            SolarSystem destination = system.getWormholeDestination();
            assertTrue(system + "'s wormhole points to itself!", destination != system);
            assertTrue(system + "'s wormhole points nowhere!", destination != null);
        }

        SolarSystem first = wormholeSystems.get(0);
        int jumps = 0;
        for (SolarSystem system = first.getWormholeDestination(); system != first; system = system.getWormholeDestination()) {
            ++jumps;
            if (jumps > wormholeSystems.size()) {
                break;
            }
        }
        assertTrue("Wormhole connections are fubar!", jumps == wormholeSystems.size() - 1);
    }

    @Test
    public void testUniqueSystems() {
        assertEquals(galaxy.systems.size(), SolarSystem.Name.values().length);
        Map<SolarSystem.Name, Integer> timesFound = new HashMap<>();
        for (SolarSystem.Name name : SolarSystem.Name.values()) {
            timesFound.put(name, 0);
        }
        for (SolarSystem system : galaxy.systems) {
            SolarSystem.Name name = system.getType();
            timesFound.put(name, timesFound.get(name) + 1);
        }
        for (SolarSystem.Name name : timesFound.keySet()) {
            int instancesOfName = timesFound.get(name);
            assertEquals(instancesOfName, 1);
        }
    }

    @Test
    public void testGetStartSystem() throws Exception {
        SolarSystem system = galaxy.getStartSystem(ShipType.Gnat.getFuelTanks());
        assertFalse(system.hasSpecialEvent());
        assertFalse(system.getTechLevel() == TechLevel.Preagricultural);
        assertFalse(system.getTechLevel() == TechLevel.HiTech);
    }

    private int systemsWithEvent(SolarSystem.SpecialEvent event) {
        return (int)galaxy.systems.stream()
                .filter(s -> s.getSpecialEvent() == event)
                .count();
    }

    private int systemsWithMercenaryNamed(Mercenary.Name name) {
        return (int)galaxy.systems.stream()
                .filter(s -> s.getMercenary() != null && s.getMercenary().getType() == name)
                .count();
    }
}
