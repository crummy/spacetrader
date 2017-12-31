package com.malcolmcrum.spacetrader.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class HoldTest {

    private lateinit var hold: Hold

    @BeforeEach
    fun setUp() {
        hold = Hold(ShipType.BEETLE)
    }

    @Test
    fun `verify adding multiple items`() {
        hold.add(TradeItem.FIREARMS, 1, 100)
        hold.add(TradeItem.FIREARMS, 1, 100)

        assertEquals(2, hold.count(TradeItem.FIREARMS))
    }

    @Test
    fun `verify adding different items`() {
        hold.add(TradeItem.FIREARMS, 1, 100)
        hold.add(TradeItem.NARCOTICS, 10, 100)

        assertEquals(1, hold.count(TradeItem.FIREARMS))
        assertEquals(10, hold.count(TradeItem.NARCOTICS))
    }

    @Test
    fun `verify removing items`() {
        hold.add(TradeItem.FIREARMS, 2, 100)
        hold.remove(TradeItem.FIREARMS, 1)

        assertEquals(1, hold.count(TradeItem.FIREARMS))
    }
}