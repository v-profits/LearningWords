package com.example.learningwords

import android.content.Context
import android.provider.MediaStore
import android.net.Uri
import android.util.Log
import androidx.annotation.RawRes
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern

class DictionaryLoader(private val context: Context) {
    val data = DataManager()

    // Загрузка содержимого файла из ресурсов raw в список пар слов
    fun loadRawResourceContentsToList(@RawRes resourceId: Int): List<Pair<String, String>> {
        // Открываем ресурс как InputStream и читаем его содержимое
        val inputStream = context.resources.openRawResource(resourceId)
        val reader = BufferedReader(InputStreamReader(inputStream))

        // Попытка прочитать содержимое и преобразовать его в список пар слов
        return reader.useLines { lines ->
            lines.mapNotNull { line ->
                parseLineToPair(line)
            }.toList() // Преобразование последовательности в список
        }
    }

    // Преобразование строки в пару слов
    private fun parseLineToPair(line: String): Pair<String, String>? {
        val pattern = Pattern.compile(data.patternPair)
        val matcher = pattern.matcher(line)
        return if (matcher.find()) {
            Pair(matcher.group(1)?.trim() ?: "", matcher.group(2)?.trim() ?: "")
        } else {
            null
        }
    }
}