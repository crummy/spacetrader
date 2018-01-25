package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.model.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest

internal class OpponentGeneratorTest {
    private lateinit var opponentGenerator: OpponentGenerator

    @BeforeEach
    fun setUp() {
        opponentGenerator = OpponentGenerator(Difficulty.BEGINNER, 1000, PoliceRecord(),
                SolarSystem("system", 0, 0, TechLevel.HI_TECH, Politics.TECHNOCRACY, SystemSize.HUGE, null, null))
    }

    @RepeatedTest(100)
    fun `verify Morgan's Laser never generated`() {
        val weapon = opponentGenerator.randomWeapon()
        assertFalse(weapon == Weapon.MORGANS)
    }
}