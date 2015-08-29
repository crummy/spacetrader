package com.malcolmcrum.spacetrader;

import java.util.*;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * Created by Malcolm on 8/28/2015.
 */
public class Galaxy {
    private static final int MAX_SOLAR_SYSTEM = 120;
    private static final int MAX_WORM_HOLES = 6;
    private static final int CLOSE_DISTANCE = 13;
    private static final int MIN_DISTANCE = 6;
    private static final int GALAXY_WIDTH = 150;
    private static final int GALAXY_HEIGHT = 110;
    private static final int MAX_CREW_MEMBER = 31;

    private final List<SolarSystem> solarSystems = new ArrayList<>();

    public Galaxy(Difficulty difficulty) {
        for (int i = 0; i < MAX_SOLAR_SYSTEM; ++i) {
            Vector2i systemLocation = generateLocation(i);
            SolarSystem system = new SolarSystem(systemLocation, i, difficulty);
            solarSystems.add(system);
        }

        Collections.shuffle(solarSystems, new Random());

        addWormholes();

        addMercenaries();

        addSpecialEvents();

        addScarabWormhole();

        // TODO: More events
    }

    private void addScarabWormhole() {
        // TODO
    }

    private void addSpecialEvents() {
        findSystem("Acamar").setSpecialEvent(SolarSystem.SpecialEvent.MonsterKilled);
        findSystem("Barata").setSpecialEvent(SolarSystem.SpecialEvent.FlyBaratas);
        findSystem("Melina").setSpecialEvent(SolarSystem.SpecialEvent.FlyMelina);
        findSystem("Regulas").setSpecialEvent(SolarSystem.SpecialEvent.FlyRegulas);
        findSystem("Zalkon").setSpecialEvent(SolarSystem.SpecialEvent.DragonflyDestroyed);
        findSystem("Japori").setSpecialEvent(SolarSystem.SpecialEvent.MedicineDelivery);
        findSystem("Utopia").setSpecialEvent(SolarSystem.SpecialEvent.MoonForSale);
        findSystem("Devidia").setSpecialEvent(SolarSystem.SpecialEvent.JarekGetsOut);
        findSystem("Kravat").setSpecialEvent(SolarSystem.SpecialEvent.WildGetsOut);
    }

    private SolarSystem findSystem(String name) {
        Optional<SolarSystem> system = solarSystems
                                        .stream()
                                        .filter(sys -> sys.getName().equals(name))
                                        .findFirst();
        if (system.isPresent()) {
            return system.get();
        } else {
            return null;
        }
    }

    private void addMercenaries() {
        int i = 1; // skip commander
        while (i < MAX_CREW_MEMBER) {
            Crew mercenary = Crew.getCrew(i);
            int selectedSystemIndex = GetRandom(MAX_SOLAR_SYSTEM);
            SolarSystem system = solarSystems.get(selectedSystemIndex);

            if (system.hasMercenary()) continue;
            if (system.getName().equals("Kravat")) continue;

            system.addMercenary(mercenary);

            ++i;
        }
    }

    private void addWormholes() {
        for (int i = 0; i < MAX_WORM_HOLES; ++i) {
            int A, B;
            do {
                A = GetRandom(MAX_SOLAR_SYSTEM);
                B = GetRandom(MAX_SOLAR_SYSTEM);
            } while (A == B || solarSystems.get(A).hasWormhole() || solarSystems.get(B).hasWormhole());
            solarSystems.get(A).addWormhole(B);
            solarSystems.get(B).addWormhole(A);
        }
    }

    private Vector2i generateLocation(int index) {
        Vector2i systemLocation = new Vector2i();
        if (index == 0) {
            // Place the first system somewhere in the center [and other wormhole systems?]
            systemLocation.x = ((CLOSE_DISTANCE >> 1) - GetRandom(CLOSE_DISTANCE)) + ((GALAXY_WIDTH * (1 + 2*(index%3)))/6);
            systemLocation.y = ((CLOSE_DISTANCE >> 1) - GetRandom(CLOSE_DISTANCE)) + ((GALAXY_HEIGHT * (index < 3 ? 1 : 3))/4);
        } else {
            boolean neighbourTooClose, neighbourFound;
            do {
                systemLocation.x = 1 + GetRandom(GALAXY_WIDTH - 2);
                systemLocation.y = 1 + GetRandom(GALAXY_HEIGHT - 2);
                int nearestSystemDistance = nearestSystemDistance(systemLocation);
                neighbourTooClose = nearestSystemDistance < MIN_DISTANCE;
                neighbourFound = nearestSystemDistance < CLOSE_DISTANCE;
            } while (!neighbourFound || neighbourTooClose);
        }
        return systemLocation;
    }

    private int nearestSystemDistance(Vector2i point) {
        int nearestDistance = Integer.MAX_VALUE;
        for (SolarSystem system : solarSystems) {
            double distance = Vector2i.Distance(point, system.getLocation());
            if (distance < nearestDistance) {
                nearestDistance = (int)distance;
            }
        }
        return nearestDistance;
    }

    public SolarSystem getStartSystem() {
        return solarSystems.get(0);
    }
}
