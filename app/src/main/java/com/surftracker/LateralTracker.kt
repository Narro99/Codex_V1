package com.surftracker

import androidx.camera.core.ImageProxy
import kotlin.math.abs

class LateralTracker(
    private val alpha: Float = 0.25f,
    private val deadZone: Float = 0.08f
) {
    private var smoothedX: Float? = null

    /**
     * Estima la posición lateral dominante en la imagen usando centroide de luminancia.
     * Retorna: IZQUIERDA / CENTRO / DERECHA.
     */
    fun estimateMovement(image: ImageProxy): String {
        val plane = image.planes.firstOrNull() ?: return "CENTRO"
        val buffer = plane.buffer
        val rowStride = plane.rowStride
        val pixelStride = plane.pixelStride
        val width = image.width
        val height = image.height

        if (width <= 0 || height <= 0) return "CENTRO"

        var weightedSumX = 0.0
        var totalWeight = 0.0

        // Muestreo para reducir carga.
        val stepX = 8
        val stepY = 8

        for (y in 0 until height step stepY) {
            val rowStart = y * rowStride
            for (x in 0 until width step stepX) {
                val index = rowStart + x * pixelStride
                if (index >= buffer.limit()) continue
                val luma = buffer.get(index).toInt() and 0xFF

                // Más peso a zonas oscuras (surfista/traje suele contrastar con cielo/espuma).
                val weight = (255 - luma).coerceAtLeast(0)
                weightedSumX += x * weight
                totalWeight += weight
            }
        }

        if (totalWeight <= 0.0) return "CENTRO"

        val rawXNorm = (weightedSumX / totalWeight / width).toFloat().coerceIn(0f, 1f)
        smoothedX = if (smoothedX == null) rawXNorm else (alpha * rawXNorm + (1 - alpha) * smoothedX!!)

        val x = smoothedX ?: 0.5f
        val distanceFromCenter = x - 0.5f

        return when {
            abs(distanceFromCenter) < deadZone -> "CENTRO"
            distanceFromCenter < 0 -> "IZQUIERDA"
            else -> "DERECHA"
        }
    }
}
