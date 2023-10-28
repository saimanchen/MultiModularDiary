package com.example.diaryapp.presentation.screens.home

import com.example.diaryapp.data.repository.DiaryEntries
import com.example.diaryapp.util.RequestState
import java.time.ZonedDateTime

data class HomeUiState(
    val diaryEntries: DiaryEntries = RequestState.Idle,
    val isDateSelected: Boolean = false
)

sealed class HomeAction {
    data class GetDiaryEntries(val zonedDateTime: ZonedDateTime?) : HomeAction()
    object ObserveAllDiaryEntries : HomeAction()
    data class ObserveFilteredDiaryEntries(val zonedDateTime: ZonedDateTime) : HomeAction()
    data class DeleteAllDiaryEntries(
        val onSuccess: () -> Unit,
        val onError: (Throwable) -> Unit
    ) : HomeAction()
}