package com.example.diaryapp.presentation.screens.write

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaryapp.data.repository.MongoDB
import com.example.diaryapp.model.remote.Diary
import com.example.diaryapp.model.GalleryImage
import com.example.diaryapp.model.local.ImagesToUploadDao
import com.example.diaryapp.model.local.entity.ImageToUpload
import com.example.diaryapp.model.remote.Mood
import com.example.diaryapp.util.Constants.WRITE_SCREEN_ARG_KEY
import com.example.diaryapp.util.GalleryState
import com.example.diaryapp.util.RequestState
import com.example.diaryapp.util.fetchImagesFromFirebase
import com.example.diaryapp.util.toRealmInstant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val imagesToUploadDao: ImagesToUploadDao
) : ViewModel() {
    var diaryState by mutableStateOf(DiaryState())
        private set
    val galleryState = GalleryState()

    init {
        getDiaryIdArgument()
        getSelectedDiary()
    }

    private fun getDiaryIdArgument() {
        diaryState = diaryState.copy(
            selectedDiaryId = savedStateHandle.get<String>(
                key = WRITE_SCREEN_ARG_KEY
            )
        )
    }

    private fun getSelectedDiary() {
        if (diaryState.selectedDiaryId != null) {
            viewModelScope.launch {
                MongoDB.getSelectedDiaryEntry(diaryId = ObjectId.invoke(diaryState.selectedDiaryId!!))
                    .catch {
                        emit(RequestState.Error(Exception("Diary entry is already deleted")))
                    }
                    .collect { diary ->
                        if (diary is RequestState.Success) {
                            setSelectedDiary(diary = diary.data)
                            setTitle(title = diary.data.title)
                            setDescription(description = diary.data.description)
                            setMood(mood = Mood.valueOf(diary.data.mood))
                            getImagesFromFirebase(diary = diary)
                        }
                    }
            }
        }
    }

    private fun setSelectedDiary(diary: Diary) {
        diaryState = diaryState.copy(
            selectedDiary = diary
        )
    }

    fun setTitle(title: String) {
        diaryState = diaryState.copy(title = title)
    }

    fun setDescription(description: String) {
        diaryState = diaryState.copy(description = description)
    }

    fun setMood(mood: Mood) {
        diaryState = diaryState.copy(mood = mood)
    }

    fun setDateTime(dateTime: ZonedDateTime?) {
        diaryState = if (dateTime != null) {
            diaryState.copy(updatedDateTime = dateTime.toInstant().toRealmInstant())
        } else {
            diaryState.copy(updatedDateTime = null)
        }
    }

    private suspend fun insertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDB.insertDiaryEntry(diary = diary.apply {
            if (diaryState.updatedDateTime != null) {
                date = diaryState.updatedDateTime!!
            }
        })

        if (result is RequestState.Success) {
            uploadImagesToFirebase()
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (result is RequestState.Error) {
            withContext(Dispatchers.Main) {
                onError(result.error.message.toString())
            }
        }
    }

    private suspend fun updateDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDB.updateDiaryEntry(diary = diary.apply {
            _id = ObjectId(diaryState.selectedDiaryId!!)
            date = if (diaryState.updatedDateTime != null) {
                diaryState.updatedDateTime!!
            } else {
                diaryState.selectedDiary!!.date
            }
        })

        if (result is RequestState.Success) {
            uploadImagesToFirebase()
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (result is RequestState.Error) {
            onError(result.error.message.toString())
        }
    }

    fun upsertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (diaryState.selectedDiaryId != null) {
                updateDiary(
                    diary = diary,
                    onSuccess = onSuccess,
                    onError = onError
                )
            } else {
                insertDiary(
                    diary = diary,
                    onSuccess = onSuccess,
                    onError = onError
                )
            }
        }
    }

    fun deleteDiaryEntry(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (diaryState.selectedDiaryId != null) {
                val result = MongoDB.deleteDiaryEntry(id = ObjectId(diaryState.selectedDiaryId!!))
                if (result is RequestState.Success) {
                    withContext(Dispatchers.Main) { onSuccess() }
                } else if (result is RequestState.Error) {
                    withContext(Dispatchers.IO) { onError(result.error.message.toString()) }
                }
            }
        }
    }

    fun generateImagePathAndAddToGalleryStateList(
        image: Uri,
        imageType: String
    ) {
        val remoteImagePath = "images/${FirebaseAuth.getInstance().currentUser?.uid}/" +
                "${image.lastPathSegment}-${System.currentTimeMillis()}.${imageType}"

        galleryState.addImage(
            GalleryImage(
                image = image,
                remoteImagePath = remoteImagePath
            )
        )
    }

    private fun uploadImagesToFirebase() {
        val storage = FirebaseStorage.getInstance().reference
        galleryState.images.forEach { galleryImage ->
            val imagePath = storage.child(galleryImage.remoteImagePath)
            imagePath.putFile(galleryImage.image)
                .addOnProgressListener {
                    val sessionUri = it.uploadSessionUri
                    if (sessionUri != null) {
                        viewModelScope.launch(Dispatchers.IO) {
                            imagesToUploadDao.addImageToUpload(
                                ImageToUpload(
                                    remoteImagePath = galleryImage.remoteImagePath,
                                    imageUri = galleryImage.image.toString(),
                                    sessionUri = sessionUri.toString()
                                ),
                            )
                        }
                    }
                }
        }
    }

    private fun getImagesFromFirebase(diary: RequestState<Diary>) {
        if (diary is RequestState.Success) {
            fetchImagesFromFirebase(
                remoteImagePaths = diary.data.images,
                onImageDownload = { downloadedImage ->
                    galleryState.addImage(
                        GalleryImage(
                            image = downloadedImage,
                            remoteImagePath = extractRemoteImagePath(
                                remotePath = downloadedImage.toString()
                            )
                        )
                    )
                }
            )
        }
    }

    private fun extractRemoteImagePath(remotePath: String): String {
        val chunks = remotePath.split("%2F")
        val imageName = chunks[2].split("?").first()
        return "images/${Firebase.auth.currentUser?.uid}/$imageName"
    }
}

data class DiaryState(
    val selectedDiaryId: String? = null,
    val selectedDiary: Diary? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral,
    val updatedDateTime: RealmInstant? = null
)