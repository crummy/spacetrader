package com.malcolmcrum.spacetrader.nouns

import com.malcolmcrum.spacetrader.model.Difficulty
import com.malcolmcrum.spacetrader.model.SolarSystem
import com.malcolmcrum.spacetrader.model.SystemStatus

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

    fun getSystem(systemName: String): SolarSystem {
        val system = systems.filter { it.name == systemName }
        if (system.size != 1) {
            throw AssertionError("Expected to find a system named $systemName but ${system.size} systems found")
        }
        return system.first()
    }
}