package com.malcolmcrum.spacetrader.nouns

import com.malcolmcrum.spacetrader.model.*

data class Ship(val ship: ShipType,
                val gadgets: List<Gadget>,
                val shields: List<Shield>,
                val weapons: List<Weapon>,
                val cargo: Hold,
                val fuel: Int,
                val crew: List<CrewMember>, var tribbles: Int, var hullStrength: Int)