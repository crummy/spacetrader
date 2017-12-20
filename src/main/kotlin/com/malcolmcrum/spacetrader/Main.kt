package com.malcolmcrum.spacetrader

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.with
import com.malcolmcrum.spacetrader.model.Difficulty
import com.malcolmcrum.spacetrader.ui.newGame
import com.malcolmcrum.spacetrader.ui.onPlanet
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.TEMPORARY_REDIRECT
import org.http4k.core.body.form
import org.http4k.filter.ResponseFilters
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import java.time.Duration

val kodein = Kodein {
    constant("galaxyWidth") with 150
    constant("galaxyHeight") with 110
    constant("maxSolarSystems") with 120
    constant("maxWormholes") with 6
    constant("closeDistance") with 13
    constant("minDistance") with 6
    constant("maxCrewMembers") with 31
}

private val log = LoggerFactory.getLogger("MAIN")!!

val gameManager = GameManager()

fun main(args: Array<String>) {
    val app: HttpHandler = routes(
            "/game/{gameId}" bind GET to { req ->
                val id = req.path("gameId")!!
                val game = gameManager.games[GameManager.Id(id)]!!
                Response(OK).body(onPlanet(game))
            },
            "/new" bind GET to {
                Response(OK).body(newGame())
            },
            "/new" bind Method.POST to { req ->
                val name = req.form("name")!!
                val difficulty = Difficulty.valueOf(req.form("difficulty") ?: "NORMAL")
                val id = gameManager.newGame(name, difficulty)
                Response(TEMPORARY_REDIRECT).header("location", "/game/${id.value}")
            }
    )

    val callLogger = ResponseFilters.ReportLatency { req: Request, resp: Response, duration: Duration ->
        log.info("${req.uri} returned ${resp.status} and took ${duration.toMillis()}ms")
    }

    callLogger.then(app).asServer(Jetty(8000)).start()
}