package com.malcolmcrum.spacetrader.model

import com.malcolmcrum.spacetrader.nouns.Galaxy
import com.malcolmcrum.spacetrader.nouns.Ship
import com.malcolmcrum.spacetrader.states.GameState

private val COSTMOON = 500000

typealias GameId = String
data class Game (val galaxy: Galaxy,
                 val id: GameId,
                 val difficulty: Difficulty,
                 var ship: Ship,
                 var state: GameState? = null,
                 val finances: Finances = Finances(1000, 0),
                 var hasEscapePod: Boolean = false,
                 var daysWithoutClaim: Int = 0,
                 var hasInsurance: Boolean = false,
                 var days: Int = 0,
                 var policeRecordScore: Int = 0,
                 var reputationScore: Int = 0,
                 var boughtMoon: Boolean = false) {

    fun currentWorth(): Int {
        return ship.value(false) + ship.hold.totalWorth() + finances.credits - finances.debt + if (boughtMoon) COSTMOON else 0
    }

    // TODO: move into a controller?
    fun dayPasses() {
        ++days
        if (hasInsurance) {
            ++daysWithoutClaim
        }
        if (days % 3 == 0 && policeRecordScore > PoliceRecord.CLEAN.score) {
            --policeRecordScore
        }
        if (policeRecordScore < PoliceRecord.DUBIOUS.score) {
            if (difficulty <= Difficulty.NORMAL) {
                policeRecordScore++
            } else if (days % difficulty.ordinal == 0) {
                policeRecordScore++
            }
        }
        // TODO: special event checks
    }

    fun getInsuranceCost(): Int {
        val cost = (((ship.valueWithoutCargo(true) * 5) / 2000) * (100 - Math.min(daysWithoutClaim, 90)) / 100)
        return Math.max(cost, 1)
    }
}