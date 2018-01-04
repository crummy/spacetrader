package com.malcolmcrum.spacetrader.model

import com.malcolmcrum.spacetrader.nouns.Galaxy
import com.malcolmcrum.spacetrader.states.GameState

typealias GameId = String
data class Game (val galaxy: Galaxy, val player: Player, val id: GameId, val difficulty: Difficulty, var state: GameState)