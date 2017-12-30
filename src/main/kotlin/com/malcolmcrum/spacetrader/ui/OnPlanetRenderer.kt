package com.malcolmcrum.spacetrader.ui

import com.malcolmcrum.spacetrader.controllers.MarketController
import com.malcolmcrum.spacetrader.gameManager
import com.malcolmcrum.spacetrader.model.*
import com.malcolmcrum.spacetrader.views.OnPlanet
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status.Companion.SEE_OTHER
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

class OnPlanetRenderer : StateRenderer {
    override fun basePath() = "onPlanet"

    override fun routes(): RoutingHttpHandler {
        return routes(
                "warp/{systemName}" bind Method.POST to { req ->
                    val gameId: GameId = req.path("gameId")!!
                    val game = gameManager.games[gameId]!!
                    val onPlanet = game.state as OnPlanet
                    val systemName = req.path("systemName")!!
                    val destination = game.galaxy.getSystem(systemName)
                    game.state = onPlanet.warp(destination)
                    Response(SEE_OTHER).header("location", "/game/$gameId")
                },
                "repair/{amount}" bind Method.POST to { req ->
                    val gameId: GameId = req.path("gameId")!!
                    val game = gameManager.games[gameId]!!
                    val onPlanet = game.state as OnPlanet
                    val amount = req.path("amount")!!
                    onPlanet.repairShip(amount.toInt())
                    Response(SEE_OTHER).header("location", "/game/$gameId")
                },
                "refuel/{amount}" bind Method.POST to { req ->
                    val gameId: GameId = req.path("gameId")!!
                    val game = gameManager.games[gameId]!!
                    val onPlanet = game.state as OnPlanet
                    val amount = req.path("amount")!!
                    onPlanet.refuelShip(amount.toInt())
                    Response(SEE_OTHER).header("location", "/game/$gameId")
                },
                "buyEscapePod" bind Method.POST to { req ->
                    val gameId: GameId = req.path("gameId")!!
                    val game = gameManager.games[gameId]!!
                    val onPlanet = game.state as OnPlanet
                    onPlanet.buyEscapePod()
                    Response(SEE_OTHER).header("location", "/game/$game")
                },
                "market/buy/{item}/{amount}" bind Method.POST to { req ->
                    val gameId: GameId = req.path("gameId")!!
                    val game = gameManager.games[gameId]!!
                    val onPlanet = game.state as OnPlanet
                    val item = TradeItem.valueOf(req.path("item")!!)
                    val amount = req.path("amount")!!.toInt()
                    val market = MarketController(onPlanet.system.market, onPlanet.system, game.difficulty, game.player.cargo, game.player.finances, game.player.ship)
                    market.buy(item, amount, game.player.traderSkill(), game.player.policeRecordScore)
                    Response(SEE_OTHER).header("location", "/game/$game")
                })
    }

    override fun render(game: Game): String {
        val state = game.state as OnPlanet
        val market = MarketController(state.system.market, state.system, game.difficulty, game.player.cargo, game.player.finances, game.player.ship)

        return createHTML().html {
            head {
                title {
                    +"SpaceTrader"
                }
                styles()
            }
            body {
                render(state.system, state.system)
                table {
                    headerRow("Status", game.player.name)
                    row("Credits", game.player.finances.credits)
                    row("Debt", game.player.finances.debt)
                    row("Escape Pod?", game.player.hasEscapePod)
                    row("Days", game.player.days)
                    row("Reputation", Reputation.of(game.player.reputationScore))
                    row("Police Record", PoliceRecord.of(game.player.policeRecordScore))
                }
                table {
                    headerRow("Ship", game.player.ship.text)
                    row("Crew", "${game.player.crew.size}/${game.player.ship.crewQuarters}")
                    row("Weapons", "${game.player.weapons.size}/${game.player.ship.weaponSlots}")
                    row("Shields", "${game.player.shields.size}/${game.player.ship.shieldSlots}")
                    row("Fuel Remaining", "${game.player.fuelLeft}/${game.player.ship.fuelTanks}")
                    row("Insurance?", game.player.hasInsurance)
                }
                table {
                    headerRow("Shipyard", "Price")
                    tr {
                        td {
                            +"Fuel"
                        }
                        td {
                            +"${game.player.fuelLeft}/${game.player.ship.fuelTanks}"
                        }
                    }
                    tr {
                        td {
                            +"${game.player.ship.costOfFuel}/tank"
                        }
                        td {
                            val refill = game.player.ship.fuelTanks - game.player.fuelLeft
                            buttonLink("Buy Full Tank", "/game/${game.id}/onPlanet/refuel/$refill")
                        }
                    }
                    tr {
                        td {
                            +"Repair"
                        }
                        td {
                            +"${game.player.hullLeft/game.player.ship.hullStrength*100}%"
                        }
                    }
                    tr {
                        td {
                            +"${game.player.ship.repairCosts}"
                        }
                        td {
                            val fullRepairs = game.player.hullLeft - game.player.ship.hullStrength
                            buttonLink("Full Repairs", "/game/${game.id}/onPlanet/repair/$fullRepairs")
                        }
                    }
                }
                table {
                    headerRow("Ships For Sale", "Price")
                    state.shipyard.shipsAvailable.forEach {
                        row(it.text, it.basePrice)
                    }
                }
                table {
                    headerRow("Equipment", "Price")
                    state.shipyard.weaponsAvailable.forEach {
                        row(it.text, it.basePrice)
                    }
                    state.shipyard.shieldsAvailable.forEach {
                        row(it.text, it.basePrice)
                    }
                    state.shipyard.gadgetsAvailable.forEach {
                        row(it.text, it.basePrice)
                    }
                }
                table {
                    headerRow("Nearby Systems", "Distance")
                    game.galaxy.systems // TODO: Read this from a controller
                            .filter { it != state.system }
                            .filter { it.distanceTo(state.system) <= ShipType.GNAT.fuelTanks + 1 }
                            .sortedBy { it.distanceTo(state.system) }
                            .forEach {
                                tr {
                                    td {
                                        buttonLink(it.name, "/game/${game.id}/onPlanet/warp/${it.name}")
                                    }
                                    td {
                                        +it.distanceTo(state.system).toString()
                                    }
                                }
                            }
                }
                table {
                    tr {
                        th {
                            +"Sell"
                        }
                        th {
                            +"Amount"
                        }
                        th {
                            +"Price"
                        }
                    }
                    TradeItem.values().forEach { item ->
                        tr {
                            td {
                                +item.text
                            }
                            td {
                                +game.player.cargo.count { cargo -> cargo.item == item }.toString()
                            }
                            td {
                                +market.getSellPrice(item, game.player.policeRecordScore).toString()
                            }
                        }
                    }
                }
                table {
                    tr {
                        th {
                            +"Buy"
                        }
                        th {
                            +"Amount"
                        }
                        th {
                            +"Price"
                        }
                        th {
                            +"Purchase?"
                        }
                        th {
                            +"Total"
                        }
                    }
                    TradeItem.values().forEach { item ->
                        tr {
                            td {
                                +item.text
                            }
                            td {
                                +"${market.getAmount(item)}"
                            }
                            td {
                                id = "${item.name}UnitPrice"
                                +"${market.getBuyPrice(item, game.player.traderSkill(), game.player.policeRecordScore)}"
                            }
                            val amount = market.getAmount(item)
                            val canAfford = market.getBuyPrice(item, game.player.traderSkill(), game.player.policeRecordScore) / game.player.finances.credits
                            td {
                                textInput {
                                    id = "${item.name}BuyAmount"
                                    value = Math.max(amount, canAfford).toString()
                                    onChange = "document.getElementById('${item.name}BuyButton').innerHTML = parseInt(document.getElementById('${item.name}UnitPrice').innerHTML) * parseInt(document.getElementById('${item.name}BuyAmount').value)"
                                }
                            }
                            td {
                                button {
                                    id = "${item.name}BuyButton"
                                    +"${Math.max(amount, canAfford) * market.getBuyPrice(item, game.player.traderSkill(), game.player.policeRecordScore)}"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun TABLE.row(left: Any, right: Any) = tr {
    td {
        +left.toString()
    }
    td {
        +right.toString()
    }
}
fun TABLE.headerRow(left: Any, right: Any) = tr {
    th {
        +left.toString()
    }
    th {
        +right.toString()
    }
}

fun TD.buttonLink(text: String, destination: String) = form {
    action = destination
    method = FormMethod.post
    button {
        type = ButtonType.submit
        +text
    }
}