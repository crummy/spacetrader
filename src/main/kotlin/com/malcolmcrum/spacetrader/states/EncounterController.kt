package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.controllers.GameStateController
import com.malcolmcrum.spacetrader.nouns.Ship
import kotlin.reflect.KFunction0

abstract class EncounterController(val opponent: Ship, val travel: GameState.Travel) : GameStateController {
    abstract fun listActions(): List<KFunction0<GameState>>
    abstract fun description(): String

    open fun attack(): GameState {
        throw InvalidActionException()
    }

    open fun submit(): GameState {
        throw InvalidActionException()
    }

    open fun ignore(): GameState {
        throw InvalidActionException()
    }

    open fun flee(): GameState {
        throw InvalidActionException()
    }

    open fun trade(): GameState {
        throw InvalidActionException()
    }

    open fun done(): GameState {
        throw InvalidActionException()
    }

    class InvalidActionException : Exception("Operation not supported on this class")
}