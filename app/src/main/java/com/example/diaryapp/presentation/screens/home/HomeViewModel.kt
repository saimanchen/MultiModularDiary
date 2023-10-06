package com.example.diaryapp.presentation.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaryapp.connectivity.ConnectivityObserver
import com.example.diaryapp.connectivity.NetworkConnectivityObserver
import com.example.diaryapp.data.repository.DiaryEntries
import com.example.diaryapp.data.repository.MongoDB
import com.example.diaryapp.model.local.ImagesToDeleteDao
import com.example.diaryapp.model.local.entity.ImageToDelete
import com.example.diaryapp.util.RequestState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val connectivity: NetworkConnectivityObserver,
    private val imagesToDeleteDao: ImagesToDeleteDao
) : ViewModel() {
    private var network by mutableStateOf(ConnectivityObserver.Status.Unavailable)
    var diaryEntries: MutableState<DiaryEntries> = mutableStateOf(RequestState.Idle)
    var isDateSelected by mutableStateOf(false)
        private set
    private lateinit var allDiaryEntriesJob: Job
    private lateinit var filteredDiaryEntriesJob: Job

    init {
        getDiaryEntries()
        viewModelScope.launch {
            connectivity.observe().collect {
                network = it
            }
        }
    }

    fun getDiaryEntries(zonedDateTime: ZonedDateTime? = null) {
        isDateSelected = zonedDateTime != null
        diaryEntries.value = RequestState.Loading
        if (isDateSelected && zonedDateTime != null) {
            observeFilteredDiaryEntries(zonedDateTime = zonedDateTime)
        } else {
            observeAllDiaryEntries()
        }
    }

    private fun observeAllDiaryEntries() {
        allDiaryEntriesJob = viewModelScope.launch {
            if (::filteredDiaryEntriesJob.isInitialized) {
                filteredDiaryEntriesJob.cancelAndJoin()
            }
            MongoDB.getAllDiaryEntries().collect { result ->
                diaryEntries.value = result
            }
        }
    }

    private fun observeFilteredDiaryEntries(zonedDateTime: ZonedDateTime) {
        filteredDiaryEntriesJob = viewModelScope.launch {
            if (::allDiaryEntriesJob.isInitialized) {
                allDiaryEntriesJob.cancelAndJoin()
            }
            MongoDB.getFilteredDiaryEntries(zonedDateTime = zonedDateTime).collect() { result ->
                diaryEntries.value = result
            }

        }
    }

    fun deleteAllDiaryEntries(
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (network == ConnectivityObserver.Status.Available) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val imagesDirectory = "images/${userId}"
            val storage = FirebaseStorage.getInstance().reference
            storage.child(imagesDirectory)
                .listAll()
                .addOnSuccessListener {
                    it.items.forEach { reference ->
                        val imagePath = "images/${userId}/${reference.name}"
                        storage.child(imagePath).delete()
                            .addOnFailureListener {
                                viewModelScope.launch(Dispatchers.IO) {
                                    imagesToDeleteDao.addImageToDelete(
                                        ImageToDelete(
                                            remoteImagePath = imagePath
                                        )
                                    )
                                }
                            }
                    }
                    viewModelScope.launch(Dispatchers.IO) {
                        val result = MongoDB.deleteAllDiaryEntries()
                        if (result is RequestState.Success) {
                            withContext(Dispatchers.Main) {
                                onSuccess()
                            }
                        } else if (result is RequestState.Error) {
                            onError(result.error)
                        }
                    }
                }
                .addOnFailureListener { onError(it) }
        } else {
            onError(Exception("No internet connection was found."))
        }
    }
}