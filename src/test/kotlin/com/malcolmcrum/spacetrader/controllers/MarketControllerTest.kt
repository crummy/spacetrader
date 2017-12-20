package com.malcolmcrum.spacetrader.controllers

import com.malcolmcrum.spacetrader.model.*
import com.malcolmcrum.spacetrader.nouns.pickRandom
import com.malcolmcrum.spacetrader.nouns.random
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.RepeatedTest
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

    @RepeatedTest(100)
    fun `verify buy and sell price always above zero in market`() {
        val market = Market()
        val controller = MarketController(market, randomSystem(), pickRandom(Difficulty.values()))
        controller.updateAmounts()
        controller.updatePrices()

        TradeItem.values().forEach { item ->
            assertTrue(market.amounts[item]!! >= 0)
            assertTrue(market.basePrices[item]!! >= 0)
            assertTrue(controller.getBuyPrice(item, (0..MAX_SKILL).random(), (-100..100).random()) >= 0)
            assertTrue(controller.getSellPrice(item, (-100..100).random()) >= 0)
        }
    }

    private fun randomSystem(): SolarSystem {
        return SolarSystem("random",
                0,
                0,
                pickRandom(TechLevel.values()),
                pickRandom(Politics.values()),
                pickRandom(SpecialResource.values()),
                pickRandom(SystemSize.values()),
                pickRandom(SystemStatus.values()))
    }
}