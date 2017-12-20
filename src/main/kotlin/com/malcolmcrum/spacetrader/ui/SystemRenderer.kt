package com.malcolmcrum.spacetrader.ui

import com.malcolmcrum.spacetrader.model.SolarSystem
import kotlinx.html.*

fun BODY.render(system: SolarSystem, currentSystem: SolarSystem) {
    table {
        tr {
            th {

            }
            th {
                +system.name
            }
        }
        tr {
            td { +"Size" }
            td { +system.size.text }
        }
        tr {
            td { +"Tech Level" }
            td { +system.tech.text }
        }
        tr {
            td { +"Government" }
            td { +system.politics.text }
        }
        tr {
            td { +"Distance" }
            td { +system.distanceTo(currentSystem).toString() }
        }
        tr {
            td { +"Police" }
            td { +"TODO"}
        }
        tr {
            td { +"Pirates" }
            td { +"TODO" }
        }
        tr {
            td { +"Current costs" }
            td { +"TODO"}
        }
    }
}