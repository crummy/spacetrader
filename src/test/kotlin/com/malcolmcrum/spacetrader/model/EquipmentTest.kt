package com.malcolmcrum.spacetrader.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class EquipmentTest {

    @Test
    fun `gadget chances should add up to 100`() {
        val total = Gadget.values().sumBy { it.chance }
        assertEquals(100, total)
    }

    @Test
    fun `weapon chances should add up to 100`() {
        val total = Weapon.values().sumBy { it.chance }
        assertEquals(100, total)
    }

    @Test
    fun `shield chances should add up to 100`() {
        val total = ShieldType.values().sumBy { it.chance }
        assertEquals(100, total)
    }
}