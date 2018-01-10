package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.controllers.GameStateController
import com.malcolmcrum.spacetrader.nouns.Ship
import kotlin.reflect.KFunction0

abstract class Encounter(val opponent: Ship, val travel: GameState.Travel) : GameStateController {
    abstract fun listActions(): List<KFunction0<GameState>>
    abstract fun description(): String
}