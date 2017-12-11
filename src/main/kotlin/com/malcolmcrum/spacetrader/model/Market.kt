package com.malcolmcrum.spacetrader.model

data class Market(val amounts: MutableMap<TradeItem, Int> = TradeItem.values().associateBy({it}, {0}).toMutableMap(),
                  val basePrices: MutableMap<TradeItem, Int> = TradeItem.values().associateBy({it}, {0}).toMutableMap(),
                  var countdown: Int = -1) {// TODO
}