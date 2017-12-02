package com.malcolmcrum.spacetrader

import com.malcolmcrum.spacetrader.game.GalaxyGenerator
import com.malcolmcrum.spacetrader.game.MIN_DISTANCE
import com.malcolmcrum.spacetrader.game.SolarSystem
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class GalaxyGeneratorTest {

    @Test
    fun verifyAllSystemsMaintainMinDistance() {
        val systems = GalaxyGenerator().generateGalaxy()

        systems.forEach { system ->
            run {
                systems.filter { it != system }.forEach { otherSystem ->
                    run {
                        assertTrue(distanceBetween(system, otherSystem) >= MIN_DISTANCE, "Systems too close: $system, $otherSystem")
                    }
                }
            }
        }
    }

    fun distanceBetween(from: SolarSystem, to: SolarSystem): Double {
        return Math.hypot((from.x - to.x).toDouble(), (from.y - to.y).toDouble())
    }

}