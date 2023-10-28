package com.example.diaryapp.presentation.screens.write

import android.net.Uri
import com.example.diaryapp.model.remote.Diary
import com.example.diaryapp.model.remote.Mood
import com.example.diaryapp.util.RequestState
import io.realm.kotlin.types.RealmInstant
import java.time.ZonedDateTime

data class WriteUiState(
    val selectedDiaryId: String? = null,
    val selectedDiary: Diary? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral,
    val updatedDateTime: RealmInstant? = null
)

sealed class WriteAction {
    object GetDiaryIdArgument : WriteAction()
    object GetSelectedDiaryEntry : WriteAction()
    data class SetSelectedDiaryEntry(val diary: Diary) : WriteAction()
    data class SetTitle(val title: String) : WriteAction()
    data class SetDescription(val description: String) : WriteAction()
    data class SetMood(val mood: Mood) : WriteAction()
    data class SetDateTime(val zonedDateTime: ZonedDateTime?) : WriteAction()
    data class InsertDiaryEntry(
        val diary: Diary,
        val onSuccess: () -> Unit,
        val onError: (String) -> Unit
    ) : WriteAction()
    data class UpdateDiaryEntry(
        val diary: Diary,
        val onSuccess: () -> Unit,
        val onError: (String) -> Unit
    ) : WriteAction()
    data class UpsertDiaryEntry(
        val diary: Diary,
        val onSuccess: () -> Unit,
        val onError: (String) -> Unit
    ) : WriteAction()
    data class DeleteDiaryEntry(
        val onSuccess: () -> Unit,
        val onError: (String) -> Unit
    ) : WriteAction()
    data class GenerateImagePathAndAddToGalleryStateList(
        val image: Uri,
        val imageType: String
    ) : WriteAction()
    object UploadImagesToFirebase : WriteAction()
    data class GetImagesFromFirebase(val diary: RequestState<Diary>) : WriteAction()
    data class DeleteImagesFromFirebase(val images: List<String>? = null) : WriteAction()
    data class ExtractRemoteImagePath(val remoteImagePath: String) : WriteAction()
}