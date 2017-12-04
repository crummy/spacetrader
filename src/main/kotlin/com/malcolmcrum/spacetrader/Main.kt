package com.malcolmcrum.spacetrader

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.with
import com.malcolmcrum.spacetrader.game.*
import com.malcolmcrum.spacetrader.ui.onPlanetUI
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route

val kodein = Kodein {
    constant("galaxyWidth") with 150
    constant(GALAXY_HEIGHT) with 110
    constant(MAX_SOLAR_SYSTEM) with 120
    constant(MAX_WORMHOLES) with 6
    constant(CLOSE_DISTANCE) with 13
    constant(MIN_DISTANCE) with 6
    constant(MAX_CREW_MEMBER) with 31
}

val gameManager = GameManager()


fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        route("/rest/v1/") {
            onPlanetUI()
        }
        get("/rest/v1/{gameId}/gameState") {
            val id = Id(call.parameters["gameId"]!!)
            val game = gameManager.games[id] ?: Exception("No game found with id $id")
            call.respond(game)
        }
    }
}