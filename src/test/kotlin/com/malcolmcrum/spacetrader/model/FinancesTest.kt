package com.malcolmcrum.spacetrader.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class FinancesTest {

    @Test
    fun canAffordSubtract() {
        val finances = Finances(1000, 0)
        finances.withdraw(500)

        assertEquals(500, finances.credits)
        assertEquals(0, finances.debt)
    }

    @Test
    fun cannotAffordSubtract() {
        val finances = Finances(1000, 0)
        finances.withdraw(1500)

        assertEquals(0, finances.credits)
        assertEquals(500, finances.debt)
    }
}