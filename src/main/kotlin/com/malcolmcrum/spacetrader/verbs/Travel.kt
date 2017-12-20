package com.malcolmcrum.spacetrader.verbs

import com.malcolmcrum.spacetrader.model.Game
import com.malcolmcrum.spacetrader.views.OnPlanet

class Travel(private val game: Game) {

    fun to(system: String) {
        val destination = game.galaxy.systems.first { it.name == system }
        // TODO: range check
        game.state = OnPlanet(destination, game.player)
    }
}