package com.malcolmcrum.spacetrader;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
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
    public void verifySpecialEvents() {
        assertTrue(systemHasEvent("Acamar", SolarSystem.SpecialEvent.MonsterKilled));
        assertTrue(systemHasEvent("Baratas", SolarSystem.SpecialEvent.FlyBaratas));
        assertTrue(systemHasEvent("Melina", SolarSystem.SpecialEvent.FlyMelina));
        assertTrue(systemHasEvent("Regulas", SolarSystem.SpecialEvent.FlyRegulas));
        assertTrue(systemHasEvent("Zalkon", SolarSystem.SpecialEvent.DragonflyDestroyed));
        assertTrue(systemHasEvent("Japori", SolarSystem.SpecialEvent.MedicineDelivery));
        assertTrue(systemHasEvent("Utopia", SolarSystem.SpecialEvent.MoonForSale));
        assertTrue(systemHasEvent("Devidia", SolarSystem.SpecialEvent.JarekGetsOut));
        assertTrue(systemHasEvent("Kravat", SolarSystem.SpecialEvent.WildGetsOut));
    }

    @Test
    public void verifyMercenaries() {
        for (Crew member : Crew.values()) {
            if (member != Crew.Captain) {
                assertTrue(memberExistsOnOneSystemOnly(member));
            }
        }
        SolarSystem kravat = galaxy.solarSystems
                .stream()
                .filter(sys -> sys.getName().equals("Kravat"))
                .findFirst()
                .get();
        assertTrue(kravat.getMercenary() == Crew.Zeethibal);
    }

    @Test
    public void systemsWithinBounds() {
        for (SolarSystem system : galaxy.solarSystems) {
            Vector2i location = system.getLocation();
            assertTrue(location.x > 0);
            assertTrue(location.x < Galaxy.GALAXY_WIDTH);
            assertTrue(location.y > 0);
            assertTrue(location.y < Galaxy.GALAXY_HEIGHT);
        }
    }

    @Test
    public void noSystemTooClose() {
        for (SolarSystem system : galaxy.solarSystems) {
            double minDistance = Integer.MAX_VALUE;
            for (SolarSystem otherSystem : galaxy.solarSystems) {
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
        for (SolarSystem system : galaxy.solarSystems) {
            double closestDistance = Integer.MAX_VALUE;
            for (SolarSystem otherSystem : galaxy.solarSystems) {
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
        for (SolarSystem system : galaxy.solarSystems) {
            Politics politics = system.getPolitics();
            TechLevel techLevel = system.getTechLevel();
            assertFalse(techLevel.isBeyond(politics.getMaxTechLevel()));
            assertFalse(techLevel.isBefore(politics.getMinTechLevel()));
            assertFalse(techLevel == TechLevel.Unattainable);
        }
    }

    @Test
    public void systemAttributes() {
        SolarSystem firstSystem = galaxy.solarSystems.get(0);
        assertSame(firstSystem.getName(), "Acamar");
        assertEquals(firstSystem.getTradeResetCountdown(), 0);
        assertFalse(firstSystem.isVisited());

        Map<TradeItem, Integer> tradeItems = firstSystem.getTradeItems();
        for (TradeItem item : TradeItem.values()) {
            assertTrue(tradeItems.containsKey(item));
        }
    }

    private boolean memberExistsOnOneSystemOnly(Crew member) {
        return galaxy.solarSystems.stream()
                .filter(system -> system.getMercenary() == member)
                .count() == 1;
    }

    private boolean systemHasEvent(String name, SolarSystem.SpecialEvent event) {
        return galaxy.solarSystems.stream()
                .filter(system -> system.getName().equals(name))
                .findFirst()
                .get()
                .getSpecialEvent() == event;
    }
}
