package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.model.*
import com.malcolmcrum.spacetrader.nouns.Ship
import com.malcolmcrum.spacetrader.nouns.pickRandom
import com.malcolmcrum.spacetrader.nouns.random
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger(OpponentGenerator::class.java)!!

class OpponentGenerator(private val difficulty: Difficulty,
                        private val currentWorth: Int,
                        private val policeRecord: PoliceRecord,
                        private val destination: SolarSystem) {

    enum class BASE_TYPES {
        POLICE,
        TRADER,
        PIRATE
    }

    // Police hunt you down harder if you are villainous or are transporting Jonathan Wild
    fun generatePolice(): Ship {
        var attempts = Math.max(1, policeRecord.policeGenerateAttempts() + difficulty.ordinal - Difficulty.NORMAL.ordinal)

        val ship = pickShip(BASE_TYPES.POLICE, attempts)
        attempts = Math.max( 1, (currentWorth / 150000) + difficulty.ordinal - Difficulty.NORMAL.ordinal)
        val captain = createCaptain()
        val gadgets = createGadgets(attempts, difficulty, ship.gadgetSlots)
        val cargoBays = ship.cargoBays + (if (gadgets.contains(Gadget.EXTRA_CARGO_BAYS)) 5 else 0)
        val cargo = createHold(BASE_TYPES.POLICE, cargoBays)
        val weapons = createWeapons(attempts, ship.weaponSlots)
        val shields = createShields(attempts, difficulty, ship.shieldSlots)
        val hullStrength = createHullStrength(ship.hullStrength, !shields.isEmpty())
        val crew = createCrew(difficulty, ship.crewQuarters)
        return Ship(ship, captain, gadgets, shields, weapons, crew, cargo, ship.fuelTanks, hullStrength)
    }

    private fun createCaptain(): CrewMember {
        // TODO: handle wild or famous captain
        return CrewMember("Captain")
    }

    // Pirates hunt you down harder if you are rich
    fun generatePirate(): Ship {
        var attempts = 1 + (currentWorth / 100000)
        attempts = Math.max( 1, attempts + difficulty.ordinal - Difficulty.NORMAL.ordinal)
        val ship = pickShip(BASE_TYPES.PIRATE, attempts)
        val captain = createCaptain()
        attempts = Math.max( 1, (currentWorth / 150000) + difficulty.ordinal - Difficulty.NORMAL.ordinal)
        val gadgets = createGadgets(attempts, difficulty, ship.gadgetSlots)
        val cargoBays = ship.cargoBays + (if (gadgets.contains(Gadget.EXTRA_CARGO_BAYS)) 5 else 0)
        val hold = createHold(BASE_TYPES.PIRATE, cargoBays)
        val weapons = createWeapons(attempts, ship.weaponSlots)
        val shields = createShields(attempts, difficulty, ship.shieldSlots)
        val hullStrength = createHullStrength(ship.hullStrength, !shields.isEmpty())
        val crew = createCrew(difficulty, ship.crewQuarters)

        return Ship(ship, captain, gadgets, shields, weapons, crew, hold, ship.fuelTanks, hullStrength)
    }

    fun generateTrader(): Ship {
        var attempts = 1
        val ship = pickShip(BASE_TYPES.TRADER, attempts)
        attempts = Math.max( 1, (currentWorth / 150000) + difficulty.ordinal - Difficulty.NORMAL.ordinal)

        TODO()
    }

    fun generateMantis(): Ship {
        var attempts = 1 + difficulty.ordinal
        val ship = ShipType.MANTIS

        TODO()
    }

    private fun createCrew(difficulty: Difficulty, maxCrewMembers: Int): MutableList<CrewMember> {
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

    private fun createHullStrength(maxStrength: Int, hasShield: Boolean): Int {
        if (hasShield && (0 until 10).random() < 7) {
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

    private fun createShields(attempts: Int, difficulty: Difficulty, maxSlots: Int): MutableList<Shield> {
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

    fun createWeapons(attempts: Int, maxWeapons: Int): MutableList<Weapon> {
        val weapons = ArrayList<Weapon>()

        val weaponSlots = when {
            maxWeapons == 0 -> 0
            maxWeapons == 1 -> 1
            difficulty == Difficulty.IMPOSSIBLE -> maxWeapons
            else -> {
                var d = (1..maxWeapons).random()
                if (d < maxWeapons) {
                    if (attempts > 4 && difficulty >= Difficulty.HARD) {
                        ++d
                    } else if (attempts > 3 || difficulty >= Difficulty.HARD) {
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
            log.debug("After $attempts attempts, chose $bestWeapon")
            weapons.add(bestWeapon)
        }

        return weapons
    }

    fun createHold(type: BASE_TYPES, maxCargo: Int): MutableList<Cargo> {
        val cargo = ArrayList<Cargo>()

        repeat(maxCargo) {
            val m: Int
            var sum: Int
            if (difficulty >= Difficulty.NORMAL) {
                m = (3 until (maxCargo - 5)).random()
                sum = Math.min(m, 15)
            } else {
                sum = maxCargo
            }
            when (type) {
                BASE_TYPES.POLICE -> sum = 1
                BASE_TYPES.PIRATE -> if (difficulty < Difficulty.NORMAL) sum = (sum * 4) / 5 else sum /= difficulty.ordinal
            }
            sum = Math.max(sum, 1)

            var i = 0
            while (i < sum) {
                val item = pickRandom(TradeItem.values())
                var amount = 1 + (0 until (10 - item.ordinal)).random()
                if (i + amount > sum) {
                    amount = sum - i
                }
                repeat(amount) {
                    cargo.add(Cargo(item, 0))
                }
                i += amount
            }
        }
        return cargo
    }

    fun createGadgets(attempts: Int, difficulty: Difficulty, maxGadgets: Int): MutableList<Gadget> {
        val gadgets: MutableList<Gadget> = ArrayList()
        val slots = if (difficulty <= Difficulty.HARD) {
            val count = (0..maxGadgets).random()
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
            if (chance < sum) {
                return gadget
            }
        }
        throw Exception("Couldn't find a gadget; chance: $chance")
    }

    fun randomWeapon(): Weapon {
        val chance = (0..100).random()
        var sum = 0
        Weapon.values().forEach { weapon ->
            sum += weapon.chance
            if (chance < sum) {
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
            if (chance < sum) {
                return shield
            }
        }
        throw Exception("Couldn't find a shield; chance: $chance")
    }

    private fun pickShip(type: BASE_TYPES, attempts: Int): ShipType {
        val k = Math.max(difficulty.ordinal - Difficulty.NORMAL.ordinal, 0)
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