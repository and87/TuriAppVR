package com.claranet.turiappvr.fixtures

import com.claranet.turiappvr.models.Crossroad

fun getTrailCrossroads(): List<Crossroad> {
    return listOf(
        Crossroad(
            latitude = 41.765996,
            longitude = 12.661161,
            radius = 15.0,
            leftTurnAvailable = true,
            rightTurnAvailable = false
        ),
        Crossroad(
            latitude = 41.766531,
            longitude = 12.662412,
            radius = 15.0,
            leftTurnAvailable = false,
            rightTurnAvailable = true
        )
    )
}
