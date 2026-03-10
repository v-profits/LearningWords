package com.example.learningwords

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat

// Создать класс для адаптеров
class SpinnerManager(
    context: Context,
    resource: Int,
    objects: List<String>,
    val data: DataManager,
    val init: UiInitializer,
    val onItemSelected: (Int) -> Unit
) : ArrayAdapter<String>(context, resource, objects) {

    override fun isEnabled(position: Int): Boolean {
        return position != 0 // делаем первый элемент некликабельным
    }

    // изменяем фон и паддинг для выпадающего списка
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView // Приводим к TextView

        // Устанавливаем цвет текста для подсказки видимой части (невыпадающего списка)
        if (view.text.toString() == data.variantsWord || view.text.toString() == data.lockSpinner)
            view.setTextColor(ContextCompat.getColor(context, R.color.text_color))

        // подкрашиваем заголовок
        if (position == 0) {
            // Установка фона для элемента
            // Применить изменения только к первому элементу
            view.setTextColor(ContextCompat.getColor(context, R.color.title_text_color))
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.title_background_color))

            // Устанавливаем
            if (view.text != data.lockSpinner)
                view.text = init.textViewEn.text // текст на первом итеме
            else
                view.text = "Выбор слов заблокирован, для продолжения используйте клавиатуру"

            view.textSize = 18f // размер текста, который вы желаете для подсказки
            view.setPadding(150, 100, 150, 100) // Установка паддинга
            view.gravity = Gravity.CENTER // Установка гравитации
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.item_background_color))
            view.textSize = 16f // размер текста
            view.setPadding(150, 32, 150, 32) // Установка паддинга
            view.gravity = Gravity.START // Установка гравитации
        }
        if (position == 1)
            view.setPadding(150, 100, 150, 32) // Установка паддинга
        if (position == 10)
            view.setPadding(150, 32, 150, 120) // Установка паддинга
        if (position > 0)
            view.setTextColor(ContextCompat.getColor(context, R.color.text_color))

        return view
    }

    // установка например фона для элемента спиннера
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView // Приводим к TextView
        // меняем вид текста в итемах спиннеров
        // Устанавливаем цвет текста для подсказки видимой части (невыпадающего списка)
        if (view.text.toString() == data.variantsWord || view.text.toString() == data.lockSpinner) {
            view.setTextColor(ContextCompat.getColor(context, R.color.text_color))
            view.textSize = 16f
        }

        return view
    }
}

// Устанавливаете обработчик выбора элемента setupSpinnerWords.
fun setupSpinnerWords(context: Context, spinner: Spinner, adapter: SpinnerManager,
                      data: DataManager, init: UiInitializer, contr: UiController, app: AppController) {
    spinner.adapter = adapter
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            view?.let {
                // если слова угадано или твич-переключатель для подсказок включен то спиннер не работает
                if (data.flagButtonSee && data.switchEye_) return

                // Если не хотите действий при выборе подсказки, можно проверить позицию
                if (position > 0) { // 0 - это подсказка
                    data.isLock = true // true - блокирует если слово перевода открыто в спинере, а не кнопкой buttonEye
                    // Получаем выбранный элемент
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    // Проверяем правильность нажатия item слова
                    if (selectedItem  == data.firstTextRu) {

                        if (view is TextView) { // если слово в спиннере угадано, меняем цвет
                            view.setTextColor(ContextCompat.getColor(context, R.color.blue_light))
                        }

                        if (data.currentCorrectCountLetters == 0 && data.currentErrorCountLetters == 0) {
                            data.currentCorrectCountLetters = data.firstTextRu.count { Regex(data.patternRu4).containsMatchIn(it.toString()) } // считываем кол-во букв
                            app.updateCorrect() // обновление корректных ответов для окрашивания прогресс бара
                        }
                    } else { // для окрашивания прогресс бара
                        if (view is TextView) { // если слово в спиннере не угадано, меняем цвет
                            view.setTextColor(ContextCompat.getColor(context, R.color.red_light))
                        }
                        data.currentErrorCountLetters = data.firstTextRu.length
                        app.updateErrors() // обновление ошибок для окрашивания прогресс бара
                    }
                    contr.buttonEye() // выводим полный текст перевода
                    data.isLock = false // true - блокирует если слово перевода открыто в спинере, а не кнопкой buttonEye
                }
                adapter.onItemSelected(position)
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>) {}
    }
}