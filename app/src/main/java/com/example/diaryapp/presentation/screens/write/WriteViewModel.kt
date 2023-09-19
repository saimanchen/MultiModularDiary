package com.example.diaryapp.presentation.screens.write

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.diaryapp.model.Mood
import com.example.diaryapp.util.Constants.WRITE_SCREEN_ARG_KEY

class WriteViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    var diaryState by mutableStateOf(DiaryState())

    init {
        getDiaryIdArgument()
    }
    private fun getDiaryIdArgument() {
        diaryState = diaryState.copy(
            selectedDiaryId = savedStateHandle.get<String>(
                key = WRITE_SCREEN_ARG_KEY
            )
        )
    }
}

data class DiaryState(
    val selectedDiaryId: String? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral
)