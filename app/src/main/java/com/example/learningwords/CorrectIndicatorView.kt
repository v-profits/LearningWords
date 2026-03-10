package com.example.learningwords

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat


class CorrectIndicatorView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var correctCount = 0 // текущее количество ошибок
    private var maxCorrect = 0 // максимальное количество ошибок
    private val indicatorColor = ContextCompat.getColor(context, R.color.blue_light) // получаем цвет
//    private val indicatorColor = Color.rgb(0, 0, 255) // получаем цвет

    fun setCorrectCount(count: Int, max: Int) {
        correctCount = count
        maxCorrect = max
        invalidate() // Перерисовка View
    }

//    private fun calculateColor(correctCount: Int): Int {
//        // Ваш код для определения цвета в зависимости от количества ошибок
//        return when (correctCount) {
//            in 0..Math.floor(maxCorrect * 0.33).toInt() -> indicatorColor // R.color.green // Color.GREEN
//            in Math.ceil(maxCorrect * 0.33).toInt()..Math.floor(maxCorrect * 0.66).toInt() -> indicatorColor // R.color.green
//            in Math.ceil(maxCorrect * 0.66).toInt()..maxCorrect -> indicatorColor // R.color.green
//            else -> Color.TRANSPARENT
//        }
//    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val proportion = correctCount.toFloat() / maxCorrect.toFloat()
        val right = (width - 20) * proportion + 10

        val paint = Paint().apply { color = indicatorColor } // Color.BLUE // { color = calculateColor(correctCount) }

        val rect = Rect(10, 10, right.toInt(), height / 4)
        canvas.drawRect(rect, paint)
    }
}