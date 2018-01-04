package com.malcolmcrum.spacetrader.ui

import com.malcolmcrum.spacetrader.model.Game
import com.malcolmcrum.spacetrader.states.GameState
import com.malcolmcrum.spacetrader.states.OnPlanet
import org.http4k.routing.RoutingHttpHandler

interface StateRenderer {


    fun render(game: Game): String
    fun routes(): RoutingHttpHandler
    fun basePath(): String
}

fun rendererFor(state: GameState) = when (state) {
    is OnPlanet -> OnPlanetRenderer()
    else -> throw RuntimeException("Unrecognized state $state")
}

// TODO this is not very OO. Sealed class maybe?
fun renderers(): Collection<StateRenderer> {
    return listOf(OnPlanetRenderer())
}