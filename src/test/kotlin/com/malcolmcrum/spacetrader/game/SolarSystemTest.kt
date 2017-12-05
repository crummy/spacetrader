package com.malcolmcrum.spacetrader.game

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class SolarSystemTest {
    @Test
    fun `verify distance calculation`() {
        val center = system(0, 0)
        Assertions.assertEquals(1, center.distanceTo(system(1, 0)))
        Assertions.assertEquals(1, center.distanceTo(system(-1, 0)))
        Assertions.assertEquals(1, center.distanceTo(system(0, -1)))
        Assertions.assertEquals(1, center.distanceTo(system(1, -1)))
        Assertions.assertEquals(5, center.distanceTo(system(3, 4)))
    }

    private fun system(x: Int, y: Int): SolarSystem {
        return SolarSystem("test", x, y, TechLevel.HI_TECH, Politics.ANARCHY, null, SystemSize.HUGE, null)
    }
}