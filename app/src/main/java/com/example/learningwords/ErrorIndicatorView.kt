package com.example.learningwords

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class ErrorIndicatorView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var errorCount = 0 // текущее количество ошибок
    private var maxErrors = 0 // максимальное количество ошибок
    private val indicatorColor = ContextCompat.getColor(context, R.color.red_light) // получаем цвет
//    private val indicatorColor = Color.rgb(255, 0, 0) // получаем цвет


    fun setErrorCount(count: Int, max: Int) {
        errorCount = count
        maxErrors = max
        invalidate() // Перерисовка View
    }

//    private fun calculateColor(errorCount: Int): Int {
//        // Ваш код для определения цвета в зависимости от количества ошибок
//        return when (errorCount) {
//            in 0..Math.floor(maxErrors * 0.33).toInt() -> indicatorColor // R.color.red // GREEN
//            in Math.ceil(maxErrors * 0.33).toInt()..Math.floor(maxErrors * 0.66).toInt() -> indicatorColor // R.color.red // YELLOW
//            in Math.ceil(maxErrors * 0.66).toInt()..maxErrors -> indicatorColor // R.color.red
//            else -> Color.TRANSPARENT
//        }
//    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val proportion = errorCount.toFloat() / maxErrors.toFloat()
        val right = (width - 20) * proportion + 10

        val paint = Paint().apply { color = indicatorColor } // Color.RED } // { color = calculateColor(errorCount) }

        val rect = Rect(10, 10, right.toInt(), height / 4)
        canvas.drawRect(rect, paint)
    }
}