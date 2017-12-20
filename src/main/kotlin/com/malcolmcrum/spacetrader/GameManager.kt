package com.malcolmcrum.spacetrader

import com.malcolmcrum.spacetrader.game.Galaxy
import com.malcolmcrum.spacetrader.game.GalaxyGenerator
import com.malcolmcrum.spacetrader.game.Player
import com.malcolmcrum.spacetrader.model.Difficulty
import com.malcolmcrum.spacetrader.model.Game
import com.malcolmcrum.spacetrader.views.OnPlanet
import org.slf4j.LoggerFactory
import java.util.*

private val log = LoggerFactory.getLogger(GameManager::class.java)!!

class GameManager {
    data class Id(val value: String) {
        constructor() : this(UUID.randomUUID().toString())
    }

    val games = HashMap<Id, Game>()

    private val galaxyGenerator = GalaxyGenerator()

    fun newGame(commanderName: String, difficulty: Difficulty): Id {
        val systems = galaxyGenerator.generateGalaxy()
        galaxyGenerator.placeMercenaries(systems)
        galaxyGenerator.placeSpecialEvents(systems)
        val player = Player(commanderName, difficulty)
        val galaxy = Galaxy(systems, 0, difficulty)

        val id = Id()
        val startingSystem = galaxyGenerator.findStartingSystem(player.ship, systems)
        val state = OnPlanet(startingSystem, player)
        games.put(id, Game(galaxy, player, id, difficulty, state))

        log.info("Created game $id for player $commanderName")
        return id
    }
}