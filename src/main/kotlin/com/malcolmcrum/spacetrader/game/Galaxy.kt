package com.malcolmcrum.spacetrader.game



class Galaxy(val systems: List<SolarSystem>, val startCountdown: Int, val difficulty: Difficulty) {

    fun shuffleStatuses() {
        val withStatuses = systems.filter { it.status != null }
        val withoutStatuses = systems.filter { it.status == null }
        withStatuses.forEach {
            if ((0..100).random() < 15) {
                it.status = null
            }
        }
        withoutStatuses.forEach {
            if ((0..100).random() < 15) {
                it.status = pickRandom(SystemStatus.values())
            }
        }
    }

    fun changeTradeQuantities() {
        systems.filter { it.countdown > 0 }.forEach {
            --it.countdown
            when {
                it.countdown > startCountdown -> it.countdown = startCountdown
                it.countdown <= 0 -> initializeTradeItems(it)
                else -> increaseTradeItems(it)
            }
        }
    }

    fun determinePrices(system: SolarSystem) {
        TradeItem.values().forEach {
            val basePrice = standardPrice(it, system.size, system.tech, system.politics, system.specialResource)
        }
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

    private fun increaseTradeItems(system: SolarSystem) {
        TradeItem.values().forEach {
            system.tradeItems[it] = when {
                it == TradeItem.NARCOTICS && !system.politics.drugsOk -> 0
                it == TradeItem.FIREARMS && !system.politics.firearmsOk -> 0
                system.tech < it.techRequiredForProduction -> 0
                else -> Math.max(0, system.tradeItems[it]!! + (0..5).random() - (0..5).random())
            }
        }
    }

    private fun initializeTradeItems(system: SolarSystem) {
        TradeItem.values().forEach {
            system.tradeItems[it] = when {
                it == TradeItem.NARCOTICS && !system.politics.drugsOk -> 0
                it == TradeItem.FIREARMS && !system.politics.firearmsOk -> 0
                system.tech < it.techRequiredForProduction -> 0
                else -> generateAmount(it, system)
            }
        }
    }

    private fun generateAmount(item: TradeItem, system: SolarSystem): Int {
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
}