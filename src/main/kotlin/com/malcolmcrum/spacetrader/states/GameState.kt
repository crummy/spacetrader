package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.model.SolarSystem
import com.malcolmcrum.spacetrader.nouns.Ship

sealed class GameState {
    data class GameOver(val days: Int) : GameState()
    data class OnPlanet(val system: SolarSystem) : GameState()
    data class Travel(val destination: SolarSystem, var clicksLeft: Int) : GameState()
    data class PoliceInspection(private val opp: Ship, private val t: Travel) : EncounterState(opp, t)
    data class PoliceAttack(private val opp: Ship, private val t: Travel) : EncounterState(opp, t)
    data class TraderIgnore(private val opp: Ship, private val t: Travel) : EncounterState(opp, t)
    data class TraderFlee(private val opp: Ship, private val t: Travel) : EncounterState(opp, t)
    data class LeaveEncounter(private val opp: Ship, private val t: Travel, val text: String) : EncounterState(opp, t)
    data class StartGame(val startingSystem: SolarSystem) : GameState()
}

open class EncounterState(val opponent: Ship, val travel: Travel) : GameState()