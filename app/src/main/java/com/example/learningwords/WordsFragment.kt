package com.example.learningwords

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.fragment.app.activityViewModels

class WordsFragment : Fragment() {

    private lateinit var viewModel: FragmentViewModel
    private lateinit var textViewWord: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Получаем ViewModel относящуюся к активности, чтобы разделить данные между всеми фрагментами
        viewModel = ViewModelProvider(requireActivity()).get(FragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_word, container, false)
        textViewWord = view.findViewById(R.id.textViewWord)
        val word = arguments?.getString("word") ?: "Слово не найдено"
        textViewWord.text = word
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Подписываемся на обновления из ViewModel
        viewModel.isTextVisible().observe(viewLifecycleOwner, { isTextVisible ->
            val textColor = if (isTextVisible) {
                R.color.text_color // Цвет текста
            } else {
                R.color.windowBackground // Цвет фона (текст как бы "невидимый")
            }
            textViewWord.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        })

        textViewWord.setOnClickListener {
            // Переключение видимости в ViewModel, что приведет к обновлению всех подписанных компонентов UI
            viewModel.toggleTextVisibility()
        }
    }
}



//class WordsFragment() : Fragment() {
//
//    private var isTextViewVisible = true
//    private lateinit var textViewWord: TextView
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
//    ): View {
//        // Убедитесь, что используете правильный идентификатор макета
//        val view = inflater.inflate(R.layout.fragment_word, container, false)
//
//        // Убедитесь, что textViewWord содержится в fragment_word.xml и правильно указан его id
//        textViewWord = view.findViewById(R.id.textViewWord)
//
//        val word = arguments?.getString("word") ?: "Слово не найдено"
//
//        // Устанавливаем текст и обзервер для изменения цвета текста
//        textViewWord.text = word
////        (activity as? UiInitializer)?.textViewEn?.text = "word"
//
//        // Наблюдение за изменениями цвета текста
//        textViewWord.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color))
////        (activity as? UiInitializer)?.textViewEn?.setTextColor(ContextCompat.getColor(requireContext(), R.color.item_background_color))
//
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val textView = view.findViewById<TextView>(R.id.textViewWord)
//
//        // Установка слушателя кликов
//        textView.setOnClickListener {
//            // Действие при клике на TextView
//            // Переключение между видимым состоянием и невидимым
//            isTextViewVisible = !isTextViewVisible
////            textView.visibility = if (isTextViewVisible) View.VISIBLE else View.INVISIBLE
//            if (isTextViewVisible)
//                textView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color))
//            else
//                textView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.item_background_color))
//        }
//    }
//}