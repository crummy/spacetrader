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

        val buyPrice = controller.getBuyPrice(TradeItem.NARCOTICS, 10, PoliceRecord())
        val sellPrice = controller.getSellPrice(TradeItem.NARCOTICS, PoliceRecord())
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

        val buyPrice = controller.getBuyPrice(TradeItem.FIREARMS, 10, PoliceRecord())
        val sellPrice = controller.getSellPrice(TradeItem.FIREARMS, PoliceRecord())
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
            assertTrue(controller.getBuyPrice(item, (0..MAX_SKILL).random(), PoliceRecord()) >= 0)
            assertTrue(controller.getSellPrice(item, PoliceRecord()) >= 0)
        }
    }

    @RepeatedTest(100)
    fun `verify buy price is never less than sell price`() {
        val market = Market()
        val controller = MarketController(market, randomSystem(), pickRandom(Difficulty.values()))
        controller.updatePrices()

        TradeItem.values().forEach { item ->
            val buyPrice = controller.getBuyPrice(item, (0..MAX_SKILL).random(), PoliceRecord())
            val sellPrice = controller.getSellPrice(item, PoliceRecord())
            assertTrue(buyPrice >= sellPrice)
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