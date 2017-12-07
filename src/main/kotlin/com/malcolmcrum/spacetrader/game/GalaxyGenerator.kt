package com.malcolmcrum.spacetrader.game

import org.slf4j.LoggerFactory
import java.util.*


const val GALAXY_WIDTH = 150
const val GALAXY_HEIGHT = 110
const val MAX_SOLAR_SYSTEM = 120
const val MAX_WORMHOLES = 6
const val CLOSE_DISTANCE = 13
const val MIN_DISTANCE = 6
const val MAX_CREW_MEMBER = 31

private data class Position(val x: Int, val y: Int)

class GalaxyGenerator {
    val log = LoggerFactory.getLogger(GalaxyGenerator::class.java)

    // TODO: This method still is quite C-ish. Needs improvement
    fun generateGalaxy(): List<SolarSystem> {

        val systems = ArrayList<SolarSystem>()
        var i = 0

        while (i < MAX_SOLAR_SYSTEM) {
            // Generate a random-ish location, especially near the center if a wormhole
            val wormholesPlaced = !systemGetsWormhole(i)
            val (x, y) = if (!wormholesPlaced) randomPositionNearCenter(i) else randomPositionAnywhere()
            log.trace("Considering $x,$y")

            // Ensure other systems are close, but not too close
            if (wormholesPlaced) {
                val veryCloseSystems = systems.count { it.distanceTo(x, y) <= MIN_DISTANCE }
                if (veryCloseSystems > 0) {
                    log.trace("Rejected $x,$y; too close to $veryCloseSystems systems")
                    continue
                }
                val closeSystems = systems.count { it.distanceTo(x, y) <= CLOSE_DISTANCE }
                if (closeSystems == 0) {
                    log.trace("Rejected $x,$y; no systems in range")
                    continue
                }
            }

            val tech = pickRandom(TechLevel.values())
            val politics = pickRandom(Politics.values())
            if (!politics.compatibleWith(tech)) {
                log.trace("Rejected $x,$y; politics incompatible")
                continue
            }

            val hasSpecialResource = (0..5).random() >= 3
            val specialResource = if (hasSpecialResource) pickRandom(SpecialResource.values()) else null

            val size = pickRandom(SystemSize.values())

            val hasStatus = (0..100).random() <= 15
            val status = if (hasStatus) pickRandom(SystemStatus.values()) else null

            val name = "TODO"

            val system = SolarSystem(name, x, y, tech, politics, specialResource, size, status)
            if (!wormholesPlaced) {
                system.wormholeDestination = system
            }
            systems.add(system)
            log.debug("Added system: $system.")
            ++i
        }

        // TODO: shuffle system locations

        // TODO: shuffle wormhole orders

        return systems
    }

    fun placeMercenaries(systems: List<SolarSystem>) {
        val mercenaries = ArrayList<CrewMember>()
        for (i in 0..MAX_CREW_MEMBER) {
            val system = pickRandom(systems.filter { it.mercenary == null })

            // TODO: avoid Kravat
            val name = "mercenary $i"// TODO: generate names

            val mercenary = CrewMember(name, randomSkillLevel(), randomSkillLevel(), randomSkillLevel(), randomSkillLevel(), system)
            mercenaries.add(mercenary)
        }

        // TODO: put Zeethibal on system 255?
    }

    fun placeSpecialEvents(systems: List<SolarSystem>) {
        // TODO
    }

    fun findStartingSystem(ship: ShipType, systems: List<SolarSystem>): SolarSystem {
        return systems
                .filter { it.specialEvent == null }
                .filter { it.tech >= TechLevel.AGRICULTURAL }
                .filter { it.tech <= TechLevel.POST_INDUSTRIAL }
                .filter { it.neighbours(ship.fuelTanks, systems) >= 3 }
                .shuffled()
                .getOrNull(0) ?: throw Exception("Couldn't find starting system")
    }

    private fun randomPositionNearCenter(i: Int): Position {
        val x = (CLOSE_DISTANCE shr 1) - (0..CLOSE_DISTANCE).random() + GALAXY_WIDTH * (1 + 2 * (i % 3)) / 6
        val y = (CLOSE_DISTANCE shr 1) - (0..CLOSE_DISTANCE).random() + GALAXY_HEIGHT * if (i < 3) 1 else 3 / 4
        return Position(x, y)
    }

    private fun randomPositionAnywhere(): Position {
        val x = 1 + (0..(GALAXY_WIDTH - 2)).random()
        val y = 1 + (0..(GALAXY_HEIGHT - 2)).random()
        return Position(x, y)
    }

    private fun systemGetsWormhole(index: Int): Boolean {
        return index < MAX_WORMHOLES
    }

    private fun SolarSystem.neighbours(range: Int, systems: List<SolarSystem>): Int {
        return systems.filter { it != this }.filter { it.distanceTo(this.x, this.y) <= range }.count()
    }
}