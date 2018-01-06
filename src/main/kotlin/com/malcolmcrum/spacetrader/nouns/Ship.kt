package com.malcolmcrum.spacetrader.nouns

import com.malcolmcrum.spacetrader.model.*

data class Ship(val type: ShipType,
                val gadgets: MutableList<Gadget>,
                val shields: MutableList<Shield>,
                val weapons: MutableList<Weapon>,
                val crew: MutableList<CrewMember>,
                private val cargo: MutableList<Cargo>,
                val fuel: Int = type.fuelTanks,
                var hullStrength: Int = type.hullStrength,
                var tribbles: Int = 0) {
    val hold = Hold(cargo, type)
}