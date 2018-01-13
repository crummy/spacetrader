package com.malcolmcrum.spacetrader.ui

import com.malcolmcrum.spacetrader.controllers.GameStateController
import com.malcolmcrum.spacetrader.controllers.OnPlanetController
import com.malcolmcrum.spacetrader.model.Game
import com.malcolmcrum.spacetrader.states.Encounter
import org.http4k.routing.RoutingHttpHandler

interface StateRenderer {
    fun render(game: Game): String
    fun routes(): RoutingHttpHandler
    fun basePath(): String
}

fun rendererFor(controller: GameStateController): StateRenderer {
    return when (controller) {
        is OnPlanetController -> OnPlanetRenderer()
        is Encounter -> EncounterRenderer()
        else -> throw RuntimeException("Unrecognized controller $controller")
    }
}