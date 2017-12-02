package com.malcolmcrum.spacetrader

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.with
import com.malcolmcrum.spacetrader.game.*

val kodein = Kodein {
    constant("galaxyWidth") with 150
    constant(GALAXY_HEIGHT) with 110
    constant(MAX_SOLAR_SYSTEM) with 120
    constant(MAX_WORMHOLES) with 6
    constant(CLOSE_DISTANCE) with 13
    constant(MIN_DISTANCE) with 6
    constant(MAX_CREW_MEMBER) with 31
}