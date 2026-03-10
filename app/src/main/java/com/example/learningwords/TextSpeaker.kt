package com.example.learningwords

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

// озвучивает текст голосом
class TextSpeaker(context: Context, private val data: DataManager, private val locale: Locale = Locale.getDefault()) : TextToSpeech.OnInitListener {

    private val tts: TextToSpeech = TextToSpeech(context, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Получаем и сохраняем список доступных голосов.
            data.availableVoices = tts.voices.filter { it.locale == Locale.US }.toList()

            // Теперь, когда список голосов получен, мы можем установить голос.
            isReady = true // Сначала устанавливаем флаг в true, так как TTS инициализирован успешно

            val selectedVoice = if (data.availableVoices.isNotEmpty())
                data.availableVoices[data.indexSpeak_]
            else {
                Log.e("aaa", "Список доступных голосов пуст")
                null
            }

            if (selectedVoice != null)
                tts.voice = selectedVoice
            else
                tts.setLanguage(Locale.US) // или Locale("ru") для русского - если список null, ставим один голос, для самсунга
            
        } else {
            Log.e("aaa", "Initialization failed")
        }
    }

    fun setVoice(index: Int) {
        if (isReady) {  // Проверяем только значение isReady, так как уже знаем, что availableVoices не пуст
            val selectedVoice = data.availableVoices[index]
            tts.voice = selectedVoice

            // При выборе голоса пользователем вы можете протестировать его, воспроизведя тестовое слово.
            tts.speak("A sample of the voice.", TextToSpeech.QUEUE_FLUSH, null, null)

            data.indexSpeak_ = index // запоминаем индекс выбранного голоса

        } else {
            Log.e("aaa", "TTS not ready or invalid voice index: $index")
        }
    }

    fun speak(text: String) {
        if (isReady) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    fun stop() {
        if (tts.isSpeaking) {
            tts.stop()
        }
    }

    fun shutdown() {
        tts.shutdown()
    }
}
