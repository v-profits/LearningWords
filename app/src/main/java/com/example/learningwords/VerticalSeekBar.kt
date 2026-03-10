package com.example.learningwords

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar

class VerticalSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.seekBarStyle
) : AppCompatSeekBar(context, attrs, defStyleAttr) {

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(h, w, oldh, oldw) // Меняем местами высоту и ширину для вертикального SeekBar.
    }

    override fun onDraw(canvas: Canvas) {
        canvas.rotate(-90f)
        canvas.translate(-height.toFloat(), 0f)

        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }

        val y = event.y
        val height = height
        val max = max

        when (event.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE,
            MotionEvent.ACTION_UP -> {
                super.onTouchEvent(event)

                // Пересчитываем координаты для вертикального слайдера
                val i = max - (max * event.y / height).toInt()
                progress = i
                // Дополнительно можно уведомить слушателей об изменении прогресса
                onSizeChanged(width, height, 0, 0) // Обновляем размер, если необходимо
                // Генерируем события, если надо иметь непрерывное обновление при движении
            }

            MotionEvent.ACTION_CANCEL -> {
                // Обрабатываем случай "отмены" касания, если это необходимо
            }
        }
        // Сообщаем, что обработка выполнена
        return true
//        return super.onTouchEvent(event)
    }
}