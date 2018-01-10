package com.malcolmcrum.spacetrader.ui

import com.malcolmcrum.spacetrader.model.Game
import com.malcolmcrum.spacetrader.states.Encounter
import com.malcolmcrum.spacetrader.states.GameState
import org.http4k.routing.RoutingHttpHandler

interface StateRenderer {
    fun render(game: Game): String
    fun routes(): RoutingHttpHandler
    fun basePath(): String
}

fun rendererFor(state: GameState) = when (state) {
    is GameState.OnPlanet -> OnPlanetRenderer()
    is Encounter -> EncounterRenderer()
    else -> throw RuntimeException("Unrecognized state $state")
}