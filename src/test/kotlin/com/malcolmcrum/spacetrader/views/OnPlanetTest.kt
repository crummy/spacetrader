package com.malcolmcrum.spacetrader.views

import com.malcolmcrum.spacetrader.controllers.MarketController
import com.malcolmcrum.spacetrader.model.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class OnPlanetTest {

    private lateinit var planet : OnPlanet
    private lateinit var player : Player

    @BeforeEach
    fun setup() {
        val system = SolarSystem("test", 0, 0, TechLevel.HI_TECH, Politics.TECHNOCRACY, null, SystemSize.HUGE, null)
        val difficulty = Difficulty.NORMAL
        val market = MarketController(system.market, system, difficulty)
        market.updateAmounts()
        market.updatePrices()
        player = Player("player", 5, 5, 5, 5, difficulty)
        planet = OnPlanet(system, player)
    }

    @Test
    fun buyTradeItem() {
        val item = TradeItem.values().first { item -> planet.market.getAmount(item) > 0 }
        val amount = planet.market.getAmount(item)
        val cost = planet.market.getBuyPrice(item, player.traderSkill(), player.policeRecordScore) * amount
        player.finances.add(cost)
        val creditsBefore = player.finances.credits

        planet.buyTradeItem(item, amount)

        assertTrue(player.finances.credits == creditsBefore - cost)
        assertTrue(player.cargo.count(item) == amount)
    }

    @Test
    fun sellTradeItem() {
        val item = TradeItem.values().first { item -> planet.market.getAmount(item) > 0 }
        val cost = planet.market.getBuyPrice(item, player.traderSkill(), player.policeRecordScore)
        player.finances.add(cost)
        planet.buyTradeItem(item, 1)

        val price = planet.market.getSellPrice(item, player.policeRecordScore)
        val creditsBefore = player.finances.credits
        planet.sellTradeItem(item, 1)

        assertTrue(player.finances.credits == creditsBefore + price)
    }
}