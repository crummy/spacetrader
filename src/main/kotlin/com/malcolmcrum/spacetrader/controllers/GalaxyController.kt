package com.malcolmcrum.spacetrader.controllers

import com.malcolmcrum.spacetrader.model.*
import com.malcolmcrum.spacetrader.nouns.pickRandom
import com.malcolmcrum.spacetrader.nouns.random
import com.malcolmcrum.spacetrader.nouns.randomSkillLevel
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

private val log = LoggerFactory.getLogger(GalaxyController::class.java)!!

// TODO: Is this really a controller?
class GalaxyController {

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

            val name = names[i]

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

    fun setPrices(systems: List<SolarSystem>, difficulty: Difficulty) {
        systems.forEach { system ->
            // TODO: Remove unneeded dependencies out of here! Split into controller and view!
            MarketController(system.market, system, difficulty).updatePrices()
        }
    }

    fun setAmounts(systems: List<SolarSystem>, difficulty: Difficulty) {
        systems.forEach { system->
            // TODO: Remove unneeded dependencies out of here! Split into controller and view!

            MarketController(system.market, system, difficulty).updateAmounts()
        }
    }
}

private val names = listOf(
        "Acamar",
        "Adahn",		// The alternate personality for The Nameless One in "Planescape: Torment"
        "Aldea",
        "Andevian",
        "Antedi",
        "Balosnee",
        "Baratas",
        "Brax",			// One of the heroes in Master of Magic
        "Bretel",		// This is a Dutch device for keeping your pants up.
        "Calondia",
        "Campor",
        "Capelle",		// The city I lived in while programming this game
        "Carzon",
        "Castor",		// A Greek demi-god
        "Cestus",
        "Cheron",
        "Courteney",	// After Courteney Cox...
        "Daled",
        "Damast",
        "Davlos",
        "Deneb",
        "Deneva",
        "Devidia",
        "Draylon",
        "Drema",
        "Endor",
        "Esmee",		// One of the witches in Pratchett's Discworld
        "Exo",
        "Ferris",		// Iron
        "Festen",		// A great Scandinavian movie
        "Fourmi",		// An ant, in French
        "Frolix",		// A solar system in one of Philip K. Dick's novels
        "Gemulon",
        "Guinifer",		// One way of writing the name of king Arthur's wife
        "Hades",		// The underworld
        "Hamlet",		// From Shakespeare
        "Helena",		// Of Troy
        "Hulst",		// A Dutch plant
        "Iodine",		// An element
        "Iralius",
        "Janus",		// A seldom encountered Dutch boy's name
        "Japori",
        "Jarada",
        "Jason",		// A Greek hero
        "Kaylon",
        "Khefka",
        "Kira",			// My dog's name
        "Klaatu",		// From a classic SF movie
        "Klaestron",
        "Korma",		// An Indian sauce
        "Kravat",		// Interesting spelling of the French word for "tie"
        "Krios",
        "Laertes",		// A king in a Greek tragedy
        "Largo",
        "Lave",			// The starting system in Elite
        "Ligon",
        "Lowry",		// The name of the "hero" in Terry Gilliam's "Brazil"
        "Magrat",		// The second of the witches in Pratchett's Discworld
        "Malcoria",
        "Melina",
        "Mentar",		// The Psilon home system in Master of Orion
        "Merik",
        "Mintaka",
        "Montor",		// A city in Ultima III and Ultima VII part 2
        "Mordan",
        "Myrthe",		// The name of my daughter
        "Nelvana",
        "Nix",			// An interesting spelling of a word meaning "nothing" in Dutch
        "Nyle",			// An interesting spelling of the great river
        "Odet",
        "Og",			// The last of the witches in Pratchett's Discworld
        "Omega",		// The end of it all
        "Omphalos",		// Greek for navel
        "Orias",
        "Othello",		// From Shakespeare
        "Parade",		// This word means the same in Dutch and in English
        "Penthara",
        "Picard",		// The enigmatic captain from ST:TNG
        "Pollux",		// Brother of Castor
        "Quator",
        "Rakhar",
        "Ran",			// A film by Akira Kurosawa
        "Regulas",
        "Relva",
        "Rhymus",
        "Rochani",
        "Rubicum",		// The river Ceasar crossed to get into Rome
        "Rutia",
        "Sarpeidon",
        "Sefalla",
        "Seltrice",
        "Sigma",
        "Sol",			// That's our own solar system
        "Somari",
        "Stakoron",
        "Styris",
        "Talani",
        "Tamus",
        "Tantalos",		// A king from a Greek tragedy
        "Tanuga",
        "Tarchannen",
        "Terosa",
        "Thera",		// A seldom encountered Dutch girl's name
        "Titan",		// The largest moon of Jupiter
        "Torin",		// A hero from Master of Magic
        "Triacus",
        "Turkana",
        "Tyrus",
        "Umberlee",		// A god from AD&D, which has a prominent role in Baldur's Gate
        "Utopia",		// The ultimate goal
        "Vadera",
        "Vagra",
        "Vandor",
        "Ventax",
        "Xenon",
        "Xerxes",		// A Greek hero
        "Yew",			// A city which is in almost all of the Ultima games
        "Yojimbo",		// A film by Akira Kurosawa
        "Zalkon",
        "Zuul"			// From the first Ghostbusters movie
)