package com.malcolmcrum.spacetrader.model

import com.malcolmcrum.spacetrader.controllers.MAX_SKILL
import com.malcolmcrum.spacetrader.model.SpecialResource.*
import com.malcolmcrum.spacetrader.model.SystemStatus.*
import com.malcolmcrum.spacetrader.model.TradeItem.*
import com.malcolmcrum.spacetrader.nouns.random

val MAX_RANGE = 30

enum class ShipType(val text: String,
                    val cargoBays: Int,
                    val weaponSlots: Int,
                    val shieldSlots: Int,
                    val gadgetSlots: Int,
                    val crewQuarters: Int,
                    val fuelTanks: Int,
                    val minTechLevel: TechLevel?,
                    val costOfFuel: Int,
                    val basePrice: Int,
                    val baseBounty: Int,
                    val occurrence: Int,
                    val hullStrength: Int,
                    val policeLevel: Int,
                    val pirateLevel: Int,
                    val traderLevel: Int,
                    val repairCosts: Int,
                    val size: Int) {

    FLEA("Flea", 10, 0, 0, 0, 1, MAX_RANGE, TechLevel.of(4), 1, 2000, 5, 2, 25, -1, -1, 0, 1, 0),
    GNAT("Gnat", 15, 1, 0, 1, 1, 14, TechLevel.of(5), 2, 10000, 50, 28, 100, 0, 0, 0, 1, 1),
    FIREFLY("Firefly", 20, 1, 1, 1, 1, 17, TechLevel.of(5), 3, 25000, 75, 20, 100, 0, 0, 0, 1, 1),
    MOSQUITO("Mosquito", 15, 2, 1, 1, 1, 13, TechLevel.of(5), 5, 30000, 100, 20, 100, 0, 1, 0, 1, 1),
    BUMBLEBEE("Bumblebee", 25, 1, 2, 2, 2, 15, TechLevel.of(5), 7, 60000, 125, 15, 100, 1, 1, 0, 1, 2),
    BEETLE("Beetle", 50, 0, 1, 1, 3, 14, TechLevel.of(5), 10, 80000, 50, 3, 50, -1, -1, 0, 1, 2),
    HORNET("Hornet", 20, 3, 2, 1, 2, 16, TechLevel.of(6), 15, 100000, 200, 6, 150, 2, 3, 1, 2, 3),
    GRASSHOPPER("Grasshopper", 30, 2, 2, 3, 3, 15, TechLevel.of(6), 15, 150000, 300, 2, 150, 3, 4, 2, 3, 3),
    TERMITE("Termite", 60, 1, 3, 2, 3, 13, TechLevel.of(7), 20, 225000, 300, 2, 200, 4, 5, 3, 4, 4),
    WASP("Wasp", 35, 3, 2, 2, 3, 14, TechLevel.of(7), 20, 300000, 500, 2, 200, 5, 6, 4, 5, 4),
    // The ships below can't be bought
    SPACE_MONSTER("Space monster", 0, 3, 0, 0, 1, 1, null, 1, 500000, 0, 0, 500, 8, 8, 8, 1, 4),
    DRAGONFLY("Dragonfly", 0, 2, 3, 2, 1, 1, null, 1, 500000, 0, 0, 10, 8, 8, 8, 1, 1),
    MANTIS("Mantis", 0, 3, 1, 3, 3, 1, null, 1, 500000, 0, 0, 300, 8, 8, 8, 1, 2),
    SCARAB("Scarab", 20, 2, 0, 0, 2, 1, null, 1, 500000, 0, 0, 400, 8, 8, 8, 1, 3),
    BOTTLE("Bottle", 0, 0, 0, 0, 0, 1, null, 1, 100, 0, 0, 10, 8, 8, 8, 1, 1);

}

enum class TechLevel(val text: String) {
    PRE_AGRICULTURAL("Pre-agricultural"),
    AGRICULTURAL("Agricultural"),
    MEDIEVAL("Medieval"),
    RENAISSANCE("Renaissance"),
    EARLY_INDUSTRIAL("Early Industrial"),
    INDUSTRIAL("Industrial"),
    POST_INDUSTRIAL("Post-industrial"),
    HI_TECH("Hi-tech");

    companion object {
        fun of(index: Int) = TechLevel.values()[index]
    }
}

enum class Politics(val text: String,
                    val illegalGoodsFoundResponseLevel: Int,
                    val strengthPolice: Int,
                    val strengthPirates: Int,
                    val strengthTraders: Int,
                    val minTechLevel: TechLevel,
                    val maxTechLevel: TechLevel,
                    val bribeLevel: Int,
                    val drugsOk: Boolean,
                    val firearmsOk: Boolean,
                    val desiredTradeItem: TradeItem?) {

    ANARCHY("Anarchy", 0, 0, 7, 1, TechLevel.of(0), TechLevel.of(5), 7, true, true, FOOD),
    CAPITALISM("Capitalist State", 2, 3, 2, 7, TechLevel.of(4), TechLevel.of(7), 1, true, true, ORE),
    COMMUNISM("Communist State", 6, 6, 4, 4, TechLevel.of(1), TechLevel.of(5), 5, true, true, null),
    CONFEDERACY("Confederacy", 5, 4, 3, 5, TechLevel.of(1), TechLevel.of(6), 3, true, true, GAMES),
    CORPORATE("Corporate State", 2, 6, 2, 7, TechLevel.of(4), TechLevel.of(7), 2, true, true, ROBOTS),
    CYBERNETIC("Cybernetic State", 0, 7, 7, 5, TechLevel.of(6), TechLevel.of(7), 0, false, false, ORE),
    DEMOCRACY("Democracy", 4, 3, 2, 5, TechLevel.of(3), TechLevel.of(7), 2, true, true, GAMES),
    DICTATORSHIP("Dictatorship", 3, 4, 5, 3, TechLevel.of(0), TechLevel.of(7), 2, true, true, null),
    FASCIST("Fascist State", 7, 7, 7, 1, TechLevel.of(4), TechLevel.of(7), 0, false, true, MACHINES),
    FEUDAL("Feudal State", 1, 1, 6, 2, TechLevel.of(0), TechLevel.of(3), 6, true, true, FIREARMS),
    MILITARY("Military State", 7, 7, 0, 6, TechLevel.of(2), TechLevel.of(7), 0, false, true, ROBOTS),
    MONARCHY("Monarchy", 3, 4, 3, 4, TechLevel.of(0), TechLevel.of(5), 4, true, true, MEDICINE),
    PACIFIST("Pacifist State", 7, 2, 1, 5, TechLevel.of(0), TechLevel.of(3), 1, true, false, null),
    SOCIALIST("Socialist State", 4, 2, 5, 3, TechLevel.of(0), TechLevel.of(5), 6, true, true, null),
    SATORI("State of Satori", 0, 1, 1, 1, TechLevel.of(0), TechLevel.of(1), 0, false, false, null),
    TECHNOCRACY("Technocracy", 1, 6, 3, 6, TechLevel.of(4), TechLevel.of(7), 2, true, true, WATER),
    THEOCRACY("Theocracy", 5, 6, 1, 4, TechLevel.of(0), TechLevel.of(4), 0, true, true, NARCOTICS);

    fun compatibleWith(tech: TechLevel): Boolean {
        return tech in minTechLevel..maxTechLevel
    }
}

enum class TradeItem(val text: String,
                     val techRequiredForProduction: TechLevel,
                     val techRequiredForUsage: TechLevel,
                     val techOptimalProduction: TechLevel,
                     val priceLowTech: Int,
                     val priceIncreasePerTechLevel: Int,
                     val priceVariance: Int,
                     val doublePriceStatus: SystemStatus?,
                     val cheapResource: SpecialResource?,
                     val expensiveResource: SpecialResource?,
                     val minTradePrice: Int,
                     val maxTradePrice: Int,
                     val roundOff: Int) {
    WATER("Water", TechLevel.of(0), TechLevel.of(0), TechLevel.of(2), 30, +3, 4, DROUGHT, LOTS_OF_WATER, DESERT, 30, 50, 1),
    FURS("Furs", TechLevel.of(0), TechLevel.of(0), TechLevel.of(0), 250, +10, 10, COLD, RICH_FAUNA, LIFELESS, 230, 280, 5),
    FOOD("Food", TechLevel.of(1), TechLevel.of(0), TechLevel.of(1), 100, +5, 5, CROP_FAILURE, RICH_SOIL, POOR_SOIL, 90, 160, 5),
    ORE("Ore", TechLevel.of(2), TechLevel.of(2), TechLevel.of(3), 350, +20, 10, WAR, MINERAL_RICH, MINERAL_POOR, 350, 420, 10),
    GAMES("Games", TechLevel.of(3), TechLevel.of(1), TechLevel.of(6), 250, -10, 5, BOREDOM, ARTISTIC, null, 160, 270, 5),
    FIREARMS("Firearms", TechLevel.of(3), TechLevel.of(1), TechLevel.of(5), 1250, -75, 100, WAR, WARLIKE, null, 600, 1100, 25),
    MEDICINE("Medicine", TechLevel.of(4), TechLevel.of(1), TechLevel.of(6), 650, -20, 10, PLAGUE, LOTS_OF_HERBS, null, 400, 700, 25),
    MACHINES("Machines", TechLevel.of(4), TechLevel.of(3), TechLevel.of(5), 900, -30, 5, LACK_OF_WORKERS, null, null, 600, 800, 25),
    NARCOTICS("Narcotics", TechLevel.of(5), TechLevel.of(0), TechLevel.of(5), 3500, -125, 150, BOREDOM, WEIRD_MUSHROOMS, null, 2000, 3000, 50),
    ROBOTS("Robots", TechLevel.of(6), TechLevel.of(4), TechLevel.of(7), 5000, -150, 100, LACK_OF_WORKERS, null, null, 3500, 5000, 100)
}

enum class Difficulty {
    BEGINNER,
    EASY,
    NORMAL,
    HARD,
    IMPOSSIBLE
}

enum class SystemStatus(val text: String) {
    WAR("at war"),
    PLAGUE("ravaged by a plague"),
    DROUGHT("suffering from a drought"),
    BOREDOM("suffering from extreme boredom"),
    COLD("suffering from a cold spell"),
    CROP_FAILURE("suffering from a crop failure"),
    LACK_OF_WORKERS("lacking enough workers")
}

enum class SpecialResource(val text: String) {
    LIFELESS("Lifeless"),
    LOTS_OF_WATER("Sweetwater oceans"),
    RICH_FAUNA("Rich fauna"),
    RICH_SOIL("Rich soil"),
    POOR_SOIL("Poor soil"),
    DESERT("Desert"),
    MINERAL_RICH("Mineral rich"),
    MINERAL_POOR("Mineral poor"),
    ARTISTIC("Artistic populace"),
    WARLIKE("Warlike populace"),
    LOTS_OF_HERBS("Special herbs"),
    WEIRD_MUSHROOMS("Weird mushrooms")
}

enum class SystemSize(val text: String) {
    TINY("Tiny"),
    SMALL("Small"),
    MEDIUM("Medium"),
    LARGE("Large"),
    HUGE("Huge")
}

enum class ShieldType(val text: String,
                      val power: Int,
                      val basePrice: Int,
                      val minTechLevel: TechLevel?,
                      val chance: Int) {
    ENERGY("Energy shield", 100,  5000, TechLevel.of(5), 70 ),
    REFLECTIVE("Reflective shield",  200, 20000, TechLevel.of(6), 30 ),
    // The shields below can't be bought
    LIGHTNING("Lightning shield",   350, 45000, null,  0 );

    fun sellPrice(): Int = basePrice * 3/4
}

enum class Gadget(val text: String,
                  val basePrice: Int,
                  val minTechLevel: TechLevel?,
                  val chance: Int) {
    EXTRA_CARGO_BAYS("5 extra cargo bays",2500, TechLevel.of(4), 35), // 5 extra holds
    AUTOREPAIR("Auto-repair system",7500, TechLevel.of(5), 20), // Increases engineer's effectivity
    NAVIGATING("Navigating system", 	   15000, TechLevel.of(6), 20), // Increases pilot's effectivity
    TARGETING("Targeting system",	   25000, TechLevel.of(6), 20), // Increases fighter's effectivity
    CLOAKING("Cloaking device",      100000, TechLevel.of(7), 5), // If you have a good engineer, nor pirates nor police will notice you
    // The gadgets below can't be bought
    FUEL_COMPACTOR("Fuel compactor",        30000, null, 0);

    fun sellPrice(): Int = basePrice * 3/4
}

enum class Weapon(val text: String,
                  val power: Int,
                  val basePrice: Int,
                  val minTechLevel: TechLevel?,
                  val chance: Int) {
    PULSE("Pulse Laser", 15, 2000, TechLevel.of(5), 50),
    BEAM("Beam Laser", 25, 12500, TechLevel.of(6), 35),
    MILITARY("Military Laser", 35, 35000, TechLevel.of(7), 15),
    // The following cannot be purchased
    // NOTE: The original source says "fix me!" by the power for the laser. Why?
    MORGANS("Morgan's Laser", 85, 50000, null, 0);

    fun sellPrice(): Int = basePrice * 3/4
}

data class CrewMember(val name: String,
                      val pilot: Int,
                      val fighter: Int,
                      val trader: Int,
                      val engineer: Int,
                      var curSystem: SolarSystem?) {
    constructor(name: String) : this(
            name,
            (1..MAX_SKILL).random(),
            (1..MAX_SKILL).random(),
            (1..MAX_SKILL).random(),
            (1..MAX_SKILL).random(),
            null
    )

    fun dailyCost(): Int {
        // TODO: handle wildstatus
        return (pilot + fighter + trader + engineer) * 3
    }

}

enum class PoliceRecord(val score: Int) {
    PSYCHOPATH(-70),
    VILLAIN(-30),
    CRIMINAL(-10),
    DUBIOUS(-5),
    CLEAN(0),
    LAWFUL(5),
    TRUSTED(10),
    HELPER(25),
    HERO(75);

    companion object {
        fun of(score: Int): PoliceRecord {
            return PoliceRecord.values()
                    .filter { score >= it.score }
                    .findLast { return it }!!
        }
    }
}

enum class Reputation(val score: Int) {
    HARMLESS(0),
    MOSTLY_HARMLESS(10),
    POOR(20),
    AVERAGE(40),
    ABOVE_AVERAGE(80),
    COMPETENT(150),
    DANGEROUS(300),
    DEADLY(600),
    ELITE(1500);

    companion object {
        fun of(score: Int): Reputation {
            return Reputation.values()
                    .filter { score >= it.score }
                    .findLast { return it }!!
        }
    }
}

data class Amount(val current: Int, val max: Int)
