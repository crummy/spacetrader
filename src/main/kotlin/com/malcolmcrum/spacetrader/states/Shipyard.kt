package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.model.*

class Shipyard(val system: SolarSystem) {
    val escapePodAvailable = system.tech >= ShipType.FLEA.minTechLevel!!
    val shipsAvailable = ShipType.values().filter { it.minTechLevel != null && system.tech >= it.minTechLevel }
    val weaponsAvailable = Weapon.values().filter { it.minTechLevel != null && system.tech >= it.minTechLevel }
    val gadgetsAvailable = Gadget.values().filter { it.minTechLevel != null && system.tech >= it.minTechLevel }
    val shieldsAvailable = ShieldType.values().filter { it.minTechLevel != null && system.tech >= it.minTechLevel }
}