package com.vkasurinen.woltmobile.presentation.details.components

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

@Stable
fun ArchShape(): Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f) // Top-left corner
            lineTo(0f, size.height - 50f) // Bottom-left corner before the arch
            quadraticBezierTo(
                size.width / 2, size.height - 100f, // Control point for the arch
                size.width, size.height - 50f // Bottom-right corner of the arch
            )
            lineTo(size.width, 0f) // Top-right corner
            close()
        }
        return Outline.Generic(path)
    }
}