package com.malcolmcrum.spacetrader.ui

import com.malcolmcrum.spacetrader.game.OnPlanet
import kotlinx.html.*
import kotlinx.html.stream.createHTML

fun onPlanet(state: OnPlanet): String {
    return createHTML().html {
        head {
            title {
                +"SpaceTrader"
            }
        }
        body {
            table {
                tr {
                    th {
                        +"Ship"
                    }
                    td {
                        +"Price"
                    }
                }
                state.shipyard.shipsAvailable.forEach {
                    tr {
                        td {
                            it.text
                        }
                        td {
                            it.basePrice
                        }
                    }
                }
            }
        }
    }
}
