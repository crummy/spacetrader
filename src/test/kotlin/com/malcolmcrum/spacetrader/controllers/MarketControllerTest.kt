package com.malcolmcrum.spacetrader.controllers

import com.malcolmcrum.spacetrader.model.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class MarketControllerTest {
    @Test
    fun `verify no drugs sold`() {
        val noDrugsSystem = SolarSystem("nodrugs", 0, 0, TechLevel.HI_TECH, Politics.FASCIST, null, SystemSize.HUGE, null)
        val market = Market()
        val controller = MarketController(market, noDrugsSystem, Difficulty.NORMAL)

        val buyPrice = controller.getBuyPrice(TradeItem.NARCOTICS, 10, 0)
        val sellPrice = controller.getSellPrice(TradeItem.NARCOTICS, 10)
        val amount = controller.getAmount(TradeItem.NARCOTICS)

        Assertions.assertEquals(0, buyPrice)
        Assertions.assertEquals(0, sellPrice)
        Assertions.assertEquals(0, amount)
    }

    @Test
    fun `verify no weapons sold`() {
        val noGunsSystem = SolarSystem("noguns", 0, 0, TechLevel.HI_TECH, Politics.PACIFIST, null, SystemSize.HUGE, null)
        val market = Market()
        val controller = MarketController(market, noGunsSystem, Difficulty.NORMAL)

        val buyPrice = controller.getBuyPrice(TradeItem.FIREARMS, 10, 0)
        val sellPrice = controller.getSellPrice(TradeItem.FIREARMS, 10)
        val amount = controller.getAmount(TradeItem.FIREARMS)

        Assertions.assertEquals(0, buyPrice)
        Assertions.assertEquals(0, sellPrice)
        Assertions.assertEquals(0, amount)
    }
}