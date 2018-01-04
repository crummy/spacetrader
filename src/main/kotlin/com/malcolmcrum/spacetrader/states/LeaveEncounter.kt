package com.malcolmcrum.spacetrader.states

import kotlin.reflect.KFunction0

class LeaveEncounter(val message: String) : Encounter() {
    override fun description(): String {
        return message
    }

    override fun listActions(): List<KFunction0<GameState>> {
        return listOf(::done)
    }

    fun done(): GameState {
        return this // TODO
    }

}