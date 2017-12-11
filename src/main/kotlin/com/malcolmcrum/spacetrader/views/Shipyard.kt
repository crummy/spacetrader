package com.malcolmcrum.spacetrader.views

import com.malcolmcrum.spacetrader.model.*

class Shipyard(val system: SolarSystem) {
    val escapePodAvailable = system.tech >= ShipType.FLEA.minTechLevel!!
    val shipsAvailable = ShipType.values().filter { it.minTechLevel != null && it.minTechLevel >= system.tech }
    val weaponsAvailable = Weapon.values().filter { it.minTechLevel != null && it.minTechLevel >= system.tech }
    val gadgetsAvailable = Gadget.values().filter { it.minTechLevel != null && it.minTechLevel >= system.tech }
    val shieldsAvailable = ShieldType.values().filter { it.minTechLevel != null && it.minTechLevel >= system.tech }
}