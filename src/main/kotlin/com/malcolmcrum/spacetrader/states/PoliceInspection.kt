package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.model.*
import kotlin.reflect.KFunction0

private val TRAFFICKING = -1
private val FLEE_FROM_INSPECTION = -2

class PoliceInspection(val cargo: Hold, val player: Player) : Encounter() {
    override fun description(): String {
        return "The police summon you to submit to an inspection."
    }

    override fun listActions(): List<KFunction0<GameState>> {
        return listOf(::submit, ::flee, ::attack)
    }

    fun submit(): GameState {
        val firearms = cargo.count(TradeItem.FIREARMS)
        val narcotics = cargo.count(TradeItem.NARCOTICS)
        if (firearms > 0 || narcotics > 0) {
            cargo.remove(TradeItem.FIREARMS, firearms)
            cargo.remove(TradeItem.NARCOTICS, narcotics)

            var fine = player.currentWorth() / (Difficulty.IMPOSSIBLE.ordinal + 2 - player.difficulty.ordinal) * 10
            if (fine % 50 != 0) {
                fine += (50 - (fine % 50))
            }
            fine = Math.max(100, Math.min(fine, 10000))
            player.finances.withdraw(fine)
            player.policeRecordScore += TRAFFICKING

            return LeaveEncounter("You have been fined $fine") // TODO fix text
        } else {
            // TODO: check wildstatus, reactorstatus
            player.policeRecordScore -= TRAFFICKING
            return LeaveEncounter("NoIllegalGoodsAlert") // TODO fix text
        }
    }

    fun flee(): GameState {
        if (player.policeRecordScore > PoliceRecord.DUBIOUS.score) {
            player.policeRecordScore = PoliceRecord.DUBIOUS.score - if (player.difficulty < Difficulty.NORMAL) 0 else 1
        } else {
            player.policeRecordScore += FLEE_FROM_INSPECTION
        }
        return this //return PoliceAttack()
    }

    fun attack(): GameState {
        return this
    }

}