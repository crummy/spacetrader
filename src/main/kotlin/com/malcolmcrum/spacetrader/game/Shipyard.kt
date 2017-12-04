package com.malcolmcrum.spacetrader.game

class Shipyard(val system: SolarSystem) {
    val escapePodAvailable = system.tech >= ShipType.FLEA.minTechLevel
    val shipsAvailable = ShipType.values().filter { it.minTechLevel >= system.tech }
}