package com.malcolmcrum.spacetrader.nouns

import com.malcolmcrum.spacetrader.model.*

data class Ship(val type: ShipType,
                val captain: CrewMember,
                val gadgets: MutableList<Gadget> = ArrayList(),
                val shields: MutableList<Shield> = ArrayList(),
                val weapons: MutableList<Weapon> = ArrayList(),
                val crew: MutableList<CrewMember> = ArrayList(),
                private val cargo: MutableList<Cargo> = ArrayList(),
                var fuel: Int = type.fuelTanks,
                var hullStrength: Int = type.hullStrength,
                var tribbles: Int = 0) {

    val hold = Hold(cargo, type)

    fun traderSkill(): Int {
        return crew.plus(captain).maxBy { it.trader }?.trader!!
    }

    fun add(weapon: Weapon) {
        assert(weapons.size < type.weaponSlots)
        weapons.add(weapon)
    }

    fun add(shield: ShieldType) {
        assert(shields.size < type.shieldSlots)
        shields.add(Shield(shield))
    }

    fun add(gadget: Gadget) {
        assert(gadgets.size < type.gadgetSlots)
        gadgets.add(gadget)
    }

    fun remove(weapon: Weapon) {
        val removed = weapons.remove(weapon)
        if (!removed) {
            throw Exception("Cannot remove $weapon; none exists")
        }
    }

    fun remove(type: ShieldType) {
        val shield = shields.find { it.type == type } ?: throw Exception("Player doesn't have a $type to sell")
        shields.remove(shield)
    }

    fun remove(gadget: Gadget) {
        val removed = gadgets.remove(gadget)
        if (!removed) {
            throw Exception("Cannot remove $gadget; none exists")
        }
    }

    fun crewCost(): Int {
        return crew.sumBy { it.dailyCost() }
    }

    fun rechargeShields() {
        shields.forEach { it.strength = it.type.power }
    }

    fun valueWithoutCargo(forInsurance: Boolean): Int {
        val shipValueMultiplier = if (tribbles > 0 && !forInsurance) 1/4 else 3/4
        val shipValue = type.basePrice * shipValueMultiplier
        val equipmentValue = weapons.sumBy { it.sellPrice() } + gadgets.sumBy { it.sellPrice() } + shields.sumBy { it.type.sellPrice() }
        return shipValue + equipmentValue
    }

    fun value(forInsurance: Boolean): Int {
        val multiplier = if (tribbles > 0 || !forInsurance) 1/4 else 3/4
        // TODO: handle if scarab rescued
        val repairCosts = (type.hullStrength - hullStrength) * type.repairCosts
        // TODO: handle fuel compactor
        val fuelCosts = (fuel - type.fuelTanks) * type.costOfFuel
        val weapons = weapons.sumBy { it.sellPrice() }
        val shields = shields.sumBy { it.type.sellPrice() }
        val gadgets = gadgets.sumBy { it.sellPrice() }

        return type.basePrice * multiplier - repairCosts - fuelCosts + weapons + shields + gadgets
    }
}