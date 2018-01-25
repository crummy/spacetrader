package com.malcolmcrum.spacetrader.model

data class SolarSystem(val name: String,
                       val x: Int,
                       val y: Int,
                       val tech: TechLevel,
                       val politics: Politics,
                       val size: SystemSize,
                       val specialResource: SpecialResource?,
                       var status: SystemStatus?) {
    // TODO: probably make these optional parameters
    var visited = false
    var special = -1 // TODO
    val mercenary: CrewMember? = null
    val specialEvent: Any? = null
    var wormholeDestination: SolarSystem? = null
    val market: Market = Market()
    //val market: Map<TradeItem, Int> = TradeItem.values().associateBy({it}, {0})

    override fun toString(): String {
        return "$name[$x, $y]"
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