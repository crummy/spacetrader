package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.controllers.MarketController
import com.malcolmcrum.spacetrader.model.*

class OnPlanet(val system: SolarSystem, private val player: Player) : GameState {
    // TODO: Should this be in GameManager?
    init {
        val market = MarketController(system.market, system, player.difficulty)
        market.updatePrices()
        market.updateAmounts()
        // Galaxy(systems).shuffleStatuses()
    }

    val market = MarketController(system.market, system, player.difficulty)
    val shipyard = Shipyard(system)

    fun repairShip(amount: Int) {
        val newHullLeft = player.hullLeft + amount
        if (newHullLeft > player.ship.hullStrength) {
            throw Exception("Adding $amount to hull leaves us with greater hull strength than our ship can take")
        }
        val repairCost = amount * player.ship.repairCosts
        if (! player.finances.canAfford(repairCost)) {
            throw Exception("Cannot afford $repairCost of repairs")
        }
        player.hullLeft = newHullLeft
        player.finances.spend(repairCost)
    }

    fun refuelShip(amount: Int) {
        val newFuel = player.fuelLeft + amount
        if (newFuel > player.ship.fuelTanks) {
            throw Exception("Adding $amount to fuel leaves us with more fuel than we can carry")
        }
        val fuelCost = amount * player.ship.costOfFuel
        if (! player.finances.canAfford(fuelCost)) {
            throw Exception("Cannot afford $fuelCost of fuel")
        }
        player.fuelLeft = newFuel
    }

    fun buyEscapePod() {
        if (player.hasEscapePod) {
            throw Exception("Player already has escape pod")
        }
        if (! shipyard.escapePodAvailable) {
            throw Exception("No escape pod available")
        }
        val escapePodCost = 2000
        if (! player.finances.canAfford(escapePodCost)) {
            throw Exception("Cannot afford $escapePodCost")
        }
        player.hasEscapePod = true
    }

    // TODO: Share logic between the sellWeapon, sellGadget and sellShield
    fun sellWeapon(type: Weapon) {
        if (! player.weapons.contains(type)) {
            throw Exception("Player doesn't have a $type to sell")
        }
        player.weapons.remove(type) // TODO: will this remove only one?
        player.finances.deposit(type.sellPrice())
    }

    fun sellGadget(type: Gadget) {
        if (! player.gadgets.contains(type)) {
            throw Exception("Player doesn't have a $type to sell")
        }
        player.gadgets.remove(type) // TODO: will this remove only one?
        player.finances.deposit(type.sellPrice())
    }

    fun sellShield(type: ShieldType) {
        val shield = player.shields.find { it.type == type } ?: throw Exception("Player doesn't have a $type to sell")

        player.shields.remove(shield)
        player.finances.deposit(type.sellPrice())
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
        if (! player.finances.canAfford(cost)) {
            throw Exception("Cannot afford $cost to buy $type")
        }
        if (player.weapons.size > player.ship.weaponSlots) {
            throw Exception("No slot on ship for weapon")
        }

        player.weapons.add(type)
        player.finances.spend(cost)
    }

    fun buyGadget(type: Gadget) {
        if (type.minTechLevel == null) {
            throw Exception("$type cannot be purchased")
        }
        if (type.minTechLevel.ordinal > system.tech.ordinal) {
            throw Exception("$type requires ${type.minTechLevel} but $system is at ${system.tech}")
        }
        val cost = type.basePrice
        if (! player.finances.canAfford(cost)) {
            throw Exception("Cannot afford $cost to buy $type")
        }
        if (player.gadgets.size > player.ship.gadgetSlots) {
            throw Exception("No slot on ship for gadget")
        }

        player.gadgets.add(type)
        player.finances.spend(cost)
    }

    fun buyShield(type: ShieldType) {
        if (type.minTechLevel == null) {
            throw Exception("$type cannot be purchased")
        }
        if (type.minTechLevel.ordinal > system.tech.ordinal) {
            throw Exception("$type requires ${type.minTechLevel} but $system is at ${system.tech}")
        }
        val cost = type.basePrice
        if (! player.finances.canAfford(cost)) {
            throw Exception("Cannot afford $cost to buy $type")
        }
        if (player.shields.size > player.ship.shieldSlots) {
            throw Exception("No slot on ship for shield")
        }

        player.shields.add(Shield(type, type.power))
        player.finances.spend(cost)
    }

    fun buyShip(type: ShipType) {
        // TODO
    }

    fun buyTradeItem(item: TradeItem, amount: Int) {
        val buyPrice = market.getBuyPrice(item, player.traderSkill(), player.policeRecordScore)
        val cost = buyPrice * amount
        if (! player.finances.canAfford(cost)) {
            throw Exception("Cannot afford $cost of $item")
        }
        if (amount > player.cargo.emptyBays()) {
            throw Exception("No room for $amount of $item")
        }
        market.remove(item, amount)
        player.cargo.add(item, amount, buyPrice)

        player.finances.spend(cost)
    }

    fun sellTradeItem(item: TradeItem, amount: Int) {
        // TODO: Ensure that this system is buying this item
        if (player.cargo.count(item) < amount) {
            throw Exception("Cannot sell $amount of $item; player does not have enough")
        }

        market.add(item, amount)

        player.cargo.remove(item, amount)

        val sellPrice = market.getSellPrice(item, player.policeRecordScore)
        val revenue = sellPrice * amount
        player.finances.deposit(revenue)
    }

    fun warp(destination: SolarSystem): GameState {
        // TODO: Ensure destination is in range

        payCosts(destination)

        player.rechargeShields()

        val travelViaWormhole = system.hasWormholeTo(destination)
        if (travelViaWormhole) { // TODO: or viaSingularity
            // TODO: special handling
            var distance = 0
        } else {
            var distance = system.distanceTo(destination)
            player.fuelLeft -= distance // TODO: or min(distance, getFuel())?
        }

        // TODO: if !viaSingularity:
        player.dayPasses()

        // TODO: travel
        return OnPlanet(destination, player)
    }

    private fun payCosts(destination: SolarSystem) {
        // TODO: if wild, weapon check
        if (player.tooMuchDebtToWarp()) {
            throw Exception("Debt ${player.finances.debt} too great to warp")
        }
        val crewCosts = player.crewCost()
        if (! player.finances.canAfford(crewCosts)) {
            throw Exception("Cannot afford to pay $crewCosts to mercenaries")
        }
        val insuranceCost = player.getInsuranceCost()
        if (!player.finances.canAfford(insuranceCost + crewCosts)) {
            throw Exception("Cannot afford to pay $insuranceCost for insurance and $crewCosts to mercenaries")
        }
        val wormholeCost = wormholeCost(system, destination)
        if (!player.finances.canAfford(insuranceCost + crewCosts + wormholeCost)) {
            throw Exception("Cannot afford to pay $wormholeCost for wormhole")
        }
        // TODO: skip costs if !viaSingularity
        player.finances.spend(wormholeCost)
        player.finances.spend(insuranceCost)
        player.finances.spend(wormholeCost)
        player.finances.payInterest()
    }

    private fun wormholeCost(system: SolarSystem, destination: SolarSystem): Int {
        val travelViaWormhole = system.hasWormholeTo(destination)
        return if (travelViaWormhole) player.ship.costOfFuel * 25 else 0
    }

}