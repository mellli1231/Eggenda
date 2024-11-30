package com.example.eggenda

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat

class OutlinedText(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {
    private val outlinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
    }

    init {
        val outlineColor = ContextCompat.getColor(context, R.color.light_brown) // Outline color
        outlinePaint.color = outlineColor
    }

    override fun onDraw(canvas: Canvas) {
        //get padding values
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop

        text?.let {
            outlinePaint.typeface = typeface
            outlinePaint.textSize = textSize
            outlinePaint.textAlign = paint.textAlign
            outlinePaint.letterSpacing = paint.letterSpacing

            // Adjust x and y to consider padding
            val x = paddingLeft.toFloat()
            val y = baseline.toFloat() + paddingTop


            canvas.drawText(it.toString(), x, y, outlinePaint)
        }
        super.onDraw(canvas)
    }
}