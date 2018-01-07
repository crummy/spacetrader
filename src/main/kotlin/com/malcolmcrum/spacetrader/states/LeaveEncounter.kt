package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.nouns.Ship
import kotlin.reflect.KFunction0

class LeaveEncounter(opponent: Ship, travel: Travel, private val message: String) : Encounter(opponent, travel) {
    override fun description(): String {
        return message
    }

    override fun listActions(): List<KFunction0<GameState>> {
        return listOf(::done)
    }

    fun done(): GameState {
        return travel.warp()
    }

}