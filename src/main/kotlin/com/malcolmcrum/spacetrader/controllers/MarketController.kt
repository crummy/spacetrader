package com.malcolmcrum.spacetrader.controllers

import com.malcolmcrum.spacetrader.game.random
import com.malcolmcrum.spacetrader.model.*

val MAX_SKILL = 10 // TODO: move elsewhere

class MarketController(private val market: Market, private val system: SolarSystem, private val difficulty: Difficulty) {
    fun updateAmounts() {
        when {
            market.countdown > getInitialCountdown(difficulty) -> market.countdown = getInitialCountdown(difficulty)
            market.countdown <= 0 -> TradeItem.values().forEach { resetQuantity(it) }
            else -> TradeItem.values().forEach { increaseQuantity(it) }
        }
    }

    fun updatePrices() {
        TradeItem.values().forEach {
            var price = standardPrice(it, system.size, system.tech, system.politics, system.specialResource)
            if (it.doublePriceStatus == system.status) {
                price = price * 3 shr 1
            }

            price = price + (0..it.priceVariance).random() - (0..it.priceVariance).random()

            market.basePrices[it] = price
        }
    }

    private fun isNotSold(item: TradeItem): Boolean {
        return when {
            item == TradeItem.NARCOTICS && !system.politics.drugsOk -> true
            item == TradeItem.FIREARMS && !system.politics.firearmsOk -> true
            system.tech < item.techRequiredForProduction -> true
            else -> false
        }
    }

    private fun resetQuantity(item: TradeItem) {
        market.amounts[item] = standardQuantity(item)
    }

    private fun standardQuantity(item: TradeItem): Int {
        if (isNotSold(item)) {
            return 0
        }

        var quantity = 9 + (0..5).random() - Math.abs(item.techOptimalProduction.ordinal - system.tech.ordinal) * (1 + system.size.ordinal)

        if (item == TradeItem.NARCOTICS || item == TradeItem.ROBOTS) {
            quantity = (quantity * (5 - difficulty.ordinal) / (6 - difficulty.ordinal)) + 1
        }

        if (system.specialResource == item.cheapResource) {
            quantity *= 4/3
        }

        if (system.specialResource == item.expensiveResource) {
            quantity = (quantity * 3) shr 2
        }

        if (system.status == item.doublePriceStatus) {
            quantity /= 5
        }

        quantity = quantity - (0..10).random() + (0..10).random()

        return Math.max(quantity, 0)
    }

    private fun getInitialCountdown(difficulty: Difficulty) = difficulty.ordinal + 3

    private fun increaseQuantity(item: TradeItem) {
        val increment = when {
            isNotSold(item) -> 0
            else -> Math.max(0, market.amounts[item]!! + (0..5).random() - (0..5).random())
        }
        val previousValue = market.amounts[item]!!
        market.amounts[item] = previousValue + increment
    }

    fun getBuyPrice(item: TradeItem, tradeSkill: Int, policeRecordScore: Int): Int {
        if (isNotSold(item)) {
            return 0
        }

        val basePrice = market.basePrices[item]!!
        var buyPrice = if (policeRecordScore <= PoliceRecord.DUBIOUS.score) {
            (basePrice * 100) / 90
        } else {
            basePrice
        }

        buyPrice = (buyPrice * (103 + (MAX_SKILL - tradeSkill)) / 100)
        return if (buyPrice <= basePrice) {
            basePrice + 1
        } else {
            buyPrice
        }
    }

    fun getSellPrice(item: TradeItem, policeRecordScore: Int): Int {
        val basePrice = market.basePrices[item]!!
        return if (policeRecordScore <= PoliceRecord.DUBIOUS.score) {
            (basePrice * 90) / 100
        } else {
            basePrice
        }
    }

    private fun buyPrice(item: TradeItem, basePrice: Int): Int {
        var buyPrice = basePrice

        if (item.doublePriceStatus == system.status) {
            buyPrice = buyPrice * 3 shr 1
        }

        buyPrice = buyPrice + (0..item.priceVariance).random() - (0..item.priceVariance).random()

        return buyPrice
    }

    private fun standardPrice(item: TradeItem, size: SystemSize, tech: TechLevel, politics: Politics, specialResource: SpecialResource?): Int {
        if (item == TradeItem.NARCOTICS && !politics.drugsOk) return 0
        if (item == TradeItem.FIREARMS && !politics.firearmsOk) return 0
        if (item.techRequiredForUsage > tech) return 0

        var price = item.minTradePrice + (tech.ordinal * item.priceIncreasePerTechLevel)

        if (politics.desiredTradeItem == item) {
            price *= 4/3
        }

        price = (price * (100 - (2 * politics.strengthTraders))) / 100

        price = (price * (100 - size.ordinal)) / 100

        if (specialResource == item.cheapResource) {
            price *= 3 / 4
        }
        if (specialResource == item.expensiveResource) {
            price *= 4 / 3
        }

        return Math.max(0, price)
    }

    fun getAmount(item: TradeItem): Int {
        return market.amounts[item]!!
    }
}