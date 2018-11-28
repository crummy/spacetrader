package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.nouns.Ship
import kotlin.reflect.KFunction0

class TraderIgnore(opponent: Ship, travel: GameState.Travel) : EncounterController(opponent, travel) {
    override fun listActions(): List<KFunction0<GameState>> {
        return listOf(::ignore, ::trade, ::attack)
    }

    override fun description(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
