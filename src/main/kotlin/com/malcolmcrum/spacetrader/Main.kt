package com.malcolmcrum.spacetrader

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.with
import com.malcolmcrum.spacetrader.game.Difficulty
import com.malcolmcrum.spacetrader.game.OnPlanet
import com.malcolmcrum.spacetrader.ui.get
import com.malcolmcrum.spacetrader.ui.newGame
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.body.form
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer

val kodein = Kodein {
    constant("galaxyWidth") with 150
    constant("galaxyHeight") with 110
    constant("maxSolarSystems") with 120
    constant("maxWormholes") with 6
    constant("closeDistance") with 13
    constant("minDistance") with 6
    constant("maxCrewMembers") with 31
}

val gameManager = GameManager()

fun main(args: Array<String>) {
    val app: HttpHandler = routes(
            "/game/{gameId}" bind GET to { req ->
                val id = req.path("gameId")!!
                val game = gameManager.games[GameManager.Id(id)]!!
                val onPlanet = game as OnPlanet
                Response(OK).body(get(onPlanet))
            },
            "/new" bind GET to {
                Response(OK).body(newGame())
            },
            "/new" bind Method.POST to { req ->
                val name = req.form("name")!!
                val difficulty = Difficulty.valueOf(req.form("difficulty") ?: "NORMAL")
                Response(OK).body(gameManager.newGame(name, difficulty).value)
            }
    )

    app.asServer(Jetty(8000)).start()
}