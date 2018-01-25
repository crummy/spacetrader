package com.malcolmcrum.spacetrader

import com.malcolmcrum.spacetrader.model.Difficulty
import com.malcolmcrum.spacetrader.model.GameId
import com.malcolmcrum.spacetrader.ui.OnPlanetRenderer
import com.malcolmcrum.spacetrader.ui.newGame
import com.malcolmcrum.spacetrader.ui.rendererFor
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.SEE_OTHER
import org.http4k.core.body.form
import org.http4k.core.then
import org.http4k.filter.ResponseFilters
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import java.time.Duration

private val log = LoggerFactory.getLogger("MAIN")!!

val gameManager = GameManager()

fun main(args: Array<String>) {
    val rootPath = "/game/{gameId}"
    val app: HttpHandler = routes(
            rootPath bind GET to { req ->
                val id: GameId = req.path("gameId")!!
                val controller = gameManager.getController(id)
                val renderer = rendererFor(controller)
                Response(SEE_OTHER).header("location", "/game/$id/${renderer.basePath()}")
            },
            "$rootPath/${OnPlanetRenderer().basePath()}" bind OnPlanetRenderer().routes(),
            "/games" bind GET to {
                Response(OK).body(gameManager.games.toString())
            },
            "/" bind GET to {
                Response(SEE_OTHER).header("location", "/new")
            },
            "/new" bind GET to {
                Response(OK).body(newGame())
            },
            "/new" bind POST to { req ->
                val name: String = req.form("name")!!
                val difficulty: Difficulty = Difficulty.valueOf(req.form("difficulty") ?: "NORMAL")
                val traderSkill: Int = req.form("trader")!!.toInt()
                val fighterSkill: Int = req.form("fighter")!!.toInt()
                val pilotSkill: Int = req.form("pilot")!!.toInt()
                val engineerSkill: Int = req.form("engineer")!!.toInt()
                val id = gameManager.newGame(name, pilotSkill, fighterSkill, traderSkill, engineerSkill, difficulty)
                Response(SEE_OTHER).header("location", "/game/$id")
            }
    )

    val callLogger = ResponseFilters.ReportLatency { req: Request, resp: Response, duration: Duration ->
        log.debug("${req.method} ${req.uri} returned ${resp.status} and took ${duration.toMillis()}ms")
    }

    callLogger.then(app).asServer(Jetty(8000)).start()
}