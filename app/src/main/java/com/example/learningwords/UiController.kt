package com.example.learningwords

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat

lateinit var sett: MySettings

class UiController(val contr: MainActivity) {

    fun createMySettingsInstance() {
        // Создаем экземпляр класса MySettings, передавая экземпляр MainActivity
        sett = MySettings(contr)
        // Теперь вы можете использовать экземпляр mySettings для вызова его методов
    }

    //--- задержка при авто листании ===================================================================

    private val handler = Handler(Looper.getMainLooper())
    private var isAutoScrolling = false

    // авто-перелистывание с задержкой
    fun startAutoScrolling() {

        if (isAutoScrolling) return // Если уже выполняется, ничего не делаем
        isAutoScrolling = true

        val runnable = object : Runnable {
            override fun run() {
                // Проверяем условие перед каждой итерацией
                if (!contr.data.flagButtonSee) {
                    // Если условие не выполняется, мы останавливаем автопрокрутку
                    stopAutoScrolling()
                    return // Выходим из Runnable
                }
                if (!isAutoScrolling) return // Если автопрокрутка остановлена, больше не запланировать

                // Запуск функций с задержкой на следующей итерации
                if (contr.data.switchNext_) {
                    contr.data.isButton = true // не позволяет viewPager дублировать fun calculateNextPosition()
                    buttonNext()
                }
                else if (contr.data.switchBack_) {
                    contr.data.isButton = true // не позволяет viewPager дублировать fun calculateNextPosition()
                    buttonBack()
                }

                if (!isAutoScrolling) return // Если автопрокрутка остановлена, больше не запланировать

                // Планируем следующий вызов Runnable с задержкой
                handler.postDelayed(this, 2000)
            }
        }
        // Запланировать первое выполнение Runnable с задержкой
        handler.postDelayed(runnable, 2000)
    }

    fun stopAutoScrolling() {
        isAutoScrolling = false
        handler.removeCallbacksAndMessages(null)
    }

    // =====================================================================================================

    // ФУНКЦИИ КОТОРЫЕ ВЫЗЫВАЮТСЯ КЛИКОМ

    //--- нажатие кнопки "Вперед" click listener
    fun buttonNext() {
        contr.data.flagButtonSee = false // флаг отслеживает - если кнопка подсказка была нажата
        contr.data.currentIndex_ = contr.calculateNextPosition(contr.data.currentIndex_, 1)
        contr.init.viewPager.setCurrentItem(contr.data.currentIndex_, false) // показываем во фрагменте слово по индексу
        contr.app.update() // по нажатии кнопок восстанавливаем все значения по умолчанию
    }

    //--- нажатие кнопки "Назад"
    fun buttonBack() {
        contr.data.flagButtonSee = false // флаг отслеживает - если кнопка подсказка была нажата
        contr.data.currentIndex_ = contr.calculateNextPosition(contr.data.currentIndex_, -1)
        contr.init.viewPager.setCurrentItem(contr.data.currentIndex_, false) // показываем во фрагменте слово по индексу
        contr.app.update() // по нажатии кнопок восстанавливаем все значения по умолчанию
    }

    //--- выводим полный текст перевода и возвращаем окрашенные кнопки на цвет по умолчанию
    fun buttonEye() {
        contr.data.flagButtonSee = true // флаг отслеживает - если кнопка подсказка была нажата

        //--- проверяем были ли частично нажаты буквы
        lettersPartialPress()

//        // показываем всплывающие данные статистики
//        if (contr.data.isStatistics)
//            sett.statistics(View.VISIBLE) // показываем или скрываем статистику

        // выводим полный текст перевода
        contr.data.newTextRu = contr.data.dictionaryTripleList_?.getOrNull(contr.data.currentIndex_)?.third ?: "" // contr.data.textScreenSaverRu
        contr.textAnimator.animateTextView(contr.init.textViewRu, contr.data.newTextRu) // присваиваем значение в русский перевод с анимацией
        contr.init.textViewRu.textSize = 24f // Устанавливаем размер текста в scale-independent pixels (sp)
        contr.app.defaultColorTextViews() // возвращаем окрашенные кнопки на цвет по умолчанию
        if (contr.data.currentCorrectCountLetters != 0 || contr.data.currentErrorCountLetters  != 0) { // сбрасываем счетчики нажатых букв
            contr.data.currentCorrectCountLetters = 0
            contr.data.currentErrorCountLetters = 0
        }
        if (contr.init.textViewEn.text.toString() != "") // если словарь уже выбрали
            contr.init.spinnerWords.isEnabled = false // после подсматривания отключаем спиннер

        startAutoScrolling() // авто-перелистывание кнопок
        lockSpinnerWords() // отключаем спиннер слов
    }

    //--- проверяем были ли частично нажаты буквы
    fun lettersPartialPress() {
        if (!(contr.data.currentCorrectCountLetters == contr.data.maxCorrectLetters || contr.data.currentErrorCountLetters == contr.data.maxErrorsLetters))
            if (contr.data.currentCorrectCountLetters != 0 || contr.data.currentErrorCountLetters != 0)
                if (contr.data.intWordListLevel_[contr.data.dictionaryTripleList_!![contr.data.currentIndex_].first] != 1) { // это слово еще не угадано
                    contr.data.intWordListLevel_[contr.data.dictionaryTripleList_!![contr.data.currentIndex_].first] = -1 // записываем в список угаданное слово
                    // выводим Toast согласно заданным параметрам
                    contr.app.updateToast(contr.data.wrongAnswer, R.color.white, R.color.red_light)
                }
        contr.app.textViewAnswers() // меняем количество (не) угаданных слов и цвет на виджите
    }

    //--- Выполнять действие при нажатии на букву ==============================================================================================
    fun onLetterClicked(view: View) {

        // если не выбрана библиотека или слово угадано, не даем обновлять textViewRu нажимать клаву
        if (stopActions()) return

        val indexKeyboard = contr.data.listKeyboardTextView.indexOf(view) // индексы списка TextView клавиатуры
        var flagLetter = false

        //----- Обработка нажатия на букву -------
        if (view is TextView) {
            // если это первый тап по клаве, выводим точки в место прочерков
            if (contr.data.currentCorrectCountLetters == 0 && contr.data.currentErrorCountLetters == 0) {
                contr.data.newTextRu = contr.data.chPoint.repeat(contr.data.firstTextRu .length) // Установите начальный вид символов прочерками в TextViewRu

                // Проверяем каждый символ из исходной строки
                for (i in contr.data.firstTextRu.indices) {
                    // Если символ присутствует в списке исключений (' ', '-', '!', '?', '"'), копируем его в новую строку
                    if (contr.data.excludedChars.contains(contr.data.firstTextRu[i])) {
                        contr.data.newTextRu = contr.data.newTextRu.substring(0, i) + contr.data.firstTextRu[i] + contr.data.newTextRu.substring(i + 1)
                    }
                }
                contr.textAnimator.animateTextView(contr.init.textViewRu, contr.data.newTextRu) // присваиваем значение в русский перевод с анимацией
            }

            // индексы литеров находящихся в слове
            contr.data.pressLetterKeyboard = view.text.toString().lowercase()
            val indices = contr.data.firstTextRu.lowercase().mapIndexedNotNull { index, c ->
                if (c == contr.data.pressLetterKeyboard[0]) index else null
            }

            //---- Добавление угаданной буквы в текст ----
            if (indices.isNotEmpty()) {
                contr.data.newTextRu = updateText(indices, contr.data.pressLetterKeyboard[0])
                flagLetter = true

                //+++ считаем число удач для прогресс бара ========================================================================================
                if (!(contr.data.currentCorrectCountLetters == contr.data.maxCorrectLetters || contr.data.currentErrorCountLetters == contr.data.maxErrorsLetters)) {
                    val totalVisibleLetters = contr.data.newTextRu.filter { !contr.data.excludedChars.contains(it) }.length // угаданных букв в слове с повторами
                    contr.data.currentCorrectCountLetters = totalVisibleLetters
                    if (indices.size == 0) //+++ считаем число ошибок для прогресс бара ====================================
                        contr.data.currentErrorCountLetters += 1
                }
                contr.app.updateCorrect()
            }

            //---- Если буква не угадана ----
            if (indices.isEmpty()) {
                //+++ считаем число ошибок для прогресс бара ========================================================================================
                if (!(contr.data.currentCorrectCountLetters == contr.data.maxCorrectLetters || contr.data.currentErrorCountLetters == contr.data.maxErrorsLetters))
                    contr.data.currentErrorCountLetters += 1
            }

            //---- Меняем текст угадываемого слова и фон тапнутой кнопки ---
            if (flagLetter) {
                //+++ **************************************************************************************************************************************
                contr.init.textViewRu.text  = contr.textAnimator.marginsForMidpoint(contr.data.newTextRu) // Строим отображаемый текст, заменяя точки на "·" и буквы на " · "
                contr.data.listKeyboardTextView[indexKeyboard].setBackgroundResource(R.color.blue_light)
                contr.data.listKeyboardTextView[indexKeyboard].setTextColor(ContextCompat.getColor(contr, R.color.white))
                contr.data.numIndexLetters[indexKeyboard] = true // этот литер нажат
            } else {
                contr.data.listKeyboardTextView[indexKeyboard].setBackgroundResource(R.color.red_light)
                contr.data.listKeyboardTextView[indexKeyboard].setTextColor(ContextCompat.getColor(contr, R.color.white))

                contr.app.updateErrors()
            }
            if (contr.data.flagButtonSee)
                contr.contr.buttonEye()

            // отключаем спиннер слов
            lockSpinnerWords()
        }
        // авто-перелистывание кнопок
        startAutoScrolling()
    }

    // отключаем спиннер слов
    fun lockSpinnerWords() {
        // отключаем спиннер слов
        if (contr.data.currentCorrectCountLetters != 0 || contr.data.currentErrorCountLetters  != 0 || //---isVision------------------------------------------------------
            (contr.data.isVision_ && contr.data.intWordListLevel_[contr.data.dictionaryTripleList_!![contr.data.currentIndex_].first] == 1 && !contr.data.flagButtonSee) ||
            (contr.data.flagButtonSee && !contr.data.isLock))
        {

            // Позже, если вы хотите обновить данные в Spinner...
            //--- спиннер для выбора слова
            val stopWord = mutableListOf(contr.data.lockSpinner)  // Обновите данные.

            // Установите новый адаптер в Spinner.
            contr.adapterWords = SpinnerManager(contr, R.layout.custom_words_spinner, stopWord, contr.data, contr.init, { position ->
                if (position > 0) { // Логика выбора слова
                    contr.init.spinnerWords.isEnabled = false // после выбора варианта отключаем спиннер
                }
            })

            // Создайте новый адаптер с новыми данными.
            setupSpinnerWords(contr, contr.init.spinnerWords, contr.adapterWords as SpinnerManager, contr.data, contr.init, this, contr.app)
        }
    }

    // добавляем литер в слово вместо " . "
    private fun updateText(indices: List<Int>, letterKlava: Char): String {
        val sb = StringBuilder(contr.data.newTextRu)
        for (i in indices)
            sb[i] = letterKlava
        return sb.toString()
    }

    // если не выбрана библиотека или слово угадано, не даем обновлять textViewRu нажимать клаву
    fun stopActions(): Boolean {
        if (contr.data.textEn == contr.init.textViewEn.text.toString())
            return true
        if (contr.data.flagButtonSee) // флаг отслеживает - если кнопка подсказка была нажата
            return true

        return false
    }
}