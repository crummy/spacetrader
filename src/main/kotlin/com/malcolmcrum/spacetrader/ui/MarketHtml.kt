package com.malcolmcrum.spacetrader.ui

import com.malcolmcrum.spacetrader.controllers.MarketController
import com.malcolmcrum.spacetrader.model.Game
import com.malcolmcrum.spacetrader.model.TradeItem
import com.malcolmcrum.spacetrader.views.OnPlanet
import kotlinx.html.*
import kotlinx.html.stream.createHTML


fun market(game: Game): String {
    val state = game.state as OnPlanet
    val controller = MarketController(state.system.market, state.system, game.difficulty)

    return createHTML().html {
        body {
            h1 {
                +"market"
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
                        item.text
                    }
                    tr {
                        game.player.cargo.count { cargo -> cargo == item }
                    }

                }
            }
        }
    }
}