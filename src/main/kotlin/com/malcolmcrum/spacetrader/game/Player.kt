package com.malcolmcrum.spacetrader.game

data class Player(val name: String,
                  var ship: ShipType,
                  val cargo: MutableList<Cargo> = ArrayList(),
                  val weapons: MutableList<Weapon> = ArrayList(),
                  var shields: MutableList<Shield> = ArrayList(),
                  val gadgets: MutableList<Gadget> = ArrayList(),
                  val crew: MutableList<CrewMember> = ArrayList(),
                  var fuelLeft: Int = 0,
                  var hullLeft: Int = 0,
                  var tribbles: Int = 0) {

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
}

data class Shield(val type: ShieldType, val strength: Int = type.power)