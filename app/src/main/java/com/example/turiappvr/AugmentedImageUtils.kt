package com.example.turiappvr

import android.content.Context
import android.graphics.BitmapFactory
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Session

fun createAugmentedImageDatabase(
    context: Context,
    session: Session
): AugmentedImageDatabase {
    val inputStream = context.assets.open("markers/terme.png")
    val bitmap = BitmapFactory.decodeStream(inputStream)

    // widthInMeters = larghezza fisica del marker nel mondo reale
    val widthInMeters = 0.20f // 20 cm

    return AugmentedImageDatabase(session).apply {
        addImage("marker", bitmap, widthInMeters)
    }
}
