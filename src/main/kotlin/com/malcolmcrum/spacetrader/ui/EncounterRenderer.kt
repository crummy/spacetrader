package com.malcolmcrum.spacetrader.ui

import com.malcolmcrum.spacetrader.gameManager
import com.malcolmcrum.spacetrader.model.Game
import com.malcolmcrum.spacetrader.model.GameId
import com.malcolmcrum.spacetrader.states.EncounterController
import com.malcolmcrum.spacetrader.states.EncounterState
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status.Companion.SEE_OTHER
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

class EncounterRenderer: StateRenderer {
    override fun basePath() = "encounter"

    override fun routes(): RoutingHttpHandler {
        return routes(
                "attack" bind Method.POST to { req ->
                    val gameId: GameId = req.path("gameId")!!
                    val game = gameManager.games[gameId]!!
                    val encounter = gameManager.getController(game) as EncounterController
                    game.state = encounter.attack()
                    Response(SEE_OTHER).header("location", "/game/${game.id}")
                }
        )
    }

    override fun render(game: Game): String {
        val encounter = gameManager.getController(game) as EncounterController
        val state = game.state as EncounterState
        val opponent = state.opponent

        return createHTML().html {
            head {
                title {
                    +"SpaceTrader"
                }
                styles()
            }
            body {
                +encounter.description()
                table {
                    tr {
                        th {
                            +"Your ship"
                        }
                    }
                    tr {
                        td {
                            val hull = percent(game.ship.hullStrength, game.ship.type.hullStrength)
                            +"Hull at $hull"
                        }
                        td {
                            if (game.ship.shields.isEmpty()) {
                                +"No shields"
                            } else {
                                val shields = percent(game.ship.shields.sumBy { it.strength }, game.ship.shields.sumBy { it.type.power })
                                +"Shields at $shields"
                            }
                        }
                    }
                }
                table {
                    tr {
                        th {
                            +"Opponent"
                        }
                    }
                    tr {
                        td {
                            val hull = percent(opponent.hullStrength, opponent.type.hullStrength)
                            +"Hull at $hull"
                        }
                        td {
                            if (opponent.shields.isEmpty()) {
                                +"No shields"
                            } else {
                                val shields = percent(opponent.shields.sumBy { it.strength }, opponent.shields.sumBy { it.type.power })
                                +"Shields at $shields"
                            }
                        }
                    }
                }
                encounter.listActions().forEach {
                    buttonLink(it.name, "/game/${game.id}/${basePath()}/${it.name}")
                }
            }
        }
    }
}

fun BODY.buttonLink(text: String, destination: String, isEnabled: Boolean = true) = form {
    action = destination
    method = FormMethod.post
    button {
        disabled = ! isEnabled
        type = ButtonType.submit
        +text
    }
}

private fun percent(part: Int, total: Int): String {
    return "${(part / total) * 100}%"
}