package com.malcolmcrum.spacetrader.states

import kotlin.reflect.KFunction0

abstract class Encounter : GameState {
    abstract fun listActions(): List<KFunction0<GameState>>

    abstract fun description(): String
}