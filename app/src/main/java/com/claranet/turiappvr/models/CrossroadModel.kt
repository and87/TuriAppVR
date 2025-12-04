package com.claranet.turiappvr.models

data class Crossroad(
    val latitude: Double,
    val longitude: Double,
    val radius: Double = 10.0, // meters - detection radius
    val leftTurnAvailable: Boolean,
    val rightTurnAvailable: Boolean,
    val straightAvailable: Boolean = true
)
