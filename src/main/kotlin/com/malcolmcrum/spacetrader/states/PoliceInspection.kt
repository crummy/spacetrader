package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.model.Difficulty
import com.malcolmcrum.spacetrader.model.Game
import com.malcolmcrum.spacetrader.model.PoliceRecord
import com.malcolmcrum.spacetrader.model.TradeItem
import com.malcolmcrum.spacetrader.nouns.Ship
import kotlin.reflect.KFunction0

private val TRAFFICKING = -1
private val FLEE_FROM_INSPECTION = -2

class PoliceInspection(opponent: Ship, travel: Travel, val game: Game) : Encounter(opponent, travel) {
    private val hold = game.ship.hold

    override fun description(): String {
        return "The police summon you to submit to an inspection."
    }

    override fun listActions(): List<KFunction0<GameState>> {
        return listOf(::submit, ::flee, ::attack)
    }

    fun submit(): GameState {
        val firearms = hold.count(TradeItem.FIREARMS)
        val narcotics = hold.count(TradeItem.NARCOTICS)
        if (firearms > 0 || narcotics > 0) {
            hold.remove(TradeItem.FIREARMS, firearms)
            hold.remove(TradeItem.NARCOTICS, narcotics)

            var fine = game.currentWorth() / (Difficulty.IMPOSSIBLE.ordinal + 2 - game.difficulty.ordinal) * 10
            if (fine % 50 != 0) {
                fine += (50 - (fine % 50))
            }
            fine = Math.max(100, Math.min(fine, 10000))
            game.finances.withdraw(fine)
            game.policeRecordScore += TRAFFICKING

            return LeaveEncounter(opponent, travel, "You have been fined $fine") // TODO fix text
        } else {
            // TODO: check wildstatus, reactorstatus
            game.policeRecordScore -= TRAFFICKING
            return LeaveEncounter(opponent, travel,"NoIllegalGoodsAlert") // TODO fix text
        }
    }

    fun flee(): GameState {
        if (game.policeRecordScore > PoliceRecord.DUBIOUS.score) {
            game.policeRecordScore = PoliceRecord.DUBIOUS.score - if (game.difficulty < Difficulty.NORMAL) 0 else 1
        } else {
            game.policeRecordScore += FLEE_FROM_INSPECTION
        }
        TODO() //return PoliceAttack()
    }

    fun attack(): GameState {
        TODO()
    }

}