package com.claranet.turiappvr.utils

import com.claranet.turiappvr.models.ArrowVisibility
import com.claranet.turiappvr.models.Crossroad
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object GPSUtils {
    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadius = 6371000.0 // meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    fun detectNearestCrossroad(
        currentLat: Double,
        currentLon: Double,
        crossroads: List<Crossroad>
    ): ArrowVisibility {
        val nearestCrossroad = crossroads.firstOrNull { crossroad ->
            val distance = calculateDistance(
                currentLat, currentLon,
                crossroad.latitude, crossroad.longitude
            )
            distance <= crossroad.radius
        }

        return if (nearestCrossroad != null) {
            ArrowVisibility(
                showLeftArrow = nearestCrossroad.leftTurnAvailable,
                showRightArrow = nearestCrossroad.rightTurnAvailable,
                showStraightArrow = nearestCrossroad.straightAvailable
            )
        } else {
            ArrowVisibility() // Default: only straight arrow
        }
    }
}