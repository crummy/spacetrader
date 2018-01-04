package com.malcolmcrum.spacetrader.model

data class Cargo(val item: TradeItem, val buyPrice: Int)

class Hold(private val items: MutableList<Cargo>, private var shipType: ShipType) {
    constructor(shipType: ShipType) : this(mutableListOf(), shipType)

    fun count(item: TradeItem): Int {
        return items.count { it.item == item }
    }

    fun add(item: TradeItem, amount: Int, buyPrice: Int) {
        repeat(amount) {
            items.add(Cargo(item, buyPrice))
        }
    }

    fun fullBays(): Int {
        return items.size
    }

    fun emptyBays(): Int {
        return shipType.cargoBays - items.size
    }

    fun remove(item: TradeItem, amount: Int) {

        for (i in (0 until amount)) {
            val toRemove = items.first { it.item == item }
            items.remove(toRemove)
        }
    }

    fun purchasePrice(item: TradeItem): Int {
        return items
                .filter { it.item == item }
                .map { it.buyPrice }
                .average()
                .toInt()
    }

    fun totalWorth(): Int {
        return items.sumBy { it.buyPrice }
    }
}