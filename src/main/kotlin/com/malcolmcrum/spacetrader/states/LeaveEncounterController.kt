package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.nouns.Ship
import kotlin.reflect.KFunction0

class LeaveEncounterController(opponent: Ship, travel: GameState.Travel, private val message: String) : EncounterController(opponent, travel) {
    override fun description(): String {
        return message
    }

    override fun listActions(): List<KFunction0<GameState>> {
        return listOf(::done)
    }

    override fun done(): GameState {
        return travel
    }

}