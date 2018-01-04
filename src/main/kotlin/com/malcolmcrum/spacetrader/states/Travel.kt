package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.model.Player
import com.malcolmcrum.spacetrader.model.PoliceRecord
import com.malcolmcrum.spacetrader.model.ShipType
import com.malcolmcrum.spacetrader.model.SolarSystem
import com.malcolmcrum.spacetrader.nouns.random

class Travel(private val player: Player, private val destination: SolarSystem, private val clicks: Int) : GameState {
    var raidedByPirates = false

    fun travel(): GameState {
        // TODO: handle going through rip

        // TODO: random chance of repairs

        // TODO: encounter spacemonster

        // TODO: encounter scarab

        // TODO: encounter dragonfly

        // TODO: encounter mantis

        var encounterTest = (0..(44 - 2 * player.difficulty.ordinal)).random()

        if (player.ship == ShipType.FLEA) {
            encounterTest *= 2
        }
        if (encounterTest < pirateStrength() && !raidedByPirates) {
            return pirateEncounter()
        } else if (encounterTest < pirateStrength() + policeStrength()) {
            return policeEncounter()
        } else if (encounterTest < pirateStrength() + policeStrength() + traderStrength()) {
            return traderEncounter()
        } else {
            // TODO: jonathan wild status

            // TODO: mantis
        }
    }

    private fun traderStrength(): Int {
        return destination.politics.strengthTraders
    }

    private fun pirateStrength(): Int {
        return destination.politics.strengthPirates
    }

    private fun policeStrength(): Int {
        return when {
            player.policeRecordScore < PoliceRecord.PSYCHOPATH.score -> 3 * destination.politics.strengthPolice
            player.policeRecordScore < PoliceRecord.VILLAIN.score -> 2 * destination.politics.strengthPolice
            else -> destination.politics.strengthPolice
        }
    }

    private fun policeEncounter(): GameState {
        return
    }

    private fun pirateEncounter(): GameState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun traderEncounter(): GameState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
