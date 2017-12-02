package com.malcolmcrum.spacetrader.game

import java.util.*


val GALAXY_WIDTH = 150
val GALAXY_HEIGHT = 110
val MAX_SOLAR_SYSTEM = 120
val MAX_WORMHOLES = 6
val CLOSE_DISTANCE = 13
val MIN_DISTANCE = 6
val MAX_CREW_MEMBER = 31

private data class Position(val x: Int, val y: Int)

class GalaxyGenerator {
    // TODO: This method still is quite C-ish. Needs improvement
    fun generateGalaxy(): List<SolarSystem> {

        val systems = ArrayList<SolarSystem>()
        var i = 0
        while (i < MAX_SOLAR_SYSTEM) {
            // Generate a random-ish location
            val (x, y) = if (systemGetsWormhole(i)) randomPositionNearCenter(i) else randomPositionAnywhere()

            // Ensure other systems are close, but not too close
            val wormholesPlaced = !systemGetsWormhole(i)
            if (wormholesPlaced) {
                val veryCloseSystems = systems.filter { it.distanceTo(x, y) <= MIN_DISTANCE }.count()
                if (veryCloseSystems > 1) {
                    continue
                }
                val closeSystems = systems.filter { it.distanceTo(x, y) <= CLOSE_DISTANCE }.count()
                if (closeSystems == 0) {
                    continue
                }
            }

            val tech = pickRandom(TechLevel.values())
            val politics = pickRandom(Politics.values())
            if (!politics.compatibleWith(tech)) {
                continue
            }

            val hasSpecialResource = (0..5).random() >= 3
            val specialResource = if (hasSpecialResource) pickRandom(SpecialResource.values()) else null

            val size = pickRandom(SystemSize.values())

            val hasStatus = (0..100).random() <= 15
            val status = if (hasStatus) pickRandom(SystemStatus.values()) else null

            val name = "TODO"

            val system = SolarSystem(x, y, tech, politics, specialResource, size, status)
            systems.add(system)
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
                .filter { it.specialEvent != null }
                .filter { it.tech >= TechLevel.AGRICULTURAL }
                .filter { it.tech <= TechLevel.POST_INDUSTRIAL }
                .filter { it.neighbours(ship.fuelTanks, systems) >= 3 }
                .shuffled()
                .getOrNull(0) ?: throw Exception("Couldn't find starting system")
    }

    private fun SolarSystem.distanceTo(x: Int, y: Int): Double {
        return Math.hypot((this.x - x).toDouble(), (this.y - y).toDouble())
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

// generate galaxy
// generate wormholes
// generate systems

// shuffle wormhole order

// initialize mercs

// place special events

// find suitable starting location

