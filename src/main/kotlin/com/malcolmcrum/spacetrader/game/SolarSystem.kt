package com.malcolmcrum.spacetrader.game

class SolarSystem(val x: Int,
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

    init {
        TradeItem.values().forEach { tradeItems[it] = 0 }
    }

    override fun toString(): String {
        return "[$x, $y]"
    }
}