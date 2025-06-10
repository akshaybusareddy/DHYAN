package com.example.dhyanapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class TriangleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = 0xFF3F51B5.toInt() // Indigo color
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        val path = Path().apply {
            moveTo(width / 2, 0f)         // Top
            lineTo(0f, height)            // Bottom left
            lineTo(width, height)         // Bottom right
            close()
        }

        canvas.drawPath(path, paint)
    }
}
