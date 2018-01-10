package com.malcolmcrum.spacetrader.model

import com.malcolmcrum.spacetrader.nouns.Galaxy
import com.malcolmcrum.spacetrader.nouns.Ship
import com.malcolmcrum.spacetrader.states.GameState

private val COSTMOON = 500000

typealias GameId = String
data class Game(val galaxy: Galaxy,
                val id: GameId,
                 val difficulty: Difficulty,
                 var ship: Ship,
                 var state: GameState,
                 val finances: Finances = Finances(1000, 0),
                 var escapePod: EscapePod = EscapePod(),
                 var insurance: Insurance = Insurance(),
                 var days: Int = 0,
                 var policeRecord: PoliceRecord = PoliceRecord(),
                 var reputationScore: Int = 0,
                 var boughtMoon: Boolean = false) {

    fun currentWorth(): Int {
        return ship.value(false) + ship.hold.totalWorth() + finances.credits - finances.debt + if (boughtMoon) COSTMOON else 0
    }

    // TODO: move into a controller? move into travel?
    fun dayPasses() {
        ++days
        insurance.dayPasses()
        policeRecord.dayPasses(days, difficulty)

        // TODO: special event checks
    }
}