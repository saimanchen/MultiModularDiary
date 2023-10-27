package com.example.home.viewmodel

import com.example.repository.DiaryEntries
import com.example.util.RequestState
import java.time.ZonedDateTime

internal data class HomeUiState(
    val diaryEntries: DiaryEntries = RequestState.Idle,
    val isDateSelected: Boolean = false
)

internal sealed class HomeAction {
    data class GetDiaryEntries(val zonedDateTime: ZonedDateTime?) : HomeAction()
    object ObserveAllDiaryEntries : HomeAction()
    data class ObserveFilteredDiaryEntries(val zonedDateTime: ZonedDateTime) : HomeAction()
    data class DeleteAllDiaryEntries(
        val onSuccess: () -> Unit,
        val onError: (Throwable) -> Unit
    ) : HomeAction()
}