package com.example.learningwords

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

class MySettings(val sett: MainActivity) {

    // View.INVISIBLE делает виджет невидимым, но он всё ещё занимает место в макете
    // View.GONE делает виджет невидимым и освобождает занимаемое им место

    //--- показываем или скрываем громкость и шкалу индикации ======================================
    fun volume() {
        if (!sett.data.isVolume_) {
            sett.init.volumeSeekBar.visibility = View.GONE
            sett.init.pressVolume.visibility = View.GONE
        } else {
            sett.init.volumeSeekBar.visibility = View.VISIBLE
            sett.init.pressVolume.visibility = View.VISIBLE
        }
    }

    //--- показываем или скрываем индикаторы =======================================================
    fun indicators(visibility: Int) {
        sett.init.wordTextIndCorr.visibility = visibility
        sett.init.textViewCorrPercen.visibility = visibility
        sett.init.textCorrPercen.visibility = visibility
        sett.init.corrProgressBar.visibility = visibility

        sett.init.wordTextIndErr.visibility = visibility
        sett.init.textViewErrPercen.visibility = visibility
        sett.init.textErrPercen.visibility = visibility
        sett.init.errProgressBar.visibility = visibility
    }

    //--- показываем или скрываем статистику =======================================================
    fun statistics(visibility: Int) {
        if (visibility == View.VISIBLE) {
            if (sett.init.textViewTotalAnswers.visibility == View.GONE)
                sleepAnimate(sett.init.textViewTotalAnswers, 0) // Функция анимации TextView
        }else
            sett.init.textViewTotalAnswers.visibility = visibility

        if (visibility == View.VISIBLE) {
            if (sett.init.textViewCurrentAnswers.visibility == View.GONE)
                sleepAnimate(sett.init.textViewCurrentAnswers, 200) // Функция анимации TextView
        } else
            sett.init.textViewCurrentAnswers.visibility = visibility

        if (visibility == View.VISIBLE) {
            if (sett.init.textViewCorrectAnswers.visibility == View.GONE)
                sleepAnimate(sett.init.textViewCorrectAnswers, 400) // Функция анимации TextView
        } else
            sett.init.textViewCorrectAnswers.visibility = visibility

        if (visibility == View.VISIBLE) {
            if (sett.init.textViewWrongAnswers.visibility == View.GONE)
                sleepAnimate(sett.init.textViewWrongAnswers, 600) // Функция анимации TextView
        } else
            sett.init.textViewWrongAnswers.visibility = visibility
    }

    fun sleepAnimate(textView: TextView, delay: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            // Устанавливаем начальные условия для анимации fadeIn и перемещения снизу вверх
            textView.apply {
                alpha = 0f
                visibility = View.INVISIBLE // Скрываем, чтобы подготовить к анимации
                translationY = textView.height.toFloat() // Начальное положение внизу
            }

            // Анимация "встряхивания"
//            val shakeAnimator = ObjectAnimator.ofFloat(
//                textView,
//                "translationX",
//                0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f
//            ).apply {
//                duration = 500
//                interpolator = AccelerateDecelerateInterpolator()
//            }
//            shakeAnimator.start()

            // Сразу после запуска анимации "встряхивания", начинаем анимации появления и движения снизу вверх
            textView.animate()
                .alpha(1f)
                .translationY(0f) // Двигаем обратно в исходное положение
                .setDuration(500)
                .withStartAction {
                    textView.visibility = View.VISIBLE
                }
                .start()
        }, delay)
    }

    //--- функция выбора уровней словарей в навигационной панели ===================================
    @SuppressLint("MissingInflatedId", "InflateParams")
    fun showDialogListLevels(prefs: PreferencesManager) {
//    fun showDialogListLevels() {

        val dialogView = LayoutInflater.from(sett).inflate(R.layout.custom_dialog, null)

        val listView = dialogView.findViewById<ListView>(R.id.your_list_view_id) // Убедитесь, что у ListView в вашем custom_dialog.xml установлен ID

        // Установите фон для listView здесь
        listView.setBackgroundColor(ContextCompat.getColor(sett, R.color.item_background_color))

        // Создание адаптера для списка уровней
        val adapter = object : ArrayAdapter<String>(sett, R.layout.menu_item, R.id.menu_item_text, sett.data.listItemsLevels) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(R.id.menu_item_text)
                val imageView = view.findViewById<ImageView>(R.id.menu_item_image)

                // Задаём иконку и цвет для каждого элемента списка
                if (!sett.data.lockImageLevel_[position]) {
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.outline_lock_24))
                } else if (position == sett.data.indexLevel_ && sett.data.lockImageLevel_[position]) {
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.outline_radio_button_checked_24))
                } else if (sett.data.lockImageLevel_[position]) {
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.outline_radio_button_unchecked_24))
                }

                return view
            }
        }
        listView.adapter = adapter

        // Создаем диалог
        val dialog = AlertDialog.Builder(sett).apply {
            setView(dialogView)
            // Другие настройки диалога, если необходимы
        }.create()

        // Показываем диалог
        dialog.show()

        // Получаем текущие параметры окна диалога
        val window = dialog.window
        val metrics = sett.resources.displayMetrics
        val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250f, metrics).toInt() // 250f это ширина в единицах dp
        val height = ViewGroup.LayoutParams.WRAP_CONTENT // MATCH_PARENT
        window?.setLayout(width, height)

        // Для установки положения самого диалога, если нужно:
        window?.setGravity(Gravity.END)

        // Обработка обычного клика по элементу списка
        listView.setOnItemClickListener { _, _, position, _ ->

            if (sett.data.lockImageLevel_[position]) {

                // Обновляем индекс выбранного элемента
                sett.data.indexLevel_ = position
                // показываем Toast
                Toast.makeText(sett, "Выбрано: ${sett.data.listItemsLevels[position]}", Toast.LENGTH_SHORT).show()
                // восстанавливаем списки из памяти телефона
                prefs.getIntList() /////////////////////////////////////////////////////////////////////

                // загружаем информацию из файла
                sett.app.loadDictionary()

                paintVectorIcon(sett.init.imageViewHeaderIcon7, sett.data.isShuffled_) // перекрашиваем векторные imageText итема "Перемешать"

                setupLevel() // обнуляем и перекрашиваем счетчики слов

                // Уведомляем адаптер диалога о том, что данные итемов изменились (если адаптер используется в другом месте UI)
                adapter.notifyDataSetChanged()
            } else {
                sett.app.updateToast("${sett.data.listItemsLevels[position]} - lock", R.color.white, R.color.red_light) // выводим Toast согласно заданным параметрам
            }

            // Закрываем диалог
            dialog.dismiss()
        }

        // Обработка долгого клика по элементу списка.
        listView.setOnItemLongClickListener { _, _, position, _ ->

            if (sett.data.lockImageLevel_[position]) {

                // Создание AlertDialog для подтверждения смены уровня
                val dialog = AlertDialog.Builder(sett, R.style.YourAlertDialogTheme)
                    .setTitle("Обнуляет статистику.")
//                    .setMessage("Выбор другого уровня обнулит вашу текущую статистику. Продолжить?")
                    .setMessage("Это действие удалит ваши достижения на \"Уровень - ${position+1}\". Продолжить?")
                    .setNegativeButton("Да") { dialog, which ->

                        // Обновляем индекс выбранного элемента
                        sett.data.indexLevel_ = position
                        // показываем Toast
                        Toast.makeText(sett, "Выбрано: ${sett.data.listItemsLevels[position]}", Toast.LENGTH_SHORT).show()
                        // загружаем информацию из файла
                        sett.app.loadDictionary()


                        sett.data.intWordListLevel_ = MutableList(sett.data.dictionaryTripleList_!!.size) { 0 } // присвоим все 0

                        if (sett.data.intWordListLevel_.all { it == 0 }) { // все позиции фрагмента обнуляются
                            sett.data.currentIndex_ = 0
                            sett.data.previousPosition = 0
                            val index = sett.calculateNextPosition(sett.data.currentIndex_, sett.data.previousPosition)
                            sett.init.viewPager.setCurrentItem(index, false)
                        }

                        // Уведомляем адаптер диалога о том, что данные итемов изменились (если адаптер используется в другом месте UI)
                        adapter.notifyDataSetChanged()
                    }
                    .setPositiveButton("Отмена") { dialog, which ->
                        dialog.dismiss() // Закрываем диалог
                    }
                    .setCancelable(false) // запрещаем закрытие диалога при клике вне его окна
                    .show()

                // цвет текста для кнопок предупреждающего диалога
//                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
//                    ContextCompat.getColor(sett, R.color.red_light)
//                )
//                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
//                    ContextCompat.getColor(sett, R.color.blue_light)
//                )

            } else {
                sett.app.updateToast("${sett.data.listItemsLevels[position]} - lock", R.color.white, R.color.red_light) // выводим Toast согласно заданным параметрам
            }

            true // Возвращаем true, чтобы указать что событие долгого клика обработано.
        }
    }

    // обнуляем и перекрашиваем счетчики слов
    fun setupLevel() {
        sett.app.update() // по нажатии кнопок восстанавливаем все значения по умолчанию

        if (sett.data.intWordListLevel_.size == 0)
            sett.data.intWordListLevel_ = MutableList(sett.data.dictionaryTripleList_!!.size) { 0 } // присвоим все 0

        sett.app.count = 0 // создаем условия для работы функции textViewAnswers()
        sett.app.textViewAnswers() // выводим данные по словам - угадал/не угадал
    }

    //--- функция выбора голосов озвучки в навигационной панели ====================================
    @SuppressLint("MissingInflatedId", "InflateParams")
    fun showDialogListSpeak() {
        val dialogView = LayoutInflater.from(sett).inflate(R.layout.dialog_voices, null)
        val listView = dialogView.findViewById<ListView>(R.id.list_voices)
        val ivFinish = dialogView.findViewById<ImageView>(R.id.iv_finish_listening)

        // Установите фон для listView здесь
        listView.setBackgroundColor(ContextCompat.getColor(sett, R.color.item_background_color))

        // оставляем только имя голоса
        val voices = sett.data.availableVoices.map { voice -> voice.name }

        // Создание адаптера для списка голосов
//        val adapter = object : ArrayAdapter<Voice>(sett, R.layout.menu_item, R.id.menu_item_text, sett.data.availableVoices) {
        val adapter = object : ArrayAdapter<String>(sett, R.layout.menu_item, R.id.menu_item_text, voices) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(R.id.menu_item_text)
                val imageView = view.findViewById<ImageView>(R.id.menu_item_image)

                // Задаём иконку для каждого элемента списка
                if (position == sett.data.indexSpeak_)
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.outline_radio_button_checked_24))
                else
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.outline_radio_button_unchecked_24))

                // Установить другие свойства для textView, если необходимо

                return view
            }
        }
        listView.adapter = adapter

        // Создаем диалог
        val dialog = AlertDialog.Builder(sett).apply {
            setView(dialogView)
            // Другие настройки диалога, если необходимы
        }.create()

        // Показываем диалог
        dialog.show()

        // Получаем текущие параметры окна диалога
        val window = dialog.window
        val metrics = sett.resources.displayMetrics
//        val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250f, metrics).toInt() // 250f это ширина в единицах dp
        val width = ViewGroup.LayoutParams.WRAP_CONTENT // MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT // MATCH_PARENT
        window?.setLayout(width, height)

        // Для установки положения самого диалога, если нужно:
        window?.setGravity(Gravity.END)

        // Обработка клика по элементу списка
        listView.setOnItemClickListener { _, _, position, _ ->

            sett.init.textSpeaker.setVoice(position) // вызываем для озвучки выбранный голос
//            Log.e("aaa", "---- voice index: $position")

            // Обновляем индекс выбранного элемента
            sett.data.indexSpeak_ = position
            // показываем Toast
            Toast.makeText(sett, "Выбрано: ${sett.data.availableVoices[position].name}", Toast.LENGTH_SHORT).show()
//            Log.e("aaa", "---- ${sett.data.availableVoices[position]}")

            // Уведомляем адаптер о том, что данные изменились (если адаптер используется в другом месте UI)
            adapter.notifyDataSetChanged()

            // Закрываем диалог
//            dialog.dismiss()
        }

        // Нажатие на кнопку "Завершить прослушивание"
        ivFinish.setOnClickListener {
            // Обрабатываем нажатие кнопки
            dialog.dismiss() // Закрываем диалог
        }
    }

    //--- Вызов функции инструкции  ================================================================
    fun manual() {
        if (sett.data.isManual) {
            sett.init.textViewManual.visibility = View.VISIBLE
        } else {
            sett.init.textViewManual.visibility = View.GONE
        }
    }


    //--- перекрашиваем все иконки по перезагрузке или смены темы ==================================

    // перекрашиваем векторные иконки на боковай панели
    fun paintVectorIcon(imageView: ImageView, switch: Boolean) {
        // Определение выбранного цвета
        val color = if (switch) {
            ContextCompat.getColor(sett, R.color.my_light) // Задаем новый цвет, который мы хотим использовать
        } else {
            ContextCompat.getColor(sett, R.color.text_color) // Задаем новый цвет, который мы хотим использовать
        }
        // Получение Drawable и применение к нему цвета
        val drawable = imageView.drawable.mutate()
        DrawableCompat.setTint(drawable, color)
        imageView.setImageDrawable(drawable)
    }

    // перекрашиваем векторные иконки на главной панели
    fun paintVectorButtons(wrappedDrawable: Drawable?, switch: Boolean) {
        if (wrappedDrawable != null) {
            val color = if (switch)
                ContextCompat.getColor(sett, R.color.my_light) // Задаем цвет для "включенного" состояния
            else
                ContextCompat.getColor(sett, R.color.text_color) // Задаем цвет для "выключенного" состояния
            DrawableCompat.setTint(wrappedDrawable, color)
        }
    }

    // перекрашиваем все иконки по перезагрузке или смены темы
    fun paintIcons(context: Context, init: UiInitializer, data: DataManager) {

        // Перекрашиваем векторные кнопки
        paintVectorButtons(init.wrappedDrawableNext, data.switchNext_)
        paintVectorButtons(init.wrappedDrawableBack, data.switchBack_)
        paintVectorButtons(init.wrappedDrawableSpeak, data.switchSpeak_)
        paintVectorButtons(init.wrappedDrawableEye, data.switchEye_)

        // перекрашиваем векторные imageText
        paintVectorIcon(init.imageViewHeaderIcon2, false)
        paintVectorIcon(init.imageViewHeaderIcon3, false)
        paintVectorIcon(init.imageViewHeaderIcon4, data.isVision_)
        paintVectorIcon(init.imageViewHeaderIcon5, data.isIgnore_)
        paintVectorIcon(init.imageViewHeaderIcon6, data.isHide_)
        paintVectorIcon(init.imageViewHeaderIcon7, data.isShuffled_)
        paintVectorIcon(init.imageViewHeaderIcon8, data.isStatistics_)
        paintVectorIcon(init.imageViewHeaderIcon9, data.isIndicator_)
        paintVectorIcon(init.imageViewHeaderIcon10, data.isVolume_)
        paintVectorIcon(init.imageViewHeaderIcon11, data.isDarkMode_)
        paintVectorIcon(init.imageViewHeaderIcon12, data.isManual)
    }
}