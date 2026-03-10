package com.example.learningwords

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesManager(private val context: Context, private val data: DataManager) {

    // Получение SharedPreferences
    private val prefs = context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    // =============================================================================================

    // восстанавливаем все значения
    fun loadState() {

        // восстанавливаем переменные
        data.isDarkMode_ = prefs.getBoolean("isDarkMode_", false) // true - меняет тему на темную
        data.switchNext_ = prefs.getBoolean("switchNext_", false) // true - включает авто кнопки "Next"
        data.switchBack_ = prefs.getBoolean("switchBack_", false) // true - включает авто кнопки "Back"
        data.switchEye_ = prefs.getBoolean("switchEye_", false) // true - включает авто кнопки "Eye"
        data.switchSpeak_ = prefs.getBoolean("switchSpeak_", false) // true - включает авто кнопки "Speak"
        data.isIgnore_ = prefs.getBoolean("isIgnore_", false) // true - исключает угаданные слова
        data.isVision_ = prefs.getBoolean("isVision_", false) // true - показывает угаданные слова
//        data.isShuffled = prefs.getBoolean("isShuffled", false) // true - словарь перемешивается
        data.isStatistics_ = prefs.getBoolean("isStatistics_", false) // true - показываем виджиты статистики
        data.isVolume_ = prefs.getBoolean("isVolume_", false) // true - показываем виджит громкости
        data.isHide_ = prefs.getBoolean("isHide_", false) // true - скрывает все переведенные и не переведенные слова
        data.isIndicator_ = prefs.getBoolean("isIndicator_", false) // true - показывает индикаторы букв слова

        // методы для загрузки данных, обновления UI и т.д.
        data.totalSize_ = prefs.getInt("totalSize_", 0) // количество слов в словаре
        data.indexLevel_ = prefs.getInt("indexLevel_", 0) // [0] - словарь по умолчанию
        data.indexSpeak_ = prefs.getInt("indexSpeak_", 0) // [0] - голос по умолчанию
//        data.currentIndex = prefs.getInt("currentIndex", 0) // индекс для текущего слова
//        data.countCorrectAnswersWords = prefs.getInt("countCorrectAnswersWords", 0) // количество угаданных слов
//        data.countWrongAnswersWords = prefs.getInt("countWrongAnswersWords", 0) // количество не угаданных слов


        // восстанавливаем списки
        getIntList()
        getBoolList()

        // восстанавливаем парные списки
        getTripleList()
    }

    // восстанавливаем списки
    fun getIntList() {
        // методы для загрузки данных, обновления UI и т.д.
//        data.currentIndex = prefs.getInt("currentIndex", 0) // индекс для текущего слова
//        data.countCorrectAnswersWords = prefs.getInt("countCorrectAnswersWords", 0) // количество угаданных слов
//        data.countWrongAnswersWords = prefs.getInt("countWrongAnswersWords", 0) // количество не угаданных слов

        val gson = Gson() // для списка
        //--- // -1 - не угаданные слова словаря, +1 - угаданные, 0 - остальные
        // Определяем тип для десериализации списка Int значений
        val type = object : TypeToken<MutableList<Int>>() {}.type

//        val json = prefs.getString("intWordListLevel", null)
        val json = when(data.indexLevel_) {
            0 -> {
                data.isShuffled_ = prefs.getBoolean("isShuffled_0_", false) // true - словарь перемешивается
                data.currentIndex_ = prefs.getInt("currentIndex_0_", 0) // индекс для текущего слова
                data.countCorrectAnswersWords_ = prefs.getInt("countCorrectAnswersWords_0_", 0) // количество угаданных слов
                data.countWrongAnswersWords_ = prefs.getInt("countWrongAnswersWords_0_", 0) // количество не угаданных слов
                prefs.getString("intWordListLevel_0_", null)
            }
            1 -> {
                data.isShuffled_ = prefs.getBoolean("isShuffled_1_", false) // true - словарь перемешивается
                data.currentIndex_ = prefs.getInt("currentIndex_1_", 0) // индекс для текущего слова
                data.countCorrectAnswersWords_ = prefs.getInt("countCorrectAnswersWords_1_", 0) // количество угаданных слов
                data.countWrongAnswersWords_ = prefs.getInt("countWrongAnswersWords_1_", 0) // количество не угаданных слов
                prefs.getString("intWordListLevel_1_", null)
            }
            2 -> {
                data.isShuffled_ = prefs.getBoolean("isShuffled_2_", false) // true - словарь перемешивается
                data.currentIndex_ = prefs.getInt("currentIndex_2_", 0) // индекс для текущего слова
                data.countCorrectAnswersWords_ = prefs.getInt("countCorrectAnswersWords_2_", 0) // количество угаданных слов
                data.countWrongAnswersWords_ = prefs.getInt("countWrongAnswersWords_2_", 0) // количество не угаданных слов
                prefs.getString("intWordListLevel_2_", null)
            }
            3 -> {
                data.isShuffled_ = prefs.getBoolean("isShuffled_3_", false) // true - словарь перемешивается
                data.currentIndex_ = prefs.getInt("currentIndex_3_", 0) // индекс для текущего слова
                data.countCorrectAnswersWords_ = prefs.getInt("countCorrectAnswersWords_3_", 0) // количество угаданных слов
                data.countWrongAnswersWords_ = prefs.getInt("countWrongAnswersWords_3_", 0) // количество не угаданных слов
                prefs.getString("intWordListLevel_3_", null)
            }
            4 -> {
                data.isShuffled_ = prefs.getBoolean("isShuffled_4_", false) // true - словарь перемешивается
                data.currentIndex_ = prefs.getInt("currentIndex_4_", 0) // индекс для текущего слова
                data.countCorrectAnswersWords_ = prefs.getInt("countCorrectAnswersWords_4_", 0) // количество угаданных слов
                data.countWrongAnswersWords_ = prefs.getInt("countWrongAnswersWords_4_", 0) // количество не угаданных слов
                prefs.getString("intWordListLevel_4_", null)
            }
            else -> {
                data.isShuffled_ = prefs.getBoolean("isShuffled_5_", false) // true - словарь перемешивается
                data.currentIndex_ = prefs.getInt("currentIndex_5_", 0) // индекс для текущего слова
                data.countCorrectAnswersWords_ = prefs.getInt("countCorrectAnswersWords_5_", 0) // количество угаданных слов
                data.countWrongAnswersWords_ = prefs.getInt("countWrongAnswersWords_5_", 0) // количество не угаданных слов
                prefs.getString("intWordListLevel_5_", null)
            }
        }

        // Преобразование JSON строки обратно в список Int значений SharedPreferences
        data.intWordListLevel_ = gson.fromJson(json, type) ?: mutableListOf()

        // при первой загрузке считывается из памяти телефона лист == []
        if (data.intWordListLevel_.size == 0) { // неугаданные слова есть -1, угаданные 1
            val listLength = data.dictionaryTripleList_?.size // количество слов в словаре
            if (listLength != null) // неугаданные слова есть -1, угаданные 1
                data.intWordListLevel_ = MutableList(listLength) { 0 } // присвоим все 0
        }
    }

    // восстанавливаем списки
    fun getBoolList() {
        val gson = Gson() // для списка
        //--- доступный уровень для открытия
        val json = prefs.getString("lockImageLevel_", null)
        // Определяем тип для десериализации списка Boolean значений
        val type = object : TypeToken<MutableList<Boolean>>() {}.type
        // Преобразование JSON строки обратно в список Boolean значений SharedPreferences
        data.lockImageLevel_ = gson.fromJson(json, type) ?: mutableListOf()

        // при первой загрузке считывается из памяти телефона лист == []
        if (data.lockImageLevel_.size == 0)
            data.lockImageLevel_ = mutableListOf(true, false, false, false, false, false) // true - доступный уровень для открытия
    }

    // восстанавливаем парные списки
//    fun getPairList() {
//        val gson = Gson() // для парного списка
//        val json = prefs.getString("dictionaryList", null)
//        // используем object : TypeToken чтобы сохранить информацию о типах генериков во время выполнения
//        val type = object : TypeToken<List<Pair<String, String>>>() {}.type
//        data.dictionaryList = if (json != null)
//            gson.fromJson(json, type) // Преобразование JSON обратно в список пар
//        else
//            null
//    }

    // восстанавливаем тройные списки
    fun getTripleList() {
        val gson = Gson() // для парного списка
        val json = prefs.getString("dictionaryTripleList_", null)
        // используем object : TypeToken чтобы сохранить информацию о типах генериков во время выполнения
        val type = object : TypeToken<List<Triple<Int, String, String>>>() {}.type
        data.dictionaryTripleList_ = if (json != null)
            gson.fromJson(json, type) // Преобразование JSON обратно в список пар
        else
            null
    }

    // =============================================================================================

    // сохраняем все значения
    fun saveState() {

        val editor = prefs.edit()

        // сохраняем из наших списков в память телефона
        saveIntList()
        saveBoolList()

        // сохраняем из наших парных списков в память телефона
        saveTripleList()

        // сохраняем из наших переменных в память телефона
        editor.putBoolean("isDarkMode_", data.isDarkMode_)
        editor.putBoolean("switchNext_", data.switchNext_)
        editor.putBoolean("switchBack_", data.switchBack_)
        editor.putBoolean("switchEye_", data.switchEye_)
        editor.putBoolean("switchSpeak_", data.switchSpeak_)
        editor.putBoolean("isIgnore_", data.isIgnore_)
        editor.putBoolean("isVision_", data.isVision_)
//        editor.putBoolean("isShuffled", data.isShuffled)
        editor.putBoolean("isStatistics_", data.isStatistics_)
        editor.putBoolean("isVolume_", data.isVolume_)
        editor.putBoolean("isHide_", data.isHide_)
        editor.putBoolean("isIndicator_", data.isIndicator_)

        // методы для загрузки данных, обновления UI и т.д.
        editor.putInt("totalSize_", data.totalSize_)
        editor.putInt("indexLevel_", data.indexLevel_)
        editor.putInt("indexSpeak_", data.indexSpeak_)
//        editor.putInt("currentIndex", data.currentIndex)
//        editor.putInt("countCorrectAnswersWords", data.countCorrectAnswersWords)
//        editor.putInt("countWrongAnswersWords", data.countWrongAnswersWords)

//        editor.apply() // асинхронно записывает изменения в файл на диске и возвращает управление
        editor.commit() // блокирует до того момента, пока изменения не будут записаны, что может вызвать заикание UI, если выполняется в главном потоке
    }

    // сохраняем из наших списков в память телефона
    fun saveIntList() {
        val editor = prefs.edit()

        // методы для загрузки данных, обновления UI и т.д.
//        editor.putInt("currentIndex", data.currentIndex)
//        editor.putInt("countCorrectAnswersWords", data.countCorrectAnswersWords)
//        editor.putInt("countWrongAnswersWords", data.countWrongAnswersWords)

        val gson = Gson()
        // Преобразование списка в JSON
        val json = gson.toJson(data.intWordListLevel_)
        // Сохранение JSON в SharedPreferences
//        editor.putString("intWordListLevel", json)
        when(data.indexLevel_) {
            0 -> {
                editor.putBoolean("isShuffled_0_", data.isShuffled_)
                editor.putInt("currentIndex_0_", data.currentIndex_)
                editor.putInt("countCorrectAnswersWords_0_", data.countCorrectAnswersWords_)
                editor.putInt("countWrongAnswersWords_0_", data.countWrongAnswersWords_)
                editor.putString("intWordListLevel_0_", json)
            }
            1 -> {
                editor.putBoolean("isShuffled_1_", data.isShuffled_)
                editor.putInt("currentIndex_1_", data.currentIndex_)
                editor.putInt("countCorrectAnswersWords_1_", data.countCorrectAnswersWords_)
                editor.putInt("countWrongAnswersWords_1_", data.countWrongAnswersWords_)
                editor.putString("intWordListLevel_1_", json)
            }
            2 -> {
                editor.putBoolean("isShuffled_2_", data.isShuffled_)
                editor.putInt("currentIndex_2_", data.currentIndex_)
                editor.putInt("countCorrectAnswersWords_2_", data.countCorrectAnswersWords_)
                editor.putInt("countWrongAnswersWords_2_", data.countWrongAnswersWords_)
                editor.putString("intWordListLevel_2_", json)
            }
            3 -> {
                editor.putBoolean("isShuffled_3_", data.isShuffled_)
                editor.putInt("currentIndex_3_", data.currentIndex_)
                editor.putInt("countCorrectAnswersWords_3_", data.countCorrectAnswersWords_)
                editor.putInt("countWrongAnswersWords_3_", data.countWrongAnswersWords_)
                editor.putString("intWordListLevel_3_", json)
            }
            4 -> {
                editor.putBoolean("isShuffled_4_", data.isShuffled_)
                editor.putInt("currentIndex_4_", data.currentIndex_)
                editor.putInt("countCorrectAnswersWords_4_", data.countCorrectAnswersWords_)
                editor.putInt("countWrongAnswersWords_4_", data.countWrongAnswersWords_)
                editor.putString("intWordListLevel_4_", json)
            }
            else -> {
                editor.putBoolean("isShuffled_5_", data.isShuffled_)
                editor.putInt("currentIndex_5_", data.currentIndex_)
                editor.putInt("countCorrectAnswersWords_5_", data.countCorrectAnswersWords_)
                editor.putInt("countWrongAnswersWords_5_", data.countWrongAnswersWords_)
                editor.putString("intWordListLevel_5_", json)
            }
        }

//        editor.apply()
        editor.commit()
    }

    // сохраняем из наших списков в память телефона
    fun saveBoolList() {
        val editor = prefs.edit()
        val gson = Gson()
        // Преобразование списка в JSON
        val json = gson.toJson(data.lockImageLevel_)
        // Сохранение JSON в SharedPreferences
        editor.putString("lockImageLevel_", json)
//        editor.apply()
        editor.commit()
    }

    // сохраняем из наших парных списков в память телефона
//    fun savePairList() {
//        val editor = prefs.edit()
//        val gson = Gson()
//        // Преобразование списка пар в JSON
//        val json = gson.toJson(data.dictionaryList)
//        // Сохранение JSON в SharedPreferences
//        editor.putString("dictionaryList", json)
////        editor.apply()
//        editor.commit()
//    }

    // сохраняем данные из наших тройных списков в память телефона
    fun saveTripleList() {
        val editor = prefs.edit()
        val gson = Gson()
        // Преобразование списка пар в JSON
        val json = gson.toJson(data.dictionaryTripleList_)
        // Сохранение JSON в SharedPreferences
        editor.putString("dictionaryTripleList_", json)
//        editor.apply()
        editor.commit()
    }

}