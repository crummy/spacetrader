package com.malcolmcrum.spacetrader.ui

import com.malcolmcrum.spacetrader.controllers.MarketController
import com.malcolmcrum.spacetrader.controllers.OnPlanetController
import com.malcolmcrum.spacetrader.gameManager
import com.malcolmcrum.spacetrader.model.*
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status.Companion.SEE_OTHER
import org.http4k.core.body.form
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
                    val onPlanet = game.state as OnPlanetController
                    val systemName = req.path("systemName")!!
                    val destination = game.galaxy.getSystem(systemName)
                    game.state = onPlanet.warp(destination)
                    Response(SEE_OTHER).header("location", "/game/${game.id}")
                },
                "repair/{amount}" bind Method.POST to { req ->
                    val gameId: GameId = req.path("gameId")!!
                    val game = gameManager.games[gameId]!!
                    val onPlanet = game.state as OnPlanetController
                    val amount = req.path("amount")!!
                    onPlanet.repairShip(amount.toInt())
                    Response(SEE_OTHER).header("location", "/game/${game.id}")
                },
                "refuel/{amount}" bind Method.POST to { req ->
                    val gameId: GameId = req.path("gameId")!!
                    val game = gameManager.games[gameId]!!
                    val onPlanet = game.state as OnPlanetController
                    val amount = req.path("amount")!!
                    onPlanet.refuelShip(amount.toInt())
                    Response(SEE_OTHER).header("location", "/game/${game.id}")
                },
                "buy/{type}/{item}" bind Method.POST to { req ->
                    val gameId: GameId = req.path("gameId")!!
                    val game = gameManager.games[gameId]!!
                    val onPlanet = game.state as OnPlanetController
                    val type = req.path("type")!!
                    val item = req.path("item")!!
                    when (type) {
                        "weapon" -> onPlanet.buyWeapon(Weapon.valueOf(item))
                        "shield" -> onPlanet.buyShield(ShieldType.valueOf(item))
                        "gadget" -> onPlanet.buyGadget(Gadget.valueOf(item))
                        else -> throw Exception("Unrecognized type: $type")
                    }
                    Response(SEE_OTHER).header("location", "/game/${game.id}")
                },
                "sell/{type}/{item}" bind Method.POST to { req ->
                    val gameId: GameId = req.path("gameId")!!
                    val game = gameManager.games[gameId]!!
                    val onPlanet = game.state as OnPlanetController
                    val type = req.path("type")!!
                    val item = req.path("item")!!
                    when (type) {
                        "weapon" -> onPlanet.sell(Weapon.valueOf(item))
                        "shield" -> onPlanet.sell(ShieldType.valueOf(item))
                        "gadget" -> onPlanet.sell(Gadget.valueOf(item))
                        else -> throw Exception("Unrecognized type: $type")
                    }
                    Response(SEE_OTHER).header("location", "/game/${game.id}")
                },
                "buyEscapePod" bind Method.POST to { req ->
                    val gameId: GameId = req.path("gameId")!!
                    val game = gameManager.games[gameId]!!
                    val onPlanet = game.state as OnPlanetController
                    onPlanet.buyEscapePod()
                    Response(SEE_OTHER).header("location", "/game/${game.id}")
                },
                "market/buy" bind Method.POST to { req ->
                    val gameId: GameId = req.path("gameId")!!
                    val game = gameManager.games[gameId]!!
                    val onPlanet = game.state as OnPlanetController
                    val item = TradeItem.valueOf(req.form("item")!!)
                    val amount = req.form("${item}BuyAmount")!!.toInt()
                    onPlanet.buyTradeItem(item, amount)
                    Response(SEE_OTHER).header("location", "/game/${game.id}")
                },
                "market/sell" bind Method.POST to { req ->
                    val gameId: GameId = req.path("gameId")!!
                    val game = gameManager.games[gameId]!!
                    val onPlanet = game.state as OnPlanetController
                    val item = TradeItem.valueOf(req.form("item")!!)
                    val amount = req.form("${item}SellAmount")!!.toInt()
                    onPlanet.sellTradeItem(item, amount)
                    Response(SEE_OTHER).header("location", "/game/${game.id}")
                })
    }

    override fun render(game: Game): String {
        val state = game.state as OnPlanetController
        val market = MarketController(state.system.market, state.system, game.difficulty)

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
                    headerRow("Status", game.ship.captain.name)
                    row("Credits", game.finances.credits)
                    row("Debt", game.finances.debt)
                    row("Escape Pod?", game.escapePod.present)
                    row("Days", game.days)
                    row("Reputation", Reputation.of(game.reputationScore))
                    row("Police Record", game.policeRecord.description())
                }
                table {
                    headerRow("Ship", game.ship.type.text)
                    headerRow("Crew", "${game.ship.crew.size}/${game.ship.type.crewQuarters}")
                    game.ship.crew.forEach {
                        row(it.name, "Fire")
                    }
                    headerRow("Weapons", "${game.ship.weapons.size}/${game.ship.type.weaponSlots}")
                    game.ship.weapons.forEach {
                        row(it.name, "Sell")
                    }
                    headerRow("Shields", "${game.ship.shields.size}/${game.ship.type.shieldSlots}")
                    game.ship.shields.forEach {
                        row(it.type.name, "Sell")
                    }
                    headerRow("Gadgets", "${game.ship.gadgets.size}/${game.ship.type.gadgetSlots}")
                    game.ship.gadgets.forEach {
                        row(it.name, "Sell")
                    }
                    headerRow("Fuel Remaining", "${game.ship.fuel}/${game.ship.type.fuelTanks}")
                    headerRow("Cargo Hold", "${game.ship.hold.fullBays()}/${game.ship.type.cargoBays}")
                    headerRow("Insurance?", game.insurance.present)
                    headerRow("Escape pod?", game.escapePod.present)
                }
                table {
                    headerRow("Shipyard", "Price")
                    tr {
                        td {
                            +"Fuel"
                        }
                        td {
                            +"${game.ship.fuel}/${game.ship.type.fuelTanks}"
                        }
                    }
                    tr {
                        td {
                            +"${game.ship.type.costOfFuel}/tank"
                        }
                        td {
                            val refill = game.ship.type.fuelTanks - game.ship.fuel
                            buttonLink("Buy Full Tank", "/game/${game.id}/onPlanet/refuel/$refill")
                        }
                    }
                    tr {
                        td {
                            +"Repair"
                        }
                        td {
                            +"${game.ship.hullStrength/game.ship.type.hullStrength*100}%"
                        }
                    }
                    tr {
                        td {
                            +"${game.ship.type.repairCosts}"
                        }
                        td {
                            val fullRepairs = game.ship.hullStrength - game.ship.type.hullStrength
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
                        tr {
                            td {
                                +it.text
                            }
                            td {
                                buttonLink(it.basePrice.toString(), "/game/${game.id}/onPlanet/buy/weapon/${it.name}")
                            }
                        }
                    }
                    state.shipyard.shieldsAvailable.forEach {
                        tr {
                            td {
                                +it.text
                            }
                            td {
                                buttonLink(it.basePrice.toString(), "/game/${game.id}/onPlanet/buy/shield/${it.name}")
                            }
                        }
                    }
                    state.shipyard.gadgetsAvailable.forEach {
                        tr {
                            td {
                                +it.text
                            }
                            td {
                                buttonLink(it.basePrice.toString(), "/game/${game.id}/onPlanet/buy/gadget/${it.name}")
                            }
                        }
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
                                        val inRange = it.distanceTo(state.system) <= game.ship.fuel
                                        buttonLink(it.name, "/game/${game.id}/onPlanet/warp/${it.name}", inRange)
                                    }
                                    td {
                                        +it.distanceTo(state.system).toString()
                                    }
                                }
                            }
                }
                form {
                    action = "/game/${game.id}/onPlanet/market/buy"
                    method = FormMethod.post
                    table {
                        tr {
                            th {
                                +"Buy"
                            }
                            th {
                                +"Available"
                            }
                            th {
                                +"Price"
                            }
                            th {
                                +"Amount?"
                            }
                            th {
                                +"Total"
                            }
                        }
                        TradeItem.values().forEach { item ->
                            tr {
                                val amount = market.getAmount(item)
                                td {
                                    +item.text
                                }
                                td {
                                    +amount.toString()
                                }
                                td {
                                    id = "${item.name}UnitPrice"
                                    +"${market.getBuyPrice(item, game.ship.traderSkill(), game.policeRecord)} cr."
                                }
                                val canAfford = market.getBuyPrice(item, game.ship.traderSkill(), game.policeRecord) / game.finances.credits
                                td {
                                    textInput {
                                        id = "${item.name}BuyAmount"
                                        name = "${item.name}BuyAmount"
                                        value = Math.max(amount, canAfford).toString()
                                        onChange = "document.getElementById('${item.name}BuyButton').innerHTML = parseInt(document.getElementById('${item.name}UnitPrice').innerHTML) * parseInt(document.getElementById('${item.name}BuyAmount').value)"
                                    }
                                }
                                td {
                                    button {
                                        id = "${item.name}BuyButton"
                                        type = ButtonType.submit
                                        name = "item"
                                        value = item.name
                                        if (amount == 0) {
                                            disabled = true
                                        }
                                        +"${Math.max(amount, canAfford) * market.getBuyPrice(item, game.ship.traderSkill(), game.policeRecord)}"
                                    }
                                }
                            }
                        }
                    }
                }
                form {
                    action = "/game/${game.id}/onPlanet/market/sell"
                    method = FormMethod.post
                    table {
                        tr {
                            th {
                                +"Sell"
                            }
                            th {
                                +"Available"
                            }
                            th {
                                +"Price"
                            }
                            th {
                                +"Amount?"
                            }
                            th {
                                +"Total"
                            }
                        }
                        TradeItem.values().forEach { item ->
                            tr {
                                val amountInCargo = +game.ship.hold.count(item)
                                td {
                                    +item.text
                                }
                                td {
                                    +amountInCargo.toString()
                                }
                                td {
                                    id = "${item.name}SellPrice"
                                    val sellPrice = market.getSellPrice(item, game.policeRecord)
                                    var text = "$sellPrice cr."
                                    if (amountInCargo > 0) {
                                        val profit = sellPrice - game.ship.hold.purchasePrice(item)
                                        text += " ($profit)"
                                    }
                                    +text
                                }
                                td {
                                    textInput {
                                        id = "${item.name}SellAmount"
                                        name = "${item.name}SellAmount"
                                        value = amountInCargo.toString()
                                        onChange = "document.getElementById('${item.name}SellButton').innerHTML = parseInt(document.getElementById('${item.name}SellPrice').innerHTML) * parseInt(document.getElementById('${item.name}SellAmount').value)"
                                    }
                                }
                                td {
                                    button {
                                        id = "${item.name}SellButton"
                                        type = ButtonType.submit
                                        name = "item"
                                        value = item.name
                                        if (amountInCargo == 0) {
                                            disabled = true
                                        }
                                        +"${amountInCargo * market.getSellPrice(item, game.policeRecord)}"
                                    }
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

fun TD.buttonLink(text: String, destination: String, isEnabled: Boolean = true) = form {
    action = destination
    method = FormMethod.post
    button {
        disabled = ! isEnabled
        type = ButtonType.submit
        +text
    }
}