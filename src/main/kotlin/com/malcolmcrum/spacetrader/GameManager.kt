package com.malcolmcrum.spacetrader

import com.malcolmcrum.spacetrader.controllers.GalaxyController
import com.malcolmcrum.spacetrader.controllers.GameStateController
import com.malcolmcrum.spacetrader.controllers.OnPlanetController
import com.malcolmcrum.spacetrader.model.*
import com.malcolmcrum.spacetrader.nouns.Galaxy
import com.malcolmcrum.spacetrader.nouns.Ship
import com.malcolmcrum.spacetrader.states.*
import org.slf4j.LoggerFactory
import java.util.*

private val log = LoggerFactory.getLogger(GameManager::class.java)!!

private const val MAX_SKILL = 10
private const val MAX_INITIAL_SKILL_TOTAL = 20

class GameManager {
    val games = HashMap<GameId, Game>()

    private val galaxyGenerator = GalaxyController()

    fun newGame(commanderName: String, pilot: Int, fighter: Int, trader: Int, engineer: Int, difficulty: Difficulty): GameId {
        assert(pilot <= MAX_SKILL)
        assert(fighter <= MAX_SKILL)
        assert(trader <= MAX_SKILL)
        assert(engineer <= MAX_SKILL)
        assert(pilot + fighter + trader + engineer <= MAX_INITIAL_SKILL_TOTAL)

        val systems = galaxyGenerator.generateGalaxy()
        galaxyGenerator.placeMercenaries(systems)
        galaxyGenerator.placeSpecialEvents(systems)
        galaxyGenerator.setPrices(systems, difficulty)
        galaxyGenerator.setAmounts(systems, difficulty)
        val galaxy = Galaxy(systems, 0, difficulty)
        val captain = CrewMember(commanderName, trader, fighter, engineer, pilot, null)
        val ship = Ship(ShipType.GNAT, captain)
        val id = createGameId()
        val startingSystem = galaxyGenerator.findStartingSystem(ship.type, systems)
        val game = Game(galaxy, id, difficulty, ship, GameState.StartGame(startingSystem))
        games.put(id, game)

        log.info("Created game $id for player $commanderName")
        return id
    }

    fun getController(gameId: GameId): GameStateController {
        val game = games[gameId] ?: throw Exception("No game found for id $gameId")
        val state = game.state
        return when(state) {
            is GameState.GameOver -> TODO()
            is GameState.StartGame -> OnPlanetController(state.startingSystem, game.finances, game.difficulty, game.ship, game.escapePod, game.policeRecord, game.insurance)
            is GameState.OnPlanet -> OnPlanetController(state.system, game.finances, game.difficulty, game.ship, game.escapePod, game.policeRecord, game.insurance)
            is GameState.Travel -> {
                val opponentGenerator = OpponentGenerator(game.difficulty, game.currentWorth(), game.policeRecord, state.destination)
                game.state = TravelController(state, game.policeRecord, game.difficulty, game.ship.type, opponentGenerator).warp()
                return getController(gameId)
            }
            is GameState.PoliceInspection -> PoliceInspection(state.opponent, state.travel, game.finances, game.policeRecord, game.ship.hold, game.difficulty, game.currentWorth())
            is GameState.PoliceAttack -> TODO()
            is GameState.LeaveEncounter -> LeaveEncounter(state.opponent, state.travel, state.text)
        }
    }

    private fun createGameId(): GameId {
        return UUID.randomUUID().toString()
    }

    fun get(id: GameId): Game {
        return games[id] ?: throw Exception("No game found for id $id")
    }
}