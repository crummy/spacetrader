package com.malcolmcrum.spacetrader;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class GalaxyTest
{
    private static Galaxy galaxy;

    @BeforeClass
    public static void setUp() {
        galaxy = new Galaxy(Difficulty.Normal);
    }

    @Test
    public void fixedSpecialEvents() {
        assertEquals(SolarSystem.Acamar.getSpecialEvent(), SolarSystem.SpecialEvent.MonsterKilled);
        assertEquals(SolarSystem.Baratas.getSpecialEvent(), SolarSystem.SpecialEvent.FlyBaratas);
        assertEquals(SolarSystem.Melina.getSpecialEvent(), SolarSystem.SpecialEvent.FlyMelina);
        assertEquals(SolarSystem.Regulas.getSpecialEvent(), SolarSystem.SpecialEvent.FlyRegulas);
        assertEquals(SolarSystem.Zalkon.getSpecialEvent(), SolarSystem.SpecialEvent.DragonflyDestroyed);
        assertEquals(SolarSystem.Japori.getSpecialEvent(), SolarSystem.SpecialEvent.MedicineDelivery);
        assertEquals(SolarSystem.Utopia.getSpecialEvent(), SolarSystem.SpecialEvent.Retirement);
        assertEquals(SolarSystem.Devidia.getSpecialEvent(), SolarSystem.SpecialEvent.JarekGetsOut);
        assertEquals(SolarSystem.Kravat.getSpecialEvent(), SolarSystem.SpecialEvent.WildGetsOut);
    }

    @Test
    public void verifyMercenaries() {
        for (Crew member : Crew.values()) {
            int count = systemsWithCrewmember(member);
            if (member == Crew.Captain || member == Crew.Zeethibal) {
                assertTrue(member + " found on " + count + " systems", count == 0);
            } else {
                assertTrue(member + " found on " + count + " systems", count == 1);
            }
        }
    }

    @Test
    public void systemsWithinGalaxyBounds() {
        for (SolarSystem system : SolarSystem.values()) {
            Vector2i location = system.getLocation();
            assertTrue(location.x > 0);
            assertTrue(location.x < Galaxy.GALAXY_WIDTH);
            assertTrue(location.y > 0);
            assertTrue(location.y < Galaxy.GALAXY_HEIGHT);
        }
    }

    @Test
    public void noSystemTooClose() {
        for (SolarSystem system : SolarSystem.values()) {
            double minDistance = Integer.MAX_VALUE;
            for (SolarSystem otherSystem : SolarSystem.values()) {
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
        for (SolarSystem system : SolarSystem.values()) {
            double closestDistance = Integer.MAX_VALUE;
            for (SolarSystem otherSystem : SolarSystem.values()) {
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
        for (SolarSystem system : SolarSystem.values()) {
            Politics politics = system.getPolitics();
            TechLevel techLevel = system.getTechLevel();
            assertFalse(techLevel.isBeyond(politics.getMaxTechLevel()));
            assertFalse(techLevel.isBefore(politics.getMinTechLevel()));
            assertFalse(techLevel == TechLevel.Unattainable);
        }
    }

    @Test
    public void systemAttributes() {
        assertSame(SolarSystem.Acamar.getName(), "Acamar");
        assertEquals(SolarSystem.Acamar.getTradeResetCountdown(), 0);
        assertFalse(SolarSystem.Acamar.isVisited());

        Map<TradeItem, Integer> tradeItems = SolarSystem.Acamar.getTradeItems();
        for (TradeItem item : TradeItem.values()) {
            assertTrue("item: " + item, tradeItems.containsKey(item));
        }
    }

    @Test
    public void specialEvents() {
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
                        occurrencesOfEvent == event.getOccurrence() || event.hasFixedLocation());
            }
        }
        assertEquals(SolarSystem.Gemulon.getSpecialEvent(), SolarSystem.SpecialEvent.GemulonRescued);
        assertEquals(SolarSystem.Daled.getSpecialEvent(), SolarSystem.SpecialEvent.ExperimentFailed);
        assertEquals(SolarSystem.Nix.getSpecialEvent(), SolarSystem.SpecialEvent.ReactorDelivered);
    }

    @Test
    public void testWormholes() {
        List<SolarSystem> wormholeSystems = galaxy.getSystemsWithWormholes();
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
    public void testGetStartSystem() throws Exception {
        SolarSystem system = galaxy.getStartSystem(ShipType.Beetle);
        assertFalse(system.hasSpecialEvent());
        assertFalse(system.getTechLevel() == TechLevel.Agricultural);
        assertFalse(system.getTechLevel() == TechLevel.HiTech);
    }

    private int systemsWithEvent(SolarSystem.SpecialEvent event) {
        int count = 0;
        for (SolarSystem system : SolarSystem.values()) {
            if (system.getSpecialEvent() == event) {
                ++count;
            }
        }
        return count;
    }

    private int systemsWithCrewmember(Crew member) {
        int count = 0;
        for (SolarSystem system : SolarSystem.values()) {
            if (system.getMercenary() == member) {
                ++count;
            }
        }
        return count;
    }
}
