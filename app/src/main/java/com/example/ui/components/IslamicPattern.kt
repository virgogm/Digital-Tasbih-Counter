package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate

fun DrawScope.drawIslamicGeometricStar(
    center: Offset,
    size: Float,
    color: Color,
    strokeWidth: Float = 1.5f
) {
    val halfSize = size / 2f
    
    // Square 1
    val rect1 = Rect(center.x - halfSize, center.y - halfSize, center.x + halfSize, center.y + halfSize)
    drawRect(
        color = color,
        topLeft = Offset(rect1.left, rect1.top),
        size = Size(size, size),
        style = Stroke(width = strokeWidth)
    )
    
    // Square 2 (rotated 45 degrees)
    rotate(degrees = 45f, pivot = center) {
        drawRect(
            color = color,
            topLeft = Offset(rect1.left, rect1.top),
            size = Size(size, size),
            style = Stroke(width = strokeWidth)
        )
    }
    
    // Concentric design lines inside
    drawCircle(
        color = color,
        radius = halfSize * 0.4f,
        center = center,
        style = Stroke(width = strokeWidth * 0.8f)
    )
    
    drawCircle(
        color = color,
        radius = halfSize * 1.1f,
        center = center,
        style = Stroke(width = strokeWidth * 0.5f)
    )
}

@Composable
fun IslamicPatternBackdrop(
    modifier: Modifier = Modifier,
    patternColor: Color = Color(0xFF2C3E43).copy(alpha = 0.04f), // 4% subtle alpha for premium feel
    cellSize: Float = 160f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        val cols = (width / cellSize).toInt() + 2
        val rows = (height / cellSize).toInt() + 2
        
        for (i in -1..cols) {
            for (j in -1..rows) {
                val cx = i * cellSize + (if (j % 2 == 0) cellSize / 2 else 0f)
                val cy = j * cellSize
                drawIslamicGeometricStar(
                    center = Offset(cx, cy),
                    size = cellSize * 0.45f,
                    color = patternColor,
                    strokeWidth = 1.2f
                )
            }
        }
    }
}
