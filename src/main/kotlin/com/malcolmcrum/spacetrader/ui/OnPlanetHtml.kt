package com.malcolmcrum.spacetrader.ui

import com.malcolmcrum.spacetrader.model.Game
import com.malcolmcrum.spacetrader.model.PoliceRecord
import com.malcolmcrum.spacetrader.model.Reputation
import com.malcolmcrum.spacetrader.model.ShipType
import com.malcolmcrum.spacetrader.views.OnPlanet
import kotlinx.html.*
import kotlinx.html.stream.createHTML

fun onPlanet(game: Game): String {
    val state = game.state as OnPlanet
    return createHTML().html {
        head {
            title {
                +"SpaceTrader"
            }
        }
        body {
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
                row("Refuel", (game.player.ship.fuelTanks - game.player.fuelLeft) * game.player.ship.fuelTanks)
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
                game.galaxy.systems
                        .filter { it != state.system }
                        .filter { it.distanceTo(state.system) <= ShipType.GNAT.fuelTanks + 1 }
                        .sortedBy { it.distanceTo(state.system) }
                        .forEach { row(it.name, it.distanceTo(state.system)) }
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