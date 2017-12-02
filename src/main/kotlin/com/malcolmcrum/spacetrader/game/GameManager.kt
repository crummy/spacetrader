package com.malcolmcrum.spacetrader.game

class GameManager() {
    val galaxyGenerator = GalaxyGenerator()

    fun startNewGame(commanderName: String) {
        val galaxy = galaxyGenerator.generateGalaxy()
        galaxyGenerator.placeMercenaries(galaxy)
        galaxyGenerator.placeSpecialEvents(galaxy)
        val player = Player("name", ShipType.GNAT)
    }
}