package com.malcolmcrum.spacetrader

import com.malcolmcrum.spacetrader.model.Difficulty
import com.malcolmcrum.spacetrader.model.Game
import com.malcolmcrum.spacetrader.model.GameId
import com.malcolmcrum.spacetrader.nouns.Galaxy
import com.malcolmcrum.spacetrader.nouns.GalaxyGenerator
import com.malcolmcrum.spacetrader.nouns.Player
import com.malcolmcrum.spacetrader.views.OnPlanet
import org.slf4j.LoggerFactory
import java.util.*

private val log = LoggerFactory.getLogger(GameManager::class.java)!!

class GameManager {
    val games = HashMap<GameId, Game>()

    private val galaxyGenerator = GalaxyGenerator()

    fun newGame(commanderName: String, pilot: Int, fighter: Int, trader: Int, engineer: Int, difficulty: Difficulty): GameId {
        // TODO: Assert each skill is between 0 and 10
        // TODO: Assert total skills == 20
        val systems = galaxyGenerator.generateGalaxy()
        galaxyGenerator.placeMercenaries(systems)
        galaxyGenerator.placeSpecialEvents(systems)
        galaxyGenerator.setPrices(systems, difficulty)
        galaxyGenerator.setAmounts(systems, difficulty)
        val player = Player(commanderName, trader, fighter, engineer, pilot, difficulty)
        val galaxy = Galaxy(systems, 0, difficulty)

        val id = createGameId()
        val startingSystem = galaxyGenerator.findStartingSystem(player.ship, systems)
        val state = OnPlanet(startingSystem, player)
        games.put(id, Game(galaxy, player, id, difficulty, state))

        log.info("Created game $id for player $commanderName")
        return id
    }

    private fun createGameId(): GameId {
        return UUID.randomUUID().toString()
    }
}