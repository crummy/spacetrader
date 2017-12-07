package com.malcolmcrum.spacetrader.game

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class GalaxyGeneratorTest {

    private val galaxyGenerator = GalaxyGenerator()

    @Test
    fun `verify all systems maintain a minimum distance`() {
        val systems = galaxyGenerator.generateGalaxy()

        printGalaxy(systems)

        systems.forEach { system ->
            run {
                systems.filter { it != system }.forEach { otherSystem ->
                    run {
                        assertTrue(system.distanceTo(otherSystem) >= MIN_DISTANCE, "Systems too close: $system, $otherSystem")
                    }
                }
            }
        }
    }

    // just a lil debugging fun
    private fun printGalaxy(systems: List<SolarSystem>) {
        for (y in 0..GALAXY_HEIGHT) {
            for (x in 0..GALAXY_WIDTH) {
                val system = systems.firstOrNull { it.x == x && it.y == y }
                if (system != null) {
                    if (system.wormholeDestination != null) {
                        print("W")
                    } else {
                        print("#")
                    }
                } else {
                    print(".")
                }
            }
            println()
        }
    }

    @Test
    fun `verify all politics and techlevels are compatible`() {
        val systems = galaxyGenerator.generateGalaxy()

        systems.forEach { assertTrue(it.politics.compatibleWith(it.tech)) }
    }

    @Test
    fun `verify we can always find a starting system`() {
        repeat(10, {
            val systems = galaxyGenerator.generateGalaxy()
            galaxyGenerator.findStartingSystem(ShipType.GNAT, systems)
        })
    }

}