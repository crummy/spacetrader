package com.malcolmcrum.spacetrader.ui

import com.malcolmcrum.spacetrader.Id
import com.malcolmcrum.spacetrader.game.OnPlanet
import com.malcolmcrum.spacetrader.gameManager
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.Route
import io.ktor.routing.get
import kotlinx.html.*


fun Route.onPlanetUI() {

    get("game/{gameId}/onplanet") {
        val id = Id(call.parameters["gameId"]!!)
        val game = gameManager.games[id] ?: Exception("No game found with id $id")
        val state = game as OnPlanet
        call.respondHtml {
            head {
                title { +"Space Trader" }
            }
            body {
                h1 { +"Space Trader" }
                ul {
                    state.shipyard.shipsAvailable.forEach {
                        li { +"${it.text}, ${it.basePrice} credits" }
                    }
                }
            }
        }
    }
}