package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.model.*
import com.malcolmcrum.spacetrader.nouns.Ship
import com.malcolmcrum.spacetrader.nouns.pickRandom
import com.malcolmcrum.spacetrader.nouns.random

class OpponentGenerator(private val player: Player, private val destination: SolarSystem) {

    enum class BASE_TYPES {
        POLICE,
        TRADER,
        PIRATE
    }

    private lateinit var ship: ShipType
    private lateinit var gadgets: List<Gadget>
    private lateinit var hold: Hold
    private lateinit var shields: List<Shield>
    private lateinit var weapons: List<Weapon>
    private lateinit var crew: List<CrewMember>
    private var hullStrength: Int = 0

    // Police hunt you down harder if you are villainous or are transporting Jonathan Wild
    fun generatePolice(): Ship {
        var attempts = 1
        if (player.policeRecordScore < PoliceRecord.VILLAIN.score) { // TODO: handle wild Status
            attempts = 3
        } else if (player.policeRecordScore < PoliceRecord.PSYCHOPATH.score) {
            attempts = 5
        }
        attempts = Math.max(1, attempts + player.difficulty.ordinal - Difficulty.NORMAL.ordinal)

        ship = pickShip(BASE_TYPES.POLICE, attempts)
        attempts = Math.max( 1, (player.currentWorth() / 150000) + player.difficulty.ordinal - Difficulty.NORMAL.ordinal)
        gadgets = createGadgets(attempts, player.difficulty, ship.gadgetSlots)
        val cargoBays = ship.cargoBays + (if (gadgets.contains(Gadget.EXTRA_CARGO_BAYS)) 5 else 0)
        hold = createHold(BASE_TYPES.POLICE, cargoBays)
        weapons = createWeapons(attempts)
        shields = createShields(attempts, player.difficulty, ship.shieldSlots)
        hullStrength = createHullStrength(ship.hullStrength)
        crew = createCrew(player.difficulty, ship.crewQuarters)
        return Ship(ship, gadgets, shields, weapons, hold, ship.fuelTanks, crew, 0, ship.hullStrength)
    }

    // Pirates hunt you down harder if you are rich
    fun generatePirate(): Ship {
        var attempts = 1 + (player.currentWorth() / 100000)
        attempts = Math.max( 1, attempts + player.difficulty.ordinal - Difficulty.NORMAL.ordinal)
        ship = pickShip(BASE_TYPES.PIRATE, attempts)
        attempts = Math.max( 1, (player.currentWorth() / 150000) + player.difficulty.ordinal - Difficulty.NORMAL.ordinal)
        gadgets = createGadgets(attempts, player.difficulty, ship.gadgetSlots)
        val cargoBays = ship.cargoBays + (if (gadgets.contains(Gadget.EXTRA_CARGO_BAYS)) 5 else 0)
        hold = createHold(BASE_TYPES.PIRATE, cargoBays)
        weapons = createWeapons(attempts)
        shields = createShields(attempts, player.difficulty, ship.shieldSlots)
        hullStrength = createHullStrength(ship.hullStrength)
        crew = createCrew(player.difficulty, ship.crewQuarters)

        return Ship(ship, gadgets, shields, weapons, hold, ship.fuelTanks, crew, 0, hullStrength)
    }

    fun generateTrader(): Ship {
        var attempts = 1
        ship = pickShip(BASE_TYPES.TRADER, attempts)
        attempts = Math.max( 1, (player.currentWorth() / 150000) + player.difficulty.ordinal - Difficulty.NORMAL.ordinal)

        TODO()
    }

    fun generateMantis(): Ship {
        var attempts = 1 + player.difficulty.ordinal
        ship = ShipType.MANTIS

        TODO()
    }

    private fun createCrew(difficulty: Difficulty, maxCrewMembers: Int): List<CrewMember> {
        val crew = ArrayList<CrewMember>()

        val captain = CrewMember("Captain")
        // TODO: handle wild

        val crewSlots = when (difficulty) {
            Difficulty.IMPOSSIBLE -> maxCrewMembers
            else -> {
                var slots = (1..maxCrewMembers).random()
                if (difficulty >= Difficulty.HARD && slots < maxCrewMembers) {
                    ++slots
                }
                slots
            }
        }

        crew.add(captain)
        repeat(crewSlots) {
            crew.add(CrewMember("Crew"))
        }

        return crew
    }

    private fun createHullStrength(maxStrength: Int): Int {
        if (! shields.isEmpty() && (0 until 10).random() < 7) {
            return maxStrength
        } else {
            var strength = 0
            repeat(5) {
                val extra = 1 + (0 until maxStrength).random()
                if (extra > strength) {
                    strength = extra
                }
            }
            return strength
        }
    }

    private fun createShields(attempts: Int, difficulty: Difficulty, maxSlots: Int): List<Shield> {
        val shields = ArrayList<Shield>()

        val shieldSlots = when {
            maxSlots == 0 -> 0
            difficulty == Difficulty.IMPOSSIBLE -> maxSlots
            else -> {
                var d = (0..maxSlots).random()
                if (d < maxSlots) {
                    if (attempts < 3) {
                        ++d
                    } else {
                        d += (0..1).random()
                    }
                }
                d
            }
        }

        repeat(shieldSlots) {
            var bestShield = randomShield()
            repeat(attempts) {
                val anotherShield = randomShield()
                if (anotherShield > bestShield) {
                    bestShield = anotherShield
                }
            }
            var strength = 0
            repeat(5) {
                val extra = 1 + (0 until bestShield.power).random()
                if (extra > strength) {
                    strength = extra
                }
            }
            shields.add(Shield(bestShield, strength))
        }

        return shields
    }

    fun createWeapons(attempts: Int): List<Weapon> {
        val weapons = ArrayList<Weapon>()

        val weaponSlots = when {
            ship.weaponSlots == 0 -> 0
            ship.weaponSlots == 1 -> 1
            player.difficulty == Difficulty.IMPOSSIBLE -> ship.weaponSlots
            else -> {
                var d = 1 + (0 until ship.weaponSlots).random()
                if (d < ship.weaponSlots) {
                    if (attempts > 4 && player.difficulty >= Difficulty.HARD) {
                        ++d
                    } else if (attempts > 3 || player.difficulty >= Difficulty.HARD) {
                        d += (0..1).random()
                    }
                }
                d
            }
        }

        repeat(weaponSlots) {
            var bestWeapon = randomWeapon()
            repeat(attempts) {
                val anotherWeapon = randomWeapon()
                if (anotherWeapon > bestWeapon) {
                    bestWeapon = anotherWeapon
                }
            }
            weapons.add(bestWeapon)
        }

        return weapons
    }

    fun createHold(type: BASE_TYPES, maxCargo: Int): Hold {
        val hold = Hold(ship)

        repeat(maxCargo) {
            val m: Int
            var sum: Int
            if (player.difficulty >= Difficulty.NORMAL) {
                m = (3 until (maxCargo - 5)).random()
                sum = Math.min(m, 15)
            } else {
                sum = maxCargo
            }
            when (type) {
                BASE_TYPES.POLICE -> sum = 1
                BASE_TYPES.PIRATE -> if (player.difficulty < Difficulty.NORMAL) sum = (sum * 4) / 5 else sum /= player.difficulty.ordinal
            }
            sum = Math.max(sum, 1)

            var i = 0
            while (i < sum) {
                val j = pickRandom(TradeItem.values())
                var k = 1 + (0 until (10 - j.ordinal)).random()
                if (i + k > sum) {
                    k = sum - i
                }
                hold.add(j, k, 0)
                i += k
            }
        }
        return hold
    }

    fun createGadgets(attempts: Int, difficulty: Difficulty, maxGadgets: Int): List<Gadget> {
        val gadgets: MutableList<Gadget> = ArrayList()
        val slots = if (difficulty <= Difficulty.HARD) {
            val count = (0..ship.gadgetSlots).random()
            when {
                count == maxGadgets -> count
                attempts > 4 -> count + 1
                attempts > 2 -> count + (0..1).random()
                else -> count
            }
        } else maxGadgets

        repeat(slots) {
            var bestGadget = randomGadget()
            repeat(attempts) {
                val anotherGadget = randomGadget()
                if (!gadgets.contains(anotherGadget) && anotherGadget > bestGadget) {
                    bestGadget = anotherGadget
                }
            }
            gadgets.add(bestGadget)
        }
        return gadgets
    }

    private fun randomGadget(): Gadget {
        val chance = (0..100).random()
        var sum = 0
        Gadget.values().forEach { gadget ->
            sum += gadget.chance
            if (sum < chance) {
                return gadget
            }
        }
        throw Exception("Couldn't find a gadget; chance: $chance")
    }

    private fun randomWeapon(): Weapon {
        val chance = (0..100).random()
        var sum = 0
        Weapon.values().forEach { weapon ->
            sum += weapon.chance
            if (sum < chance) {
                return weapon
            }
        }
        throw Exception("Couldn't find a weapon; chance: $chance")
    }

    private fun randomShield(): ShieldType {
        val chance = (0..100).random()
        var sum = 0
        ShieldType.values().forEach { shield ->
            sum += shield.chance
            if (sum < chance) {
                return shield
            }
        }
        throw Exception("Couldn't find a shield; chance: $chance")
    }

    private fun pickShip(type: BASE_TYPES, attempts: Int): ShipType {
        val k = Math.max(player.difficulty.ordinal - Difficulty.NORMAL.ordinal, 0)
        var opponentType = if (type == BASE_TYPES.TRADER) 0 else 1

        repeat(attempts) {

            var redo = true
            var i = 0
            while (redo) {

                val d = (0..100).random()
                i = 0
                var sum = ShipType.FLEA.occurrence
                while (sum < d) {
                    if (i >= ShipType.WASP.ordinal - 1) {
                        break
                    }
                    ++i
                    sum += ShipType.values()[i].occurrence
                }

                val potentialShipType = ShipType.values()[i]
                if (type == BASE_TYPES.POLICE &&
                        (potentialShipType.policeLevel < 0 || destination.politics.strengthPolice + k < potentialShipType.policeLevel)) {
                    continue
                } else if (type == BASE_TYPES.PIRATE &&
                        (potentialShipType.pirateLevel < 0 || destination.politics.strengthPirates + k < potentialShipType.pirateLevel)) {
                    continue
                } else if (type == BASE_TYPES.TRADER &&
                        (potentialShipType.traderLevel < 0 || destination.politics.strengthTraders + k < potentialShipType.traderLevel)) {
                    continue
                }

                redo = false
            }

            if (i > opponentType) {
                opponentType = i
            }
        }

        return ShipType.values()[opponentType]
    }

}