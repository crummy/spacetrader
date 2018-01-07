package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.controllers.MarketController
import com.malcolmcrum.spacetrader.model.*

class OnPlanet(val system: SolarSystem, val game: Game) : GameState {
    private val finances = game.finances
    private val difficulty = game.difficulty
    private val ship = game.ship
    private val market = MarketController(system.market, system, difficulty)
    val shipyard = Shipyard(system)

    init {    // TODO: Should this be in GameManager?
        market.updatePrices()
        market.updateAmounts()
        // Galaxy(systems).shuffleStatuses()
    }

    fun repairShip(amount: Int) {
        val newHullStrength = ship.hullStrength + amount
        if (newHullStrength > ship.type.hullStrength) {
            throw Exception("Adding $amount to hull leaves us with greater hull strength than our ship can take")
        }
        val repairCost = amount * ship.type.repairCosts
        if (! finances.canAfford(repairCost)) {
            throw Exception("Cannot afford $repairCost of repairs")
        }
        ship.hullStrength = newHullStrength
        finances.spend(repairCost)
    }

    fun refuelShip(amount: Int) {
        val newFuel = ship.fuel + amount
        if (newFuel > ship.type.fuelTanks) {
            throw Exception("Adding $amount to fuel leaves us with more fuel than we can carry")
        }
        val fuelCost = amount * ship.type.costOfFuel
        if (! finances.canAfford(fuelCost)) {
            throw Exception("Cannot afford $fuelCost of fuel")
        }
        ship.fuel = newFuel
    }

    fun buyEscapePod() {
        if (game.hasEscapePod) {
            throw Exception("Player already has escape pod")
        }
        if (! shipyard.escapePodAvailable) {
            throw Exception("No escape pod available")
        }
        val escapePodCost = 2000
        if (! finances.canAfford(escapePodCost)) {
            throw Exception("Cannot afford $escapePodCost")
        }
        game.hasEscapePod = true
    }

    // TODO: Share logic between the sellWeapon, sellGadget and sellShield
    fun sell(type: Weapon) {
        ship.remove(type) // TODO: will this remove only one?
        finances.deposit(type.sellPrice())
    }

    fun sell(type: Gadget) {
        ship.remove(type) // TODO: will this remove only one?
        finances.deposit(type.sellPrice())
    }

    fun sell(type: ShieldType) {
        ship.remove(type)
        finances.deposit(type.sellPrice())
    }

    // TODO: Share logic between the buyWeapon, buyGadget and buyShield
    fun buyWeapon(type: Weapon) {
        if (type.minTechLevel == null) {
            throw Exception("$type cannot be purchased")
        }
        if (type.minTechLevel.ordinal > system.tech.ordinal) {
            throw Exception("$type requires ${type.minTechLevel} but $system is at ${system.tech}")
        }
        val cost = type.basePrice
        if (! finances.canAfford(cost)) {
            throw Exception("Cannot afford $cost to buy $type")
        }

        ship.add(type)
        finances.spend(cost)
    }

    fun buyGadget(type: Gadget) {
        if (type.minTechLevel == null) {
            throw Exception("$type cannot be purchased")
        }
        if (type.minTechLevel.ordinal > system.tech.ordinal) {
            throw Exception("$type requires ${type.minTechLevel} but $system is at ${system.tech}")
        }
        val cost = type.basePrice
        if (! finances.canAfford(cost)) {
            throw Exception("Cannot afford $cost to buy $type")
        }

        ship.add(type)
        finances.spend(cost)
    }

    fun buyShield(type: ShieldType) {
        if (type.minTechLevel == null) {
            throw Exception("$type cannot be purchased")
        }
        if (type.minTechLevel.ordinal > system.tech.ordinal) {
            throw Exception("$type requires ${type.minTechLevel} but $system is at ${system.tech}")
        }
        val cost = type.basePrice
        if (! finances.canAfford(cost)) {
            throw Exception("Cannot afford $cost to buy $type")
        }

        ship.add(type)
        finances.spend(cost)
    }

    fun buyShip(type: ShipType) {
        // TODO
    }

    fun buyTradeItem(item: TradeItem, amount: Int) {
        val buyPrice = market.getBuyPrice(item, ship.traderSkill(), game.policeRecordScore)
        val cost = buyPrice * amount
        if (! finances.canAfford(cost)) {
            throw Exception("Cannot afford $cost of $item")
        }
        if (amount > ship.hold.emptyBays()) {
            throw Exception("No room for $amount of $item")
        }
        market.remove(item, amount)
        ship.hold.add(item, amount, buyPrice)

        finances.spend(cost)
    }

    fun sellTradeItem(item: TradeItem, amount: Int) {
        // TODO: Ensure that this system is buying this item
        if (ship.hold.count(item) < amount) {
            throw Exception("Cannot sell $amount of $item; player does not have enough")
        }

        market.add(item, amount)

        ship.hold.remove(item, amount)

        val sellPrice = market.getSellPrice(item, game.policeRecordScore)
        val revenue = sellPrice * amount
        finances.deposit(revenue)
    }

    fun warp(destination: SolarSystem): GameState {
        // TODO: Ensure destination is in range

        payCosts(destination)

        ship.rechargeShields()

        val travelViaWormhole = system.hasWormholeTo(destination)
        val distance: Int
        if (travelViaWormhole) { // TODO: or viaSingularity
            // TODO: special handling
            distance = 0
        } else {
            distance = system.distanceTo(destination)
            ship.fuel -= distance // TODO: or min(distance, getFuel())?
        }

        // TODO: if !viaSingularity:
        game.dayPasses()

        return Travel(game, destination, distance).warp()
    }

    private fun payCosts(destination: SolarSystem) {
        // TODO: if wild, weapon check
        if (finances.tooMuchDebtToWarp()) {
            throw Exception("Debt ${finances.debt} too great to warp")
        }
        val crewCosts = ship.crewCost()
        if (! finances.canAfford(crewCosts)) {
            throw Exception("Cannot afford to pay $crewCosts to mercenaries")
        }
        val insuranceCost = game.getInsuranceCost()
        if (!finances.canAfford(insuranceCost + crewCosts)) {
            throw Exception("Cannot afford to pay $insuranceCost for insurance and $crewCosts to mercenaries")
        }
        val wormholeCost = wormholeCost(system, destination)
        if (!finances.canAfford(insuranceCost + crewCosts + wormholeCost)) {
            throw Exception("Cannot afford to pay $wormholeCost for wormhole")
        }
        // TODO: skip costs if !viaSingularity
        finances.spend(wormholeCost)
        finances.spend(insuranceCost)
        finances.spend(wormholeCost)
        finances.payInterest()
    }

    private fun wormholeCost(system: SolarSystem, destination: SolarSystem): Int {
        val travelViaWormhole = system.hasWormholeTo(destination)
        return if (travelViaWormhole) ship.type.costOfFuel * 25 else 0
    }

}