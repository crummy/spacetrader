package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.model.Difficulty
import com.malcolmcrum.spacetrader.model.Hold
import com.malcolmcrum.spacetrader.model.Player
import com.malcolmcrum.spacetrader.model.TradeItem
import kotlin.reflect.KFunction0

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
            player.finances.spend(10000)
        }
        return this //TODO
    }

    fun flee(): GameState {
        return this // TODO
    }

    fun attack(): GameState {
        return this // TODO
    }

}