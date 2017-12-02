package com.malcolmcrum.spacetrader.game

fun repairShip() {
    // TODO
}

fun refuelShip() {
    // TODO
}

fun buyEscapePod() {
    // TODO
}

fun buyShip() {
    // TODO
}

class Shipyard(val system: SolarSystem) {
    val escapePodAvailable = system.tech >= ShipType.FLEA.minTechLevel
    val shipsAvailable = ShipType.values().filter { it.minTechLevel >= system.tech }
}