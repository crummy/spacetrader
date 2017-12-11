package com.malcolmcrum.spacetrader.game

import com.malcolmcrum.spacetrader.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MarketTest {
    @Test
    fun `verify no drugs sold`() {
        val noDrugsSystem = SolarSystem("nodrugs", 0, 0, TechLevel.HI_TECH, Politics.FASCIST, null, SystemSize.HUGE, null)
        val market = Market(noDrugsSystem, Difficulty.NORMAL)

        val prices = market.items[TradeItem.NARCOTICS]!!

        assertEquals(0, prices.getBuyPrice(0, 0))
        assertEquals(0, prices.getSellPrice(0))
        assertEquals(0, prices.amount)
    }

    @Test
    fun `verify no weapons sold`() {
        val noGunsSystem = SolarSystem("noguns", 0, 0, TechLevel.HI_TECH, Politics.PACIFIST, null, SystemSize.HUGE, null)
        val market = Market(noGunsSystem, Difficulty.NORMAL)

        val prices = market.items[TradeItem.FIREARMS]!!

        assertEquals(0, prices.getBuyPrice(0, 0))
        assertEquals(0, prices.getSellPrice(0))
        assertEquals(0, prices.amount)
    }
}