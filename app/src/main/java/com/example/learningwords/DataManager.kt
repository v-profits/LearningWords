package com.example.learningwords

import android.speech.tts.Voice
import android.widget.TextView

class DataManager {

    // отслеживают долгое нажатие кнопок
    var switchNext_ = false // true - включает авто кнопки "Next"
    var switchBack_ = false // true - включает авто кнопки "Back"
    var switchEye_ = false // true - включает авто кнопки "Eye"
    var switchSpeak_ = false // true - включает авто кнопки "Speak"
    var isDarkMode_ = false // true - меняет тему на темную
    var isIgnore_ = false // true - исключает угаданные слова
    var isHide_ = false // true - скрывает все переведенные и не переведенные слова
    var isIndicator_ = false // true - показывает индикаторы букв слова
    var isVision_ = false // true - показывает угаданные слова
    var isShuffled_ = false // true - словарь перемешивается
    var isStatistics_ = false // true - показываем виджиты статистики
    var isVolume_ = false // true - показываем виджит громкости
    var isManual = false // true - показываем текст инструкции
    var isLock = false // true - блокирует если слово перевода открыто в спинере, а не кнопкой buttonEye
//    var isLevel = false // true - когда выбор уровня, обнуляем позиции
    var colorFirstItemView = false // true - когда первый итем спиннера окрашен в красный или синий цвет

    var isButton = false // true - кнопка вперед нажата

    var indexLevel_ = 0 // [0] - словарь по умолчанию
    var indexSpeak_ = 0 // [0] - голос по умолчанию
    var totalSize_ = 0 // количество слов в словаре


    var flagButtonSee = false // флаг отслеживает - если слово подсказано, угадано или закончились попытки (true)

    // методы для загрузки данных, обновления UI и т.д.
    var maxCorrectLetters = 0 // максимальное количество правильных ответов
    var maxErrorsLetters = 0 // максимальное количество ошибок
    var currentCorrectCountLetters = 0 // количество правильно угаданных букв с повторами символов
    var currentErrorCountLetters = 0 // количество неправильно угаданных букв
    var currentIndex_ = 0 // индекс для текущего слова
    var countCorrectAnswersWords_ = 0 // количество угаданных слов
    var countWrongAnswersWords_ = 0 // количество не угаданных слов

    var previousPosition = -1  // Начальная позиция до перелистывания или предыдущая позиция по перелистывании

//    val millisecScrollDelayNext =1000L // миллисекунд задержка перелистывания
//    val millisecScrollDelayNextEye =3000L // миллисекунд задержка перелистывания

    val patternRu1 = """[,/].*""" // Удалить все после запятой или слеша
//    val patternRu1 = """[,].*""" // Удалить все после запятой или слеша
    val patternRu2 = """[!?]""" // Оставляем только содержимое до первого восклицательного или вопросительного знака
    val patternRu3 = """\s*\([^)]*\)""" // Удаляем скобки и их содержимое
    val patternRu4 = "[а-яА-ЯёЁ]" // считываем колличество букв в русском слове
    val patternPair = """([A-Za-z()"',.!?/-]+(?:\s+(?!\([А-Яа-яЁё])[A-Za-z()"',.!?/-]+)*)\s+([А-Яа-яЁё"(),.!?/-]+(?:\s+(?!\([A-Za-z])[А-Яа-яЁё"(),.!?/-]+)*)"""//.toRegex() // паттерн для преобразования строки в пару

    val chPoint = "•"// "·"
    val chLine = "_" // замена для chPoint для первого показа до нажатия клавиш
    val chReplace = " ▪ "
//    val chReplace = " • " // заменяем chLine
//    val chReplace = " ◦ "
//    val chReplace = " ● "
//    val chReplace = " ◌ "
//    val chReplace = " ▫ "
//    val chReplace = " × " // заменяем chLine


    //    val excludedChars = setOf(' ', '-', '•', '!', '?', '"') //
    val excludedChars = setOf(' ', '-', chPoint[0], '!', '?', '"', '/') //

    val textEn = "" // ""Choose a dictionary!" // "Выберите словарь!" //
    val textRu = ""
    val correctAnswer = "true" // "TRUE" // "ПРАВИЛЬНО" // "Правильно!" // "Это, ДА!"
    val wrongAnswer = "false" // "FALSE" // "НЕПРАВИЛЬНО" // "Неправильно" // "Это, НЕТ"
    val lockSpinner = "LOCK"
    val variantsWord = "OPEN"


//    var dictionaryList: List<Pair<String, String>>? = null // парный список всех слов из словаря файла
    var dictionaryTripleList_: List<Triple<Int, String, String>>? = null // Создание трехмерного списка типа List<Triple<Int, String, String>>
    var wordsListSpinner = mutableListOf(variantsWord) // список слов для спинера
    lateinit var intWordListLevel_: MutableList<Int> // -1 - не угаданные слова словаря, +1 - угаданные, 0 - остальные

    var lockImageLevel_ = mutableListOf(true, false, false, false, false, false) // true - доступный уровень для открытия
    lateinit var availableVoices: List<Voice>  // Используем переменную для хранения списка доступных голосов

    lateinit var listKeyboardTextView: List<TextView> // список букв клавиатуры
    var numIndexLetters = MutableList(33) { false } // записываем ошибочные литеры


    var fullTextRu = "" // русское слово без применения регулярных выражений
    var firstTextRu = "" // первое русское выражение, list символов, выбранного из fullTextRu
    var pressLetterKeyboard = "" // нажатая буква на клавиатуре
    var newTextRu = "" // list символов, которые выводятся по мере угадывания выражения русского перевода

    val listItemsLevels = arrayOf("Уровень 1", "Уровень 2", "Уровень 3",
        "Уровень 4", "Уровень 5", "Уровень 6") // имя уровня в меню выбора уровня

    val filesRawDictionary = listOf(R.raw.dictionary_100, R.raw.dictionary_600, R.raw.dictionary_1000,
        R.raw.dictionary_1025, R.raw.dictionary_3000, R.raw.dictionary_8000) // имя файла из которого скопируем словарь
}
