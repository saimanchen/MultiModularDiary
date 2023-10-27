package com.example.write.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.ImagesToDeleteDao
import com.example.database.ImagesToUploadDao
import com.example.database.entity.ImageToDelete
import com.example.database.entity.ImageToUpload
import com.example.repository.MongoDBRepositoryImpl
import com.example.ui.GalleryImage
import com.example.ui.GalleryState
import com.example.util.Constants.WRITE_SCREEN_ARG_KEY
import com.example.util.RequestState
import com.example.util.fetchImagesFromFirebase
import com.example.util.model.Diary
import com.example.util.model.Mood
import com.example.util.toRealmInstant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
internal class WriteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val imagesToUploadDao: ImagesToUploadDao,
    private val imagesToDeleteDao: ImagesToDeleteDao
) : ViewModel() {
    private val _uiState = MutableStateFlow(WriteUiState())
    val uiState: StateFlow<WriteUiState> = _uiState.asStateFlow()
    val galleryState = GalleryState()

    fun onAction(action: WriteAction) {
        when (action) {
            is WriteAction.GetDiaryIdArgument -> getDiaryIdArgument()
            is WriteAction.GetSelectedDiaryEntry -> getSelectedDiaryEntry()
            is WriteAction.SetSelectedDiaryEntry -> setSelectedDiaryEntry(action.diary)
            is WriteAction.SetTitle -> setTitle(action.title)
            is WriteAction.SetDescription -> setDescription(action.description)
            is WriteAction.SetMood -> setMood(action.mood)
            is WriteAction.SetDateTime -> setDateTime(action.zonedDateTime)
            is WriteAction.InsertDiaryEntry -> viewModelScope.launch {
                insertDiary(
                    diary = action.diary,
                    onSuccess = action.onSuccess,
                    onError = action.onError
                )
            }

            is WriteAction.UpdateDiaryEntry -> viewModelScope.launch {
                updateDiary(
                    diary = action.diary,
                    onSuccess = action.onSuccess,
                    onError = action.onError
                )
            }

            is WriteAction.UpsertDiaryEntry -> {
                upsertDiary(
                    diary = action.diary,
                    onSuccess = action.onSuccess,
                    onError = action.onError
                )
            }

            is WriteAction.DeleteDiaryEntry -> {
                deleteDiaryEntry(onSuccess = action.onSuccess, onError = action.onError)
            }

            is WriteAction.GenerateImagePathAndAddToGalleryStateList -> {
                generateImagePathAndAddToGalleryStateList(
                    image = action.image,
                    imageType = action.imageType
                )
            }

            is WriteAction.UploadImagesToFirebase -> uploadImagesToFirebase()
            is WriteAction.GetImagesFromFirebase -> getImagesFromFirebase(action.diary)
            is WriteAction.DeleteImagesFromFirebase -> deleteImagesFromFirebase(action.images)
            is WriteAction.ExtractRemoteImagePath -> extractRemoteImagePath(action.remoteImagePath)
        }
    }

    init {
        onAction(WriteAction.GetDiaryIdArgument)
        onAction(WriteAction.GetSelectedDiaryEntry)
    }

    private fun getDiaryIdArgument() {
        _uiState.update {
            it.copy(
                selectedDiaryId = savedStateHandle.get<String>(
                    key = WRITE_SCREEN_ARG_KEY
                )
            )
        }
    }

    private fun getSelectedDiaryEntry() {
        if (uiState.value.selectedDiaryId != null) {
            viewModelScope.launch {
                MongoDBRepositoryImpl.getSelectedDiaryEntry(
                    diaryId = ObjectId.invoke(uiState.value.selectedDiaryId!!)
                ).catch {
                    emit(RequestState.Error(Exception("Diary entry is already deleted")))
                }.collect { diary ->
                    if (diary is RequestState.Success) {
                        onAction(WriteAction.SetSelectedDiaryEntry(diary = diary.data))
                        onAction(WriteAction.SetTitle(title = diary.data.title))
                        onAction(WriteAction.SetDescription(description = diary.data.description))
                        onAction(WriteAction.SetMood(mood = Mood.valueOf(diary.data.mood)))
                        onAction(WriteAction.GetImagesFromFirebase(diary = diary))
                    }
                }
            }
        }
    }

    private fun setSelectedDiaryEntry(diary: Diary) {
        _uiState.update { it.copy(selectedDiary = diary) }
    }

    private fun setTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    private fun setDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    private fun setMood(mood: Mood) {
        _uiState.update { it.copy(mood = mood) }
    }

    private fun setDateTime(dateTime: ZonedDateTime?) {
        if (dateTime != null) {
            _uiState.update { it.copy(updatedDateTime = dateTime.toInstant().toRealmInstant()) }
        } else {
            _uiState.update { it.copy(updatedDateTime = null) }
        }
    }

    private suspend fun insertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDBRepositoryImpl.insertDiaryEntry(diary = diary.apply {
            if (uiState.value.updatedDateTime != null) {
                date = uiState.value.updatedDateTime!!
            }
        })

        if (result is RequestState.Success) {
            onAction(WriteAction.UploadImagesToFirebase)
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
        val result = MongoDBRepositoryImpl.updateDiaryEntry(diary = diary.apply {
            _id = ObjectId(uiState.value.selectedDiaryId!!)
            date = if (uiState.value.updatedDateTime != null) {
                uiState.value.updatedDateTime!!
            } else {
                uiState.value.selectedDiary!!.date
            }
        })

        if (result is RequestState.Success) {
            onAction(WriteAction.UploadImagesToFirebase)
            onAction(WriteAction.DeleteImagesFromFirebase())
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (result is RequestState.Error) {
            onError(result.error.message.toString())
        }
    }

    private fun upsertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.value.selectedDiaryId != null) {
                onAction(
                    WriteAction.UpdateDiaryEntry(
                        diary = diary,
                        onSuccess = onSuccess,
                        onError = onError
                    )
                )
            } else {
                onAction(
                    WriteAction.InsertDiaryEntry(
                        diary = diary,
                        onSuccess = onSuccess,
                        onError = onError
                    )
                )
            }
        }
    }

    private fun deleteDiaryEntry(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.value.selectedDiaryId != null) {
                val result = MongoDBRepositoryImpl.deleteDiaryEntry(
                    id = ObjectId(uiState.value.selectedDiaryId!!)
                )
                if (result is RequestState.Success) {
                    withContext(Dispatchers.Main) {
                        uiState.value.selectedDiary?.let {
                            onAction(WriteAction.DeleteImagesFromFirebase(images = it.images))
                        }
                        onSuccess()
                    }
                } else if (result is RequestState.Error) {
                    withContext(Dispatchers.IO) { onError(result.error.message.toString()) }
                }
            }
        }
    }

    private fun generateImagePathAndAddToGalleryStateList(
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

    private fun deleteImagesFromFirebase(
        images: List<String>? = null
    ) {
        val storage = FirebaseStorage.getInstance().reference
        if (images != null) {
            images.forEach { remotePath ->
                storage.child(remotePath).delete()
                    .addOnFailureListener {
                        viewModelScope.launch {
                            imagesToDeleteDao.addImageToDelete(
                                ImageToDelete(remoteImagePath = remotePath)
                            )
                        }
                    }
            }
        } else {
            galleryState.imagesToBeDeleted.map { it.remoteImagePath }.forEach { remotePath ->
                storage.child(remotePath).delete()
                    .addOnFailureListener {
                        viewModelScope.launch {
                            imagesToDeleteDao.addImageToDelete(
                                ImageToDelete(remoteImagePath = remotePath)
                            )
                        }
                    }
            }
        }
    }

    private fun extractRemoteImagePath(remotePath: String): String {
        val chunks = remotePath.split("%2F")
        val imageName = chunks[2].split("?").first()
        return "images/${Firebase.auth.currentUser?.uid}/$imageName"
    }
}