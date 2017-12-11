package com.malcolmcrum.spacetrader.game

import com.malcolmcrum.spacetrader.model.*

data class Player(val name: String,
                  var difficulty: Difficulty,
                  var ship: ShipType = ShipType.GNAT,
                  val cargo: MutableList<Cargo> = ArrayList(),
                  val weapons: MutableList<Weapon> = ArrayList(),
                  var shields: MutableList<Shield> = ArrayList(),
                  val gadgets: MutableList<Gadget> = ArrayList(),
                  val crew: MutableList<CrewMember> = ArrayList(),
                  var fuelLeft: Int = 0,
                  var hullLeft: Int = 0,
                  var tribbles: Int = 0,
                  val finances: Finances = Finances(1000, 0),
                  var hasEscapePod: Boolean = false,
                  var daysWithoutClaim: Int = 0,
                  var hasInsurance: Boolean = false,
                  var days: Int = 0,
                  var policeRecordScore: Int = 0,
                  var reputationScore: Int = 0) {

    fun getHull() = Amount(hullLeft, ship.hullStrength)

    fun getFuel() = Amount(fuelLeft, ship.fuelTanks)

    fun setShipType(type: ShipType) {
        this.ship = type
        this.fuelLeft = type.fuelTanks
        this.hullLeft = type.hullStrength
    }

    fun addWeapon(type: Weapon) {
        assert(weapons.size <= ship.weaponSlots)
        weapons.add(type)
    }

    fun addShield(type: ShieldType) {
        assert(shields.size <= ship.shieldSlots)
        shields.add(Shield(type))
    }

    fun canAfford(price: Int): Boolean {
        return finances.credits > price
        // TODO: ReserveMoney feature... or does it belong in the UI?
    }

    fun tooMuchDebtToWarp(): Boolean {
        return finances.debt > 100000
    }

    fun crewCost(): Int {
        return crew.sumBy { it.dailyCost() }
    }

    fun getInsuranceCost(): Int {
        val cost = (((shipValueWithoutCargo(true) * 5) / 2000) * (100 - Math.min(daysWithoutClaim, 90)) / 100)
        return Math.max(cost, 1)
    }

    private fun shipValueWithoutCargo(forInsurance: Boolean): Int {
        val shipValueMultiplier = if (tribbles > 0 && !forInsurance) 1/4 else 3/4
        val shipValue = ship.basePrice * shipValueMultiplier
        val equipmentValue = weapons.sumBy { it.sellPrice() } + gadgets.sumBy { it.sellPrice() } + shields.sumBy { it.type.sellPrice() }
        return shipValue + equipmentValue
    }

    fun dayPasses() {
        ++days
        if (hasInsurance) {
            ++daysWithoutClaim
        }
        if (days % 3 == 0 && policeRecordScore > PoliceRecord.CLEAN.score) {
            --policeRecordScore
        }
        if (policeRecordScore < PoliceRecord.DUBIOUS.score) {
            if (difficulty <= Difficulty.NORMAL) {
                policeRecordScore++
            } else if (days % difficulty.ordinal == 0) {
                policeRecordScore++
            }
        }
        // TODO: special event checks
    }

    fun rechargeShields() {
        shields.forEach { it.strength = it.type.power }
    }

}

data class Shield(val type: ShieldType, var strength: Int = type.power)