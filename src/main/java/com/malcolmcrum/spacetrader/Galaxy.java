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
    public static final int GALAXY_WIDTH = 150;
    public static final int GALAXY_HEIGHT = 110;

    private final PlayerShip ship;
    private final Captain captain;
    private final Difficulty difficulty;
    protected final List<SolarSystem> systems;

    public Galaxy(Captain captain, PlayerShip ship, Difficulty difficulty) {
        this.captain = captain;
        this.ship = ship;
        this.difficulty = difficulty;
        systems = new ArrayList<>();

        addSolarSystems();
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
        logger.debug("SCATTERING EVENTS");
        for (SolarSystem.SpecialEvent event : SolarSystem.SpecialEvent.values()) {
            logger.debug("Adding event " + event.title);
            if (event.hasFixedLocation) {
                logger.trace("  Event has fixed location; no need to scatter it.");
                continue;
            }
            for (int occurrence = 0; occurrence < event.occurrence; ++occurrence) {
                boolean keepLooking = true;
                while (keepLooking) {
                    SolarSystem system = getRandomSystem();
                    if (!system.hasSpecialEvent()) {
                        if (scarabEndpointExists || event != SolarSystem.SpecialEvent.ScarabStolen) {
                            system.setSpecialEvent(event);
                            logger.trace("  Attached event to system: " + system.getType());
                        } else {
                            logger.trace("  Skipping - Scarab event but no Scarab endpoint exists.");
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
        SolarSystem daled = systemNamed(SolarSystem.Name.Daled);
        for (SolarSystem system : systems) {
            int distanceToDaled = distanceBetween(system, daled);
            if (distanceToDaled >= 70
                    && distanceToDaled < minDistance
                    && !system.hasSpecialEvent()) {
                foundSystem = system;
                minDistance = distanceToDaled;
            }
        }
        if (foundSystem != null) {
            foundSystem.setSpecialEvent(SolarSystem.SpecialEvent.DangerousExperiment);
            daled.setSpecialEvent(SolarSystem.SpecialEvent.ExperimentFailed);
        } else {
            logger.warn("Could not find a system for DangerousExperiment quest!");
        }
    }

    private void addGemulonInvasion() {
        int minDistance = Integer.MAX_VALUE;
        SolarSystem foundSystem = null;
        SolarSystem gemulon = systemNamed(SolarSystem.Name.Gemulon);
        for (SolarSystem system : systems) {
            int distanceToGemulon = distanceBetween(system, gemulon);
            if (distanceToGemulon >= 70
                    && distanceToGemulon < minDistance
                    && !system.hasSpecialEvent()
                    && system != systemNamed(SolarSystem.Name.Daled)
                    && system != gemulon) {
                foundSystem = system;
                minDistance = distanceToGemulon;
            }
        }
        if (foundSystem != null) {
            foundSystem.setSpecialEvent(SolarSystem.SpecialEvent.AlienInvasion);
            gemulon.setSpecialEvent(SolarSystem.SpecialEvent.GemulonRescued);
        } else {
            logger.warn("Could not find a system for Gemulon invasion quest!");
        }
    }

    private void addReactorQuest() {
        int minDistance = Integer.MAX_VALUE;
        SolarSystem foundSystem = null;
        SolarSystem nix = systemNamed(SolarSystem.Name.Nix);
        for (SolarSystem system : systems) {
            int distanceToNix = distanceBetween(system, nix);
            if (distanceToNix >= 70
                    && distanceToNix < minDistance
                    && !system.hasSpecialEvent()
                    && system != systemNamed(SolarSystem.Name.Daled)
                    && system != systemNamed(SolarSystem.Name.Gemulon)) {
                foundSystem = system;
                minDistance = distanceToNix;
            }
        }
        if (foundSystem != null) {
            foundSystem.setSpecialEvent(SolarSystem.SpecialEvent.MorgansReactor);
            nix.setSpecialEvent(SolarSystem.SpecialEvent.ReactorDelivered);
        } else {
            logger.warn("Could not find a system for Morgan's Reactor quest!");
        }
    }

    /**
     * Finds any suitable system for the artifact delivery quest
     */
    private boolean addArtifactDeliveryQuest() {
        SolarSystem foundSystem = null;

        Collections.shuffle(systems);

        for (SolarSystem system : systems) {
            if (!system.hasSpecialEvent()
                    && system.getTechLevel() == TechLevel.HiTech
                    && system != systemNamed(SolarSystem.Name.Daled)
                    && system != systemNamed(SolarSystem.Name.Gemulon)) {
                foundSystem = system;
                break;
            }
        }
        if (foundSystem != null) {
            foundSystem.setSpecialEvent(SolarSystem.SpecialEvent.ArtifactDelivery);
        } else {
            logger.warn("Could not find a system for Artifact Delivery quest!");
        }
        return foundSystem != null;
    }

    /**
     * Finds a wormhole that points to a system without a specialEvent,
     * and puts the Scarab event at the destination.
     * If none is found, then there is no Scarab quest.
     * @return True if the destination was found.
     */
    private boolean addScarabWormhole() {
        SolarSystem system = systems.stream()
                .filter(s -> s.hasWormhole()
                        && !s.getWormholeDestination().hasSpecialEvent()
                        && s.getWormholeDestination() != systemNamed(SolarSystem.Name.Daled)
                        && s.getWormholeDestination() != systemNamed(SolarSystem.Name.Nix)
                        && s.getWormholeDestination() != systemNamed(SolarSystem.Name.Gemulon))
                .findFirst()
                .get();
        if (system != null) {
            SolarSystem scarabSystem = system.getWormholeDestination();
            scarabSystem.setSpecialEvent(SolarSystem.SpecialEvent.ScarabDestroyed);
            return true;
        } else {
            return false;
        }
    }

    private SolarSystem systemNamed(SolarSystem.Name name) {
        return systems.stream()
                .filter(s -> s.getType() == name)
                .findFirst().get();
    }

    private void addFixedSpecialEvents() {
        systemNamed(SolarSystem.Name.Acamar).setSpecialEvent(SolarSystem.SpecialEvent.MonsterKilled);
        systemNamed(SolarSystem.Name.Baratas).setSpecialEvent(SolarSystem.SpecialEvent.FlyBaratas);
        systemNamed(SolarSystem.Name.Melina).setSpecialEvent(SolarSystem.SpecialEvent.FlyMelina);
        systemNamed(SolarSystem.Name.Regulas).setSpecialEvent(SolarSystem.SpecialEvent.FlyRegulas);
        systemNamed(SolarSystem.Name.Zalkon).setSpecialEvent(SolarSystem.SpecialEvent.DragonflyDestroyed);
        systemNamed(SolarSystem.Name.Japori).setSpecialEvent(SolarSystem.SpecialEvent.MedicineDelivery);
        systemNamed(SolarSystem.Name.Utopia).setSpecialEvent(SolarSystem.SpecialEvent.Retirement);
        systemNamed(SolarSystem.Name.Devidia).setSpecialEvent(SolarSystem.SpecialEvent.JarekGetsOut);
        systemNamed(SolarSystem.Name.Kravat).setSpecialEvent(SolarSystem.SpecialEvent.WildGetsOut);
    }


    private void addMercenaries() {
        for (int i = 0; i < Mercenary.getTotalMercenaries() - 1; ++i) { // skip zeethibal
            SolarSystem system = getRandomSystem();
            while (system.hasMercenary() || system == systemNamed(SolarSystem.Name.Kravat)) {
                system = getRandomSystem();
            }
            system.addMercenary(new Mercenary(i));
        }
    }

    /**
     * Shuffle systems around, except systems with wormholes??
     */
    private void shuffleSystems() {
        for (SolarSystem system : systems) {
            SolarSystem otherSystem = getRandomSystem();
            if (otherSystem.hasWormhole()) {
                continue;
            }
            Vector2i tempLocation = system.getLocation();
            system.setLocation(otherSystem.getLocation());
            otherSystem.setLocation(tempLocation);
        }
    }

    private void addWormholes() {
        SolarSystem initialSystem = getRandomSystem();
        SolarSystem source = initialSystem;
        SolarSystem destination;

        for (int i = 0; i < MAX_WORM_HOLES - 1; ++i) {
            do {
                destination = getRandomSystem();
            } while (destination.hasWormhole() || source == destination);
            source.setWormhole(destination);
            logger.debug("Pointed wormhole from " + source.getName() + " to " + destination.getName());
            source = destination;
        }
        source.setWormhole(initialSystem);
        logger.debug("Pointed wormhole from " + source.getName() + " to " + initialSystem.getName());
    }


    private void addSolarSystems() {

        for (int i = 0; i < SolarSystem.GetMaxSystems(); ++i) {
            Vector2i location = new Vector2i();
            boolean neighbourTooClose = false;
            boolean neighbourTooFar = false;
            do {
                if (i < MAX_WORM_HOLES) {
                    // I'm not totally sure what this math is doing.
                    location.x = ((CLOSE_DISTANCE >> 1) - GetRandom(CLOSE_DISTANCE)) + ((GALAXY_WIDTH * (1 + 2 * (i % 3))) / 6);
                    location.y = ((CLOSE_DISTANCE >> 1) - GetRandom(CLOSE_DISTANCE)) + ((GALAXY_HEIGHT * (i < 3 ? 1 : 3)) / 4);
                } else {
                    location.x = 1 + GetRandom(GALAXY_WIDTH - 2);
                    location.y = 1 + GetRandom(GALAXY_HEIGHT - 2);
                    int nearestSystemDistance = nearestSystemDistance(location);
                    neighbourTooClose = (Math.pow(nearestSystemDistance, 2) <= Math.pow(MIN_DISTANCE+1, 2));
                    neighbourTooFar = (nearestSystemDistance >= CLOSE_DISTANCE);
                }
            } while(i > 0 && (neighbourTooClose || neighbourTooFar));

            SolarSystem system = new SolarSystem(captain, ship, difficulty, i);
            system.setLocation(location);
            systems.add(system);
        }
    }

    private int nearestSystemDistance(Vector2i point) {
        int nearestDistance = Integer.MAX_VALUE;
        for (SolarSystem system : systems) {
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

    /**
     * Finds the perfect star system: one with at least three systems in range of
     * current ship, with no special event, at least agricultural tech level, but not
     * hitech.
     * @param maxRange Maximum range of current ship.
     * @return A goldilock system in the galaxy.
     */
    public SolarSystem getStartSystem(int maxRange) {
        SolarSystem system;
        boolean threeNearbySystems, noSpecialEvent, atLeastAgricultural, beforeHiTech;

        do {
            system = getRandomSystem();

            atLeastAgricultural = system.getTechLevel().isBeyond(TechLevel.Preagricultural);

            beforeHiTech = system.getTechLevel().isBefore(TechLevel.HiTech);

            noSpecialEvent = !system.hasSpecialEvent();

            int neighboursInRange = 0;
            for (SolarSystem nearbySystem : systems) {
                if (nearbySystem == system) {
                    continue;
                }
                int distanceToNeighbour = distanceBetween(nearbySystem, system);
                if (distanceToNeighbour <= maxRange) {
                    ++neighboursInRange;
                }
            }
            threeNearbySystems = (neighboursInRange >= 3);
        } while(!threeNearbySystems || !noSpecialEvent || !atLeastAgricultural || !beforeHiTech);
        return system;
    }

    public SolarSystem getRandomSystem() {
        int index = GetRandom(systems.size());
        return systems.get(index);
    }

    public int distanceBetween(SolarSystem origin, SolarSystem destination) {
        return (int)Vector2i.Distance(origin.getLocation(), destination.getLocation());
    }

    /**
     * Randomly assigns statuses to solar systems. Used on arrival to a system.
     * 15% chance to go from Interesting -> Uneventful.
     * 15% chance to go from Uneventful -> Interesting.
     */
    public void shuffleStatuses() {
        for (SolarSystem system : systems) {
            if (system.getStatus() != SolarSystem.Status.Uneventful) {
                if (GetRandom(100) < 15) {
                    system.setStatus(SolarSystem.Status.Uneventful);
                }
            } else if (GetRandom(100) < 15) {
                system.setStatus(RandomEnum(SolarSystem.Status.class, 1));
            }
        }
    }

    public void changeTradeItemQuantities() {
        for (SolarSystem system : systems) {
            system.getMarket().performTradeCountdown();
        }
    }

    public boolean wormholeExistsBetween(SolarSystem system, SolarSystem destination) {
        return system.getWormholeDestination() == destination;
    }

    public List<SolarSystem> getSystems() {
        return systems;
    }
}
