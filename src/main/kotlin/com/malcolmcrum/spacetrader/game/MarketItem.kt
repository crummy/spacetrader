package com.malcolmcrum.spacetrader.game

class MarketItem(private val system: SolarSystem, val item: TradeItem) {
    fun amount() = system.tradeItems[item]
}