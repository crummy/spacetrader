package com.malcolmcrum.spacetrader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;
import static com.malcolmcrum.spacetrader.Utils.RandomEnum;

/**
 * Galaxy generator - basically sets up SolarSystems.
 * Created by Malcolm on 8/28/2015.
 */
public class Galaxy {
    private static final Logger logger = LoggerFactory.getLogger(Galaxy.class);

    static final int MAX_WORM_HOLES = 6;
    static final int CLOSE_DISTANCE = 13;   // Each system should have another system within this range
    static final int MIN_DISTANCE = 6;      // No system should have another system this close
    static final int GALAXY_WIDTH = 150;
    static final int GALAXY_HEIGHT = 110;

    public Galaxy(Difficulty difficulty) {
        addSolarSystems(difficulty);

        shuffleSystems();

        addWormholes();

        addMercenaries();

        addFixedSpecialEvents();

        boolean scarabEndpointExists = addScarabWormhole();

        addReactorQuest();

        addArtifactDeliveryQuest();

        addGemulonInvasion();

        addExperimentQuest();

        addScatteredSpecialEvents(scarabEndpointExists);
    }

    private void addScatteredSpecialEvents(boolean scarabEndpointExists) {
        logger.info("SCATTERING EVENTS");
        for (SolarSystem.SpecialEvent event : SolarSystem.SpecialEvent.values()) {
            logger.info("Adding event " + event.getTitle());
            if (event.hasFixedLocation()) {
                logger.info("  Event has fixed location; no need to scatter it.");
                continue;
            }
            for (int occurrence = 0; occurrence < event.getOccurrence(); ++occurrence) {
                boolean keepLooking = true;
                while (keepLooking) {
                    SolarSystem system = RandomEnum(SolarSystem.class, 1);
                    if (system.getSpecialEvent() == null) {
                        if (scarabEndpointExists || event != SolarSystem.SpecialEvent.ScarabStolen) {
                            system.setSpecialEvent(event);
                            logger.info("  Attached event to system: " + system.getName());
                        } else {
                            logger.info("  Skipping - Scarab event but no Scarab endpoint exists.");
                        }
                        keepLooking = false;
                    }
                }
            }
        }
    }

    /**
     * Places the ExperimentFailed event at Daled, and the DangerousExperiment
     * event at the system closest to 70 units away
     */
    private void addExperimentQuest() {
        int minDistance = Integer.MAX_VALUE;
        SolarSystem foundSystem = null;
        for (SolarSystem system : SolarSystem.values()) {
            int distanceToDaled = (int)Vector2i.Distance(system.getLocation(), SolarSystem.Daled.getLocation());
            if (distanceToDaled >= 70
                    && distanceToDaled < minDistance
                    && system.getSpecialEvent() == null) {
                foundSystem = system;
                minDistance = distanceToDaled;
            }
        }
        if (foundSystem != null) {
            foundSystem.setSpecialEvent(SolarSystem.SpecialEvent.DangerousExperiment);
            SolarSystem.Daled.setSpecialEvent(SolarSystem.SpecialEvent.ExperimentFailed);
        } else {
            logger.warn("Could not find a system for DangerousExperiment quest!");
        }
    }

    private void addGemulonInvasion() {
        int minDistance = Integer.MAX_VALUE;
        SolarSystem foundSystem = null;
        for (SolarSystem system : SolarSystem.values()) {
            int distanceToGemulon = (int)Vector2i.Distance(system.getLocation(), SolarSystem.Gemulon.getLocation());
            if (distanceToGemulon >= 70
                    && distanceToGemulon < minDistance
                    && system.getSpecialEvent() == null
                    && system != SolarSystem.Daled
                    && system != SolarSystem.Gemulon) {
                foundSystem = system;
                minDistance = distanceToGemulon;
            }
        }
        if (foundSystem != null) {
            foundSystem.setSpecialEvent(SolarSystem.SpecialEvent.AlienInvasion);
            SolarSystem.Gemulon.setSpecialEvent(SolarSystem.SpecialEvent.GemulonRescued);
        } else {
            logger.warn("Could not find a system for Gemulon invasion quest!");
        }
    }

    private void addReactorQuest() {
        int minDistance = Integer.MAX_VALUE;
        SolarSystem foundSystem = null;
        for (SolarSystem system : SolarSystem.values()) {
            int distanceToNix = (int)Vector2i.Distance(system.getLocation(), SolarSystem.Nix.getLocation());
            if (distanceToNix >= 70
                    && distanceToNix < minDistance
                    && system.getSpecialEvent() == null
                    && system != SolarSystem.Daled
                    && system != SolarSystem.Gemulon) {
                foundSystem = system;
                minDistance = distanceToNix;
            }
        }
        if (foundSystem != null) {
            foundSystem.setSpecialEvent(SolarSystem.SpecialEvent.MorgansReactor);
            SolarSystem.Nix.setSpecialEvent(SolarSystem.SpecialEvent.ReactorDelivered);
        } else {
            logger.warn("Could not find a system for Morgan's Reactor quest!");
        }
    }

    /**
     * Finds any suitable system for the artifact delivery quest
     */
    private void addArtifactDeliveryQuest() {
        SolarSystem foundSystem = null;

        List<SolarSystem> shuffledSystems = Arrays.asList(SolarSystem.values());
        Collections.shuffle(shuffledSystems);

        for (SolarSystem system : shuffledSystems) {
            if (system.getSpecialEvent() == null
                    && system.getTechLevel() == TechLevel.HiTech
                    && system != SolarSystem.Daled
                    && system != SolarSystem.Gemulon) {
                foundSystem = system;
                break;
            }
        }
        if (foundSystem != null) {
            foundSystem.setSpecialEvent(SolarSystem.SpecialEvent.ArtifactDelivery);
        } else {
            logger.warn("Could not find a system for Artifact Delivery quest!");
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
        List<SolarSystem> wormholeSystems = new ArrayList<>();
        for (SolarSystem system : SolarSystem.values()) {
            if (system.hasWormhole()) {
                wormholeSystems.add(system);
            }
        }
        return wormholeSystems;
    }

    private void addFixedSpecialEvents() {
        SolarSystem.Acamar.setSpecialEvent(SolarSystem.SpecialEvent.MonsterKilled);
        SolarSystem.Baratas.setSpecialEvent(SolarSystem.SpecialEvent.FlyBaratas);
        SolarSystem.Melina.setSpecialEvent(SolarSystem.SpecialEvent.FlyMelina);
        SolarSystem.Regulas.setSpecialEvent(SolarSystem.SpecialEvent.FlyRegulas);
        SolarSystem.Zalkon.setSpecialEvent(SolarSystem.SpecialEvent.DragonflyDestroyed);
        SolarSystem.Japori.setSpecialEvent(SolarSystem.SpecialEvent.MedicineDelivery);
        SolarSystem.Utopia.setSpecialEvent(SolarSystem.SpecialEvent.Retirement);
        SolarSystem.Devidia.setSpecialEvent(SolarSystem.SpecialEvent.JarekGetsOut);
        SolarSystem.Kravat.setSpecialEvent(SolarSystem.SpecialEvent.WildGetsOut);
    }


    private void addMercenaries() {
        for (Crew crew : Crew.values()) {
            if (crew == Crew.Captain || crew == Crew.Zeethibal) {
                continue;
            }
            SolarSystem system = RandomEnum(SolarSystem.class);
            while (system.hasMercenary() || system == SolarSystem.Kravat) {
                system = RandomEnum(SolarSystem.class);
            }
            system.addMercenary(crew);
        }
    }

    /**
     * Shuffle systems around, except systems with wormholes??
     */
    private void shuffleSystems() {
        for (SolarSystem system : SolarSystem.values()) {
            SolarSystem otherSystem = RandomEnum(SolarSystem.class);
            if (otherSystem.hasWormhole()) {
                continue;
            }
            Vector2i tempLocation = system.getLocation();
            system.setLocation(otherSystem.getLocation());
            otherSystem.setLocation(tempLocation);
        }
    }

    private void addWormholes() {
        SolarSystem initialSystem = RandomEnum(SolarSystem.class);
        SolarSystem source = initialSystem;
        SolarSystem destination;

        for (int i = 0; i < MAX_WORM_HOLES - 1; ++i) {
            do {
                destination = RandomEnum(SolarSystem.class);
            } while (destination.hasWormhole());
            source.setWormhole(destination);
            logger.info("Pointed wormhole from " + source + " to " + destination);
            source = destination;
        }
        source.setWormhole(initialSystem);
        logger.info("Pointed wormhole from " + source + " to " + initialSystem);
    }


    private void addSolarSystems(Difficulty difficulty) {
        int count = 0;
        for (SolarSystem system : SolarSystem.values()) {
            Vector2i location = new Vector2i();
            boolean neighbourTooClose = false;
            boolean neighbourTooFar = false;
            do {
                if (count < MAX_WORM_HOLES) {
                    // I'm not totally sure what this math is doing.
                    location.x = ((CLOSE_DISTANCE >> 1) - GetRandom(CLOSE_DISTANCE)) + ((GALAXY_WIDTH * (1 + 2 * (count % 3))) / 6);
                    location.y = ((CLOSE_DISTANCE >> 1) - GetRandom(CLOSE_DISTANCE)) + ((GALAXY_HEIGHT * (count < 3 ? 1 : 3)) / 4);
                } else {
                    location.x = 1 + GetRandom(GALAXY_WIDTH - 2);
                    location.y = 1 + GetRandom(GALAXY_HEIGHT - 2);
                    int nearestSystemDistance = nearestSystemDistance(location);
                    neighbourTooClose = (Math.pow(nearestSystemDistance, 2) <= Math.pow(MIN_DISTANCE+1, 2));
                    neighbourTooFar = (nearestSystemDistance >= CLOSE_DISTANCE);
                }
            } while(count > MAX_WORM_HOLES && (neighbourTooClose || neighbourTooFar));

            system.setLocation(location);
            system.initializeTradeItems(difficulty);
            ++count;
        }
    }

    private int nearestSystemDistance(Vector2i point) {
        int nearestDistance = Integer.MAX_VALUE;
        for (SolarSystem system : SolarSystem.values()) {
            if (system.getLocation() == null) {
                // probably hasn't been initialized.
                continue;
            }
            double distance = Vector2i.Distance(point, system.getLocation());
            if (distance < nearestDistance) {
                nearestDistance = (int)distance;
            }
        }
        return nearestDistance;
    }

    public SolarSystem getStartSystem(ShipType ship) {
        SolarSystem system;
        boolean threeNearbySystems, noSpecialEvent, atLeastAgricultural, beforeHiTech;

        do {
            system = RandomEnum(SolarSystem.class);

            atLeastAgricultural = system.getTechLevel().isBeyond(TechLevel.Preagricultural);

            beforeHiTech = system.getTechLevel().isBefore(TechLevel.HiTech);

            noSpecialEvent = (system.getSpecialEvent() == null);

            int neighboursInRange = 0;
            for (SolarSystem nearbySystem : SolarSystem.values()) {
                if (nearbySystem == system) {
                    continue;
                }
                int distanceToNeighbour = (int)Vector2i.Distance(system.getLocation(), nearbySystem.getLocation());
                if (distanceToNeighbour <= ship.getFuelTanks()) {
                    ++neighboursInRange;
                }
            }
            threeNearbySystems = (neighboursInRange >= 3);
        } while(!threeNearbySystems || !noSpecialEvent || !atLeastAgricultural || !beforeHiTech);
        return system;
    }
}
