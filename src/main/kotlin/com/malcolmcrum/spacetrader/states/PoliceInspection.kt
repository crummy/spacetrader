package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.model.*
import com.malcolmcrum.spacetrader.nouns.Ship
import kotlin.reflect.KFunction0

class PoliceInspection(opponent: Ship,
                       travel: GameState.Travel,
                       private val finances: Finances,
                       private val policeRecord: PoliceRecord,
                       private val hold: Hold,
                       private val difficulty: Difficulty,
                       private val currentWorth: Int) : EncounterController(opponent, travel) {

    override fun description(): String {
        return "The police summon you to submit to an inspection."
    }

    override fun listActions(): List<KFunction0<GameState>> {
        return listOf(::submit, ::flee, ::attack)
    }

    override fun submit(): GameState {
        val firearms = hold.count(TradeItem.FIREARMS)
        val narcotics = hold.count(TradeItem.NARCOTICS)
        if (firearms > 0 || narcotics > 0) {
            hold.remove(TradeItem.FIREARMS, firearms)
            hold.remove(TradeItem.NARCOTICS, narcotics)

            var fine = currentWorth / (Difficulty.IMPOSSIBLE.ordinal + 2 - difficulty.ordinal) * 10
            if (fine % 50 != 0) {
                fine += (50 - (fine % 50))
            }
            fine = Math.max(100, Math.min(fine, 10000))
            finances.withdraw(fine)
            policeRecord.caughtTrafficking()

            return GameState.LeaveEncounter(opponent, travel, "You have been fined $fine") // TODO fix text
        } else {
            // TODO: check wildstatus, reactorstatus
            policeRecord.passedInspection()
            return GameState.LeaveEncounter(opponent, travel,"NoIllegalGoodsAlert") // TODO fix text
        }
    }

    override fun flee(): GameState {
        policeRecord.fledPolice(difficulty)

        TODO() //return PoliceAttack()
    }

    override fun attack(): GameState {
        TODO()
    }

}