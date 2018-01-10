package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.model.SolarSystem
import com.malcolmcrum.spacetrader.nouns.Ship

sealed class GameState {
    data class GameOver(val days: Int) : GameState()
    data class OnPlanet(val system: SolarSystem) : GameState()
    data class Travel(val destination: SolarSystem, var clicksLeft: Int) : GameState()
    data class PoliceInspection(val opponent: Ship, val travel: Travel) : GameState()
    data class PoliceAttack(val opponent: Ship) : GameState()
    data class LeaveEncounter(val opponent: Ship, val travel: Travel, val text: String) : GameState()
    data class StartGame(val startingSystem: SolarSystem) : GameState()
}