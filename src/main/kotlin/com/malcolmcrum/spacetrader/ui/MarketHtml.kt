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
        head {
            styles()
        }
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
                    tr {
                        controller.getSellPrice(item, game.player.policeRecordScore)
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
                }
                TradeItem.values().forEach { item ->
                    tr {
                        item.text
                    }
                    tr {
                        controller.getAmount(item)
                    }
                    tr {
                        controller.getBuyPrice(item, 0, game.player.policeRecordScore)
                    }
                }
            }
        }
    }
}