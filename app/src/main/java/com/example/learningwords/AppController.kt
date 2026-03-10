package com.example.learningwords

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat

class AppController(val app: MainActivity) {

    private val context = app.applicationContext
    var count = -1 // предыдущий текущий индекс

    // функции =====================================================================================

    //--- создает список из десяти случайных ру слов для спиннера
    fun randomListRu(): List<String> {
        var randomElements = app.data.dictionaryTripleList_?.shuffled()?.take(9)?.map { it.third }?.toMutableList() ?: emptyList() // Берём случайные вторые элементы 9 пар
        randomElements += app.data.fullTextRu // Добавляем fullText
        val randomSpinnerWords = randomElements.shuffled() // и перемешиваем
        val transformedSpinnerWords = randomSpinnerWords.map { extractPhrases(it) } // применяем паттерн для оставления первых выражений

        return if (transformedSpinnerWords.toSet().size != transformedSpinnerWords.size) { // если были повторы слов, то вызываем fun повторно
            randomListRu()
        } else {
            transformedSpinnerWords
        }
    }

    //--- считывание выбранного словаря из raw
    fun loadDictionary() {
        // создаем объект класса словаря en-ru слов
        val dictionaryLoader = DictionaryLoader(app)

        // Загрузка содержимого словаря и создание тройного списка
        val mutableTripleList: List<Triple<Int, String, String>> = dictionaryLoader
            .loadRawResourceContentsToList(app.data.filesRawDictionary[app.data.indexLevel_])
            .mapIndexed { index, (word, definition) ->  // Здесь destructuring использован для pair
                Triple(index, word, definition)
            }
        val listLength = mutableTripleList.size // количество слов в словаре

        // перемешиваем тройной список словаря
        if (app.data.isShuffled_) {
            app.data.dictionaryTripleList_ = mutableTripleList.shuffled() // Перемешаем тройной список
        } else {
            app.data.dictionaryTripleList_ = mutableTripleList // не перемешиваем тройной список словаря
        }

        if (listLength != null) { // неугаданные слова есть false, угаданные true
//            app.data.intWordListLevel = MutableList(listLength) { 0 } // присвоим все 0
            app.init.textViewTotalAnswers.text = listLength.toString() // выводим на экран количество слов в словаре
            app.data.totalSize_ = listLength // количество слов в словаре
            textViewAnswers() // меняем количество (не) угаданных слов и цвет на виджете
        }

        updateFragmentWords() // Обновить список элементов во фрагменте англ. слов
        textViewUpdate() // обновляем слово в textViewEn и firstWordRu и в spinnerWords
    }

    // извлекать фразы для перевода
    fun extractPhrases(text: String): String {
        // Удаляем всё после первых встреченных символов , / ! ?
        var modifiedPhrase = text.replace(Regex(app.data.patternRu1), "") // Удалить все после запятой или слеша

        // Оставляем только содержимое до первого восклицательного или вопросительного знака
        modifiedPhrase = Regex(app.data.patternRu2).find(modifiedPhrase)
            ?.let { matchResult -> modifiedPhrase.substring(0, matchResult.range.first + 1) }
            ?: modifiedPhrase // Если знаки отсутствуют, возвращаем всю строку

        // Удаляем скобки и их содержимое
        modifiedPhrase = modifiedPhrase.replace(Regex(app.data.patternRu3), "").trim()

        return modifiedPhrase
    }

    //--- обновляем слово в textViewEn и firstWordRu
    @SuppressLint("SetTextI18n")
    fun textViewUpdate() { // Обновление TextView новыми словами

        // en
        app.init.textViewEn.text = app.data.dictionaryTripleList_?.getOrNull(app.data.currentIndex_)?.second ?: app.data.textEn

        app.init.textViewEn.text = app.init.textViewEn.text // дублируем для сохранения размера виджета в макете

        if (app.data.dictionaryTripleList_ != null)
            app.init.textViewCurrentAnswers.text = (app.data.currentIndex_ + 1).toString() // обновляем в виджите текущий порядковый номер слова

        //ru
        app.data.fullTextRu = app.data.dictionaryTripleList_?.getOrNull(app.data.currentIndex_)?.third ?: app.data.textRu
        // выбираем только первое слово из русского перевода
        app.data.firstTextRu = extractPhrases(app.data.fullTextRu)

        if (app.data.isVision_ && app.data.intWordListLevel_[app.data.dictionaryTripleList_!![app.data.currentIndex_].first] == 1)
            app.contr.buttonEye() //если слово уже раньше было угадано - показываем его
        else
            app.data.newTextRu = "________" // иначе прочерки для слова

        app.textAnimator.animateTextView(app.init.textViewRu, app.data.newTextRu) // присваиваем значение в русский перевод с анимацией
        app.data.newTextRu = app.data.chPoint.repeat(app.data.firstTextRu .length) // Установите начальный вид символов прочерками в TextViewRu

        // обновляем все тапнутые литеры как False
        app.data.numIndexLetters.forEachIndexed { index, _ ->
            app.data.numIndexLetters[index] = false
        }
        app.updateSpinnerAndFragmentWords() // Обновить список элементов в спиннере слов и фрагменте
    }

    //--- Обновить список элементов во фрагменте англ. слов
    fun updateFragmentWords() {
        // Чтобы сделать TextView видимым
        app.init.textViewEn.visibility = View.INVISIBLE
        // Установить размер текста в sp (масштабируемые пиксели)
        app.init.textViewEn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)

        val wordsList = app.data.dictionaryTripleList_?.map { it.second } ?: emptyList()
        app.init.wordsPagerAdapter = WordsPagerAdapter(app.supportFragmentManager, app.lifecycle, wordsList)
        app.init.viewPager.adapter = app.init.wordsPagerAdapter
        app.init.wordsPagerAdapter.updateWordsEn(wordsList)
    }

    //--- возвращаем все окрашенные кнопки на цвет по умолчанию
    fun defaultColorTextViews() {
        for(tv in app.data.listKeyboardTextView) {
            tv.setBackgroundResource(0) // Сброс фона к значению по умолчанию
            tv.setTextColor(ContextCompat.getColor(app, R.color.text_color))
        }
    }

    //--- если switch включен/выключен
    private fun onSwitch() {
        // если свитч на speak включен
        if (app.data.switchSpeak_) // Свитч включен
            app.init.textSpeaker.speak(app.init.textViewEn.text.toString()) // Озвучиваем все англ. слова
        // если свитч на eye включен
        if (app.data.switchEye_) // Свитч включен
            app.contr.buttonEye() // Показываем перевод всех англ. слов
    }

    // визуализация виджитов индикатора corr
    fun viewVisibleCorr(visibility: Int) {
        app.init.textViewCorrPercen.visibility = visibility
        app.init.wordTextIndCorr.visibility = visibility
        app.init.textCorrPercen.visibility = visibility
    }

    // визуализация виджитов индикатора err
    fun viewVisibleErr(visibility: Int) {
        app.init.textViewErrPercen.visibility = visibility
        app.init.wordTextIndErr.visibility = visibility
        app.init.textErrPercen.visibility = visibility
    }

    //--- обновление корректных ответов для окрашивания прогресс бара ==============================
    fun updateCorrect(flag: Boolean = true) {
        // если не выбрана библиотека или слово угадано, не даем обновлять textViewRu нажимать клаву
        if (app.contr.stopActions() && flag) return

        app.data.maxCorrectLetters = app.data.firstTextRu.count { Regex(app.data.patternRu4).containsMatchIn(it.toString()) } // максимальное допустимое количество ошибок привязываем к длине слова
        app.init.corrProgressBar.setCorrectCount(app.data.currentCorrectCountLetters, app.data.maxCorrectLetters)

        if (app.data.currentCorrectCountLetters != 0) {
            count = -1 // при смене уровня, для отображения первого результата в счетчике угаданных слов
            val s = (app.data.currentCorrectCountLetters * 100.0 / app.data.maxCorrectLetters).toInt().toString()
            app.init.textViewCorrPercen.text = s
            app.init.textViewCorrPercen.setTextColor(ContextCompat.getColor(app, R.color.blue_light)) //меняем цвет
            if (s == "100") { // прибавляем на экране к количеству правильному ответу
//                Log.e("aaa","-------${app.data.intWordListLevel_}")
                if (app.data.intWordListLevel_.size == 0)
                    app.data.intWordListLevel_ = MutableList(app.data.dictionaryTripleList_!!.size) { 0 } // присвоим все 0

                app.data.intWordListLevel_[app.data.dictionaryTripleList_!![app.data.currentIndex_].first] = 1 // записываем в список угаданное слово
                updateToast(app.data.correctAnswer, R.color.white, R.color.blue_light) // выводим Toast согласно заданным параметрам
                textViewAnswers() // меняем количество (не) угаданных слов и цвет на виджите
                if (app.data.isStatistics_)
                    sett.statistics(View.VISIBLE) // показываем или скрываем статистику
            }
            if (app.init.textViewCorrPercen.visibility == View.GONE && app.data.isIndicator_)
                viewVisibleCorr(View.VISIBLE) // визуализация виджитов индикатора corr


//        } else if (app.data.flagButtonSee && app.data.isStatistics) {
//            sett.statistics(View.VISIBLE) // показываем или скрываем статистику

        } else {
            app.init.textViewCorrPercen.text = "0"
            viewVisibleCorr(View.GONE) // визуализация виджитов индикатора corr
//            app.init.textViewCorrPercen.setTextColor(ContextCompat.getColor(app, R.color.text_color)) // Изменяем цвет фона
            if (app.data.isStatistics_)
                sett.statistics(View.GONE) // показываем или скрываем статистику
        }
        if (app.data.maxCorrectLetters == app.data.currentCorrectCountLetters)
            app.contr.buttonEye() // выводим полный текст перевода и возвращаем окрашенные кнопки на цвет по умолчанию
    }

    // меняем количество (не) угаданных слов и цвет на виджете
    fun textViewAnswers() {
        if (count != app.data.currentIndex_) {
            count = app.data.currentIndex_

            app.data.countCorrectAnswersWords_ = app.data.intWordListLevel_.count { it == 1 } // считаем количество правильных ответов
            app.data.countWrongAnswersWords_ = app.data.intWordListLevel_.count { it == -1 } // считаем количество не правильных ответов

            app.init.textViewCorrectAnswers.text = app.data.countCorrectAnswersWords_.toString()
            app.init.textViewWrongAnswers.text = app.data.countWrongAnswersWords_.toString()

            if (app.data.countCorrectAnswersWords_ == 0)
                app.init.textViewCorrectAnswers.setTextColor(ContextCompat.getColor(app, R.color.text_color))  // получаем цвет
            else
                app.init.textViewCorrectAnswers.setTextColor(ContextCompat.getColor(app, R.color.blue_light))  // получаем цвет

            if (app.data.countWrongAnswersWords_ == 0)
                app.init.textViewWrongAnswers.setTextColor(ContextCompat.getColor(app, R.color.text_color))  // получаем цвет
            else
                app.init.textViewWrongAnswers.setTextColor(ContextCompat.getColor(app, R.color.red_light))  // получаем цвет

            //======================================================================================

            val size = app.data.intWordListLevel_.size // длина текущего словаря

            // если массив заполнен правильными ответами, то открываем доступ к следующему уровню
            if (app.data.countCorrectAnswersWords_ == size && size != 0) {
                if (app.data.indexLevel_ < 5) {
                    if (app.data.lockImageLevel_[app.data.indexLevel_+1] == false) { // усли следующий уровень еще не открыт для доступа
                        app.data.lockImageLevel_[app.data.indexLevel_+1] = true
                        updateToast("${app.data.listItemsLevels[app.data.indexLevel_ + 1]} - open", R.color.white, R.color.blue_light)
                    }
                }
            }

            // если массив заполнен одно-значными ответами, то обнуляем
            if (app.data.countCorrectAnswersWords_ == size || app.data.countWrongAnswersWords_ == size)
                app.data.intWordListLevel_.fill(0) // присвоим все 0
        }
    }

    //--- обновление ошибок для окрашивания прогресс бара ==========================================
    fun updateErrors(flag: Boolean = true) {
        // если не выбрана библиотека или слово угадано, не даем обновлять textViewRu нажимать клаву
        if (app.contr.stopActions() && flag) return

        app.data.maxErrorsLetters = app.data.firstTextRu.count { Regex(app.data.patternRu4).containsMatchIn(it.toString()) } // максимальное допустимое количество ошибок привязываем к длине слова
        app.init.errProgressBar.setErrorCount(app.data.currentErrorCountLetters, app.data.maxErrorsLetters)

        if (app.data.currentErrorCountLetters != 0) {
            count = -1 // при смене уровня, для отображения первого результата в счетчике не угаданных слов
            val s = (app.data.currentErrorCountLetters * 100.0 / app.data.maxErrorsLetters).toInt().toString()
            app.init.textViewErrPercen.text = s
            app.init.textViewErrPercen.setTextColor(ContextCompat.getColor(app, R.color.red_light)) //меняем цвет
            if (s == "100") { // прибавляем на экране к количеству не правильному ответу

                if (app.data.intWordListLevel_.size == 0)
                    app.data.intWordListLevel_ = MutableList(app.data.dictionaryTripleList_!!.size) { 0 } // присвоим все 0
                
                app.data.intWordListLevel_[app.data.dictionaryTripleList_!![app.data.currentIndex_].first] = -1 // записываем в список не угаданное слово
                updateToast(app.data.wrongAnswer, R.color.white, R.color.red_light) // выводим Toast согласно заданным параметрам
                textViewAnswers() // меняем количество (не) угаданных слов и цвет на виджите
                if (app.data.isStatistics_)
                    sett.statistics(View.VISIBLE) // показываем или скрываем статистику и индикацию
            }
            if (app.init.textViewErrPercen.visibility == View.GONE && app.data.isIndicator_)
                viewVisibleErr(View.VISIBLE) // визуализация виджитов индикатора err
        } else {
            app.init.textViewErrPercen.text = "0"
            viewVisibleErr(View.GONE) // визуализация виджитов индикатора err
//            app.init.textViewErrPercen.setTextColor(ContextCompat.getColor(app, R.color.text_color)) //меняем цвет
            if (app.data.isStatistics_)
                sett.statistics(View.GONE) // показываем или скрываем статистику и индикацию
        }
        if (app.data.maxErrorsLetters == app.data.currentErrorCountLetters)
            app.contr.buttonEye() // выводим полный текст перевода и возвращаем окрашенные кнопки на цвет по умолчанию
    }

    //--- по нажатии кнопок восстанавливаем все значения по умолчанию
    fun update() {
        textViewUpdate() // обновляем слово в textViewEn и firstWordRu и в spinnerWords
        defaultColorTextViews() // возвращаем все окрашенные кнопки на цвет по умолчанию
        onSwitch() // если switch включен

        app.data.currentCorrectCountLetters = 0 // количество неправильно угаданных букв
        updateCorrect(false) // обновление ошибок для окрашивания прогресс бара

        app.data.currentErrorCountLetters = 0 // количество неправильно угаданных букв
        updateErrors(false) // цвет прогресс бара
    }

    //--- выводим Toast согласно заданным параметрам
    fun updateToast(message: String, colorText: Int, colorBackground: Int, sizeText: Float = 16f, padding: Int = 4, length: Boolean = false) {
        // Подготавливаем содержимое кастомного Toast
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_toast_message, null)

        // Для изменения текста используйте:
        val textView = layout.findViewById<TextView>(R.id.custom_toast_message)
        textView.text = message
        textView.textSize = sizeText // Установите размер текста здесь в "float"

        // Расширение для конвертации dp в px
        fun Int.dpToPixels(context: Context): Int {
            return (this * context.resources.displayMetrics.density).toInt()
        }
        // Установить padding в конце (end) - для версии API 17 и выше.
//        textView.setPaddingRelative(textView.paddingStart, textView.paddingTop, 16.dpToPixels(context), textView.paddingBottom) // образец
        textView.setPaddingRelative(80.dpToPixels(context), padding.dpToPixels(context), 80.dpToPixels(context), padding.dpToPixels(context))

        // Для изменения цвета фона:
        val drawable: GradientDrawable = layout.background as GradientDrawable
        drawable.setColor(ContextCompat.getColor(context, colorBackground)) // Изменяем цвет фона

        // Для изменения цвета текста:
        textView.setTextColor(ContextCompat.getColor(context, colorText))

        // Создаем и отображаем кастомный Toast
        val toast = Toast(context)
        toast.setGravity(Gravity.CENTER, 0, -180)
        if (length) toast.duration = Toast.LENGTH_LONG
        else toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}
