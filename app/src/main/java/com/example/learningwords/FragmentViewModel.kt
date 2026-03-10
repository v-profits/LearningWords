package com.example.learningwords

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FragmentViewModel : ViewModel() {
    // Одно общее состояние для всех фрагментов
    private val isTextVisibleLiveData = MutableLiveData<Boolean>(true)

    fun toggleTextVisibility() {
        isTextVisibleLiveData.value = !(isTextVisibleLiveData.value ?: true)
    }

    fun isTextVisible(): LiveData<Boolean> = isTextVisibleLiveData
}