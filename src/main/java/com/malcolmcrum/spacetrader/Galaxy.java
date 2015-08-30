package com.malcolmcrum.spacetrader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * Galaxy generator - basically sets up SolarSystems.
 * Created by Malcolm on 8/28/2015.
 */
public class Galaxy {
    private static final Logger logger = LoggerFactory.getLogger(Galaxy.class);

    private static final int MAX_SOLAR_SYSTEMS = 120;
    private static final int MAX_WORM_HOLES = 6;
    static final int CLOSE_DISTANCE = 13;
    static final int MIN_DISTANCE = 6;
    static final int GALAXY_WIDTH = 150;
    static final int GALAXY_HEIGHT = 110;
    private static final int MAX_CREW_MEMBER = 31;

    final List<SolarSystem> solarSystems = new ArrayList<>(MAX_SOLAR_SYSTEMS);

    public Galaxy(Difficulty difficulty) {
        addSolarSystems(difficulty);

        shuffleSystems();

        addMercenaries();

        addSpecialEvents();

        boolean scarabEndpointExists = addScarabWormhole();

        addReactorQuest();

        addArtifactDeliveryQuest();

        addGemulonInvasion();

        addExperimentQuest();

        addScatteredSpecialEvents(scarabEndpointExists);
    }

    private void addScatteredSpecialEvents(boolean scarabEndpointExists) {
        for (SolarSystem.SpecialEvent event : SolarSystem.SpecialEvent.values()) {
            if (event.hasFixedLocation()) {
                continue;
            }
            boolean keepLooking = true;
            while (keepLooking) {
                int randomSystemIndex = 1 + GetRandom(MAX_SOLAR_SYSTEMS - 1);
                SolarSystem system = solarSystems.get(randomSystemIndex);
                if (system.getSpecialEvent() == null
                        && (scarabEndpointExists || event != SolarSystem.SpecialEvent.ScarabStolen)) {
                    system.setSpecialEvent(event);
                    keepLooking = false;
                }
            }
        }
    }

    private void addExperimentQuest() {
        int d = 999;
        int k = -1;
        SolarSystem daledSystem = findSystem("Daled");
        for (int i = 0; i < MAX_SOLAR_SYSTEMS; ++i) {
            SolarSystem system = solarSystems.get(i);
            int j = (int)Vector2i.Distance(daledSystem.getLocation(), system.getLocation());
            if (j >= 70
                    && j < d
                    && system.getSpecialEvent() == null) {
                k = i;
                d = j;
            }
        }
        boolean foundSystemForExperiment = (k >= 0);
        if (foundSystemForExperiment) {
            solarSystems.get(k).setSpecialEvent(SolarSystem.SpecialEvent.DangerousExperiment);
            daledSystem.setSpecialEvent(SolarSystem.SpecialEvent.ExperimentFailed);
        }
    }

    private void addGemulonInvasion() {
        int d = 999;
        int k = -1;
        SolarSystem gemulonSystem = findSystem("Gemulon");
        for (int i = 0; i < MAX_SOLAR_SYSTEMS; ++i) {
            SolarSystem system = solarSystems.get(i);
            int j = (int)Vector2i.Distance(gemulonSystem.getLocation(), system.getLocation());
            if (j >= 70
                    && j < d
                    && system.getSpecialEvent() == null
                    && !solarSystems.get(k).getName().equals("Daled")
                    && !solarSystems.get(k).getName().equals("Gemulon")) {
                k = i;
                d = j;
            }
        }
        boolean foundSystemForInvasionWarning = (k >= 0);
        if (foundSystemForInvasionWarning) {
            solarSystems.get(k).setSpecialEvent(SolarSystem.SpecialEvent.AlienInvasion);
            gemulonSystem.setSpecialEvent(SolarSystem.SpecialEvent.GemulonRescued);
        }
    }

    private void addReactorQuest() {
        int d = 999;
        int k = -1;
        SolarSystem nixSystem = findSystem("Nix");
        for (int i = 0; i < MAX_SOLAR_SYSTEMS; ++i) {
            SolarSystem system = solarSystems.get(i);
            int j = (int)Vector2i.Distance(nixSystem.getLocation(), system.getLocation());
            if (j >= 70
                    && j < d
                    && system.getSpecialEvent() == null
                    && !system.getName().equals("Gemulon")
                    && !system.getName().equals("Daled")) {
                k = i;
                d = j;
            }
        }
        boolean foundLocationForReactor = (k >= 0);
        if (foundLocationForReactor) {
            solarSystems.get(k).setSpecialEvent(SolarSystem.SpecialEvent.MorgansReactor);
            nixSystem.setSpecialEvent(SolarSystem.SpecialEvent.ReactorDelivered);
        }
    }

    private void addArtifactDeliveryQuest() {
        int i = 0;
        while (i < MAX_SOLAR_SYSTEMS) {
            int d = 1 + (GetRandom(MAX_SOLAR_SYSTEMS + 1));
            SolarSystem system = solarSystems.get(d);
            if (system.getSpecialEvent() == null
                    && system.getTechLevel() == TechLevel.HiTech
                    && !system.getName().equals("Gemulon")
                    && !system.getName().equals("Daled")) {
                system.setSpecialEvent(SolarSystem.SpecialEvent.ArtifactDelivery);
                break;
            }
            ++i;
        }
        boolean didNotFindSystemForArtifactQuest = (i >= MAX_SOLAR_SYSTEMS);
        if (didNotFindSystemForArtifactQuest) {
            SolarSystem.SpecialEvent.AlienArtifact.setOccurrence(0);
        }
    }

    /**
     * Finds a wormhole that points to a system without a specialEvent,
     * and puts the Scarab event at the destination.
     * If none is found, then there is no Scarab quest.
     * @return True if the destination was found.
     */
    private boolean addScarabWormhole() {
        List<SolarSystem> wormholeSystems = getSystemsWithWormholes();
        Collections.shuffle(wormholeSystems);
        for (SolarSystem system : wormholeSystems) {
            if (system.getSpecialEvent() == null
                    && system.getName().equals("Daled")
                    && system.getName().equals("Nix")
                    && system.getName().equals("Gemulon")) {
                SolarSystem scarabSystem = system.getWormholeDestination();
                scarabSystem.setSpecialEvent(SolarSystem.SpecialEvent.ScarabDestroyed);
                return true;
            }
        }
        return false;
    }

    List<SolarSystem> getSystemsWithWormholes() {
        return solarSystems.stream()
                .filter(SolarSystem::hasWormhole)
                .collect(Collectors.toList());
    }

    private void addSpecialEvents() {
        findSystem("Acamar").setSpecialEvent(SolarSystem.SpecialEvent.MonsterKilled);
        findSystem("Baratas").setSpecialEvent(SolarSystem.SpecialEvent.FlyBaratas);
        findSystem("Melina").setSpecialEvent(SolarSystem.SpecialEvent.FlyMelina);
        findSystem("Regulas").setSpecialEvent(SolarSystem.SpecialEvent.FlyRegulas);
        findSystem("Zalkon").setSpecialEvent(SolarSystem.SpecialEvent.DragonflyDestroyed);
        findSystem("Japori").setSpecialEvent(SolarSystem.SpecialEvent.MedicineDelivery);
        findSystem("Utopia").setSpecialEvent(SolarSystem.SpecialEvent.MoonForSale);
        findSystem("Devidia").setSpecialEvent(SolarSystem.SpecialEvent.JarekGetsOut);
        findSystem("Kravat").setSpecialEvent(SolarSystem.SpecialEvent.WildGetsOut);
    }

    SolarSystem findSystem(String name) {
        Optional<SolarSystem> system = solarSystems
                                        .stream()
                                        .filter(sys -> sys.getName().equals(name))
                                        .findFirst();
        return system.get();
    }

    private void addMercenaries() {
        int i = 1; // skip captain
        while (i < MAX_CREW_MEMBER - 1) {
            Crew mercenary = Crew.getCrew(i);
            int selectedSystemIndex = GetRandom(MAX_SOLAR_SYSTEMS - 1);
            SolarSystem system = solarSystems.get(selectedSystemIndex);

            // One mercenary per system.
            if (system.hasMercenary()) continue;

            // Kravat is a special system, which gets a custom mercenary.
            if (system.getName().equals("Kravat")) continue;

            system.addMercenary(mercenary);

            ++i;
        }
        findSystem("Kravat").addMercenary(Crew.Zeethibal); // TODO: Should this actually be system #255?
    }

    private void shuffleSystems() {
        for (int i = 0; i < MAX_SOLAR_SYSTEMS; ++i) {
            int j = GetRandom(MAX_SOLAR_SYSTEMS - 1);
            if (solarSystems.get(j).hasWormhole()) {
                continue;
            }
            Vector2i tempLocation = solarSystems.get(i).getLocation();
            solarSystems.get(i).setLocation(solarSystems.get(j).getLocation());
            solarSystems.get(j).setLocation(tempLocation);
        }
    }


    private void addSolarSystems(Difficulty difficulty) {
        int i = 0;
        while (i < MAX_SOLAR_SYSTEMS) {
            Vector2i systemLocation = new Vector2i();
            if (i < MAX_WORM_HOLES) {
                // I'm not totally sure what this math is doing.
                systemLocation.x = ((CLOSE_DISTANCE >> 1) - GetRandom(CLOSE_DISTANCE)) + ((GALAXY_WIDTH * (1 + 2*(i%3)))/6);
                systemLocation.y = ((CLOSE_DISTANCE >> 1) - GetRandom(CLOSE_DISTANCE)) + ((GALAXY_HEIGHT * (i < 3 ? 1 : 3))/4);
            } else {
                systemLocation.x = 1 + GetRandom(GALAXY_WIDTH - 2);
                systemLocation.y = 1 + GetRandom(GALAXY_HEIGHT - 2);
            }

            if (i >= MAX_WORM_HOLES) {
                // Do we not care about max/min distance for systems with wormholes?
                int nearestSystemDistance = nearestSystemDistance(systemLocation);
                if (Math.pow(nearestSystemDistance, 2) <= Math.pow(MIN_DISTANCE+1, 2)) continue;
                if (nearestSystemDistance >= CLOSE_DISTANCE) continue;
            }

            SolarSystem system = new SolarSystem(systemLocation, i, difficulty);
            if (i < MAX_WORM_HOLES) {
                system.addWormhole(system);
            }
            solarSystems.add(system);
            ++i;
        }
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
