package com.malcolmcrum.spacetrader.game

class SolarSystem(val name: String,
                  val x: Int,
                  val y: Int,
                  val tech: TechLevel,
                  val politics: Politics,
                  val specialResource: SpecialResource?,
                  val size: SystemSize,
                  var status: SystemStatus?,
                  val tradeItems: MutableMap<TradeItem, Int> = HashMap()) {
    var visited = false
    var special = -1 // TODO
    var countdown = -1 // TODO
    val mercenary: CrewMember? = null
    val specialEvent: Any? = null
    var wormholeDestination: SolarSystem? = null


    init {
        TradeItem.values().forEach { tradeItems[it] = 0 }
    }

    override fun toString(): String {
        return "[$x, $y]"
    }

    fun hasWormholeTo(system: SolarSystem): Boolean {
        return wormholeDestination == system
    }

    fun distanceTo(destination: SolarSystem): Int {
        return distanceTo(destination.x, destination.y)
    }

    fun distanceTo(x: Int, y: Int): Int {
        return Math.hypot((this.x - x).toDouble(), (this.y - y).toDouble()).toInt()
    }

}