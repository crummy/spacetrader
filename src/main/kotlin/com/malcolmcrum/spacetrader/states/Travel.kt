package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.model.Game
import com.malcolmcrum.spacetrader.model.PoliceRecord
import com.malcolmcrum.spacetrader.model.ShipType
import com.malcolmcrum.spacetrader.model.SolarSystem
import com.malcolmcrum.spacetrader.nouns.random

class Travel(private val game: Game, private val destination: SolarSystem, clicks: Int) {
    private var raidedByPirates = false
    private var clicksRemaining = clicks

    fun warp(): GameState {
        assert(clicksRemaining >= 0)
        if (clicksRemaining == 0) {
            // TODO: handle post-Marie looting
            return arrival()
        }
        --clicksRemaining

        // TODO: handle going through rip

        // TODO: random chance of repairs

        // TODO: encounter spacemonster

        // TODO: encounter scarab

        // TODO: encounter dragonfly

        // TODO: encounter mantis

        var encounterTest = (0..(44 - 2 * game.difficulty.ordinal)).random()

        if (game.ship.type == ShipType.FLEA) {
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

        return warp()
    }

    private fun arrival(): GameState { // TODO: should this be in OnPlanet init?
        // TODO: handle reactor status

        // TODO: handle tribbles

        // TODO: repair a little bit

        // TODO: easter egg

        return OnPlanet(destination, game)
    }

    private fun traderStrength(): Int {
        return destination.politics.strengthTraders
    }

    private fun pirateStrength(): Int {
        return destination.politics.strengthPirates
    }

    private fun policeStrength(): Int {
        return when {
            game.policeRecordScore < PoliceRecord.PSYCHOPATH.score -> 3 * destination.politics.strengthPolice
            game.policeRecordScore < PoliceRecord.VILLAIN.score -> 2 * destination.politics.strengthPolice
            else -> destination.politics.strengthPolice
        }
    }

    private fun policeEncounter(): GameState {
        val opponent = OpponentGenerator(game.difficulty, game.currentWorth(), game.policeRecordScore, destination).generatePolice()
        return PoliceInspection(opponent, this, game)
    }

    private fun pirateEncounter(): GameState {
        val opponent = OpponentGenerator(game.difficulty, game.currentWorth(), game.policeRecordScore, destination).generatePirate()
        TODO()
    }

    private fun traderEncounter(): GameState {
        val opponent = OpponentGenerator(game.difficulty, game.currentWorth(), game.policeRecordScore, destination).generateTrader()
        TODO()
    }
}
