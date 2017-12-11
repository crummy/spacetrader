package com.malcolmcrum.spacetrader.model

import com.malcolmcrum.spacetrader.GameManager
import com.malcolmcrum.spacetrader.game.Galaxy
import com.malcolmcrum.spacetrader.game.GameState
import com.malcolmcrum.spacetrader.game.Player

data class Game (val galaxy: Galaxy, val player: Player, val id: GameManager.Id, var state: GameState)