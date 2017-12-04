package com.malcolmcrum.spacetrader.game

import com.malcolmcrum.spacetrader.GameManager

data class Game (val galaxy: Galaxy, val player: Player, val id: GameManager.Id)