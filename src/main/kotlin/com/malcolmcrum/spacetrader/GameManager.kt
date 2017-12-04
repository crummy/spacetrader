package com.malcolmcrum.spacetrader

import com.malcolmcrum.spacetrader.game.GameState
import java.util.*

data class Id(val value: String) {
    constructor() : this(UUID.randomUUID().toString())
}

class GameManager {
    val games = HashMap<Id, GameState>()
}