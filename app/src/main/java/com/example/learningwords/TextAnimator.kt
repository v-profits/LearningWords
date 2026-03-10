package com.example.learningwords

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.core.content.ContextCompat

class TextAnimator(private val context: Context) {

    //--- Функция анимации TextViewRu
    fun animateTextView(textView: TextView, newText: String) {
        //--- если слово не изменилось, потряхиваем текст в виджите
        if (textView.text.toString() == newText) {

            // Создаем ObjectAnimator для изменения свойства translationX
            val shake = ObjectAnimator.ofFloat(
                textView, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f
            )
            shake.apply {
                duration = 500 // Сделаем продолжительность анимации 500 миллисекунд
                interpolator = AccelerateDecelerateInterpolator() // Плавное ускорение и замедление
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        textView.translationX = 0f // Возвращаем view на исходную позицию
                    }
                })
                start()
            }
            return // и выходим из функции
        }

        //--- движение снизу вверх
        // Получаем начальную позицию Y
        val startY = textView.height.toFloat()

        // Устанавливаем текст и делаем View невидимым, но уже на нужной позиции
        textView.text = marginsForMidpoint(newText) // Строим отображаемый текст, заменяя точки на "·" и буквы на " · "
//        textView.text = newText
        textView.alpha = 0f
        textView.translationY = startY

        // Анимируем прозрачность и позицию Y одновременно
        textView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500) // Длительность анимации в мс
            .setListener(object : AnimatorListenerAdapter() {})
            .start()
    }

    //--- Строим отображаемый текст, заменяя точки на "·" и буквы на " · "
    val data = DataManager()

    fun marginsForMidpoint(newText: String): SpannableString {
        val text = newText.toMutableList().joinToString("") { char ->
            if (char == data.chPoint.single()) data.chReplace else "$char" // " • " else "$char"
//            if (char == data.chPoint[0]) data.chReplace else "$char" // " • " else "$char"
        }
        val spannable = SpannableString(text)
        text.indices.forEach { index ->
            // Проверяем, является ли символ точкой или подчеркиванием
            if (text[index] == data.chLine[0]) {
                spannable.setSpan(
//                    ForegroundColorSpan(Color.RED), // Устанавливаем цвет текста красным для точек и подчеркиваний
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.my_light)), // Используйте цвет из ресурсов
                    index,
                    index + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (text[index] == data.chPoint[0] || text[index] == data.chReplace[1]) {
                spannable.setSpan(
//                    ForegroundColorSpan(Color.RED), // Устанавливаем цвет текста красным для точек и подчеркиваний
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.my_light)), // Используйте цвет из ресурсов
                    index,
                    index + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        return spannable
    }
}
