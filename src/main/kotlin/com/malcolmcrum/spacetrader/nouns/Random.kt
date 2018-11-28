package com.malcolmcrum.spacetrader.nouns

import java.util.*

private val RNG = Random()

fun ClosedRange<Int>.random(): Int {
    val bound = endInclusive - start
    assert(bound > 0) { "Bound must be positive but was given $bound" }
    return RNG.nextInt(bound) + start
}

fun randomSkillLevel(): Int {
    return 1 + (0..5).random() + (0..6).random()
}

fun <T : Any> pickRandom(values: List<T>): T {
    val index = (0 until values.size).random()
    return values[index]
}

fun <T : Any> pickRandom(values: Array<T>): T {
    val index = (0 until values.size).random()
    return values[index]
}