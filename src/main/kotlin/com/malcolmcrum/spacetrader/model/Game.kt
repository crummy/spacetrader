package com.malcolmcrum.spacetrader.model

import com.malcolmcrum.spacetrader.game.Galaxy
import com.malcolmcrum.spacetrader.game.GameState
import com.malcolmcrum.spacetrader.game.Player

typealias GameId = String
data class Game (val galaxy: Galaxy, val player: Player, val id: GameId, val difficulty: Difficulty, var state: GameState)