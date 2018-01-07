package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.nouns.Ship
import kotlin.reflect.KFunction0

abstract class Encounter(val opponent: Ship, val travel: Travel) : GameState {
    abstract fun listActions(): List<KFunction0<GameState>>
    abstract fun description(): String
}