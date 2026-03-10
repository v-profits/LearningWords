package com.example.learningwords

import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

//Создайте адаптер для ViewPager2, который будет использоваться для управления фрагментами.
// Этот адаптер должен наследоваться от FragmentStateAdapter.
class WordsPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private var words: List<String> // Сделайте words изменяемым, используя var
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = words.size

    override fun createFragment(position: Int): Fragment {
        val fragment = WordsFragment()
        fragment.arguments = bundleOf("word" to words[position])
        return fragment
    }

    // обновления списка слов
    fun updateWordsEn(newWords: List<String>) {
        words = newWords
        notifyDataSetChanged() // Уведомляем адаптер о том, что данные изменились
    }
}
