package com.example.diaryapp.presentation.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaryapp.data.repository.DiaryEntries
import com.example.diaryapp.data.repository.MongoDB
import com.example.diaryapp.util.RequestState
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    init {
        observeAllDiaryEntries()
    }

    var diaryEntries: MutableState<DiaryEntries> = mutableStateOf(RequestState.Idle)

    private fun observeAllDiaryEntries() {
        viewModelScope.launch {
            MongoDB.getAllDiaryEntries().collect { result ->
                diaryEntries.value = result
            }
        }
    }
}